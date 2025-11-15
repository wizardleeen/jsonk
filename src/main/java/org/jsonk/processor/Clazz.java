package org.jsonk.processor;

import jakarta.annotation.Nullable;
import lombok.Getter;
import org.jsonk.util.StringUtil;
import org.jsonk.util.Util;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.function.BiConsumer;

final class Clazz {
    private final Annotations annotations;
    private final TypesExt typesExt;
    @Getter
    private final TypeElement element;
    @Getter
    private final @Nullable Constructor constructor;
    @Getter
    private final List<Property> properties;
    private final AnnotationMirror annotation;
    private final List<TypeName> typeNames;

    Clazz(
            Annotations annotations, TypesExt typesExt, TypeElement element,
            Constructor constructor,
            Collection<Property> properties,
            AnnotationMirror annotation,
            Collection<TypeName> typeNames
    ) {
        this.annotations = annotations;
        this.typesExt = typesExt;
        this.element = element;
        this.constructor = constructor;
        this.properties = new ArrayList<>(properties);
        this.annotation = annotation;
        this.typeNames = new ArrayList<>(typeNames);
    }

    String getName() {
        return element.getSimpleName().toString();
    }

    String getQualName() {
        return element.getQualifiedName().toString();
    }

    boolean isJavaBean() {
        return constructor != null && constructor.parameters().isEmpty();
    }

    TypeMirror asType() {
        return element.asType();
    }

    boolean isConcreteSuperType() {
        return Util.anyMatch(getSubTypes(), t -> t.type().equals(asType()));
    }

    boolean isRecord() {
        if (constructor == null)
            return false;
        if (element.getKind() == ElementKind.RECORD)
            return true;
        var initArgNames = Util.mapToSet(constructor.parameters(), Parameter::name);
        return Util.allMatch(properties, p -> initArgNames.contains(p.name()));
    }

    List<Parameter> getConstructorParams() {
        return Objects.requireNonNull(constructor).parameters();
    }

    boolean isPolymorphic() {
        return StringUtil.isNotEmpty(getTypeProperty());
    }

    String getTypeProperty() {
        return annotations.getTypeProperty(annotation);
    }

    Type getTypePropertyType() {
        var d = getTypeProperty();
        if (d != null)
            return Util.findRequired(properties, p -> p.name().equals(d)).getType();
        else
            throw new IllegalStateException("Not a polymorphic type");
    }

    List<SubType> getSubTypes() {
        var subTypes = new ArrayList<SubType>();
        forEachSubType((name, type) -> subTypes.add(new SubType(name, type)));
        return subTypes;
    }

    int numWritableProps() {
        var cnt = 0;
        for (Property property : properties) {
            if (property.isReadable())
                cnt++;
        }
        return cnt;
    }

    Property firstWritableProp() {
        for (Property property : properties) {
            if (property.isReadable())
                return property;
        }
        throw new NoSuchElementException();
    }

    void forEachReferenceType(BiConsumer<Type, Map<String, Object>> action) {
        var visited = new HashSet<List<?>>();
        visited.add(List.of(Type.from(element.asType()), Map.of()));
        for (Property prop : properties) {
            var df = prop.getDateTimeFormat();
            var type = prop.getType();
            Map<String, Object> attrs = df != null ? Map.of("dateFormat", df) : Map.of();
            if (type instanceof ClassType ct && ct.element().getKind() != ElementKind.ENUM
                    && !ct.qualName().contentEquals("java.lang.String")
                    || type instanceof ArrayType || type instanceof TypeVariable) {
                if (visited.add(List.of(type, attrs)))
                    action.accept(type, attrs);
            }
        }
        forEachSubType((name, t) -> {
            var type = Type.from(t);
            if (visited.add(List.of(type, Map.of())))
                action.accept(type, Map.of());
        });
    }

    void forEachSubType(BiConsumer<String, DeclaredType> action) {
        annotations.forEachSubType(annotation, action);
    }

    public List<TypeName> getTypeNames() {
        return Collections.unmodifiableList(typeNames);
    }

    public boolean isParameterized() {
        return !element.getTypeParameters().isEmpty();
    }

}
