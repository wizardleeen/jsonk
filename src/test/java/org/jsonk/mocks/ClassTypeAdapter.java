package org.jsonk.mocks;

import java.util.Map;
import java.util.List;
import org.jsonk.JsonWriter;
import org.jsonk.JsonReader;
import org.jsonk.Adapter;
import org.jsonk.Type;
import org.jsonk.AdapterKey;
import org.jsonk.AdapterEnv;

public class ClassTypeAdapter implements Adapter<org.jsonk.mocks.ClassType> {

    private static final char[][] keys = new char[][] {
        null,
        new char[] {'n', 'a', 'm', 'e'}};
    private static final int[] ordinals = new int[] {-1, 0};
    private static final long seed = 7461291490168357326L;
    private static final char[] chars0 = new char[] {'"', 't', 'y', 'p', 'e', '"'};
    private static final char[] chars1 = new char[] {'"', 'c', 'l', 'a', 's', 's', '"'};
    private static final char[] chars2 = new char[] {'"', 'n', 'a', 'm', 'e', '"'};

    @Override
    public void init(AdapterEnv env) {
    }

    @Override
    public void toJson(org.jsonk.mocks.ClassType o, JsonWriter writer) {
        writer.writeLBrace();
        writer.write(chars0);
        writer.writeColon();
        writer.write(chars1);
        var v0 = o.name();
        if (v0 != null) {
            writer.writeComma();
            writer.write(chars2);
            writer.writeColon();
            writer.writeString(v0);
        }
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.ClassType fromJson(JsonReader reader) {
        java.lang.String v1 = null;
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
                case 0 -> v1 = reader.readStringOrNull();
                default -> reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept('}');
        var o = new ClassType(v1);
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.ClassType.class);
    }

}
