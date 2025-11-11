package org.jsonk;

import lombok.Getter;
import lombok.SneakyThrows;
import org.jsonk.adapters.ArrayListAdapter;
import org.jsonk.adapters.HashMapAdapter;
import org.jsonk.adapters.HashSetAdapter;
import org.jsonk.jdk.internal.DoubleToDecimal;
import org.jsonk.jdk.internal.FloatToDecimal;
import org.jsonk.util.StringUtil;

import java.io.Closeable;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

class JsonWriterImpl implements Closeable, JsonWriter {

    private final AdapterRegistry adapterRegistry;
    private final Writer writer;
    private final char[] buf = BufferPool.instance.take();
    private final int limit = buf.length;
    private int offset;

    public JsonWriterImpl(AdapterRegistry adapterRegistry, Writer writer) {
        this.adapterRegistry = adapterRegistry;
        this.writer = writer;
    }

    @Override
    @SneakyThrows
    public void write(char c) {
        require(1);
        buf[offset++] = c;
    }

    @Override
    @SneakyThrows
    public void write(String s, int begin, int len) {
        var buffered = Math.min(len, limit - offset);
        if (buffered > 0) {
            s.getChars(begin, begin + buffered, buf, offset);
            offset += buffered;
        }
        var remaining = len - buffered;
        if (remaining > 0) {
            flush();
            if (remaining > limit)
                writer.write(s, begin + buffered, remaining);
            else {
                s.getChars(begin + buffered, begin + len, buf, 0);
                offset += remaining;
            }
        }
    }

    @Override
    public void write(CharSequence csq, int offset, int length) {
        if (csq instanceof String s)
            write(s, offset, length);
        else {
            var buf = this.buf;
            var i = this.offset;
            for (int j = offset; j < offset + length; j++) {
                if (i == limit) {
                    flush();
                    i = offset;
                }
                buf[i++] = csq.charAt(j);
            }
            syncOffset(i);
        }
    }

    @Override
    public void write(CharSequence csq) {
        write(csq, 0, csq.length());
    }

    @Override
    @SneakyThrows
    public void write(String s) {
        write(s, 0, s.length());
    }

    @Override
    public void write(char[] c) {
        write(c, 0, c.length);
    }

    @Override
    @SneakyThrows
    public void write(char[] c, int begin, int len) {
        var available = limit - offset;
        if (len <= available) {
            System.arraycopy(c, begin, buf, offset, len);
            offset += len;
            return;
        }
        if (available > 0) {
            System.arraycopy(c, begin, buf, offset, available);
            offset = limit;
            flush();
            begin += available;
            len -= available;
        }
        if (len > limit)
            writer.write(c, begin, len);
        else {
            System.arraycopy(c, begin, buf, 0, len);
            offset += len;
        }
    }

    @Override
    public void writeLBrace() {
        write('{');
    }

    @Override
    public void writeRBrace() {
        write('}');
    }

    @Override
    public void writeLBracket() {
        write('[');
    }

    @Override
    public void writeRBracket() {
        write(']');
    }

    @Override
    public void writeChar(char c) {
        write('"');
        if (StringUtil.requireEscape(c))
            write(StringUtil.escapeChar(c));
        else
            write(c);
        write('"');
    }

    @Override
    public void writeStringOrNull(String s) {
        if (s == null)
            writeNull();
        else
            writeString(s);
    }

    @Override
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

    @Override
    public void writeComma() {
        write(',');
    }

    @Override
    public void writeColon() {
        write(':');
    }

    @Override
    public void writeByte(byte v) {
        writeInt(v);
    }

    @Override
    public void writeShort(short v) {
        writeInt(v);
    }

    private void syncOffset(int i) {
        this.offset = i;
    }

    @Override
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

    @Override
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

    @Override
    public void writeFloat(float v) {
        require(23);
        offset += FloatToDecimal.toDecimal(v, buf, offset);
    }

    @Override
    public void writeDouble(double v) {
        require(24);
        offset += DoubleToDecimal.toDecimal(v, buf, offset);
    }

    private void require(int len) {
        if (offset + len >= limit)
            flush();
    }

    @Override
    @SneakyThrows
    public void writeNull() {
        write(Chars.NULL);
    }

    protected <E> void writeElement(E element, Consumer<? super E> writeElement) {
        writeElement.accept(element);
    }

    @Override
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

    @Override
    public void writeBoolean(boolean b) {
        write(b ? Chars.TRUE : Chars.FALSE);
    }

    @Override
    public void writeObject(Object o) {
        //noinspection rawtypes
        Adapter adapter = adapterRegistry.getAdapter(Type.from(o.getClass()), Map.of(), () -> switch (o) {
            case Map<?, ?> ignored -> new HashMapAdapter<>(Type.from(o.getClass()), Map.of());
            case List<?> ignored -> new ArrayListAdapter<>(Type.from(o.getClass()), Map.of());
            case Set<?> ignored -> new HashSetAdapter<>(Type.from(o.getClass()), Map.of());
            default -> throw new IllegalStateException("No adapter found for class: " + o.getClass());
        });
        //noinspection unchecked
        writeObject(o, adapter);
    }

    @Override
    public <T> void writeObjectOrNull(T o, Adapter<T> adapter) {
        if (o == null)
            writeNull();
        else
            writeObject(o, adapter);
    }

    @Override
    public  <T> void writeObject(T o, Adapter<T> adapter) {
        writeValue(o, adapter);
    }

    @Override
    public <T> void writeValueOrNull(T o, Adapter<T> adapter) {
        if (o == null)
            writeNull();
        else
            writeValue(o, adapter);
    }

    @Override
    public <T> void writeValue(T o, Adapter<T> adapter) {
        adapter.toJson(o, this);
    }

    @Override
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
        if (offset > 0)
            flush();
        BufferPool.instance.ret(buf);
    }
}
