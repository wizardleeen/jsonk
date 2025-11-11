package org.jsonk.mocks;

import java.util.Map;
import java.util.List;
import org.jsonk.JsonWriter;
import org.jsonk.JsonReader;
import org.jsonk.Adapter;
import org.jsonk.Type;
import org.jsonk.AdapterKey;
import org.jsonk.AdapterRegistry;

public class ArrayTypeAdapter implements Adapter<org.jsonk.mocks.ArrayType> {

    private Adapter<org.jsonk.mocks.Type> adapter0;
    private static final char[][] keys = new char[][] {
        null,
        new char[] {'c', 'o', 'm', 'p', 'o', 'n', 'e', 'n', 't', 'T', 'y', 'p', 'e'}};
    private static final int[] ordinals = new int[] {-1, 0};
    private static final long seed = -1760909142441537709L;
    private static final char[] chars0 = new char[] {'"', 't', 'y', 'p', 'e', '"'};
    private static final char[] chars1 = new char[] {'"', 'a', 'r', 'r', 'a', 'y', '"'};
    private static final char[] chars2 = new char[] {'"', 'c', 'o', 'm', 'p', 'o', 'n', 'e', 'n', 't', 'T', 'y', 'p', 'e', '"'};

    @Override
    public void init(AdapterRegistry registry) {
        adapter0 = registry.getAdapter(org.jsonk.mocks.Type.class);
    }

    @Override
    public void toJson(org.jsonk.mocks.ArrayType o, JsonWriter writer) {
        writer.writeLBrace();
        writer.write(chars0);
        writer.writeColon();
        writer.write(chars1);
        var v0 = o.componentType();
        if (v0 != null) {
            writer.writeComma();
            writer.write(chars2);
            writer.writeColon();
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
            var name = reader.readName(keys, ordinals, seed);
            reader.skipWhitespace();
            reader.accept(':');
            reader.skipWhitespace();
            switch(name) {
                case 0 -> v1 = reader.readObject(adapter0);
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
