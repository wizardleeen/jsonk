package org.jsonk.adapters;

import org.jsonk.*;
import org.jsonk.Type;

public class FloatAdapter implements Adapter<Float> {
    @Override
    public void init(AdapterRegistry registry) {
        
    }

    @Override
    public void toJson(Float o, JsonWriter writer) {
        writer.writeFloat(o);
    }

    @Override
    public Float fromJson(JsonReader reader) {
        return reader.readFloat();
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(Type.from(Float.class));
    }
}
