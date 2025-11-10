package org.jsonk.util;

import jakarta.annotation.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class Util {

    public static <T, R> List<R> map(List<T> list, Function<T, R> mapper) {
        var result = new ArrayList<R>();
        for (T t : list) {
            result.add(mapper.apply(t));
        }
        return result;
    }

    public static <T, R> List<R> map(T[] array, Function<T, R> mapper) {
        var result = new ArrayList<R>();
        for (T t : array) {
            result.add(mapper.apply(t));
        }
        return result;
    }

    public static <T, R> Set<R> mapToSet(Iterable<T> list, Function<T, R> mapper) {
        var result = new HashSet<R>();
        for (T t : list) {
            result.add(mapper.apply(t));
        }
        return result;
    }

    public static <T, R> Set<R> mapToSet(T[] array, Function<T, R> mapper) {
        var result = new HashSet<R>();
        for (T t : array) {
            result.add(mapper.apply(t));
        }
        return result;
    }

    public static <T> boolean anyMatch(Iterable<T> iterable, Predicate<T> test) {
        for (T t : iterable) {
            if (test.test(t))
                return true;
        }
        return false;
    }

    public static <T> boolean allMatch(Iterable<T> iterable, Predicate<T> test) {
        for (T t : iterable) {
            if (!test.test(t))
                return false;
        }
        return true;
    }

    public static <T> @Nullable T find(Iterable<T> iterable, Predicate<T> filter) {
        for (T t : iterable) {
            if (filter.test(t))
                return t;
        }
        return null;
    }

    public static <T> List<T> filter(Iterable<T> iterable, Predicate<T> filter) {
        var result = new ArrayList<T>();
        for (T t : iterable) {
            if (filter.test(t))
                result.add(t);
        }
        return result;
    }

    public static <T> @Nullable T findLast(Iterable<T> iterable, Predicate<T> filter) {
        T found = null;
        for (T t : iterable) {
            if (filter.test(t))
                found = t;
        }
        return found;
    }


    public void ensure() {

    }

    public static <K, V> Map<K, V> toMap(Iterable<K> keys, Iterable<V> values) {
        var map = new HashMap<K, V>();
        var it1 = keys.iterator();
        var it2 = values.iterator();
        while (it1.hasNext() && it2.hasNext())
            map.put(it1.next(), it2.next());
        if (it1.hasNext() || it2.hasNext())
            throw new IllegalArgumentException("Iterables have different lengths");
        return map;
    }

    public static <T1, T2> void biForEach(Iterable<T1> iterable1, Iterable<T2> iterable2, BiConsumer<T1, T2> action) {
        var it1 = iterable1.iterator();
        var it2 = iterable2.iterator();
        while (it1.hasNext() && it2.hasNext())
            action.accept(it1.next(), it2.next());
        if (it1.hasNext() || it2.hasNext())
            throw new IllegalArgumentException("Iterables have different lengths");
    }

    public static <T1, T2> boolean allMatch(Iterable<T1> iterable1, Iterable<T2> iterable2, BiPredicate<T1, T2> test) {
        var it1 = iterable1.iterator();
        var it2 = iterable2.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            if (!test.test(it1.next(), it2.next()))
                return false;
        }
        return !it1.hasNext() && !it2.hasNext();
    }

    public static <T, R> @Nullable R safeCall(T t, Function<T, R> call) {
        return t != null ? call.apply(t) : null;
    }

    public static <T> T findRequired(Iterable<T> iterable, Predicate<T> filter) {
        for (T t : iterable) {
            if (filter.test(t))
                return t;
        }
        throw new NoSuchElementException();
    }

}
