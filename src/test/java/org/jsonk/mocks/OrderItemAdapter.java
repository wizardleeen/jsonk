package org.jsonk.mocks;

import java.util.Map;
import java.util.List;
import org.jsonk.JsonWriter;
import org.jsonk.JsonReader;
import org.jsonk.Adapter;
import org.jsonk.Type;
import org.jsonk.AdapterKey;
import org.jsonk.AdapterEnv;

public class OrderItemAdapter implements Adapter<org.jsonk.mocks.OrderItem> {

    private static final char[][] keys = new char[][] {
        null,
        null,
        null,
        null,
        new char[] {'p', 'r', 'o', 'd', 'u', 'c', 't', 'I', 'd'},
        null,
        new char[] {'q', 'u', 'a', 'n', 't', 'i', 't', 'y'},
        new char[] {'p', 'r', 'i', 'c', 'e'}};
    private static final int[] ordinals = new int[] {-1, -1, -1, -1, 0, -1, 1, 2};
    private static final long seed = -2438569752504735364L;
    private static final char[] chars0 = new char[] {'"', 'p', 'r', 'o', 'd', 'u', 'c', 't', 'I', 'd', '"'};
    private static final char[] chars1 = new char[] {'"', 'q', 'u', 'a', 'n', 't', 'i', 't', 'y', '"'};
    private static final char[] chars2 = new char[] {'"', 'p', 'r', 'i', 'c', 'e', '"'};

    @Override
    public void init(AdapterEnv env) {
    }

    @Override
    public void toJson(org.jsonk.mocks.OrderItem o, JsonWriter writer) {
        writer.writeLBrace();
        var first = true;
        var v0 = o.productId();
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
        writer.writeInt(o.quantity());
        writer.writeComma();
        writer.write(chars2);
        writer.writeColon();
        writer.writeLong(o.price());
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.OrderItem fromJson(JsonReader reader) {
        java.lang.String v1 = null;
        int v2 = 0;
        long v3 = 0;
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
                case 1 -> v2 = reader.readInt();
                case 2 -> v3 = reader.readLong();
                default -> reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept('}');
        var o = new OrderItem(v1, v2, v3);
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.OrderItem.class);
    }

}
