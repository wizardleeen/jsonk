package org.jsonk.adapters;

import org.jsonk.*;
import org.jsonk.Type;

import java.time.Instant;

public class InstantAdapter implements Adapter<Instant>  {


    public InstantAdapter() {
    }

    @Override
    public void init(AdapterEnv env) {
    }

    @Override
    public void toJson(Instant o, JsonWriter writer) {
        writer.writeLong(o.getEpochSecond());
        writer.write('.');
        writer.writeInt(o.getNano());
    }

    @Override
    public Instant fromJson(JsonReader reader) {
        var seconds = reader.readLong();
        reader.accept('.');
        var nanos = reader.readInt();
        return Instant.ofEpochSecond(seconds, nanos);
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(Type.from(Instant.class));
    }
}
