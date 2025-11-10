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

public class RoleAdapter implements Adapter<org.jsonk.mocks.Role> {

    @Override
    public void init(AdapterRegistry registry) {
    }

    @Override
    public void toJson(org.jsonk.mocks.Role o, JsonWriter writer) {
        writer.writeLBrace();
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.Role fromJson(JsonReader reader) {
        var o = new Role();
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
                default -> reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept('}');
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.Role.class);
    }

}
