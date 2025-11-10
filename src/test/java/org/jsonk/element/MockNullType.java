package org.jsonk.element;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.NullType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVisitor;
import java.lang.annotation.Annotation;
import java.util.List;

public class MockNullType extends MockType implements NullType {
    @Override
    public TypeKind getKind() {
        return TypeKind.NULL;
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return null;
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
        return v.visitNull(this, p);
    }

    @Override
    protected void write(ElementWriter writer) {
        writer.write("null");
    }

    @Override
    public boolean isAssignableFrom(MockType that) {
        return this == that.getUpperBound();
    }
}
