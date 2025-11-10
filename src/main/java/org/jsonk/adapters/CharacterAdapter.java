package org.jsonk.adapters;

import org.jsonk.*;
import org.jsonk.Type;

public class CharacterAdapter implements Adapter<Character> {
    @Override
    public void init(AdapterRegistry registry) {

    }

    @Override
    public void toJson(Character o, JsonWriter writer) {
        writer.writeChar(o);
    }

    @Override
    public Character fromJson(JsonReader reader) {
        return reader.readChar();
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(Type.from(Character.class));
    }
}
