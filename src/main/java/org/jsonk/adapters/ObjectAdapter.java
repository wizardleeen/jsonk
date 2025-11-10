package org.jsonk.adapters;

import org.jsonk.*;

public class ObjectAdapter implements Adapter<Object> {
    @Override
    public void init(AdapterRegistry registry) {

    }

    @Override
    public void toJson(Object o, JsonWriter writer) {
        writer.writeValue(o);
    }

    @Override
    public Object fromJson(JsonReader reader) {
        return reader.readValue();
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(Object.class);
    }
}
