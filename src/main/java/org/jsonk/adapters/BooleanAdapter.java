package org.jsonk.adapters;

import org.jsonk.*;
import org.jsonk.Type;

public class BooleanAdapter implements Adapter<Boolean> {
    @Override
    public void init(AdapterEnv env) {
        
    }

    @Override
    public void toJson(Boolean o, JsonWriter writer) {
        writer.writeBoolean(o);
    }

    @Override
    public Boolean fromJson(JsonReader reader) {
        return reader.readBoolean();
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(Type.from(Boolean.class));
    }
}
