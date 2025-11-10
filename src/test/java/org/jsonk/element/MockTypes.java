package org.jsonk.element;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.*;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockTypes implements Types {

    public static final MockTypes instance = new MockTypes();

    private final ElementFactory elementFactory = ElementFactory.instance;
    private final TypeFactory typeFactory = TypeFactory.instance;

    @Override
    public Element asElement(TypeMirror t) {
        return switch (t) {
            case MockDeclaredType declType -> declType.asElement();
            case MockTypeVariable typeVar -> typeVar.asElement();
            default -> null;
        };
    }

    @Override
    public boolean isSameType(TypeMirror t1, TypeMirror t2) {
        return t1 == t2;
    }

    @Override
    public boolean isSubtype(TypeMirror t1, TypeMirror t2) {
        return false;
    }

    @Override
    public boolean isAssignable(TypeMirror t1, TypeMirror t2) {
        var mt1 = (MockType) t1;
        var mt2 = (MockType) t2;
        return mt2.isAssignableFrom(mt1);
    }

    @Override
    public boolean contains(TypeMirror t1, TypeMirror t2) {
        var mt1 = (MockType) t1;
        var mt2 = (MockType) t2;
        return mt1.contains(mt2);
    }

    @Override
    public boolean isSubsignature(ExecutableType m1, ExecutableType m2) {
        return false;
    }

    @Override
    public List<? extends TypeMirror> directSupertypes(TypeMirror t) {
        return null;
    }

    @Override
    public TypeMirror erasure(TypeMirror t) {
        if (t instanceof MockDeclaredType declType)
            return typeFactory.getClassType(null, (MockClass) declType.asElement(), List.of());
        else
            return t;
    }

    @Override
    public TypeElement boxedClass(PrimitiveType p) {
        var cls = switch (p.getKind()) {
            case BYTE -> Byte.class;
            case SHORT -> Short.class;
            case INT -> Integer.class;
            case LONG -> Long.class;
            case CHAR -> Character.class;
            case FLOAT -> Float.class;
            case DOUBLE -> Double.class;
            case BOOLEAN -> Boolean.class;
            case VOID -> Void.class;
            default -> throw new IllegalArgumentException("Unknown primitive type: " + p);
        };
        return elementFactory.buildClass(cls);
    }

    @Override
    public PrimitiveType unboxedType(TypeMirror t) {
        if (t instanceof MockDeclaredType declType) {
            var cls = (MockClass) declType.asElement();
            return switch (cls.getQualifiedName().toString()) {
                case "java.lang.Byte" -> typeFactory.getByteType();
                case "java.lang.Short" -> typeFactory.getShortType();
                case "java.lang.Integer" -> typeFactory.getIntType();
                case "java.lang.Long" -> typeFactory.getLongType();
                case "java.lang.Character" -> typeFactory.getCharType();
                case "java.lang.Float" -> typeFactory.getFloatType();
                case "java.lang.Double" -> typeFactory.getDoubleType();
                case "java.lang.Boolean" -> typeFactory.getBooleanType();
                case "java.lang.Void" -> typeFactory.getVoidType();
                default -> throw new IllegalArgumentException("Not a boxed primitive type: " + t);
            };
        }
        throw new IllegalArgumentException("Not a boxed primitive type: " + t);
    }

    @Override
    public TypeMirror capture(TypeMirror t) {
        return null;
    }

    @Override
    public PrimitiveType getPrimitiveType(TypeKind kind) {
        return switch (kind) {
            case BYTE -> typeFactory.getByteType();
            case SHORT -> typeFactory.getShortType();
            case INT -> typeFactory.getIntType();
            case LONG -> typeFactory.getLongType();
            case CHAR -> typeFactory.getCharType();
            case FLOAT -> typeFactory.getFloatType();
            case DOUBLE -> typeFactory.getDoubleType();
            case BOOLEAN -> typeFactory.getBooleanType();
            case VOID -> typeFactory.getVoidType();
            default -> throw new IllegalArgumentException("Unknown primitive type kind: " + kind);
        };
    }

    @Override
    public NullType getNullType() {
        return typeFactory.getNullType();
    }

    @Override
    public NoType getNoType(TypeKind kind) {
        return typeFactory.getNoType();
    }

    @Override
    public ArrayType getArrayType(TypeMirror componentType) {
        return typeFactory.getArrayType((MockType) componentType);
    }

    @Override
    public WildcardType getWildcardType(TypeMirror extendsBound, TypeMirror superBound) {
        return typeFactory.getWildcardType((MockType) superBound, (MockType) extendsBound);
    }

    @Override
    public DeclaredType getDeclaredType(TypeElement typeElem, TypeMirror... typeArgs) {
        return getDeclaredType(null, typeElem, typeArgs);
    }

    @Override
    public DeclaredType getDeclaredType(DeclaredType containing, TypeElement typeElem, TypeMirror... typeArgs) {
        var typeArgList = new ArrayList<MockType>();
        for (TypeMirror typeArg : typeArgs) {
            typeArgList.add((MockType) typeArg);
        }
        return typeFactory.getClassType((MockDeclaredType) containing, (MockClass) typeElem, typeArgList);
    }

    @Override
    public TypeMirror asMemberOf(DeclaredType containing, Element element) {
        return null;
    }
}
