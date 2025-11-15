package org.jsonk;

public interface Adapter<T> {

    void init(AdapterEnv env);

    void toJson(T o, JsonWriter writer);

    T fromJson(JsonReader reader);

    AdapterKey getKey();

}
