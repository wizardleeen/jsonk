package org.jsonk.adapters;

import org.jsonk.Adapter;
import org.jsonk.AdapterFactory;
import org.jsonk.util.Util;
import org.jsonk.Type;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DateAdapterFactory implements AdapterFactory {
    @Override
    public Adapter<?> create(Type type, Map<String, Object> attributes) {
        var df = Util.safeCall(attributes.get("dateFormat"), d -> new SimpleDateFormat((String) d));
        if (df == null)
            return new DateAdapter();
        else
            return new FormattedDateAdapter(df);
    }

    @Override
    public boolean isSupported(Type type, Map<String, Object> attributes) {
        return type.clazz() == Date.class;
    }
}
