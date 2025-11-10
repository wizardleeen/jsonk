package org.jsonk.element;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import java.lang.annotation.Annotation;
import java.util.List;

public class MockArrayType extends MockType implements ArrayType {

    private final MockType componentType;

    public MockArrayType(MockType componentType) {
        this.componentType = componentType;
    }

    @Override
    public TypeMirror getComponentType() {
        return componentType;
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.ARRAY;
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
        return v.visitArray(this, p);
    }

    @Override
    public void write(ElementWriter writer) {
        writer.write(componentType).write("[]");
    }

    @Override
    public boolean isAssignableFrom(MockType that) {
        return this == that.getUpperBound() || that.getUpperBound() instanceof MockArrayType thatAt && componentType.isAssignableFrom(thatAt.componentType);
    }
}
