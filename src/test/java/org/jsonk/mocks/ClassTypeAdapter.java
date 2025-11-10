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

public class ClassTypeAdapter implements Adapter<org.jsonk.mocks.ClassType> {

    private Adapter<java.lang.String> adapter0;

    @Override
    public void init(AdapterRegistry registry) {
        adapter0 = registry.getAdapter(java.lang.String.class);
    }

    @Override
    public void toJson(org.jsonk.mocks.ClassType o, JsonWriter writer) {
        writer.writeLBrace();
        writer.writeName("type");
        writer.writeString("class");
        var v0 = o.name();
        if (v0 != null) {
            writer.writeComma();
            writer.writeName("name");
            writer.writeString(v0);
        }
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.ClassType fromJson(JsonReader reader) {
        java.lang.String v1 = null;
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
                case "name" -> v1 = reader.readStringOrNull();
                default -> reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept('}');
        var o = new ClassType(v1);
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.ClassType.class);
    }

}
