package org.jsonk;

import lombok.SneakyThrows;

public interface JsonWriter {
    
    void write(char c);

    void write(String s, int begin, int len);

    void write(CharSequence csq, int offset, int length);

    void write(CharSequence csq);

    void write(String s);

    void write(char[] c);

    @SneakyThrows
    void write(char[] c, int begin, int len);

    void writeLBrace();

    void writeRBrace();

    void writeLBracket();

    void writeRBracket();

    void writeChar(char c);

    void writeStringOrNull(String s);

    void writeString(String s);

    void writeComma();

    void writeColon();

    void writeByte(byte v);

    void writeShort(short v);

    void writeInt(int v);

    void writeLong(long v);

    void writeFloat(float v);

    void writeDouble(double v);

    void writeNull();

    void writeValue(Object o);

    void writeBoolean(boolean b);

    void writeObject(Object o);

    <T> void writeObjectOrNull(T o, Adapter<T> adapter);

    <T> void writeObject(T o, Adapter<T> adapter);

    <T> void writeValueOrNull(T o, Adapter<T> adapter);

    <T> void writeValue(T o, Adapter<T> adapter);

    void flush();

    Appendable getAppendable();

}
