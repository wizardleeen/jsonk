package org.jsonk.adapters;

import org.jsonk.*;
import org.jsonk.Type;

import java.util.Date;

public class DateAdapter implements Adapter<Date> {
    @Override
    public void init(AdapterEnv env) {

    }

    @Override
    public void toJson(Date o, JsonWriter writer) {
        writer.writeLong(o.getTime());
    }

    @Override
    public Date fromJson(JsonReader reader) {
        return new Date(reader.readLong());
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(Type.from(Date.class));
    }
}
