package org.jsonk.adapters;

import org.jsonk.*;

import java.util.Arrays;

public class IntArrayAdapter implements Adapter<int[]> {
    @Override
    public void init(AdapterEnv env) {

    }

    @Override
    public void toJson(int[] o, JsonWriter writer) {
        if (o.length == 0) {
            writer.write("[]");
        }
        writer.writeLBracket();
        writer.writeInt(o[0]);
        for (var i = 1; i < o.length; i++) {
            writer.writeComma();
            writer.writeInt(o[i]);
        }
        writer.writeRBracket();
    }

    @Override
    public int[] fromJson(JsonReader reader) {
        var a = new int[10];
        var i = 0;
        reader.accept('[');
        do {
            reader.skipWhitespace();
            if (reader.is(']'))
                break;
            if (i == a.length)
                a = Arrays.copyOf(a, a.length << 1);
            a[i++] = reader.readInt();
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
