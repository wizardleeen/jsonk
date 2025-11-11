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
        try (var w = option.indent ? new IndentJsonWriter(registry, writer) : new JsonWriterImpl(registry, writer)) {
            w.writeValue(o);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return fromJson(new StringReader(json), clazz);
    }

    public static <T> T fromJson(Reader reader, Class<T> clazz) {
        //noinspection unchecked
        return (T) fromJson(reader, Type.from(clazz));
    }

    public static Object fromJson(String json, Type type) {
        return fromJson(new StringReader(json), type);
    }

    public static Object fromJson(Reader reader, Type type) {
        try (var jsonReader = new JsonReaderImpl(reader)) {
            jsonReader.skipWhitespace();
            return jsonReader.readObject(registry.getAdapter(type));
        }
    }

}
