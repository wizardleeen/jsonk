package org.jsonk.element;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsonk.util.Util;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

@Slf4j
public class ElementFactory {

    public static final ElementFactory instance = new ElementFactory();
    private final Map<String, MockPackageElement> packages = new HashMap<>();
    private final Map<String, MockClass> classes = new HashMap<>();
    private final Map<TypeVariable<?>, MockTypeParamElement> typeParams = new HashMap<>();

    public MockClass buildClass(Class<?> cls) {
//        log.debug("Building class: {}", cls.getName());
        var existing = classes.get(cls.getName());
        if (existing != null)
            return existing;
        var pkg = getPackage(cls.getPackageName());
        ElementKind kind;
        if (cls.isEnum())
            kind = ElementKind.ENUM;
        else if (cls.isInterface())
            kind = ElementKind.INTERFACE;
        else if (cls.isRecord())
            kind = ElementKind.RECORD;
        else
            kind = ElementKind.CLASS;
        var k = new MockClass(
                kind,
                pkg,
                MockName.of(cls.getSimpleName()),
                buildMods(cls.getModifiers())
        );
        classes.put(cls.getName(), k);
        if (cls.getEnclosingClass() != null)
            k.setOwner(buildClass(cls.getEnclosingClass()));
        k.setTypeParameters(buildTypeVariables(k, cls.getTypeParameters()));
        k.setType(TypeFactory.instance.getClassType(null, k, List.of()));
        if (cls.getGenericSuperclass() != null)
            k.setSuperClass((MockDeclaredType) buildType(cls.getGenericSuperclass()));
        k.setInterfaces(Util.map(cls.getGenericInterfaces(), this::buildType));
        var members = new ArrayList<MockElement>();
        for (Field field : cls.getDeclaredFields()) {
            members.add(buildField(k, field));
        }
        for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
            members.add(buildConstructor(k, constructor));
        }
        for (Method method : cls.getDeclaredMethods()) {
            members.add(buildMethod(k, method));
        }
        for (Class<?> c : cls.getDeclaredClasses()) {
            members.add(buildClass(c));
        }
        k.setMembers(members);
        if (cls.isRecord()) {
            var methodMap = new HashMap<String, ExecutableElement>();
            for (Element e : k.getEnclosedElements()) {
                if (e instanceof ExecutableElement m && m.getParameters().isEmpty()) {
                    methodMap.put(m.getSimpleName().toString(), m);
                }
            }
            k.setRecordComponents(
                Util.map(cls.getRecordComponents(), rc -> buildRecordComponent(k, rc, methodMap.get(rc.getName())))
            );
        }
        setAnnotations(k, cls);
        return k;
    }


    private void setAnnotations(MockElement element, AnnotatedElement reflectElem) {
        element.setAnnotations(Util.map(reflectElem.getAnnotations(), this::buildAnnotation));
    }

    @SneakyThrows
    private AnnotationMirror buildAnnotation(Annotation annotation) {
        var type = (MockDeclaredType) buildType(annotation.annotationType());
        var values = new HashMap<String, AnnotationValue>();
        for (Method m : annotation.annotationType().getDeclaredMethods()) {
            try {
                var value = m.invoke(annotation);
                values.put(m.getName(), convertAnnotationValue(value));
            } catch (IllegalAccessException ignored) {
                // Module access control can lead to failure
            }
        }
        return new MockAnnotation(type, values);
    }

    private AnnotationValue convertAnnotationValue(Object value) {
        var v = switch (value) {
            case Object[] array -> {
                var list = new ArrayList<>();
                for (Object o : array) {
                    list.add(convertAnnotationValue(o));
                }
                yield list;
            }
            case Annotation annotation -> buildAnnotation(annotation);
            case Type type -> buildType(type);
            default -> value;
        };
        return new MockAnnotationValue(v);
    }

    private RecordComponentElement buildRecordComponent(TypeElement clazz, RecordComponent rc, ExecutableElement accessor) {
        var result = new MockRecordComponentElement(clazz, MockName.of(rc.getName()), buildType(rc.getGenericType()), accessor);
        setAnnotations(result, rc);
        return result;
    }

    private MockElement buildMethod(MockClass clazz, Method method) {
//        log.debug("Building method: {}", method);
        var exec = new MockExecElement(
                clazz,
                buildMods(method.getModifiers()),
                MockName.of(method.getName()),
                clazz.asType(),
                ElementKind.METHOD,
                method.isDefault(),
                method.isVarArgs()
        );
        exec.setTypeParams(buildTypeVariables(exec, method.getTypeParameters()));
        exec.setRetType(buildType(method.getGenericReturnType()));
        exec.setThrowTypes(Util.map(method.getExceptionTypes(), this::buildType));
        exec.setParams(Util.map(method.getParameters(), p -> buildParam(p, exec)));
        exec.setType(TypeFactory.instance.getExecutableType(
                Util.map(exec.getTypeParameters(), tp -> (MockTypeVariable) tp.asType()),
                java.lang.reflect.Modifier.isStatic(method.getModifiers()) ? null : clazz.asType(),
                Util.map(exec.getParameters(), MockVarElement::asType),
                exec.getReturnType(),
                exec.getThrownTypes()
        ));
        setAnnotations(exec, method);
        return exec;
    }

    private MockExecElement buildConstructor(MockClass clazz, Constructor<?> constructor) {
        var exec = new MockExecElement(
                clazz,
                buildMods(constructor.getModifiers()),
                MockName.of("<init>"),
                clazz.asType(),
                ElementKind.CONSTRUCTOR,
                false,
                constructor.isVarArgs()
        );
        exec.setTypeParams(buildTypeVariables(exec, constructor.getTypeParameters()));
        exec.setRetType(TypeFactory.instance.getVoidType());
        exec.setThrowTypes(Util.map(constructor.getExceptionTypes(), this::buildType));
        exec.setParams(Util.map(constructor.getParameters(), p -> buildParam(p, exec)));
        exec.setType(TypeFactory.instance.getExecutableType(
                Util.map(exec.getTypeParameters(), tp -> (MockTypeVariable) tp.asType()),
                null,
                Util.map(exec.getParameters(), MockVarElement::asType),
                exec.getReturnType(),
                exec.getThrownTypes()
        ));
        setAnnotations(exec, constructor);
        return exec;
    }

    private List<MockTypeParamElement> buildTypeVariables(Element owner, TypeVariable<?>[] typeVars) {
        if (typeVars.length == 0)
            return List.of();
        var result = new ArrayList<MockTypeParamElement>();
        for (TypeVariable<?> typeVar : typeVars) {
            var tp = new MockTypeParamElement(owner, MockName.of(typeVar.getName()));
            typeParams.put(typeVar, tp);
            var type = (MockTypeVariable) TypeFactory.instance.getTypeVariable(tp);
            tp.setType(type);
            result.add(tp);
        }
        var it = result.iterator();
        for (TypeVariable<?> typeVar : typeVars) {
            var bounds = Util.map(typeVar.getBounds(), this::buildType);
            var tp = it.next();
            tp.setBounds(bounds);
            ((MockTypeVariable) tp.asType()).setUpperBound(TypeFactory.instance.getIntersectionType(new HashSet<>(bounds)));
            setAnnotations(tp, typeVar);
        }
        return result;
    }

    private MockVarElement buildParam(Parameter param, ExecutableElement exec) {
        var e = new MockVarElement(
                ElementKind.PARAMETER,
                exec,
                buildMods(param.getModifiers()),
                MockName.of(param.getName()),
                buildType(param.getParameterizedType())
        );
        setAnnotations(e, param);
        return e;
    }

    private MockVarElement buildField(TypeElement clazz, Field field) {
        var e = new MockVarElement(
                ElementKind.FIELD,
                clazz,
                buildMods(field.getModifiers()),
                MockName.of(field.getName()),
                buildType(field.getGenericType())
        );
        setAnnotations(e, field);
        return e;
    }

    private MockType buildType(Type type) {
        var typeFactory = TypeFactory.instance;
        return switch (type) {
            case Class<?> cls -> {
                if (cls.isPrimitive()) {
                    yield switch (cls.getName()) {
                        case "boolean" -> typeFactory.getBooleanType();
                        case "byte" -> typeFactory.getByteType();
                        case "short" -> typeFactory.getShortType();
                        case "int" -> typeFactory.getIntType();
                        case "long" -> typeFactory.getLongType();
                        case "char" -> typeFactory.getCharType();
                        case "float" -> typeFactory.getFloatType();
                        case "double" -> typeFactory.getDoubleType();
                        case "void" -> typeFactory.getVoidType();
                        default -> throw new IllegalStateException("Unexpected primitive type: " + cls.getName());
                    };
                }
                else if (cls.isArray())
                    yield typeFactory.getArrayType(buildType(cls.getComponentType()));
                else
                    yield typeFactory.getClassType(
                        null,
                        buildClass(cls),
                        List.of()
                );
            }
            case ParameterizedType pType -> typeFactory.getClassType(
                    (MockDeclaredType) Util.safeCall(pType.getOwnerType(), this::buildType),
                    buildClass((Class<?>) pType.getRawType()),
                    Util.map(Arrays.asList(pType.getActualTypeArguments()), this::buildType)
            );
            case GenericArrayType arrayType -> typeFactory.getArrayType(buildType(arrayType.getGenericComponentType()));
            case WildcardType wildcardType -> {
                MockType lb = wildcardType.getLowerBounds().length == 0 ? null :
                     typeFactory.getUnionType(Util.mapToSet(wildcardType.getLowerBounds(), this::buildType));
                MockType ub = wildcardType.getUpperBounds().length == 0 ? null :
                        typeFactory.getIntersectionType(Util.mapToSet(wildcardType.getUpperBounds(), this::buildType));
                yield typeFactory.getWildcardType(lb, ub);
            }
            case TypeVariable<?> typeVar -> typeFactory.getTypeVariable(getTypeParam(typeVar));
            default -> throw new IllegalStateException("Unexpected type: " + type);
        };
    }

    private MockTypeParamElement getTypeParam(TypeVariable<?> tv) {
        var existing = typeParams.get(tv);
        if (existing != null)
            return existing;
        switch (tv.getGenericDeclaration()) {
            case Class<?> cls -> buildClass(cls);
            case Method method -> buildClass(method.getDeclaringClass());
            default -> throw new IllegalStateException("Unexpected generic declaration: " + tv.getGenericDeclaration());
        }
        return Objects.requireNonNull(typeParams.get(tv));
    }

    private Set<Modifier> buildMods(int flags) {
        var mods = new TreeSet<Modifier>();
        if (java.lang.reflect.Modifier.isAbstract(flags))
            mods.add(Modifier.ABSTRACT);
        if (java.lang.reflect.Modifier.isFinal(flags))
            mods.add(Modifier.FINAL);
        if (java.lang.reflect.Modifier.isStatic(flags))
            mods.add(Modifier.STATIC);
        if (java.lang.reflect.Modifier.isPublic(flags))
            mods.add(Modifier.PUBLIC);
        if (java.lang.reflect.Modifier.isProtected(flags))
            mods.add(Modifier.PROTECTED);
        if (java.lang.reflect.Modifier.isPrivate(flags))
            mods.add(Modifier.PRIVATE);
        return mods;
    }

    public MockPackageElement getPackage(String name) {
        return packages.computeIfAbsent(name, MockPackageElement::new);
    }

}
