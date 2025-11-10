package org.jsonk.adapters;

import org.jsonk.Adapter;
import org.jsonk.AdapterFactory;
import org.jsonk.Type;

import java.util.Map;

public class ArrayAdapterFactory implements AdapterFactory {

    @Override
    public Adapter<?> create(Type type, Map<String, Object> attributes) {
        if (isSupported(type, attributes))
            return new ArrayAdapter<>(type);
        else
            throw new IllegalArgumentException("Unsupported type: " + type);
    }

    @Override
    public boolean isSupported(Type type, Map<String, Object> attributes) {
        return type.clazz().isArray() && !type.clazz().getComponentType().isPrimitive();
    }
}
