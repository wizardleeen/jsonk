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

public class ArrayTypeAdapter implements Adapter<org.jsonk.mocks.ArrayType> {

    private Adapter<org.jsonk.mocks.Type> adapter0;

    @Override
    public void init(AdapterRegistry registry) {
        adapter0 = registry.getAdapter(org.jsonk.mocks.Type.class);
    }

    @Override
    public void toJson(org.jsonk.mocks.ArrayType o, JsonWriter writer) {
        writer.writeLBrace();
        writer.writeName("type");
        writer.writeString("array");
        var v0 = o.componentType();
        if (v0 != null) {
            writer.writeComma();
            writer.writeName("componentType");
            writer.writeObject(v0, adapter0);
        }
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.ArrayType fromJson(JsonReader reader) {
        org.jsonk.mocks.Type v1 = null;
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
                case "componentType" -> v1 = reader.readObject(adapter0);
                default -> reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept('}');
        var o = new ArrayType(v1);
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.ArrayType.class);
    }

}
