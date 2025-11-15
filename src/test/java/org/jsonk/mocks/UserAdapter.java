package org.jsonk.mocks;

import java.util.Map;
import java.util.List;
import org.jsonk.JsonWriter;
import org.jsonk.JsonReader;
import org.jsonk.Adapter;
import org.jsonk.Type;
import org.jsonk.AdapterKey;
import org.jsonk.AdapterEnv;

public class UserAdapter implements Adapter<org.jsonk.mocks.User> {

    private Adapter<java.util.Map<java.lang.String,java.lang.Object>> adapter0;
    private static final char[][] keys = new char[][] {
        null,
        new char[] {'a', 't', 't', 'r', 'i', 'b', 'u', 't', 'e', 's'},
        null,
        new char[] {'c', 'r', 'e', 'a', 't', 'o', 'r'},
        null,
        null,
        new char[] {'a', 'c', 't', 'i', 'v', 'a', 't', 'e', 'd'},
        new char[] {'k', 'i', 'n', 'd'},
        new char[] {'n', 'a', 'm', 'e'},
        null,
        null,
        null,
        null,
        new char[] {'p', 'a', 's', 's', 'w', 'o', 'r', 'd'},
        null,
        null};
    private static final int[] ordinals = new int[] {-1, 5, -1, 4, -1, -1, 3, 1, 0, -1, -1, -1, -1, 2, -1, -1};
    private static final long seed = 4678264231828607429L;
    private static final char[] chars0 = new char[] {'"', 'n', 'a', 'm', 'e', '"'};
    private static final char[] chars1 = new char[] {'"', 'k', 'i', 'n', 'd', '"'};
    private static final char[] chars2 = new char[] {'"', 'p', 'a', 's', 's', 'w', 'o', 'r', 'd', '"'};
    private static final char[] chars3 = new char[] {'"', 'a', 'c', 't', 'i', 'v', 'a', 't', 'e', 'd', '"'};
    private static final char[] chars4 = new char[] {'"', 'c', 'r', 'e', 'a', 't', 'o', 'r', '"'};
    private static final char[] chars5 = new char[] {'"', 'a', 't', 't', 'r', 'i', 'b', 'u', 't', 'e', 's', '"'};

    @Override
    public void init(AdapterEnv env) {
        adapter0 = (Adapter) env.getAdapter(Type.from(java.util.Map.class, Type.from(java.lang.String.class), Type.from(java.lang.Object.class)));
    }

    @Override
    public void toJson(org.jsonk.mocks.User o, JsonWriter writer) {
        writer.writeLBrace();
        var first = true;
        var v0 = o.name();
        if (v0 != null) {
            first = false;
            writer.write(chars0);
            writer.writeColon();
            writer.writeString(v0);
        }
        var v1 = o.kind();
        if (v1 != null) {
            if (first) {
                first = false;
            } else {
                writer.writeComma();
            }
            writer.write(chars1);
            writer.writeColon();
            writer.writeString(v1.name());
        }
        var v2 = o.password();
        if (v2 != null) {
            if (first) {
                first = false;
            } else {
                writer.writeComma();
            }
            writer.write(chars2);
            writer.writeColon();
            writer.writeString(v2);
        }
        if (!first) {
            writer.writeComma();
        }
        writer.write(chars3);
        writer.writeColon();
        writer.writeBoolean(o.activated());
        var v3 = o.creator();
        if (v3 != null) {
            writer.writeComma();
            writer.write(chars4);
            writer.writeColon();
            writer.writeObjectOrNull(v3, this);
        }
        var v4 = o.attributes();
        if (v4 != null) {
            writer.writeComma();
            writer.write(chars5);
            writer.writeColon();
            writer.writeObject(v4, adapter0);
        }
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.User fromJson(JsonReader reader) {
        java.lang.String v5 = null;
        org.jsonk.mocks.UserKind v6 = null;
        java.lang.String v7 = null;
        boolean v8 = false;
        org.jsonk.mocks.User v9 = null;
        java.util.Map<java.lang.String,java.lang.Object> v10 = null;
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
                case 0 -> v5 = reader.readStringOrNull();
                case 1 -> v6 = reader.readNullable(() -> org.jsonk.mocks.UserKind.valueOf(reader.readString()));
                case 2 -> v7 = reader.readStringOrNull();
                case 3 -> v8 = reader.readBoolean();
                case 4 -> v9 = reader.readObject(this);
                case 5 -> v10 = reader.readObject(adapter0);
                default -> reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept('}');
        var o = new User(v5, v6, v7, v8, v9, v10);
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.User.class);
    }

}
