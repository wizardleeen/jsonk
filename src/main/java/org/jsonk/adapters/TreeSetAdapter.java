package org.jsonk.adapters;

import org.jsonk.Type;

import java.util.Map;
import java.util.TreeSet;

public class TreeSetAdapter<E> extends CollectionAdapter<E, TreeSet<E>> {

    public TreeSetAdapter(Type type, Map<String, Object> valueAttributes) {
        super(type, valueAttributes);
    }

    @Override
    protected TreeSet<E> createCollection() {
        //noinspection SortedCollectionWithNonComparableKeys
        return new TreeSet<>();
    }
}
