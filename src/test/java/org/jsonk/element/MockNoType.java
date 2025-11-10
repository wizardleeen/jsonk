package org.jsonk.element;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVisitor;
import java.lang.annotation.Annotation;
import java.util.List;

public class MockNoType extends MockType implements NoType {
    @Override
    public TypeKind getKind() {
        return TypeKind.NONE;
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
        return v.visitNoType(this, p);
    }

    @Override
    public void write(ElementWriter writer) {
        writer.write("none");
    }

    @Override
    public boolean isAssignableFrom(MockType that) {
        return this == that.getUpperBound();
    }
}
