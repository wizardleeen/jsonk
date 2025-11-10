package org.jsonk.element;

import javax.lang.model.element.*;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class MockRecordComponentElement extends MockElement implements RecordComponentElement {

    private final Element owner;
    private final Name name;
    private final MockType type;
    private final ExecutableElement accessor;

    public MockRecordComponentElement(Element owner, Name name, MockType type, ExecutableElement accessor) {
        this.owner = owner;
        this.name = name;
        this.type = type;
        this.accessor = accessor;
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
        return v.visitRecordComponent(this, p);
    }

    @Override
    public void write(ElementWriter writer) {
        writer.write(type).write(" ").write(name);
    }

    @Override
    public MockType asType() {
        return type;
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.RECORD_COMPONENT;
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
    public ExecutableElement getAccessor() {
        return accessor;
    }
}
