package org.jsonk;

import lombok.SneakyThrows;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public class Jsonk {

    private static final AdapterRegistry registry = AdapterRegistry.instance;
    public static int fetches;
    public static int grows;
    public static int compacts;

    public static String toJson(Object o) {
        return toJson(o, Option.DEFAULT);
    }

    public static String toJson(Object o, Option option) {
        var w = new StringWriter();
        toJson(o, w, option);
        return w.toString();
    }

    @SneakyThrows
    public static void toJson(Object o, Writer writer) {
        toJson(o, writer, Option.DEFAULT);
    }

    @SneakyThrows
    public static void toJson(Object o, Writer writer, Option option) {
        try (var w = option.indent ? new IndentJsonWriter(registry, writer) : new JsonWriter(registry, writer)) {
            w.writeValue(o);
            w.flush();
        }
    }

    public static <T> T fromJson(Class<T> clazz, String json) {
        return fromJson(clazz, new StringReader(json));
    }

    public static <T> T fromJson(Class<T> clazz, Reader reader) {
        //noinspection unchecked
        return (T) fromJson(Type.from(clazz), reader);
    }

    public static Object fromJson(Type type, String json) {
        return fromJson(type, new StringReader(json));
    }

    public static Object fromJson(Type type, Reader reader) {
        try (var jsonReader = new JsonReader(reader)) {
            jsonReader.skipWhitespace();
            return jsonReader.readObject(registry.getAdapter(type));
        }
    }

}
