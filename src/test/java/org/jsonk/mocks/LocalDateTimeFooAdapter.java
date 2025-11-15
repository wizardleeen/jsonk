package org.jsonk.mocks;

import java.util.Map;
import java.util.List;
import org.jsonk.JsonWriter;
import org.jsonk.JsonReader;
import org.jsonk.Adapter;
import org.jsonk.Type;
import org.jsonk.AdapterKey;
import org.jsonk.AdapterEnv;

public class LocalDateTimeFooAdapter implements Adapter<org.jsonk.mocks.LocalDateTimeFoo> {

    private Adapter<java.time.LocalDateTime> adapter0;
    private Adapter<java.time.LocalDateTime> adapter1;
    private static final char[][] keys = new char[][] {
        null,
        new char[] {'t', 'i', 'm', 'e'},
        new char[] {'t', 'i', 'm', 'e', '1'},
        null};
    private static final int[] ordinals = new int[] {-1, 0, 1, -1};
    private static final long seed = -8066454907577447300L;
    private static final char[] chars0 = new char[] {'"', 't', 'i', 'm', 'e', '"'};
    private static final char[] chars1 = new char[] {'"', 't', 'i', 'm', 'e', '1', '"'};

    @Override
    public void init(AdapterEnv env) {
        adapter0 = env.getAdapter(java.time.LocalDateTime.class, Map.ofEntries(
            Map.entry("dateFormat", "yyyy-MM-dd HH:mm:ss")
        )
        );
        adapter1 = env.getAdapter(java.time.LocalDateTime.class);
    }

    @Override
    public void toJson(org.jsonk.mocks.LocalDateTimeFoo o, JsonWriter writer) {
        writer.writeLBrace();
        var first = true;
        var v0 = o.time();
        if (v0 != null) {
            first = false;
            writer.write(chars0);
            writer.writeColon();
            writer.writeValue(v0, adapter0);
        }
        var v1 = o.time1();
        if (v1 != null) {
            if (first) {
                first = false;
            } else {
                writer.writeComma();
            }
            writer.write(chars1);
            writer.writeColon();
            writer.writeValue(v1, adapter1);
        }
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.LocalDateTimeFoo fromJson(JsonReader reader) {
        java.time.LocalDateTime v2 = null;
        java.time.LocalDateTime v3 = null;
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
                case 0 -> v2 = reader.readObject(adapter0);
                case 1 -> v3 = reader.readObject(adapter1);
                default -> reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept('}');
        var o = new LocalDateTimeFoo(v2, v3);
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.LocalDateTimeFoo.class);
    }

}
