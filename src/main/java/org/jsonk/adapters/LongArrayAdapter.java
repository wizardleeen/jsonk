package org.jsonk.adapters;

import org.jsonk.*;

import java.util.Arrays;

public class LongArrayAdapter implements Adapter<long[]> {
    @Override
    public void init(AdapterRegistry registry) {

    }

    @Override
    public void toJson(long[] o, JsonWriter writer) {
        if (o.length == 0) {
            writer.write("[]");
        }
        writer.writeLBracket();
        writer.writeLong(o[0]);
        for (var i = 1; i < o.length; i++) {
            writer.writeComma();
            writer.writeLong(o[i]);
        }
        writer.writeRBracket();
    }

    @Override
    public long[] fromJson(JsonReader reader) {
        var a = new long[10];
        var i = 0;
        reader.accept('[');
        do {
            reader.skipWhitespace();
            if (reader.is(']'))
                break;
            if (i == a.length)
                a = Arrays.copyOf(a, a.length << 1);
            a[i++] = reader.readLong();
            reader.skipWhitespace();
        } while(reader.skipComma());
        reader.accept(']');
        if (i < a.length)
            a = Arrays.copyOf(a, i);
        return a;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(long[].class);
    }
}
