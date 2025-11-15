package org.jsonk.adapters;

import org.jsonk.*;
import org.jsonk.Type;

public class LongAdapter implements Adapter<Long> {
    @Override
    public void init(AdapterEnv env) {
        
    }

    @Override
    public void toJson(Long o, JsonWriter writer) {
        writer.writeLong(o);
    }

    @Override
    public Long fromJson(JsonReader reader) {
        return reader.readLong();
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(Type.from(Long.class));
    }
}
