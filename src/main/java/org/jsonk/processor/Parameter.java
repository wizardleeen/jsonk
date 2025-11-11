package org.jsonk.processor;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.Map;

public record Parameter(
        String name,
        Element element,
        TypeMirror type,
        PropertyConfig propertyConfig
) implements Variable {

    public String getDateTimeFormat() {
        return propertyConfig.dateTimeFormat();
    }

    public String getDateFormatField() {
        return getDateTimeFormat() != null ? name + "Fmt" : null;
    }

    public Map<String, Object> getAttributes() {
        return propertyConfig.getAttributes();
    }

}
