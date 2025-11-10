package org.jsonk.adapters;

import org.jsonk.*;

import java.util.Arrays;

public class ShortArrayAdapter implements Adapter<short[]> {
    @Override
    public void init(AdapterRegistry registry) {

    }

    @Override
    public void toJson(short[] o, JsonWriter writer) {
        if (o.length == 0) {
            writer.write("[]");
        }
        writer.writeLBracket();
        writer.writeShort(o[0]);
        for (var i = 1; i < o.length; i++) {
            writer.writeComma();
            writer.writeShort(o[i]);
        }
        writer.writeRBracket();
    }

    @Override
    public short[] fromJson(JsonReader reader) {
        var a = new short[10];
        var i = 0;
        reader.accept('[');
        do {
            reader.skipWhitespace();
            if (reader.is(']'))
                break;
            if (i == a.length)
                a = Arrays.copyOf(a, a.length << 1);
            a[i++] = reader.readShort();
            reader.skipWhitespace();
        } while(reader.skipComma());
        reader.accept(']');
        if (i < a.length)
            a = Arrays.copyOf(a, i);
        return a;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(int[].class);
    }
}
