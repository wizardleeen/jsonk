package org.jsonk.adapters;

import org.jsonk.JsonReader;
import org.jsonk.Type;

import java.util.HashMap;
import java.util.Map;

public class HashMapAdapter<E> extends MapAdapter<String, E, HashMap<String, E>> {

    public HashMapAdapter(Type type, Map<String, Object> valueAttributes) {
        super(type, valueAttributes);
    }

    @Override
    protected String readKey(JsonReader reader) {
        return reader.readString();
    }

    @Override
    protected HashMap<String, E> createMap() {
        return new HashMap<>();
    }
}
