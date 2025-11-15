package org.jsonk.adapters;

import org.jsonk.*;

public class StringAdapter implements Adapter<String> {
    @Override
    public void init(AdapterEnv env) {

    }

    @Override
    public void toJson(String o, JsonWriter writer) {
        writer.writeString(o);
    }

    @Override
    public String fromJson(JsonReader reader) {
        return reader.readString();
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(String.class);
    }
}
