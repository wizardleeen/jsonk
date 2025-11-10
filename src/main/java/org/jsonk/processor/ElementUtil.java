package org.jsonk.processor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.util.function.BiConsumer;

public class ElementUtil {

    public static void forEachAttribute(AnnotationMirror annotation, BiConsumer<String, Object> action) {
        for (var entry : annotation.getElementValues().entrySet()) {
            var name = entry.getKey().getSimpleName().toString();
            var value = entry.getValue().getValue();
            action.accept(name, value);
        }
    }

    static String getName(Element element) {
        return element.getSimpleName().toString();
    }

    static boolean isPublic(Element element) {
        return element.getModifiers().contains(Modifier.PUBLIC);
    }

    static boolean isStatic(Element element) {
        return element.getModifiers().contains(Modifier.STATIC);
    }
}
