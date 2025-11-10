package org.jsonk.adapters;

import org.jsonk.Type;

import java.util.LinkedList;
import java.util.Map;

public class LinkedListAdapter<E> extends CollectionAdapter<E, LinkedList<E>> {

    public LinkedListAdapter(Type type, Map<String, Object> valueAttributes) {
        super(type, valueAttributes);
    }

    @Override
    protected LinkedList<E> createCollection() {
        return new LinkedList<>();
    }

}
