package org.jsonk.processor;

import org.jsonk.util.StringUtil;
import org.jsonk.util.Util;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import java.util.*;
import java.util.function.Supplier;

class Introspects {

    private final Annotations annotations;
    private final MyNames names;
    private final TypesExt typesExt;
    private final Env env;

    Introspects(Annotations annotations, MyNames names, TypesExt typesExt, Env env) {
        this.annotations = annotations;
        this.names = names;
        this.typesExt = typesExt;
        this.env = env;
    }

    Clazz introspect(TypeElement element, Env env) {
        var record = element.getKind() == ElementKind.RECORD;
        Constructor constructor = null;
        var props = new LinkedHashMap<String, Property>();
        Set<ExecutableElement> recordAccessors = record ? new HashSet<>() : Set.of();
        var typeNames = getTypeNames(element, env);
        var typeNameSet = Util.mapToSet(typeNames, TypeName::property);
        if (record) {
            var params = new ArrayList<Parameter>();
            for (RecordComponentElement recordComponent : element.getRecordComponents()) {
                var name = getPropName(recordComponent, () ->
                        getPropName(recordComponent.getAccessor(), () -> ElementUtil.getName(recordComponent)));
                if (typeNameSet.contains(name))
                    continue;
                params.add(new Parameter(name, recordComponent, recordComponent.asType(),
                        annotations.getPropertyConfig(recordComponent.getAccessor())));
                var prop = createProperty(name);
                props.put(name, prop);
                prop.setRecordComponent(recordComponent);
            }
            constructor = new Constructor(params);
        }
        var c = element;
        var classes = new LinkedList<TypeElement>();
        for (;;) {
            classes.addFirst(c);
            if (c.getSuperclass() instanceof DeclaredType dt && !typesExt.isObjectType(dt))
                c = (TypeElement) dt.asElement();
            else
                break;
        }
        for (var c1 : classes) {
            for (Element member : c1.getEnclosedElements()) {
                switch (member) {
                    case VariableElement field -> {
                        if (!record && !ElementUtil.isStatic(field)) {
                            var name = getPropName(field, () -> ElementUtil.getName(field));
                            if (typeNameSet.contains(name))
                                continue;
                            var prop = props.computeIfAbsent(name, this::createProperty);
                            prop.setField(field);
                        }
                    }
                    case ExecutableElement method -> {
                        if (!ElementUtil.isStatic(member) && !recordAccessors.contains(method)) {
                            if (c1 == element && method.getKind() == ElementKind.CONSTRUCTOR && constructor == null) {
                                constructor = buildConstructor(method);
                                continue;
                            }
                            var name = ElementUtil.getName(member);
                            if (name.startsWith("get")) {
                                if (method.getParameters().isEmpty()) {
                                    var propName = StringUtil.decapitalize(name.substring(3));
                                    if (typeNameSet.contains(propName))
                                        continue;
                                    props.computeIfAbsent(propName, this::createProperty).setGetter(method);
                                }
                            } else if (name.startsWith("is")) {
                                if (method.getParameters().isEmpty() && typesExt.isBooleanType(method.getReturnType())) {
                                    var propName = StringUtil.decapitalize(name.substring(2));
                                    if (typeNameSet.contains(propName))
                                        continue;
                                    props.computeIfAbsent(propName, this::createProperty).setGetter(method);
                                }
                            } else if (name.startsWith("set")) {
                                if (method.getParameters().size() == 1 && method.getReturnType().getKind() == TypeKind.VOID) {
                                    var propName = StringUtil.decapitalize(name.substring(3));
                                    if (typeNameSet.contains(propName))
                                        continue;
                                    props.computeIfAbsent(propName, this::createProperty).setSetter(method);
                                }
                            }
                        }
                    }
                    default -> {
                    }
                }
            }
        }
        props.values().forEach(this::checkPropertyType);
        var annotation = annotations.getAnnotation(element, names.classJson);
        if (constructor == null && annotations.getTypeProperty(annotation) == null)
            throw new IllegalArgumentException("No public constructor found for " + element.getQualifiedName());
        return new Clazz(
                annotations,
                typesExt,
                element,
                constructor,
                Util.filter(props.values(), p -> !p.isIgnored()),
                annotation,
                typeNames
        );
    }

    private void checkPropertyType(Property property) {
        env.setCurrentElement(property.getMainElement());
        var type = property.getValueType();
        if (type instanceof PrimitiveType || type instanceof TypeVariable)
            return;
        if (type instanceof ClassType ct) {
            if (names.builtinClassNames.contains(ct.qualName()))
                return;
            var clazz = (TypeElement) ct.element();
            if (annotations.isAnnotationPresent(clazz, names.classJson))
                return;
        }
        env.addError("Missing annotation @Json on '" + property.getValueType() + "'");
    }

    private Property createProperty(String name) {
        return new Property(annotations, names, typesExt, name, env);
    }

    private Constructor buildConstructor(ExecutableElement method) {
        return new Constructor(
                Util.map(method.getParameters(), p -> new Parameter(
                        getPropName(p, () -> ElementUtil.getName(p)),
                        p,
                        p.asType(),
                        annotations.getPropertyConfig(p))
                )
        );
    }

    private String getPropName(Element element, Supplier<String> getDefault) {
        var annotation = annotations.getAnnotation(element, names.classJsonProperty);
        if (annotation != null) {
            var name = (String) annotations.getAttribute(annotation, names.value);
            if (StringUtil.isNotEmpty(name))
                return name;
        }
        return getDefault.get();
    }

    private Collection<TypeName> getTypeNames(TypeElement clazz, Env env) {
        env.setCurrentElement(clazz);
        var queue = new LinkedList<TypeElement>();
        if (clazz.getSuperclass() instanceof DeclaredType superCls && !typesExt.isObjectType(superCls))
            queue.offer((TypeElement) superCls.asElement());
        clazz.getInterfaces().forEach(it -> queue.offer(typesExt.getClazz(it)));
        var typeNames = new HashMap<String, TypeName>();
        while (!queue.isEmpty()) {
            var s = queue.poll();
            var annotation = annotations.getAnnotation(s, names.classJson);
            if (annotation != null) {
                var typeProp = annotations.getTypeProperty(annotation);
                if (typeProp != null) {
                    annotations.forEachSubType(annotation, (name, type) -> {
                        if (type.asElement() == clazz) {
                            var existing = typeNames.put(typeProp, new TypeName(typeProp, name));
                            if (existing != null && !existing.name().equals(name)) {
                                // The current type has two type names with the same property but different names
                                env.addError("Conflicting type names for type " + clazz.getQualifiedName() +
                                        ": '" + existing.name() + "' and '" + name + "' for typeProperty '" + typeProp + "'");
                            }
                        }
                    });
                }
            }
        }
        return typeNames.values();
    }

}
