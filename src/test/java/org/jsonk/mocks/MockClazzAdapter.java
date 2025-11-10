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

public class MockClazzAdapter implements Adapter<org.jsonk.mocks.MockClazz> {

    @Override
    public void init(AdapterRegistry registry) {
    }

    @Override
    public void toJson(org.jsonk.mocks.MockClazz o, JsonWriter writer) {
        writer.writeLBrace();
        writer.writeName("abstract");
        writer.writeBoolean(o.isAbstract());
        writer.writeRBrace();
    }

    @Override
    public org.jsonk.mocks.MockClazz fromJson(JsonReader reader) {
        boolean v0 = false;
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
                case "abstract" -> v0 = reader.readBoolean();
                default -> reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        reader.accept('}');
        var o = new MockClazz(v0);
        return o;
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.MockClazz.class);
    }

}
