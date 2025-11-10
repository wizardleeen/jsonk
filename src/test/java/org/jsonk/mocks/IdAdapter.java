package org.jsonk.mocks;

import org.jsonk.Adapter;
import org.jsonk.AdapterRegistry;
import org.jsonk.JsonReader;
import org.jsonk.JsonWriter;
import org.jsonk.AdapterKey;

public class IdAdapter implements Adapter<Id> {
    @Override
    public void init(AdapterRegistry registry) {

    }

    @Override
    public void toJson(Id o, JsonWriter writer) {
        writer.writeString(o.id());
    }

    @Override
    public Id fromJson(JsonReader reader) {
        return new Id(reader.readString());
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(Id.class);
    }
}
