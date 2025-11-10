package org.jsonk.processor;

import org.jsonk.util.Util;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

interface Type {

    String getText();

    default Type getValueType() {
        return this;
    }

    default boolean isNullable() {
        return true;
    }

    default boolean isParameterized() {
        return false;
    }

    String getErasedText();

    static Type from(TypeMirror typeMirror) {
        return switch (typeMirror.getKind()) {
            case BOOLEAN -> PrimitiveType.BOOLEAN;
            case BYTE -> PrimitiveType.BYTE;
            case SHORT -> PrimitiveType.SHORT;
            case INT -> PrimitiveType.INT;
            case LONG -> PrimitiveType.LONG;
            case FLOAT -> PrimitiveType.FLOAT;
            case DOUBLE -> PrimitiveType.DOUBLE;
            case CHAR -> PrimitiveType.CHAR;
            case VOID -> PrimitiveType.VOID;
            case ARRAY -> new ArrayType(
                    from(((javax.lang.model.type.ArrayType) typeMirror).getComponentType())
            );
            case DECLARED -> {
                var dt = (DeclaredType) typeMirror;
                var clazz = (TypeElement) dt.asElement();
                yield new ClassType(
                        clazz,
                        Util.map(dt.getTypeArguments(), Type::from)
                );
            }
            case WILDCARD -> {
                var wc = (javax.lang.model.type.WildcardType) typeMirror;
                yield new WildcardType(
                        Util.safeCall(wc.getSuperBound(), Type::from),
                        Util.safeCall(wc.getExtendsBound(), Type::from)
                );
            }
            case UNION -> {
                var uc = (javax.lang.model.type.UnionType) typeMirror;
                yield new UnionType(Util.mapToSet(uc.getAlternatives(), Type::from));
            }
            case INTERSECTION -> {
                var ic = (javax.lang.model.type.IntersectionType) typeMirror;
                yield new IntersectionType(Util.mapToSet(ic.getBounds(), Type::from));
            }
            case TYPEVAR -> {
                var tv = (javax.lang.model.type.TypeVariable) typeMirror;
                yield new TypeVariable((TypeParameterElement) tv.asElement());
            }
            default -> new UnknownType();
        };
    }

}

class UnknownType implements Type {

    @Override
    public String getText() {
        return "unknown";
    }

    @Override
    public String getErasedText() {
        return getText();
    }


}

record ClassType(
        TypeElement element,
        List<Type> typeArgs
) implements Type {

    public Name qualName() {
        return element.getQualifiedName();
    }

    @Override
    public String getText() {
        if (typeArgs.isEmpty())
            return qualName().toString();
        var sb = new StringBuilder(qualName().toString()).append('<');
        var it = typeArgs.iterator();
        sb.append(it.next().getText());
        while (it.hasNext()) {
            sb.append(',');
            sb.append(it.next().getText());
        }
        sb.append('>');
        return sb.toString();
    }

    @Override
    public Type getValueType() {
        var typeName = qualName().toString();
        return switch (typeName) {
            case "java.util.List", "java.util.Set", "java.util.Collection", "java.util.ArrayList", "java.util.LinkedList",
                    "java.util.Queue", "java.util.Deque", "java.util.SortedSet", "java.util.NavigableSet",
                    "java.util.SequencedSet" ->
                    typeArgs.isEmpty() ? null : typeArgs().getFirst();
            case "java.util.Map", "java.util.HashMap", "java.util.LinkedHashMap", "java.util.TreeMap",
                    "java.util.SortedMap", "java.util.NavigableMap", "java.util.SequencedMap" ->
                    typeArgs.isEmpty() ? null : typeArgs.getLast();
            default -> this;
        };
    }

    @Override
    public boolean isParameterized() {
        return !typeArgs.isEmpty();
    }

    @Override
    public String getErasedText() {
        return qualName().toString();
    }
}

record ArrayType(
        Type componentType
) implements Type {
    @Override
    public String getText() {
        return componentType.getText() + "[]";
    }

    @Override
    public Type getValueType() {
        return componentType.getValueType();
    }

    @Override
    public boolean isParameterized() {
        return componentType.isParameterized();
    }

    @Override
    public String getErasedText() {
        return componentType.getErasedText() + "[]";
    }
}

record WildcardType(
        Type lowerBound,
        Type upperBound
) implements Type {
    @Override
    public String getText() {
        if (lowerBound != null)
            return "? super " + lowerBound.getText();
        else
            return "? extends " + upperBound.getText();
    }

    @Override
    public String getErasedText() {
        if (lowerBound != null)
            return "? super " + lowerBound.getErasedText();
        else
            return "? extends " + upperBound.getErasedText();
    }

}

record TypeVariable(
   TypeParameterElement element
) implements Type {
    @Override
    public String getText() {
        return element.getSimpleName().toString();
    }

    @Override
    public String getErasedText() {
        return getText();
    }

}

record UnionType(Set<Type> alternatives) implements Type {
    @Override
    public String getText() {
        return alternatives.stream().map(Type::getText)
                .collect(Collectors.joining("|"));
    }

    @Override
    public String getErasedText() {
        return alternatives.stream().map(Type::getErasedText)
                .collect(Collectors.joining("|"));
    }

}

record IntersectionType(Set<Type> bounds) implements Type {
    @Override
    public String getText() {
        return bounds.stream().map(Type::getText)
                .collect(Collectors.joining("&"));
    }

    @Override
    public String getErasedText() {
        return bounds.stream().map(Type::getErasedText)
                .collect(Collectors.joining("&"));
    }

}

enum PrimitiveType implements Type {
    BYTE,
    SHORT,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    BOOLEAN,
    CHAR,
    VOID;

    @Override
    public String getText() {
        return name().toLowerCase();
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public String getErasedText() {
        return getText();
    }
}
