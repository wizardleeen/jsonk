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

public class LocalDateTimeFooAdapter implements Adapter<org.jsonk.mocks.LocalDateTimeFoo> {

    private Adapter<java.time.LocalDateTime> adapter0;
    private Adapter<java.time.LocalDateTime> adapter1;

    @Override
    public void init(AdapterRegistry registry) {
        adapter0 = registry.getAdapter(java.time.LocalDateTime.class, Map.ofEntries(
            Map.entry("dateFormat", "yyyy-MM-dd HH:mm:ss")
        )
        );
        adapter1 = registry.getAdapter(java.time.LocalDateTime.class);
    }

    @Override
    public void toJson(org.jsonk.mocks.LocalDateTimeFoo o, JsonWriter writer) {
        writer.writeLBrace();
        var first = true;
        var v0 = o.time();
        if (v0 != null) {
            first = false;
            writer.writeName("time");
            writer.writeValue(v0, adapter0);
        }
        var v1 = o.time1();
        if (v1 != null) {
            if (first) {
                first = false;
            } else {
                writer.writeComma();
            }
            writer.writeName("time1");
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
            var name = reader.readString();
            reader.skipWhitespace();
            reader.accept(':');
            reader.skipWhitespace();
            switch(name) {
                case "time" -> v2 = reader.readObject(adapter0);
                case "time1" -> v3 = reader.readObject(adapter1);
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
