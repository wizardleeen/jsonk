package org.jsonk.adapters;

import org.jsonk.*;

import java.util.Arrays;

public class BooleanArrayAdapter implements Adapter<boolean[]> {
    @Override
    public void init(AdapterEnv env) {

    }

    @Override
    public void toJson(boolean[] o, JsonWriter writer) {
        if (o.length == 0) {
            writer.write("[]");
        }
        writer.writeLBracket();
        writer.writeBoolean(o[0]);
        for (var i = 1; i < o.length; i++) {
            writer.writeComma();
            writer.writeBoolean(o[i]);
        }
        writer.writeRBracket();
    }

    @Override
    public boolean[] fromJson(JsonReader reader) {
        var a = new boolean[10];
        var i = 0;
        reader.accept('[');
        do {
            reader.skipWhitespace();
            if (reader.is(']'))
                break;
            if (i == a.length)
                a = Arrays.copyOf(a, a.length << 1);
            a[i++] = reader.readBoolean();
            reader.skipWhitespace();
        } while(reader.skipComma());
        reader.accept(']');
        if (i < a.length)
            a = Arrays.copyOf(a, i);
        return a;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(boolean[].class);
    }
}
