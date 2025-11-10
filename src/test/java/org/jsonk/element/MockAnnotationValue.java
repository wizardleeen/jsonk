package org.jsonk.element;

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;

public class MockAnnotationValue implements AnnotationValue {

    private final Object value;

    public MockAnnotationValue(Object value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public <R, P> R accept(AnnotationValueVisitor<R, P> v, P p) {
        throw new UnsupportedOperationException();
    }
}
