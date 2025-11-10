package org.jsonk.element;

import java.util.function.Consumer;

public class MockElementUtil {

    public static void traverse(MockElement element, Consumer<MockElement> action) {
        action.accept(element);
        element.forEachChild(c -> traverse(c, action));
    }

}
