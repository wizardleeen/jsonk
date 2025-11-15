package org.jsonk.adapters;

import org.jsonk.*;
import org.jsonk.Type;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ArrayAdapter<E> implements Adapter<E[]> {

    private Adapter<E> elementAdapter;
    private final Type type;
    private final Class<?> componentClass;

    public ArrayAdapter(Type type) {
        this.type = type;
        componentClass = type.clazz().getComponentType();
    }

    @Override
    public void init(AdapterEnv env) {
        //noinspection unchecked
        elementAdapter = (Adapter<E>) env.getAdapter(componentClass);
    }

    @Override
    public void toJson(E[] o, JsonWriter writer) {
        if (o.length == 0) {
            writer.write("[]");
            return;
        }
        writer.writeLBracket();
        writer.writeObjectOrNull(o[0], elementAdapter);
        for (int i = 1; i < o.length; i++) {
            writer.writeComma();
            writer.writeObjectOrNull(o[i], elementAdapter);
        }
        writer.writeRBracket();
    }

    @Override
    public E[] fromJson(JsonReader reader) {
        //noinspection unchecked
        var a = (E[]) Array.newInstance(componentClass, 10);
        reader.accept('[');
        var i = 0;
        do {
            reader.skipWhitespace();
            if (reader.is(']'))
                break;
            if (i == a.length)
                a = Arrays.copyOf(a, a.length << 1);
            a[i++] = reader.readObject(elementAdapter);
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept(']');
        if (i > a.length)
            a = Arrays.copyOf(a, i);
        return a;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(type);
    }
}
