package org.jsonk.processor;

import java.util.Map;

public record PropertyConfig(
        String name,
        boolean includeNull,
        String dateTimeFormat
) {

    public static final PropertyConfig DEFAULT = new PropertyConfig(null, false, null);

    public Map<String, Object> getAttributes() {
        return dateTimeFormat != null ? Map.of("dateFormat", dateTimeFormat) : Map.of();
    }

}
