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

public class ProductAdapter implements Adapter<org.jsonk.mocks.Product> {

    private Adapter<java.lang.String> adapter0;

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
            writer.writeName("name");
            writer.writeString(v0);
        }
        if (!first) {
            writer.writeComma();
        }
        writer.writeName("price");
        writer.writeDouble(o.getPrice());
        writer.writeComma();
        writer.writeName("stock");
        writer.writeInt(o.getStock());
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.Product fromJson(JsonReader reader) {
        reader.mark();
        java.lang.String v1 = null;
        double v2 = 0;
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
                case "price" -> v2 = reader.readDouble();
                default -> reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept('}');
        var o = new Product(v1, v2);
        reader.rollback();
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
                case "stock" -> o.setStock(reader.readInt());
                default -> reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept('}');
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.Product.class);
    }

}
