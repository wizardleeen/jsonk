package org.jsonk.mocks;

import java.util.Map;
import java.util.List;
import org.jsonk.JsonWriter;
import org.jsonk.JsonReader;
import org.jsonk.Adapter;
import org.jsonk.Type;
import org.jsonk.AdapterKey;
import org.jsonk.AdapterEnv;

public class BOMAdapter implements Adapter<org.jsonk.mocks.BOM> {

    private Adapter<org.jsonk.mocks.Material> adapter0;
    private static final char[][] keys = new char[][] {
        null,
        null,
        new char[] {'v', 'e', 'r', 's', 'i', 'o', 'n'},
        new char[] {'p', 'r', 'o', 'd', 'u', 'c', 't'}};
    private static final int[] ordinals = new int[] {-1, -1, 1, 0};
    private static final long seed = 4992628780046536239L;
    private static final char[] chars0 = new char[] {'"', 'p', 'r', 'o', 'd', 'u', 'c', 't', '"'};
    private static final char[] chars1 = new char[] {'"', 'v', 'e', 'r', 's', 'i', 'o', 'n', '"'};

    @Override
    public void init(AdapterEnv env) {
        adapter0 = env.getAdapter(org.jsonk.mocks.Material.class);
    }

    @Override
    public void toJson(org.jsonk.mocks.BOM o, JsonWriter writer) {
        writer.writeLBrace();
        var first = true;
        var v0 = o.getProduct();
        if (v0 != null) {
            first = false;
            writer.write(chars0);
            writer.writeColon();
            writer.writeObject(v0, adapter0);
        }
        if (!first) {
            writer.writeComma();
        }
        writer.write(chars1);
        writer.writeColon();
        writer.writeLong(o.getVersion());
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.BOM fromJson(JsonReader reader) {
        org.jsonk.mocks.Material v1 = null;
        long v2 = 0;
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
                case 1 -> v2 = reader.readLong();
                default -> reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept('}');
        var o = new BOM(v1);
        o.setVersion(v2);
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.BOM.class);
    }

}
