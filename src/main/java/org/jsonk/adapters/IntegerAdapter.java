package org.jsonk.adapters;

import org.jsonk.*;
import org.jsonk.Type;

public class IntegerAdapter implements Adapter<Integer> {
    @Override
    public void init(AdapterEnv env) {
        
    }

    @Override
    public void toJson(Integer o, JsonWriter writer) {
        writer.writeInt(o);
    }

    @Override
    public Integer fromJson(JsonReader reader) {
        return reader.readInt();
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(Type.from(Integer.class));
    }
}
