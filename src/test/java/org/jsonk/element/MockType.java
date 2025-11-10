package org.jsonk.element;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;

public abstract class MockType implements TypeMirror {

    protected abstract void write(ElementWriter writer);

    @Override
    public String toString() {
        var w = new ElementWriter();
        write(w);
        return w.toString();
    }

    public abstract boolean isAssignableFrom(MockType that);

    public MockType getUpperBound() {
        return this;
    }

    public boolean contains(MockType that) {
        return this == that;
    }

    public <R> R accept(TypeVisitor<R, Void> v) {
        return accept(v, null);
    }
}
