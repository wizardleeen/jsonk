package org.jsonk.mocks;

import java.util.Map;
import java.util.List;
import org.jsonk.JsonWriter;
import org.jsonk.JsonReader;
import org.jsonk.Adapter;
import org.jsonk.Type;
import org.jsonk.AdapterKey;
import org.jsonk.AdapterRegistry;

public class ProductAdapter implements Adapter<org.jsonk.mocks.Product> {

    private Adapter<java.lang.String> adapter0;
    private static final char[][] keys = new char[][] {
        new char[] {'n', 'a', 'm', 'e'},
        null,
        new char[] {'p', 'r', 'i', 'c', 'e'},
        new char[] {'s', 't', 'o', 'c', 'k'},
        null,
        null,
        null,
        null};
    private static final int[] ordinals = new int[] {0, -1, 1, 2, -1, -1, -1, -1};
    private static final long seed = -6343609588066691367L;
    private static final char[] chars0 = new char[] {'"', 'n', 'a', 'm', 'e', '"'};
    private static final char[] chars1 = new char[] {'"', 'p', 'r', 'i', 'c', 'e', '"'};
    private static final char[] chars2 = new char[] {'"', 's', 't', 'o', 'c', 'k', '"'};

    @Override
    public void init(AdapterRegistry registry) {
        adapter0 = registry.getAdapter(java.lang.String.class);
    }

    @Override
    public void toJson(org.jsonk.mocks.Product o, JsonWriter writer) {
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
        writer.writeDouble(o.getPrice());
        writer.writeComma();
        writer.write(chars2);
        writer.writeColon();
        writer.writeInt(o.getStock());
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.Product fromJson(JsonReader reader) {
        java.lang.String v1 = null;
        double v2 = 0;
        int v3 = 0;
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
                case 2 -> v3 = reader.readInt();
                default -> reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept('}');
        var o = new Product(v1, v2);
        o.setStock(v3);
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.Product.class);
    }

}
