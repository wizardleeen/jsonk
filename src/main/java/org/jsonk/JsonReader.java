package org.jsonk;

import jakarta.annotation.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public interface JsonReader {
    void accept(char c);

    boolean skip(char c);

    boolean skipComma();

    boolean isRBrace();

    boolean is(char c);

    Object readValue();

    <T> T readObject(Adapter<T> adapter);

    void skipValue();

    <T> T readNull();

    String readStringOrNull();

    String readString();

    default int readName(char[][] names, int[] ordinals, long seed, long seed2) {
        return 0;
    }

    int readName(char[][] names, int[] ordinals, long seed);

    char readChar();

    boolean readBoolean();

    byte readByte();

    short readShort();

    int readInt();

    long readLong();

    float readFloat();

    double readDouble();

    Map<String, Object> readMap();

    <T> @Nullable T readNullable(Supplier<T> read);

    char current();

    boolean isEof();

    void next();

    void mark();

    void clearMark();

    void rollback();

    JsonParseException parseException(String message);

    void skipWhitespace();
}
