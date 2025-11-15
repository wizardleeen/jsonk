package org.jsonk.mocks;

import java.util.Map;
import java.util.List;
import org.jsonk.JsonWriter;
import org.jsonk.JsonReader;
import org.jsonk.Adapter;
import org.jsonk.Type;
import org.jsonk.AdapterKey;
import org.jsonk.AdapterEnv;

public class OrderAdapter implements Adapter<org.jsonk.mocks.Order> {

    private Adapter<java.util.List<org.jsonk.mocks.OrderItem>> adapter0;
    private static final char[][] keys = new char[][] {
        new char[] {'i', 't', 'e', 'm', 's'},
        null};
    private static final int[] ordinals = new int[] {0, -1};
    private static final long seed = -2006895371468814518L;
    private static final char[] chars0 = new char[] {'"', 'i', 't', 'e', 'm', 's', '"'};

    @Override
    public void init(AdapterEnv env) {
        adapter0 = (Adapter) env.getAdapter(Type.from(java.util.List.class, Type.from(org.jsonk.mocks.OrderItem.class)));
    }

    @Override
    public void toJson(org.jsonk.mocks.Order o, JsonWriter writer) {
        writer.writeLBrace();
        var v0 = o.items();
        if (v0 != null) {
            writer.write(chars0);
            writer.writeColon();
            writer.writeObject(v0, adapter0);
        }
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.Order fromJson(JsonReader reader) {
        java.util.List<org.jsonk.mocks.OrderItem> v1 = null;
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
        var o = new Order(v1);
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.Order.class);
    }

}
