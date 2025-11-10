package org.jsonk.adapters;

import org.jsonk.JsonReader;
import org.jsonk.Type;

import java.util.TreeMap;
import java.util.Map;

public class TreeMapAdapter<E> extends MapAdapter<String, E, TreeMap<String, E>> {

    public TreeMapAdapter(Type type, Map<String, Object> valueAttributes) {
        super(type, valueAttributes);
    }

    @Override
    protected String readKey(JsonReader reader) {
        return reader.readString();
    }

    @Override
    protected TreeMap<String, E> createMap() {
        return new TreeMap<>();
    }
}
