package org.jsonk.element;

import javax.lang.model.element.Name;
import java.util.HashMap;
import java.util.Map;

public class MockName implements Name {

    public static MockName of(String s) {
        return names.computeIfAbsent(s, MockName::new);
    }

    private static final Map<String, MockName> names = new HashMap<>();

    private final String name;

    public MockName(String name) {
        this.name = name;
    }

    @Override
    public boolean contentEquals(CharSequence cs) {
        return name.contentEquals(cs);
    }

    @Override
    public int length() {
        return name.length();
    }

    @Override
    public char charAt(int index) {
        return name.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return name.subSequence(start, end);
    }

    @Override
    public String toString() {
        return name;
    }
}
