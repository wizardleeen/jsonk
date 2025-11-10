package org.jsonk;

public interface Adapter<T> {

    void init(AdapterRegistry registry);

    void toJson(T o, JsonWriter writer);

    T fromJson(JsonReader reader);

    AdapterKey getKey();

}
