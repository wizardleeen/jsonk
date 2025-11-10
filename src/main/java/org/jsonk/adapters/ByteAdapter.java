package org.jsonk.adapters;

import org.jsonk.*;
import org.jsonk.Type;

public class ByteAdapter implements Adapter<Byte> {
    @Override
    public void init(AdapterRegistry registry) {
        
    }

    @Override
    public void toJson(Byte o, JsonWriter writer) {
        writer.writeByte(o);
    }

    @Override
    public Byte fromJson(JsonReader reader) {
        return reader.readByte();
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(Type.from(Byte.class));
    }
}
