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

public class CyclicRefFooAdapter implements Adapter<org.jsonk.mocks.CyclicRefFoo> {

    private Adapter<java.lang.Object> adapter0;

    @Override
    public void init(AdapterRegistry registry) {
        adapter0 = registry.getAdapter(java.lang.Object.class);
    }

    @Override
    public void toJson(org.jsonk.mocks.CyclicRefFoo o, JsonWriter writer) {
        writer.writeLBrace();
        var v0 = o.getValue();
        if (v0 != null) {
            writer.writeName("value");
            writer.writeObject(v0, adapter0);
        }
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.CyclicRefFoo fromJson(JsonReader reader) {
        var o = new CyclicRefFoo();
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
                case "value" -> o.setValue(reader.readObject(adapter0));
                default -> reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept('}');
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.CyclicRefFoo.class);
    }

}
