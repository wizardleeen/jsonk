package org.jsonk.mocks;

import java.util.Map;
import java.util.List;
import org.jsonk.JsonWriter;
import org.jsonk.JsonReader;
import org.jsonk.Adapter;
import org.jsonk.Type;
import org.jsonk.AdapterKey;
import org.jsonk.AdapterEnv;

public class ItemAdapter<T> implements Adapter<org.jsonk.mocks.Item<T>> {

    private final Type type;
    private Adapter<T> adapter0;
    private static final char[][] keys = new char[][] {
        new char[] {'v', 'a', 'l', 'u', 'e'},
        null};
    private static final int[] ordinals = new int[] {0, -1};
    private static final long seed = 8143703029326192678L;
    private static final char[] chars0 = new char[] {'"', 'v', 'a', 'l', 'u', 'e', '"'};

    public ItemAdapter(Type type) {
        this.type = type;
    }

    @Override
    public void init(AdapterEnv env) {
        adapter0 = (Adapter) env.getAdapter(type.typeArguments().get(0));
    }

    @Override
    public void toJson(org.jsonk.mocks.Item<T> o, JsonWriter writer) {
        writer.writeLBrace();
        var v0 = o.value();
        if (v0 != null) {
            writer.write(chars0);
            writer.writeColon();
            writer.writeObject(v0, adapter0);
        }
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.Item<T> fromJson(JsonReader reader) {
        T v1 = null;
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
                case 0 -> v1 = reader.readObject(adapter0);
                default -> reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept('}');
        var o = new Item(v1);
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(type);
    }

}
