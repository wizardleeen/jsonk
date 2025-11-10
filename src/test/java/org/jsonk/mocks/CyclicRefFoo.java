package org.jsonk.mocks;

import org.jsonk.Json;

@Json
public class CyclicRefFoo {

    private Object value;

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
