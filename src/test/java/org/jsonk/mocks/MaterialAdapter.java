package org.jsonk.mocks;

import java.util.Map;
import java.util.List;
import org.jsonk.JsonWriter;
import org.jsonk.JsonReader;
import org.jsonk.Adapter;
import org.jsonk.Type;
import org.jsonk.AdapterKey;
import org.jsonk.AdapterEnv;

public class MaterialAdapter implements Adapter<org.jsonk.mocks.Material> {

    private static final char[][] keys = new char[][] {
        new char[] {'n', 'a', 'm', 'e'},
        null,
        null,
        new char[] {'a', 'm', 'o', 'u', 'n', 't'}};
    private static final int[] ordinals = new int[] {0, -1, -1, 1};
    private static final long seed = 4996116371030011712L;
    private static final char[] chars0 = new char[] {'"', 'n', 'a', 'm', 'e', '"'};
    private static final char[] chars1 = new char[] {'"', 'a', 'm', 'o', 'u', 'n', 't', '"'};

    @Override
    public void init(AdapterEnv env) {
    }

    @Override
    public void toJson(org.jsonk.mocks.Material o, JsonWriter writer) {
        writer.writeLBrace();
        var first = true;
        var v0 = o.getName();
        if (v0 != null) {
            first = false;
            writer.write(chars0);
            writer.writeColon();
            writer.writeString(v0);
        }
        if (!first) {
            writer.writeComma();
        }
        writer.write(chars1);
        writer.writeColon();
        writer.writeDouble(o.getAmount());
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.Material fromJson(JsonReader reader) {
        java.lang.String v1 = null;
        double v2 = 0;
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
                case 1 -> v2 = reader.readDouble();
                default -> reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept('}');
        var o = new Material(v1);
        o.setAmount(v2);
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.Material.class);
    }

}
