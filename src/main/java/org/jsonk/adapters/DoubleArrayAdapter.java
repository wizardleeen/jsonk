package org.jsonk.adapters;

import org.jsonk.*;

import java.util.Arrays;

public class DoubleArrayAdapter implements Adapter<double[]> {
    @Override
    public void init(AdapterEnv env) {

    }

    @Override
    public void toJson(double[] o, JsonWriter writer) {
        if (o.length == 0) {
            writer.write("[]");
        }
        writer.writeLBracket();
        writer.writeDouble(o[0]);
        for (var i = 1; i < o.length; i++) {
            writer.writeComma();
            writer.writeDouble(o[i]);
        }
        writer.writeRBracket();
    }

    @Override
    public double[] fromJson(JsonReader reader) {
        var a = new double[10];
        var i = 0;
        reader.accept('[');
        do {
            reader.skipWhitespace();
            if (reader.is(']'))
                break;
            if (i == a.length)
                a = Arrays.copyOf(a, a.length << 1);
            a[i++] = reader.readDouble();
            reader.skipWhitespace();
        } while(reader.skipComma());
        reader.accept(']');
        if (i < a.length)
            a = Arrays.copyOf(a, i);
        return a;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(double[].class);
    }
}
