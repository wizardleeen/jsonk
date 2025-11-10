package org.jsonk.element;

import javax.lang.model.element.*;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class MockVarElement extends MockElement implements VariableElement {

    private final ElementKind kind;
    private final Element owner;
    private final Set<Modifier> modifiers;
    private final Name name;
    private final MockType type;

    public MockVarElement(ElementKind kind, Element owner, Set<Modifier> modifiers, Name name, MockType type) {
        this.kind = kind;
        this.owner = owner;
        this.modifiers = modifiers;
        this.name = name;
        this.type = type;
    }

    @Override
    public MockType asType() {
        return type;
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.FIELD;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return Collections.unmodifiableSet(modifiers);
    }

    @Override
    public Object getConstantValue() {
        return null;
    }

    @Override
    public Name getSimpleName() {
        return name;
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
        return v.visitVariable(this, p);
    }

    @Override
    public void write(ElementWriter writer) {
        writer.writeModifiers(modifiers);
        writer.write(type).write(" ").write(name);
        if (kind == ElementKind.FIELD)
            writer.writeln(";");
    }
}
