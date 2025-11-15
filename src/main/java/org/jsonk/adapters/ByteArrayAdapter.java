package org.jsonk.adapters;

import org.jsonk.*;

import java.util.Arrays;

public class ByteArrayAdapter implements Adapter<byte[]> {
    @Override
    public void init(AdapterEnv env) {

    }

    @Override
    public void toJson(byte[] o, JsonWriter writer) {
        if (o.length == 0) {
            writer.write("[]");
        }
        writer.writeLBracket();
        writer.writeByte(o[0]);
        for (var i = 1; i < o.length; i++) {
            writer.writeComma();
            writer.writeByte(o[i]);
        }
        writer.writeRBracket();
    }

    @Override
    public byte[] fromJson(JsonReader reader) {
        var a = new byte[10];
        var i = 0;
        reader.accept('[');
        do {
            reader.skipWhitespace();
            if (reader.is(']'))
                break;
            if (i == a.length)
                a = Arrays.copyOf(a, a.length << 1);
            a[i++] = reader.readByte();
            reader.skipWhitespace();
        } while(reader.skipComma());
        reader.accept(']');
        if (i < a.length)
            a = Arrays.copyOf(a, i);
        return a;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(byte[].class);
    }
}
