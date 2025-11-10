package org.jsonk.processor;

import jakarta.annotation.Nullable;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

class TypesExt {

    private final CommonNames commonNames;

    TypesExt(CommonNames commonNames) {
        this.commonNames = commonNames;
    }

    boolean isClassType(DeclaredType type, Name qualName) {
        return getClassName(type).equals(qualName);
    }

    public Name getClassName(DeclaredType declType) {
        return ((TypeElement) declType.asElement()).getQualifiedName();
    }

    public boolean isBooleanType(TypeMirror type) {
        return switch (type.getKind()) {
            case BOOLEAN -> true;
            case DECLARED -> getClassName((DeclaredType) type).equals(commonNames.classBoolean);
            default -> false;
        };
    }

    public boolean isDateType(TypeMirror type) {
        return type instanceof DeclaredType dt && isClassType(dt, commonNames.classDate);
    }

    public boolean isInstantType(TypeMirror type) {
        return type instanceof DeclaredType dt && isClassType(dt, commonNames.classInstant);
    }

    public boolean isTemporalType(TypeMirror type, Elements elements, Types types) {
        if (type instanceof DeclaredType dt) {
            var temporalType = elements.getTypeElement(commonNames.classTemporal).asType();
            return types.isAssignable(dt, temporalType);
        } else
            return false;
    }

    public boolean isStringType(TypeMirror type) {
        return type instanceof DeclaredType dt && isClassType(dt, commonNames.classString);
    }

    public boolean isObjectType(DeclaredType type) {
        return isClassType(type, commonNames.classObject);
    }

    public TypeElement getClazz(TypeMirror type) {
        return (TypeElement) ((DeclaredType) type).asElement();
    }

    public boolean isPojoType(DeclaredType type) {
        var element = (TypeElement) type.asElement();
        if (element.getKind() == ElementKind.ENUM)
            return false;
        var name = element.getQualifiedName().toString();
        return switch (name) {
            case "java.lang.Boolean", "java.lang.Byte", "java.lang.Short", "java.lang.Integer",
                    "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.Character",
                    "java.lang.String", "java.math.BigInteger", "java.math.BigDecimal",
                    "java.util.Date", "java.time.LocalDate", "java.time.LocalDateTime",
                    "java.time.LocalTime", "java.time.OffsetDateTime", "java.time.OffsetTime",
                    "java.time.ZonedDateTime", "java.time.Instant", "java.util.UUID",
                    "java.util.List", "java.util.Set", "java.util.Map", "java.util.Collection",
                    "java.util.TreeMap", "java.util.HashMap", "java.util.LinkedHashMap",
                    "java.util.TreeSet", "java.util.HashSet", "java.util.Queue", "java.util.Deque",
                    "java.util.NavigableSet", "java.util.SortedSet", "java.util.ArrayList", "java.util.LinkedList",
                    "java.util.NavigableMap", "java.util.SortedMap", "java.lang.Object"
                    -> false;
            default -> true;
        };
    }

    public boolean isNullable(TypeMirror type) {
        return !(type instanceof PrimitiveType);
    }

    @Nullable TypeMirror getValueType(TypeMirror type) {
        return switch (type) {
            case PrimitiveType primitiveType -> primitiveType;
            case DeclaredType declaredType -> {
                var typeName = getClassName(declaredType).toString();
                yield switch (typeName) {
                    case "java.util.List", "java.util.Set", "java.util.Collection", "java.util.ArrayList", "java.util.LinkedList",
                            "java.util.Queue", "java.util.Deque", "java.util.SortedSet", "java.util.NavigableSet",
                            "java.util.SequencedSet" ->
                        declaredType.getTypeArguments().isEmpty() ? null : declaredType.getTypeArguments().getFirst();
                    case "java.util.Map", "java.util.HashMap", "java.util.LinkedHashMap", "java.util.TreeMap",
                            "java.util.SortedMap", "java.util.NavigableMap", "java.util.SequencedMap" ->
                        declaredType.getTypeArguments().isEmpty() ? null : declaredType.getTypeArguments().getLast();
                    default -> declaredType;
                };
            }
            case ArrayType arrayType -> getValueType(arrayType.getComponentType());
            default -> type;
        };
    }

    public String getErasedText(TypeMirror type) {
        return switch (type) {
            case PrimitiveType primitiveType -> primitiveType.toString();
            case DeclaredType declaredType -> getClassName(declaredType).toString();
            case ArrayType arrayType -> getErasedText(arrayType.getComponentType()) + "[]";
            default -> throw new IllegalArgumentException("Unsupported type: " + type);
        };
    }

    public boolean isParameterized(TypeMirror type) {
        return switch (type) {
            case ArrayType arrayType -> isParameterized(arrayType.getComponentType());
            case DeclaredType declaredType -> !declaredType.getTypeArguments().isEmpty();
            default -> false;
        };
    }

}
