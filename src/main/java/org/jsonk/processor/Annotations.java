package org.jsonk.processor;

import org.jsonk.util.Util;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

class Annotations {

    private final MyNames myNames;
    private final TypesExt typesExt;

    Annotations(MyNames myNames, TypesExt typesExt) {
        this.myNames = myNames;
        this.typesExt = typesExt;
    }

    public boolean isAnnotationPresent(Element element, Name name) {
        return getAnnotation(element, name) != null;
    }

    public PropertyConfig getPropertyConfig(Element element) {
        var annotation = getAnnotation(element, myNames.classJsonProperty);
        if (annotation == null)
            return PropertyConfig.DEFAULT;
        return new PropertyConfig(
                emptyToNull((String) getAttribute(annotation, myNames.value)),
                (boolean) getAttribute(annotation, myNames.includeNull, false),
                emptyToNull((String) getAttribute(annotation, myNames.dateTimeFormat))
        );
    }

    private String emptyToNull(String s) {
        return "".equals(s) ? null : s;
    }

    public boolean isIgnorePresent(Element element) {
        return getAnnotation(element, myNames.classJsonIgnore) != null;
    }

    public boolean hasCustomAdapter(TypeElement element) {
        var annotation = getAnnotation(element, myNames.classJson);
        var adapter = getAdapter(annotation);
        return adapter != null && !typesExt.getClassName(adapter).equals(myNames.classAdapter);
    }

    public boolean hasCustomAdapterFactory(TypeElement element) {
        var annotation = getAnnotation(element, myNames.classJson);
        var adapter = getAdapterFactory(annotation);
        return adapter != null && !typesExt.getClassName(adapter).equals(myNames.classAdapterFactory);
    }

    public AnnotationMirror getAnnotation(Element element, Name name) {
        return Util.find(element.getAnnotationMirrors(),
                a -> typesExt.isClassType(a.getAnnotationType(), name));
    }

    public String getTypeProperty(AnnotationMirror annotation) {
        return (String) getAttribute(annotation, myNames.typeProperty);
    }

    public Object getAttribute(AnnotationMirror annotation, Name attribute) {
        return getAttribute(annotation, attribute, (Object) null);
    }

    public Object getAttribute(AnnotationMirror annotation, Name attribute, Object defaultValue) {
        return getAttribute(annotation, attribute, () -> defaultValue);
    }

    public Object getAttribute(AnnotationMirror annotation, Name attribute, Supplier<Object> getDefault) {
        var elements = annotation.getElementValues();
        for (var e : elements.entrySet()) {
            if (e.getKey().getSimpleName().equals(attribute)) {
                return e.getValue().getValue();
            }
        }
        return getDefault.get();
    }

    public DeclaredType getAdapter(AnnotationMirror annotation) {
        var elements = annotation.getElementValues();
        for (var e : elements.entrySet()) {
            if (e.getKey().getSimpleName().equals(myNames.adapter)) {
                return (DeclaredType) Objects.requireNonNull(e.getValue().getValue());
            }
        }
        return null;
    }

    public DeclaredType getAdapterFactory(AnnotationMirror annotation) {
        var elements = annotation.getElementValues();
        for (var e : elements.entrySet()) {
            if (e.getKey().getSimpleName().equals(myNames.classAdapter)) {
                return (DeclaredType) Objects.requireNonNull(e.getValue().getValue());
            }
        }
        return null;
    }

    public void forEachSubType(AnnotationMirror annotation, BiConsumer<String, DeclaredType> action) {
        var elements = annotation.getElementValues();
        for (var e : elements.entrySet()) {
            if (e.getKey().getSimpleName().equals(myNames.subTypes)) {
                //noinspection unchecked
                for (AnnotationValue a : (List<AnnotationValue>) e.getValue().getValue()) {
                    String value = null;
                    DeclaredType type = null;
                    var m = (AnnotationMirror) a.getValue();
                    for (var entry : m.getElementValues().entrySet()) {
                        var key = entry.getKey().getSimpleName();
                        if (key.equals(myNames.value))
                            value = (String) entry.getValue().getValue();
                        else if (key.equals(myNames.type))
                            type = (DeclaredType) entry.getValue().getValue();
                    }
                    action.accept(value, type);
                }
            }
        }
    }

}
