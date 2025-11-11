package org.jsonk.mocks;

import java.util.Map;
import java.util.List;
import org.jsonk.JsonWriter;
import org.jsonk.JsonReader;
import org.jsonk.Adapter;
import org.jsonk.Type;
import org.jsonk.AdapterKey;
import org.jsonk.AdapterRegistry;

public class TypeAdapter implements Adapter<org.jsonk.mocks.Type> {

    private Adapter<java.lang.String> adapter0;
    private Adapter<org.jsonk.mocks.ClassType> adapter1;
    private Adapter<org.jsonk.mocks.PrimitiveType> adapter2;
    private Adapter<org.jsonk.mocks.ArrayType> adapter3;
    private static final char[] chars0 = new char[] {'"', 't', 'y', 'p', 'e', '"'};

    @Override
    public void init(AdapterRegistry registry) {
        adapter0 = registry.getAdapter(java.lang.String.class);
        adapter1 = registry.getAdapter(org.jsonk.mocks.ClassType.class);
        adapter2 = registry.getAdapter(org.jsonk.mocks.PrimitiveType.class);
        adapter3 = registry.getAdapter(org.jsonk.mocks.ArrayType.class);
    }

    @Override
    public void toJson(org.jsonk.mocks.Type o, JsonWriter writer) {
        switch(o) {
            case org.jsonk.mocks.ClassType v0 -> adapter1.toJson(v0, writer);
            case org.jsonk.mocks.PrimitiveType v1 -> adapter2.toJson(v1, writer);
            case org.jsonk.mocks.ArrayType v2 -> adapter3.toJson(v2, writer);
            default -> throw new IllegalStateException("Unexpected value: " + o);
        }
    }

    @Override
    public org.jsonk.mocks.Type fromJson(JsonReader reader) {
        reader.mark();
        reader.accept('{');
        do {
            reader.skipWhitespace();
            if (reader.is('}'))
                break;
            var name = reader.readString();
            reader.skipWhitespace();
            reader.accept(':');
            reader.skipWhitespace();
            if (name.equals("type")) {
                var typeProp = reader.readString();
                switch (typeProp) {
                    case "class" -> {
                        reader.rollback();
                        return adapter1.fromJson(reader);
                    }
                    case "primitive" -> {
                        reader.rollback();
                        return adapter2.fromJson(reader);
                    }
                    case "array" -> {
                        reader.rollback();
                        return adapter3.fromJson(reader);
                    }
                    default -> throw reader.parseException("Unknown type type property: " + typeProp);
                }
            } else {
                reader.skipValue();
            }
            reader.skipWhitespace();
        } while (reader.skip(','));
        throw reader.parseException("Type property 'type' not found");
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(org.jsonk.mocks.Type.class);
    }

}
