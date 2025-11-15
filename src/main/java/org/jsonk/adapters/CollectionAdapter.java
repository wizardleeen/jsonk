package org.jsonk.adapters;

import org.jsonk.*;
import org.jsonk.Type;

import java.util.Collection;
import java.util.Map;

public abstract class CollectionAdapter<E, C extends Collection<E>> implements Adapter<C> {

    private final Type type;
    private final Map<String, Object> attributes;
    private Adapter<E> elementAdapter;

    public CollectionAdapter(Type type, Map<String, Object> attributes) {
        this.type = type;
        this.attributes = attributes;
    }

    @Override
    public void init(AdapterEnv env) {
        var elementType = type.typeArguments().isEmpty() ?
                Type.from(Object.class) : type.typeArguments().getFirst();
        //noinspection unchecked
        elementAdapter = (Adapter<E>) env.getAdapter(elementType, attributes);
    }

    @Override
    public void toJson(C o, JsonWriter writer) {
        if (o.isEmpty()) {
            writer.write("[]");
            return;
        }
        writer.writeLBracket();
        var it = o.iterator();
        writer.writeObjectOrNull(it.next(), elementAdapter);
        while (it.hasNext()) {
            writer.writeComma();
            writer.writeObjectOrNull(it.next(), elementAdapter);
        }
        writer.writeRBracket();
    }

    @Override
    public C fromJson(JsonReader reader) {
        var a = createCollection();
        reader.accept('[');
        do {
            reader.skipWhitespace();
            if (reader.is(']'))
                break;
            a.add(reader.readObject(elementAdapter));
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept(']');
        return a;
    }

    protected abstract C createCollection();

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(type);
    }

}
