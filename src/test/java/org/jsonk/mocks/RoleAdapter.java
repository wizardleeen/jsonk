package org.jsonk.mocks;

import java.util.Map;
import java.util.List;
import org.jsonk.JsonWriter;
import org.jsonk.JsonReader;
import org.jsonk.Adapter;
import org.jsonk.Type;
import org.jsonk.AdapterKey;
import org.jsonk.AdapterEnv;

public class RoleAdapter implements Adapter<org.jsonk.mocks.Role> {

    private static final char[][] keys = new char[][] {
        null};
    private static final int[] ordinals = new int[] {-1};
    private static final long seed = 3952059428146633563L;

    @Override
    public void init(AdapterEnv env) {
    }

    @Override
    public void toJson(org.jsonk.mocks.Role o, JsonWriter writer) {
        writer.writeLBrace();
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.Role fromJson(JsonReader reader) {
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
                default -> reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept('}');
        var o = new Role();
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.Role.class);
    }

}
