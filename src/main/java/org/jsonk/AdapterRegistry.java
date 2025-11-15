package org.jsonk;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class AdapterRegistry {

    public static final AdapterRegistry instance = new AdapterRegistry();

    private volatile Map<AdapterKey, Adapter<?>> adapters = new HashMap<>();
    private volatile List<AdapterFactory> adapterFactories = new ArrayList<>();

    private AdapterRegistry() {
        var factories = ServiceLoader.load(AdapterFactory.class, AdapterRegistry.class.getClassLoader());
        for (var factory : factories) {
            adapterFactories.add(factory);
        }
        var adapters = ServiceLoader.load(Adapter.class, AdapterRegistry.class.getClassLoader());
        for (Adapter<?> adapter : adapters) {
            try {
                this.adapters.put(adapter.getKey(), adapter);
            } catch (NoClassDefFoundError ignored) {
                // This can happen in IDE when class is removed
            }
        }
        addPrimitiveAdapters();
        var env = new TmpAdapterEnv(this);
        for (Adapter<?> adapter : adapters) {
            adapter.init(env);
        }
    }

    private void addPrimitiveAdapters() {
        reuseAdapter(Byte.class, byte.class);
        reuseAdapter(Short.class, short.class);
        reuseAdapter(Integer.class, int.class);
        reuseAdapter(Long.class, long.class);
        reuseAdapter(Float.class, float.class);
        reuseAdapter(Double.class, double.class);
        reuseAdapter(Boolean.class, boolean.class);
        reuseAdapter(Character.class, char.class);
    }

    private void reuseAdapter(Class<?> sourceClass, Class<?> targetClass) {
        adapters.put(AdapterKey.of(targetClass), adapters.get(AdapterKey.of(sourceClass)));
    }

    public <T> Adapter<T> getAdapter(Class<T> cls) {
        //noinspection unchecked
        return (Adapter<T>) getAdapter(Type.from(cls));
    }

    public Adapter<?> getAdapter(Type type) {
        return getAdapter(type, Map.of(), new TmpAdapterEnv(this));
    }

    Adapter<?> getAdapter(Type type, Map<String, Object> attributes, AdapterEnv env) {
        var key = new AdapterKey(type, attributes);
        var adapter = adapters.get(key);
        if (adapter != null)
            return adapter;
        synchronized (this) {
            adapter = adapters.get(key);
            if (adapter != null)
                return adapter;
            for (AdapterFactory factory : adapterFactories) {
                if (factory.isSupported(type, Map.of())) {
                    adapter = factory.create(type, attributes);
                    env.addAdapter(adapter);
                    adapter.init(env);
                    publishAdapter(adapter);
                    return adapter;
                }
            }
            throw new IllegalStateException("No adapter registered for type: " + type + ". Make sure the involved classes are annotated with @Json");
        }
    }

    synchronized void addAdapterFactory(AdapterFactory factory) {
        var newFactories = new ArrayList<>(adapterFactories);
        newFactories.add(factory);
        adapterFactories = newFactories;
    }

    synchronized void addAdapter(Adapter<?>...adapters) {
        var env = new TmpAdapterEnv(this);
        for (Adapter<?> adapter : adapters) {
            env.addAdapter(adapter);
        }
        for (Adapter<?> adapter : adapters) {
            adapter.init(env);
        }
        publishAdapter(adapters);
    }

    private void publishAdapter(Adapter<?>...adapters) {
        var newAdapter = new HashMap<>(this.adapters);
        for (Adapter<?> adapter : adapters) {
            newAdapter.put(adapter.getKey(), adapter);
        }
        this.adapters = newAdapter;
    }

}
