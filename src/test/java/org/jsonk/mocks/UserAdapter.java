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

public class UserAdapter implements Adapter<org.jsonk.mocks.User> {

    private Adapter<java.lang.String> adapter0;
    private Adapter<java.util.Map<java.lang.String,java.lang.Object>> adapter1;

    @Override
    public void init(AdapterRegistry registry) {
        adapter0 = registry.getAdapter(java.lang.String.class);
        adapter1 = (Adapter) registry.getAdapter(Type.from(java.util.Map.class, Type.from(java.lang.String.class), Type.from(java.lang.Object.class)));
    }

    @Override
    public void toJson(org.jsonk.mocks.User o, JsonWriter writer) {
        writer.writeLBrace();
        var first = true;
        var v0 = o.name();
        if (v0 != null) {
            first = false;
            writer.writeName("name");
            writer.writeString(v0);
        }
        var v1 = o.kind();
        if (v1 != null) {
            if (first) {
                first = false;
            } else {
                writer.writeComma();
            }
            writer.writeName("kind");
            writer.writeString(v1.name());
        }
        var v2 = o.password();
        if (v2 != null) {
            if (first) {
                first = false;
            } else {
                writer.writeComma();
            }
            writer.writeName("password");
            writer.writeString(v2);
        }
        if (!first) {
            writer.writeComma();
        }
        writer.writeName("activated");
        writer.writeBoolean(o.activated());
        var v3 = o.creator();
        if (v3 != null) {
            writer.writeComma();
            writer.writeName("creator");
            writer.writeObjectOrNull(v3, this);
        }
        var v4 = o.attributes();
        if (v4 != null) {
            writer.writeComma();
            writer.writeName("attributes");
            writer.writeObject(v4, adapter1);
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
            var name = reader.readString();
            reader.skipWhitespace();
            reader.accept(':');
            reader.skipWhitespace();
            switch(name) {
                case "name" -> v5 = reader.readStringOrNull();
                case "kind" -> v6 = reader.readNullable(() -> org.jsonk.mocks.UserKind.valueOf(reader.readString()));
                case "password" -> v7 = reader.readStringOrNull();
                case "activated" -> v8 = reader.readBoolean();
                case "creator" -> v9 = reader.readObject(this);
                case "attributes" -> v10 = reader.readObject(adapter1);
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
