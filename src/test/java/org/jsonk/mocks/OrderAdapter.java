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

public class OrderAdapter implements Adapter<org.jsonk.mocks.Order> {

    private Adapter<java.util.List<org.jsonk.mocks.OrderItem>> adapter0;

    @Override
    public void init(AdapterRegistry registry) {
        adapter0 = (Adapter) registry.getAdapter(Type.from(java.util.List.class, Type.from(org.jsonk.mocks.OrderItem.class)));
    }

    @Override
    public void toJson(org.jsonk.mocks.Order o, JsonWriter writer) {
        writer.writeLBrace();
        var v0 = o.items();
        if (v0 != null) {
            writer.writeName("items");
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
            var name = reader.readString();
            reader.skipWhitespace();
            reader.accept(':');
            reader.skipWhitespace();
            switch(name) {
                case "items" -> v1 = reader.readObject(adapter0);
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
