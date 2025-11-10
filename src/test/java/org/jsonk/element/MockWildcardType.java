package org.jsonk.element;

import jakarta.annotation.Nullable;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.WildcardType;
import java.lang.annotation.Annotation;
import java.util.List;

public class MockWildcardType extends MockType implements WildcardType {

    private final @Nullable MockType lowerBound;
    private final @Nullable MockType upperBound;

    public MockWildcardType(@Nullable MockType lowerBound, @Nullable MockType upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public TypeMirror getExtendsBound() {
        return upperBound;
    }

    @Override
    public TypeMirror getSuperBound() {
        return lowerBound;
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.WILDCARD;
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
        return v.visitWildcard(this, p);
    }

    @Override
    public void write(ElementWriter writer) {
        writer.write("?");
        if (lowerBound != null)
            writer.write(" super ").write(lowerBound);
        if (upperBound != null)
            writer.write(" extends ").write(upperBound);
    }

    @Override
    public boolean isAssignableFrom(MockType that) {
        return lowerBound != null && lowerBound.isAssignableFrom(that.getUpperBound());
    }

    @Override
    public boolean contains(MockType that) {
        return (lowerBound == null || that.isAssignableFrom(lowerBound)) && (upperBound == null || upperBound.isAssignableFrom(that));
    }
}
