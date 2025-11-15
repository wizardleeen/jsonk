package org.jsonk;

import java.util.HashMap;
import java.util.Map;

class TmpAdapterEnv implements AdapterEnv {

    private final AdapterRegistry registry;
    private final Map<AdapterKey, Adapter<?>> adapters = new HashMap<>();

    TmpAdapterEnv(AdapterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void addAdapter(Adapter<?> adapter) {
        adapters.put(adapter.getKey(), adapter);
    }

    @Override
    public Adapter<?> getAdapter(Type type, Map<String, Object> attributes) {
        var adapter = adapters.get(new AdapterKey(type, attributes));
        if (adapter != null)
            return adapter;
        return registry.getAdapter(type, attributes, this);
    }
}
