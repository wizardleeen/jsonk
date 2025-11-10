package org.jsonk.mocks;

import java.util.Map;
import java.util.List;
import org.jsonk.JsonWriter;
import org.jsonk.JsonReader;
import org.jsonk.Adapter;
import org.jsonk.Type;
import org.jsonk.AdapterKey;
import org.jsonk.AdapterRegistry;
import org.jsonk.util.MinimalPerfectHash;

public class ArrayFooAdapter implements Adapter<org.jsonk.mocks.ArrayFoo> {

    private Adapter<java.lang.String[]> adapter0;

    @Override
    public void init(AdapterRegistry registry) {
        adapter0 = registry.getAdapter(java.lang.String[].class);
    }

    @Override
    public void toJson(org.jsonk.mocks.ArrayFoo o, JsonWriter writer) {
        writer.writeLBrace();
        var v0 = o.tags();
        if (v0 != null) {
            writer.writeName("tags");
            writer.writeValue(v0, adapter0);
        }
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.ArrayFoo fromJson(JsonReader reader) {
        java.lang.String[] v1 = null;
        reader.accept('{');
        do {
             reader.skipWhitespace();
            if (reader.is('}'))
                break;
            var name = reader.readString();
            reader.skipWhitespace();
            reader.accept(':');
            reader.skipWhitespace();
            switch(name) {
                case "tags" -> v1 = reader.readObject(adapter0);
                default -> reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept('}');
        var o = new ArrayFoo(v1);
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.ArrayFoo.class);
    }

}
