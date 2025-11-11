package org.jsonk.mocks;

import java.util.Map;
import java.util.List;
import org.jsonk.JsonWriter;
import org.jsonk.JsonReader;
import org.jsonk.Adapter;
import org.jsonk.Type;
import org.jsonk.AdapterKey;
import org.jsonk.AdapterRegistry;

public class PrimitiveTypeAdapter implements Adapter<org.jsonk.mocks.PrimitiveType> {

    private Adapter<java.lang.String> adapter0;
    private static final char[][] keys = new char[][] {
        null,
        new char[] {'k', 'i', 'n', 'd'}};
    private static final int[] ordinals = new int[] {-1, 0};
    private static final long seed = 4323861132194585665L;
    private static final char[] chars0 = new char[] {'"', 't', 'y', 'p', 'e', '"'};
    private static final char[] chars1 = new char[] {'"', 'p', 'r', 'i', 'm', 'i', 't', 'i', 'v', 'e', '"'};
    private static final char[] chars2 = new char[] {'"', 'k', 'i', 'n', 'd', '"'};

    @Override
    public void init(AdapterRegistry registry) {
        adapter0 = registry.getAdapter(java.lang.String.class);
    }

    @Override
    public void toJson(org.jsonk.mocks.PrimitiveType o, JsonWriter writer) {
        writer.writeLBrace();
        writer.write(chars0);
        writer.writeColon();
        writer.write(chars1);
        var v0 = o.kind();
        if (v0 != null) {
            writer.writeComma();
            writer.write(chars2);
            writer.writeColon();
            writer.writeString(v0);
        }
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.PrimitiveType fromJson(JsonReader reader) {
        java.lang.String v1 = null;
        reader.accept('{');
        do {
             reader.skipWhitespace();
            if (reader.is('}'))
                break;
            var name = reader.readName(keys, ordinals, seed);
            reader.skipWhitespace();
            reader.accept(':');
            reader.skipWhitespace();
            switch(name) {
                case 0 -> v1 = reader.readStringOrNull();
                default -> reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept('}');
        var o = new PrimitiveType(v1);
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.PrimitiveType.class);
    }

}
