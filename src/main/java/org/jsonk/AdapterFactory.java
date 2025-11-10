package org.jsonk;

import java.util.Map;

public interface AdapterFactory {

    Adapter<?> create(Type type, Map<String, Object> attributes);

    boolean isSupported(Type type, Map<String, Object> attributes);

}
