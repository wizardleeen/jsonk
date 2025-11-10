package org.jsonk.element;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVisitor;
import java.lang.annotation.Annotation;
import java.util.List;

public class MockPrimType extends MockType implements PrimitiveType {

    private final TypeKind kind;

    public MockPrimType(TypeKind kind) {
        this.kind = kind;
    }

    @Override
    public TypeKind getKind() {
        return kind;
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return List.of();
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
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        return v.visitPrimitive(this, p);
    }

    @Override
    public void write(ElementWriter writer) {
        writer.write(kind.name().toLowerCase());
    }

    @Override
    public boolean isAssignableFrom(MockType that) {
        return this == that;
    }
}
