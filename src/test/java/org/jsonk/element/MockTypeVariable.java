package org.jsonk.element;

import lombok.Setter;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import java.lang.annotation.Annotation;
import java.util.List;

public class MockTypeVariable extends MockType implements TypeVariable {

    private final TypeParameterElement element;
    @Setter
    private MockType lowerBound;
    @Setter
    private MockType upperBound;

    public MockTypeVariable(TypeParameterElement element) {
        this.element = element;
    }

    @Override
    public Element asElement() {
        return element;
    }

    @Override
    public MockType getUpperBound() {
        return upperBound;
    }

    @Override
    public MockType getLowerBound() {
        return lowerBound;
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.TYPEVAR;
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
        return v.visitTypeVariable(this, p);
    }

    @Override
    public void write(ElementWriter writer) {
        writer.write(element.getSimpleName());
    }

    @Override
    public boolean isAssignableFrom(MockType that) {
        var t = that;
        for (;;) {
            if (t == this)
                return true;
            var ub = t.getUpperBound();
            if (ub == t)
                return false;
            t = ub;
        }
    }

}
