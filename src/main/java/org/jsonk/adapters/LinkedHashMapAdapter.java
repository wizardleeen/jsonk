package org.jsonk.adapters;

import org.jsonk.JsonReader;
import org.jsonk.Type;

import java.util.Map;
import java.util.LinkedHashMap;

public class LinkedHashMapAdapter<E> extends MapAdapter<String, E, LinkedHashMap<String, E>> {

    public LinkedHashMapAdapter(Type type, Map<String, Object> valueAttributes) {
        super(type, valueAttributes);
    }

    @Override
    protected String readKey(JsonReader reader) {
        return reader.readString();
    }

    @Override
    protected LinkedHashMap<String, E> createMap() {
        return new LinkedHashMap<>();
    }
}
