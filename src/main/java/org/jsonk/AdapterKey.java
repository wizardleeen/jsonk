package org.jsonk;

import java.util.Map;

public record AdapterKey(Type type, Map<String, Object> attributes) {

    public static AdapterKey of(Class<?> clazz) {
        return of(Type.from(clazz));
    }

    public static AdapterKey of(Type type) {
        return new AdapterKey(type, Map.of());
    }

}
