package org.jsonk;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class AdapterRegistry {

    public static final AdapterRegistry instance = new AdapterRegistry();

    private final Map<AdapterKey, Adapter<?>> adapters = new HashMap<>();
    private final Queue<Adapter<?>> uninitializedAdapters = new LinkedList<>();
    private final List<AdapterFactory> adapterFactories = new ArrayList<>();

    private AdapterRegistry() {
        var factories = ServiceLoader.load(AdapterFactory.class, AdapterRegistry.class.getClassLoader());
        for (var factory : factories) {
            adapterFactories.add(factory);
        }
        var adapters = ServiceLoader.load(Adapter.class, AdapterRegistry.class.getClassLoader());
        for (Adapter<?> adapter : adapters) {
            addAdapter(adapter);
        }
        initAdapters();
    }

    public <T> Adapter<T> getAdapter(Class<T> clazz) {
        //noinspection unchecked,rawtypes
        return (Adapter) getAdapter(Type.from(clazz));
    }

    public <T> Adapter<T> getAdapter(Class<T> clazz, Map<String, Object> attributes) {
        //noinspection unchecked,rawtypes
        return (Adapter) getAdapter(Type.from(clazz), attributes);
    }

    public Adapter<?> getAdapter(Type type) {
        return getAdapter(type, Map.of());
    }

    public Adapter<?> getAdapter(Type type, Map<String, Object> attributes) {
        var key = new AdapterKey(type, attributes);
        var adapter = adapters.get(key);
        if (adapter != null) {
            return adapter;
        }
        for (AdapterFactory factory : adapterFactories) {
            if (factory.isSupported(type, Map.of())) {
                var adapter1 = factory.create(type, attributes);
                adapters.put(key, adapter1);
                adapter1.init(this);
                return adapter1;
            }
        }
        throw new IllegalStateException("No adapter registered for type: " + type + ". Make sure the involved classes are annotated with @Json");
    }

    void addAdapterFactory(AdapterFactory factory) {
        adapterFactories.add(factory);
    }

    void addAdapter(Adapter<?> adapter) {
        try {
            adapters.put(adapter.getKey(), adapter);
            uninitializedAdapters.offer(adapter);
        } catch (NoClassDefFoundError ignored) {
            // This can happen in IDE environments where class is removed
        }
    }

    void initAdapters() {
        while (!uninitializedAdapters.isEmpty()) {
            uninitializedAdapters.poll().init(this);
        }
    }

}
