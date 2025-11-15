package org.jsonk.adapters;

import org.jsonk.*;
import org.jsonk.Type;

public class DoubleAdapter implements Adapter<Double> {
    @Override
    public void init(AdapterEnv env) {
        
    }

    @Override
    public void toJson(Double o, JsonWriter writer) {
        writer.writeDouble(o);
    }

    @Override
    public Double fromJson(JsonReader reader) {
        return reader.readDouble();
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(Type.from(Double.class));
    }
}
