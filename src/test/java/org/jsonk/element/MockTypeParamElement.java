package org.jsonk.element;

import lombok.Setter;

import javax.lang.model.element.*;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class MockTypeParamElement extends MockElement implements TypeParameterElement {

    private final Element owner;
    private final Name name;
    @Setter
    private MockTypeVariable type;
    private final List<MockType> bounds = new ArrayList<>();

    public MockTypeParamElement(Element owner, Name name) {
        this.owner = owner;
        this.name = name;
    }

    @Override
    public MockType asType() {
        return type;
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.TYPE_PARAMETER;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return Set.of();
    }

    @Override
    public Name getSimpleName() {
        return name;
    }

    @Override
    public Element getGenericElement() {
        return owner;
    }

    @Override
    public List<? extends MockType> getBounds() {
        return Collections.unmodifiableList(bounds);
    }

    public void setBounds(List<MockType> bounds) {
        this.bounds.clear();
        this.bounds.addAll(bounds);
    }

    @Override
    public Element getEnclosingElement() {
        return owner;
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        return List.of();
    }

    @Override
    public void forEachChild(Consumer<MockElement> action) {

    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return null;
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        return null;
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return v.visitTypeParameter(this, p);
    }

    @Override
    public void write(ElementWriter writer) {
        writer.write(name);
        if (!bounds.isEmpty()) {
            writer.write(" extends ").writeList(bounds, writer::write, ",");
        }
    }
}
