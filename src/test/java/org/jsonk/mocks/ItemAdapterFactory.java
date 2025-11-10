package org.jsonk.mocks;

import java.util.Map;
import org.jsonk.Adapter;
import org.jsonk.AdapterFactory;
import org.jsonk.Type;

public class ItemAdapterFactory implements AdapterFactory {

    public Adapter<?> create(Type type, Map<String, Object> attributes) {
        return new org.jsonk.mocks.ItemAdapter<>(type);
    }

    public boolean isSupported(Type type, Map<String, Object> attributes) {
        return type.clazz() == org.jsonk.mocks.Item.class;
    }

}
