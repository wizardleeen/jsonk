package org.jsonk;

import lombok.Getter;
import lombok.SneakyThrows;
import org.jsonk.jdk.internal.DoubleToDecimal;
import org.jsonk.jdk.internal.FloatToDecimal;
import org.jsonk.util.BufferPool;
import org.jsonk.util.Chars;
import org.jsonk.util.StringUtil;

import java.io.Closeable;
import java.io.Writer;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class JsonWriter implements Closeable {


    private final AdapterRegistry adapterRegistry;
    private final Writer writer;
    private final char[] buf = BufferPool.instance.take();
    private final int cap = buf.length;
    private int offset;
    private final Map<Object, Object> visiting = new IdentityHashMap<>();

    public JsonWriter(AdapterRegistry adapterRegistry, Writer writer) {
        this.adapterRegistry = adapterRegistry;
        this.writer = writer;
    }

    @SneakyThrows
    public void write(char c) {
        require(1);
        buf[offset++] = c;
    }

    @SneakyThrows
    public void write(String s, int begin, int len) {
        var buffered = Math.min(len, cap - offset);
        if (buffered > 0) {
            s.getChars(begin, begin + buffered, buf, offset);
            offset += buffered;
        }
        var remaining = len - buffered;
        if (remaining > 0) {
            flush();
            if (remaining > cap)
                writer.write(s, begin + buffered, remaining);
            else {
                s.getChars(begin + buffered, begin + len, buf, 0);
                offset += remaining;
            }
        }
    }

    public void write(CharSequence csq, int offset, int length) {
        if (csq instanceof String s)
            write(s, offset, length);
        else {
            var buf = this.buf;
            var i = this.offset;
            for (int j = offset; j < offset + length; j++) {
                if (i == cap) {
                    flush();
                    i = offset;
                }
                buf[i++] = csq.charAt(j);
            }
            syncOffset(i);
        }
    }

    public void write(CharSequence csq) {
        write(csq, 0, csq.length());
    }

    @SneakyThrows
    public void write(String s) {
        write(s, 0, s.length());
    }

    @SneakyThrows
    public void write(char[] c) {
        var len = c.length;
        var buffered = Math.min(len, cap - offset);
        if (buffered > 0) {
            System.arraycopy(c, 0, buf, offset, buffered);
            offset += buffered;
        }
        var remaining = len - buffered;
        if (remaining > 0) {
            flush();
            if (len > cap)
                writer.write(c, buffered, remaining);
            else {
                System.arraycopy(c, buffered, buf, 0, remaining);
                offset += remaining;
            }
        }
    }

    public void writeLBrace() {
        write('{');
    }

    public void writeRBrace() {
        write('}');
    }

    public void writeLBracket() {
        write('[');
    }

    public void writeRBracket() {
        write(']');
    }

    public void writeChar(char c) {
        write('"');
        if (StringUtil.requireEscape(c))
            write(StringUtil.escapeChar(c));
        else
            write(c);
        write('"');
    }

    public void writeStringOrNull(String s) {
        if (s == null)
            writeNull();
        else
            writeString(s);
    }

    @SneakyThrows
    public void writeString(String s) {
        write('"');
        var len = s.length();
        var last = 0;
        for (int i = 0; i < len; i++) {
            var c = s.charAt(i);
            if (StringUtil.requireEscape(c)) {
                if (last < i) {
                    write(s, last, i - last);
                }
                write(StringUtil.escapeChar(c));
                last = i + 1;
            }
        }
        if (last < len)
            write(s, last, len - last);
        write('"');
    }

    @SneakyThrows
    public void writeName(String name) {
        writeString(name);
        writeColon();
    }

    public void writeComma() {
        write(',');
    }

    public void writeColon() {
        write(':');
    }

    public void writeByte(byte v) {
        writeInt(v);
    }

    public void writeShort(short v) {
        writeInt(v);
    }

    private void syncOffset(int i) {
        this.offset = i;
    }

    public void writeInt(int v) {
        if (v == 0) {
            write('0');
            return;
        }
        var len = len(v);
        require(len);
        var x = -v;
        if (v < 0) {
            x = v;
            buf[offset] = '-';
        }
        var i = offset + len - 1;
        while (x < 0) {
            buf[i--] = (char) ('0' - x % 10);
            x = x / 10;
        }
        offset += len;
    }

    public void writeLong(long v) {
        if (v == 0) {
            write('0');
            return;
        }
        var len = len(v);
        require(len);
        var x = -v;
        if (v < 0) {
            x = v;
            buf[offset] = '-';
        }
        var i = offset + len - 1;
        while (x < 0) {
            buf[i--] = (char) ('0' - x % 10);
            x /= 10;
        }
        offset += len;
    }

    public void writeFloat(float v) {
        require(23);
        offset += FloatToDecimal.toDecimal(v, buf, offset);
    }

    public void writeDouble(double v) {
        require(24);
        offset += DoubleToDecimal.toDecimal(v, buf, offset);
    }

    private void require(int len) {
        if (offset + len >= cap)
            flush();
    }

    @SneakyThrows
    public void writeNull() {
        write(Chars.NULL);
    }

    protected <E> void writeElement(E element, Consumer<? super E> writeElement) {
        writeElement.accept(element);
    }

    public void writeValue(Object o) {
        switch (o) {
            case Boolean b -> writeBoolean(b);
            case Byte b -> writeByte(b);
            case Short s -> writeShort(s);
            case Integer i -> writeInt(i);
            case Long l -> writeLong(l);
            case Float f -> writeFloat(f);
            case Double d -> writeDouble(d);
            case String s -> writeString(s);
            case null -> writeNull();
            default -> writeObject(o);
        }
    }

    public void writeBoolean(boolean b) {
        write(b ? Chars.TRUE : Chars.FALSE);
    }

    public void writeObject(Object o) {
        //noinspection rawtypes
        Adapter adapter = adapterRegistry.getAdapter(o.getClass());
        //noinspection unchecked
        writeObject(o, adapter);
    }

    public <T> void writeObjectOrNull(T o, Adapter<T> adapter) {
        if (o == null)
            writeNull();
        else
            writeObject(o, adapter);
    }

    public  <T> void writeObject(T o, Adapter<T> adapter) {
        if (visiting.put(o, o) != null)
            throw new JsonWriteException("Cyclic reference detected for object: " + o);
        writeValue(o, adapter);
        visiting.remove(o);
    }

    public <T> void writeValueOrNull(T o, Adapter<T> adapter) {
        if (o == null)
            writeNull();
        else
            writeValue(o, adapter);
    }

    public <T> void writeValue(T o, Adapter<T> adapter) {
        adapter.toJson(o, this);
    }

    @SneakyThrows
    public void flush() {
        writer.write(buf, 0, offset);
        offset = 0;
    }

    private static int len(int v) {
        var len = v >>> 31;
        while (v != 0) {
            v /= 10;
            len++;
        }
        return len;
    }

    private static int len(long v) {
        var len = (int) (v >>> 63);
        while (v != 0) {
            v /= 10;
            len++;
        }
        return len;
    }

    @Getter
    private final Appendable appendable = new Appendable() {
        @Override
        public Appendable append(CharSequence csq) {
            write(csq);
            return this;
        }

        @Override
        public Appendable append(CharSequence csq, int start, int end) {
            write(csq, start, end);
            return this;
        }

        @Override
        public Appendable append(char c) {
            write(c);
            return this;
        }
    };


    @Override
    public void close() {
        BufferPool.instance.ret(buf);
    }
}
