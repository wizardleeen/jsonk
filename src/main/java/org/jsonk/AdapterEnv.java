package org.jsonk;

import java.util.Map;

public interface AdapterEnv {

    void addAdapter(Adapter<?> adapter);

    default <T> Adapter<T> getAdapter(Class<T> clazz) {
        //noinspection unchecked
        return (Adapter<T>) getAdapter(Type.from(clazz));
    }


    default <T> Adapter<T> getAdapter(Class<T> clazz, Map<String, Object> attributes) {
        //noinspection unchecked
        return (Adapter<T>) getAdapter(Type.from(clazz), attributes);
    }

    default Adapter<?> getAdapter(Type type) {
        return getAdapter(type, Map.of());
    }

    Adapter<?> getAdapter(Type type, Map<String, Object> attributes);

}
