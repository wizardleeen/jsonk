package org.jsonk.adapters;

import org.jsonk.*;
import org.jsonk.Type;

public class ShortAdapter implements Adapter<Short> {
    @Override
    public void init(AdapterRegistry registry) {
        
    }

    @Override
    public void toJson(Short o, JsonWriter writer) {
        writer.writeShort(o);
    }

    @Override
    public Short fromJson(JsonReader reader) {
        return reader.readShort();
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(Type.from(Short.class));
    }
}
