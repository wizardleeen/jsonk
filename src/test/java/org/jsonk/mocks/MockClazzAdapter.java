package org.jsonk.mocks;

import java.util.Map;
import java.util.List;
import org.jsonk.JsonWriter;
import org.jsonk.JsonReader;
import org.jsonk.Adapter;
import org.jsonk.Type;
import org.jsonk.AdapterKey;
import org.jsonk.AdapterRegistry;

public class MockClazzAdapter implements Adapter<org.jsonk.mocks.MockClazz> {

    private static final char[][] keys = new char[][] {
        new char[] {'a', 'b', 's', 't', 'r', 'a', 'c', 't'},
        null};
    private static final int[] ordinals = new int[] {0, -1};
    private static final long seed = -4904386468386775572L;
    private static final char[] chars0 = new char[] {'"', 'a', 'b', 's', 't', 'r', 'a', 'c', 't', '"'};

    @Override
    public void init(AdapterRegistry registry) {
    }

    @Override
    public void toJson(org.jsonk.mocks.MockClazz o, JsonWriter writer) {
        writer.writeLBrace();
        writer.write(chars0);
        writer.writeColon();
        writer.writeBoolean(o.isAbstract());
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.MockClazz fromJson(JsonReader reader) {
        boolean v0 = false;
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
                case 0 -> v0 = reader.readBoolean();
                default -> reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept('}');
        var o = new MockClazz(v0);
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.MockClazz.class);
    }

}
