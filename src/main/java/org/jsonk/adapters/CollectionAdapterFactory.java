package org.jsonk.adapters;

import org.jsonk.Adapter;
import org.jsonk.AdapterFactory;
import org.jsonk.Type;

import java.util.*;

/** @noinspection rawtypes*/
public class CollectionAdapterFactory implements AdapterFactory {

    private static final Set<Class<?>> supportedClasses = Set.of(
        List.class, ArrayList.class, LinkedList.class, Queue.class, Deque.class,
        Set.class, HashSet.class, NavigableSet.class, SortedSet.class, SequencedSet.class,
        Map.class, HashMap.class, LinkedHashMap.class, NavigableMap.class, SortedMap.class,
        SequencedMap.class, TreeMap.class
    );

    @Override
    public Adapter create(Type type, Map<String, Object> attributes) {
        var className = type.clazz().getName();
        return switch (className) {
            case "java.util.List", "java.util.ArrayList" ->
                new ArrayListAdapter<>(type, attributes);
            case "java.LinkedList", "java.util.Queue", "java.util.Deque" ->
                new LinkedListAdapter<>(type, attributes);
            case "java.util.Set", "java.util.HashSet" ->
                new HashSetAdapter<>(type, attributes);
            case "java.util.NavigableSet", "java.util.SortedSet", "java.util.SequencedSet",
                    "java.util.TreeSet" ->
                new TreeSetAdapter<>(type,attributes);
            case "java.util.Map", "java.util.HashMap" ->
                new HashMapAdapter<>(type, attributes);
            case "java.util.LinkedHashMap" -> new LinkedHashMapAdapter<>(type, attributes);
            case "java.util.NavigableMap", "java.util.SortedMap", "java.util.SequencedMap", "java.util.TreeMap" ->
                    new TreeMapAdapter<>(type, attributes);
            default -> throw new IllegalStateException("Unsupported collection type: " + className);
        };
    }

    @Override
    public boolean isSupported(Type type, Map attributes) {
        return supportedClasses.contains(type.clazz());
    }

}
