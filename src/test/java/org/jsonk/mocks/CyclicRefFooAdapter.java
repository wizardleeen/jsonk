package org.jsonk.mocks;

import java.util.Map;
import java.util.List;
import org.jsonk.JsonWriter;
import org.jsonk.JsonReader;
import org.jsonk.Adapter;
import org.jsonk.Type;
import org.jsonk.AdapterKey;
import org.jsonk.AdapterRegistry;

public class CyclicRefFooAdapter implements Adapter<org.jsonk.mocks.CyclicRefFoo> {

    private Adapter<java.lang.Object> adapter0;
    private static final char[][] keys = new char[][] {
        new char[] {'v', 'a', 'l', 'u', 'e'},
        null};
    private static final int[] ordinals = new int[] {0, -1};
    private static final long seed = -5542535026285684763L;
    private static final char[] chars0 = new char[] {'"', 'v', 'a', 'l', 'u', 'e', '"'};

    @Override
    public void init(AdapterRegistry registry) {
        adapter0 = registry.getAdapter(java.lang.Object.class);
    }

    @Override
    public void toJson(org.jsonk.mocks.CyclicRefFoo o, JsonWriter writer) {
        writer.writeLBrace();
        var v0 = o.getValue();
        if (v0 != null) {
            writer.write(chars0);
            writer.writeColon();
            writer.writeObject(v0, adapter0);
        }
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.CyclicRefFoo fromJson(JsonReader reader) {
        java.lang.Object v1 = null;
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
        var o = new CyclicRefFoo();
        o.setValue(v1);
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.CyclicRefFoo.class);
    }

}
