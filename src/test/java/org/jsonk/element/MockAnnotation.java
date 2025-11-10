package org.jsonk.element;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MockAnnotation implements AnnotationMirror {

    private final MockDeclaredType annotationType;
    private final Map<String, ? extends AnnotationValue> elementValues;
    private Map<ExecutableElement, AnnotationValue> values;

    public MockAnnotation(MockDeclaredType annotationType, Map<String, ? extends AnnotationValue> elementValues) {
        this.annotationType = annotationType;
        this.elementValues = new HashMap<>(elementValues);
    }

    @Override
    public MockDeclaredType getAnnotationType() {
        return annotationType;
    }

    @Override
    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValues() {
        if (values == null) {
            // Lazy init to get around the circular resolution issue during build
            values = new HashMap<>();
            var clazz = (MockClass) annotationType.asElement();
            for (MockElement member : clazz.getMembers()) {
                if (member instanceof ExecutableElement e) {
                    values.put(e, elementValues.get(e.getSimpleName().toString()));
                }
            }
        }
        return Collections.unmodifiableMap(values);
    }
}
