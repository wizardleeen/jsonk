package org.jsonk.adapters;

import org.jsonk.Type;

import java.util.ArrayList;
import java.util.Map;

public class ArrayListAdapter<E> extends CollectionAdapter<E, ArrayList<E>> {

    public ArrayListAdapter(Type type, Map<String, Object> valueAttributes) {
        super(type, valueAttributes);
    }

    @Override
    protected ArrayList<E> createCollection() {
        return new ArrayList<>();
    }
}
