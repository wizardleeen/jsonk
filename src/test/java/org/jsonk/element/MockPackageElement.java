package org.jsonk.element;

import javax.lang.model.element.*;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class MockPackageElement extends MockElement implements PackageElement {

    private final List<MockClass> classes = new ArrayList<>();

    private final Name simpName;
    private final Name qualName;

    public MockPackageElement(String name) {
        qualName = MockName.of(name);
        var lastDotIdx = name.lastIndexOf('.');
        simpName = lastDotIdx == -1 ? qualName : MockName.of(name.substring(lastDotIdx + 1));
    }

    @Override
    public void write(ElementWriter writer) {
        writer.writeln("package ").write(qualName).write(";");
    }

    @Override
    public MockType asType() {
        return null;
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.PACKAGE;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return Set.of();
    }

    @Override
    public Name getQualifiedName() {
        return qualName;
    }

    @Override
    public Name getSimpleName() {
        return simpName;
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        return Collections.unmodifiableList(classes);
    }

    @Override
    public void forEachChild(Consumer<MockElement> action) {
        classes.forEach(action);
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
        return v.visitPackage(this, p);
    }

    @Override
    public boolean isUnnamed() {
        return false;
    }

    @Override
    public Element getEnclosingElement() {
        return null;
    }

    public void addClass(MockClass clazz) {
        classes.add(clazz);
    }
}
