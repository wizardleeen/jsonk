package org.jsonk.adapters;

import org.jsonk.*;
import org.jsonk.Type;

import java.util.Map;
import java.util.Objects;

public abstract class MapAdapter<K, V, M extends Map<K, V>> implements Adapter<M> {

    private final Type type;
    private final Map<String, Object> attributes;
    private Adapter<V> valueAdapter;

    public MapAdapter(Type type, Map<String, Object> attributes) {
        this.type = type;
        this.attributes = attributes;
    }

    @Override
    public void init(AdapterEnv env) {
        var valueType = type.typeArguments().isEmpty() ?
                Type.from(Object.class) : type.typeArguments().getLast();
        //noinspection unchecked
        valueAdapter = (Adapter<V>) Objects.requireNonNull(env.getAdapter(valueType, attributes),
                () -> "Adapter not found for type: " + valueType.clazz().getName());
    }

    @Override
    public void toJson(M o, JsonWriter writer) {
        writer.writeLBrace();
        var first = true;
        for (var e : o.entrySet()) {
            if (first)
                first = false;
            else
                writer.writeComma();
            if (e.getKey() instanceof String key)
                writer.writeString(key);
            else
                throw new IllegalArgumentException("Map keys must be strings");
            writer.writeColon();
            writer.writeObjectOrNull(e.getValue(), valueAdapter);
        }
        writer.writeRBrace();
    }

    @Override
    public M fromJson(JsonReader reader) {
        var map = createMap();
        reader.accept('{');
        do {
            reader.skipWhitespace();
            if (reader.is('}'))
                break;
            var key = readKey(reader);
            reader.skipWhitespace();
            reader.accept(':');
            reader.skipWhitespace();
            map.put(key, reader.readObject(valueAdapter));
            reader.skipWhitespace();
        } while (reader.skipComma());
        reader.accept('}');
        return map;
    }

    protected abstract K readKey(JsonReader reader);

    protected abstract M createMap();

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(type);
    }
}
