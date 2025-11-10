package org.jsonk.adapters;

import org.jsonk.Type;

import java.util.HashSet;
import java.util.Map;

public class HashSetAdapter<E> extends CollectionAdapter<E, HashSet<E>> {

    public HashSetAdapter(Type type, Map<String, Object> valueAttributes) {
        super(type, valueAttributes);
    }

    @Override
    protected HashSet<E> createCollection() {
        return new HashSet<>();
    }
}
