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

public class OrderItemAdapter implements Adapter<org.jsonk.mocks.OrderItem> {

    private Adapter<java.lang.String> adapter0;

    @Override
    public void init(AdapterRegistry registry) {
        adapter0 = registry.getAdapter(java.lang.String.class);
    }

    @Override
    public void toJson(org.jsonk.mocks.OrderItem o, JsonWriter writer) {
        writer.writeLBrace();
        var first = true;
        var v0 = o.productId();
        if (v0 != null) {
            first = false;
            writer.writeName("productId");
            writer.writeString(v0);
        }
        if (!first) {
            writer.writeComma();
        }
        writer.writeName("quantity");
        writer.writeInt(o.quantity());
        writer.writeComma();
        writer.writeName("price");
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
            var name = reader.readString();
            reader.skipWhitespace();
            reader.accept(':');
            reader.skipWhitespace();
            switch(name) {
                case "productId" -> v1 = reader.readStringOrNull();
                case "quantity" -> v2 = reader.readInt();
                case "price" -> v3 = reader.readLong();
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
