package org.jsonk;

import jakarta.annotation.Nullable;
import lombok.SneakyThrows;
import org.jsonk.jdk.internal.FloatingDecimal;
import org.jsonk.util.PerfectHashTable;
import org.jsonk.util.StringUtil;

import java.io.Closeable;
import java.io.Reader;
import java.util.*;
import java.util.function.Supplier;

/** @noinspection types, DuplicatedCode */
class JsonReaderImpl implements JsonReader, Closeable {

    public static final char EOI = 0x1A;

    private final Reader reader;
    private char[] buf = BufferPool.instance.take();
    private int limit;
    private int offset;
    private int row;
    private int lineStartOffset;
    private int mark = -1;

    public JsonReaderImpl(Reader reader) {
        this.reader = reader;
        load();
    }

    @Override
    public void accept(char c) {
        if (current() != c)
            throw parseException("Expected '" + c + "' but found '" + current() + "'");
        next();
    }

    @Override
    public boolean skip(char c) {
        if (current() == c) {
            next();
            return true;
        }
        return false;
    }

    @Override
    public boolean skipComma() {
        return skip(',');
    }

    private boolean isLBrace() {
        return current() == '{';
    }

    @Override
    public  boolean isRBrace() {
        return current() == '}';
    }

    @Override
    public boolean is(char c) {
        return current() == c;
    }

    private  boolean isComma() {
        return current() == ',';
    }

    private boolean isQuote() {
        return current() == '"';
    }

    private boolean isColon() {
        return current() == ':';
    }

    private boolean isDigit() {
        return current() >= '0' && current() <= '9';
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    @Override
    public Object readValue() {
        return switch (current()) {
            case '{' -> readMap();
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-' -> readNumber();
            case '"' -> readString();
            case 't', 'f' -> readBoolean();
            case 'n' -> readNull();
            case '[' -> readList();
            default -> throw parseException("Unexpected value: " + current());
        };
    }

    @Override
    public <T> T readObject(Adapter<T> adapter) {
        if (current() == 'n')
            return readNull();
        else
            return adapter.fromJson(this);
    }

    @Override
    public void skipValue() {
        switch (current()) {
            case '{' -> skipObject();
            case '"' -> skipString();
            case '[' -> skipArray();
            case 't', 'f', 'n' -> skipKeyword();
            case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> skipNumber();
            default -> throw parseException("Unexpected value: " + current());
        }
    }

    private void skipNumber() {
        ensureAvailable(25);
        var buf = this.buf;
        var i = offset;
        if (buf[i] == '-')
            i++;
        if (!isDigit(buf[i]))
            throw parseException("Expected digit but found: " + current());
        do {
            i++;
        } while (isDigit(buf[i]));
        if (buf[i] == '.') {
            i++;
            if (!isDigit(buf[i]))
                throw parseException("Expected digit after decimal point but found: " + buf[i]);
            do {
                i++;
            } while (isDigit(buf[i]));
        }
        if (buf[i] == 'e' || buf[i] == 'E') {
            i++;
            if (buf[i] == '-')
                i++;
            while (isDigit(buf[i]))
                i++;
        }
        offset = i;
    }

    private void skipKeyword() {
        ensureAvailable(6);
        var i = offset;
        var buf = this.buf;
        while (buf[i] >= 'a' && buf[i] <= 'z')
            i++;
        syncOffset(i);
    }

    private void skipArray() {
        assert current() == '[';
        next();
        do {
            skipWhitespace();
            if (is(']'))
                break;
            skipValue();
        } while (skipComma());
        accept(']');
    }

    private void skipObject() {
        assert current() == '{';
        next();
        do {
            skipWhitespace();
            if (skip('}'))
                break;
            skipString();
            skipWhitespace();
            accept(':');
            skipWhitespace();
            skipValue();
            skipWhitespace();
        } while (skipComma());
    }

    private void skipString() {
        var i = offset;
        var e = limit;
        var buf = this.buf;
        if (buf[i++] != '"')
            throw parseException("Expected string");

        // Fast path
        while (i < e) {
            var c = buf[i];
            if (c == '\\' || c <= 0x1F)
                break;
            if (c == '"') {
                syncOffset(i);
                next();
                return;
            }
            i++;
        }

        // Slow path
        for(;;) {
            if (i == e) {
                syncOffset(i);
                load();
                i = offset;
                e = limit;
            }
            var c = buf[i];
            switch (c) {
                case '"' -> {
                    syncOffset(i);
                    next();
                    return;
                }
                case '\\' -> {
                    i++;
                    if (i == e) {
                        syncOffset(i);
                        load();
                        i = offset;
                        e = limit;
                    }
                    switch (buf[i++]) {
                        case 't', 'n', 'r', '/', '\\', '"' -> {}
                        case 'u' -> {
                            for (int j = 0; j < 4; j++) {
                                if (i == e) {
                                    syncOffset(i);
                                    load();
                                    i = offset;
                                    e = limit;
                                }
                                var c1 = buf[i++];
                                var hex = hexDigit(c1);
                                if (hex == -1)
                                    throw parseException("Invalid unicode escape sequence");
                            }
                        }
                        default -> throw parseException("Invalid escape character: " + current());
                    }
                }
                case '\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007',
                        '\b', '\n', '\u000B', '\f', '\r',
                        '\u000E', '\u000F', '\u0010', '\u0011', '\u0012', '\u0013',
                        '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019',
                        '\u001B', '\u001C', '\u001D', '\u001E', '\u001F' -> {
                    syncOffset(i);
                    throw parseException("Illegal character in string: U+" + String.format("%04X", (int) c));
                }
                case EOI -> {
                    syncOffset(i);
                    throw parseException("Unterminated string literal");
                }
                default -> i++;
            }
        }
    }

    @Override
    public <T> T readNull() {
        ensureAvailable(5);
        var buf = this.buf;
        var i = offset;
        var e = limit;
        if (e - i >= 5 && buf[i] == 'n' && buf[i + 1] == 'u' && buf[i + 2] == 'l' && buf[i + 3] == 'l' && !isIdentifierChar(buf[i + 4])) {
            syncOffset(i + 4);
            return null;
        } else {
            throw parseException("Expected null but found: " + current());
        }
    }

    @Override
    public String readStringOrNull() {
        if (is('n'))
            return readNull();
        else
            return readString();
    }

    @Override
    public String readString() {
        var i = offset;
        var e = limit;
        var buf = this.buf;
        if (buf[i++] != '"')
            throw parseException("Expected string");
        var start = i;

        // Fast path
        while (i < e) {
            var c = buf[i];
            if (c == '\\' || c <= 0x1F)
                break;
            if (c == '"') {
                syncOffset(i);
                var s = new String(buf, start, i - start);
                next();
                return s;
            }
            i++;
        }

        // Slow path
        var marked = mark == -1;
        if (marked)
            mark();
        var shifts = 0;
        var segStart = 0;
        for(;;) {
            if (i == e) {
                syncOffset(i);
                load();
                var d = i - offset;
                start -= d;
                segStart -= d;
                i = offset;
                e = limit;
            }
            var c = buf[i];
            switch (c) {
                case '"' -> {
                    if (shifts > 0 && i > segStart)
                        System.arraycopy(buf, segStart, buf, segStart - shifts, i - segStart);
                    var end = i - shifts;
                    var s = toString(start, end - start);
                    if (marked)
                        clearMark();
                    else if (mark != -1 && shifts > 0) {
                        // Restore the string
                        StringUtil.escape(buf, start, end - start);
                    }
                    syncOffset(i);
                    next();
                    return s;
                }
                case '\\' -> {
                    if (shifts > 0 && i - segStart > 0)
                        System.arraycopy(buf, segStart, buf, segStart - shifts, i - segStart);
                    var charStart = i++;
                    if (i == e) {
                        syncOffset(i);
                        load();
                        var d = i - offset;
                        start -= d;
                        i = offset;
                        e = limit;
                    }
                    var ch = switch (buf[i++]) {
                        case 't' -> '\t';
                        case 'n' -> '\n';
                        case 'r' -> '\r';
                        case '/' -> '/';
                        case '\\' -> '\\';
                        case '"' -> '"';
                        case 'u' -> {
                            var code = 0;
                            for (int j = 0; j < 4; j++) {
                                if (i == e) {
                                    syncOffset(i);
                                    load();
                                    var d = i - offset;
                                    start -= d;
                                    i = offset;
                                    e = limit;
                                }
                                var c1 = buf[i++];
                                var hex = hexDigit(c1);
                                if (hex == -1)
                                    throw parseException("Invalid unicode escape sequence");
                                code = code << 4 | hex;
                            }
                            yield code;
                        }
                        default -> throw parseException("Invalid escape character: " + current());
                    };
                    writeChar(ch, charStart - shifts);
                    shifts += i - charStart - Character.charCount(ch);
                    segStart = i;
                }
                case '\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007',
                     '\b', '\n', '\u000B', '\f', '\r',
                     '\u000E', '\u000F', '\u0010', '\u0011', '\u0012', '\u0013',
                     '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019',
                     '\u001B', '\u001C', '\u001D', '\u001E', '\u001F' -> {
                    syncOffset(i);
                    throw parseException("Illegal character in string: U+" + String.format("%04X", (int) c));
                }
                case EOI -> {
                    syncOffset(i);
                    throw parseException("Unterminated string literal");
                }
                default -> i++;
            }
        }
    }

    private static final long FNV_64_PRIME = 0x100000001b3L;

    @Override
    public int readName(char[][] names, int[] ordinals, long seed) {
        var buf = this.buf;
        var i = offset;
        var e = limit;
        var h = seed;
        if (buf[i++] != '"')
            throw parseException("Expected string for name but found: " + current());
        var begin = i;
        var hasEscapes = false;
        out:
        {
            while (i < e) {
                var ch = buf[i];
                if (ch == '\\' | ch <= 0x1F | ch == '"') {
                    if (ch == '"')
                        break out;
                    else
                        break;
                }
                i++;
                h = (h ^ ch) * FNV_64_PRIME;
            }
            var marked = mark == -1;
            if (marked)
                mark();
            for (;;) {
                if (i == e) {
                    offset = i;
                    load();
                    i = offset;
                    e = limit;
                }
                var c = buf[i];
                if (c == '\\') {
                    hasEscapes = true;
                    i++;
                    var c1 = buf[i++];
                    switch (c1) {
                        case 'b' -> h = (h ^ '\b') * FNV_64_PRIME;
                        case 'n' -> h = (h ^ '\n') * FNV_64_PRIME;
                        case 'r' -> h = (h ^ '\r') * FNV_64_PRIME;
                        case 't' -> h = (h ^ '\t') * FNV_64_PRIME;
                        case 'f' -> h = (h ^ '\f') * FNV_64_PRIME;
                        case '"' -> h = (h ^ '"') * FNV_64_PRIME;
                        case '\\' -> h = (h ^ '\\') * FNV_64_PRIME;
                        case '/' -> h = (h ^ '/') * FNV_64_PRIME;
                        case 'u' -> {
                            int code = 0;
                            for (int j = 0; j < 4; j++) {
                                if (i == e) {
                                    offset = i;
                                    load();
                                    i = offset;
                                    e = limit;
                                }
                                var c2 = buf[i++];
                                var hex = hexDigit(c2);
                                if (hex == -1)
                                    throw parseException("Invalid unicode escape sequence");
                                code = code << 4 | hex;
                            }
                            if (Character.isBmpCodePoint(code)) {
                                h = (h ^ code) * FNV_64_PRIME;
                            } else {
                                var hi = Character.highSurrogate(code);
                                var lo = Character.lowSurrogate(code);
                                h = (h ^ hi) * FNV_64_PRIME;
                                h = (h ^ lo) * FNV_64_PRIME;
                            }
                        }
                        default -> throw parseException("Invalid escape character: " + current());
                    }
                } else if (c == '"') {
                    if (marked)
                        clearMark();
                    break out;
                } else {
                    h = (h ^ c) * FNV_64_PRIME;
                    i++;
                }
            }
        }
        var index = PerfectHashTable.index(h, names.length - 1);
        var target = names[index];
        var r = target != null && (hasEscapes ? match(begin, i, target) : bufEquals(begin, i, target));
        offset = i + 1;
        if (offset == e)
            load();
        return r ? ordinals[index] : -1;
    }

    private boolean bufEquals(int begin, int end, char[] target) {
        return Arrays.equals(buf, begin, end, target, 0, target.length);
    }

    private boolean match(int begin, int end, char[] target) {
        var buf = this.buf;
        int i, j;
        for (i = begin, j = 0; i < end && j < target.length; j++) {
            var ch = buf[i++];
            var ch1 = target[j];
            if (ch == '\\') {
                switch (buf[i++]) {
                    case 'b' -> {
                        if (ch1 != '\b')
                            return false;
                    }
                    case 'r' -> {
                        if (ch1 != '\r')
                            return false;
                    }
                    case 'n' -> {
                        if (ch1 != '\n')
                            return false;
                    }
                    case 't' -> {
                        if (ch1 != '\t')
                            return false;
                    }
                    case 'f' -> {
                        if (ch1 != '\f')
                            return false;
                    }
                    case '"' -> {
                        if (ch1 != '"')
                            return false;
                    }
                    case '\\' -> {
                        if (ch1 != '\\')
                            return false;
                    }
                    case '/' -> {
                        if (ch1 != '/')
                            return false;
                    }
                    case 'u' -> {
                        int code = buf[i++];
                        for (int k = 0; k < 3; k++) {
                            code = code << 4 | hexDigit(buf[i++]);
                        }
                        if (Character.isBmpCodePoint(code)) {
                            if (ch1 != code)
                                return false;
                        } else {
                            j++;
                            if (Character.highSurrogate(code) != ch1 || j >= target.length || target[j] != Character.lowSurrogate(code))
                                return false;
                        }
                    }
                    default -> throw parseException("Invalid escape character: " + current());
                };
            }
            else {
                if (ch1 != ch)
                    return false;
            }
        }
        return i == end && j == target.length;
    }

    private void syncOffset(int i) {
        offset = i;
    }

    @Override
    public char readChar() {
        ensureAvailable(9);
        var buf = this.buf;
        var i = offset;
        if (buf[i++] != '"')
            throw parseException("Expected string but found: " + current());
        char c = buf[i++];
        if (c == '\\') {
            //noinspection DuplicatedCode
            var c2 = switch (buf[i++]) {
                case 't' -> '\t';
                case 'n' -> '\n';
                case 'r' -> '\r';
                case 'u' -> {
                    var code = 0;
                    for (int k = 0; k < 4; k++) {
                        var c1 = buf[i++];
                        var hex = hexDigit(c1);
                        if (hex == -1)
                            throw parseException("Invalid unicode escape sequence");
                        code = code << 4 | hex;
                    }
                    yield code;
                }
                default -> throw parseException("Invalid escape character: " + current());
            };
            if (c2 > Character.MAX_VALUE)
                throw parseException("Invalid char value");
            c = (char) c2;
        }
        if (buf[i++] != '"')
            throw parseException("Expected closing quote for char but found: " + buf[i]);
        syncOffset(i);
        return c;
    }

    @Override
    public boolean readBoolean() {
        ensureAvailable(6);
        var buf = this.buf;
        var i = offset;
        var e = limit;
        if (e - i >= 6 && buf[i] == 'f') {
            if (buf[i + 1] == 'a' && buf[i + 2] == 'l' && buf[i + 3] == 's' && buf[i + 4] == 'e' && !isIdentifierChar(buf[i + 5])) {
                syncOffset(i + 5);
                return false;
            }
        } else if (e - i >= 5 && buf[i] == 't') {
            if (buf[i + 1] == 'r' && buf[i + 2] == 'u' && buf[i + 3] == 'e' && !isIdentifierChar(buf[i + 4])) {
                syncOffset(i + 4);
                return true;
            }
        }
        throw parseException("Invalid boolean value");
    }

    private boolean isIdentifierChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || isDigit(c) || c == '_';
    }

    @Override
    public byte readByte() {
        var i = readInt();
        if (i < Byte.MIN_VALUE || i > Byte.MAX_VALUE)
            throw parseException("Value out of range for byte: " + i);
        return (byte) i;
    }

    @Override
    public short readShort() {
        var i = readInt();
        if (i < Short.MIN_VALUE || i > Short.MAX_VALUE)
            throw parseException("Value out of range for short: " + i);
        return (short) i;
    }

    @Override
    public int readInt() {
        ensureAvailable(12);
        var buf = this.buf;
        int i = offset;
        var neg = false;
        if (buf[i] == '-') {
            i++;
            neg = true;
        }
        if (!isDigit(buf[i]))
            throw parseException("Expected digit but found: " + buf[i]);
        var v = -(buf[i++] - '0');
        var limit = Integer.MIN_VALUE;
        var multmin = limit / 10;
        while (isDigit(buf[i])) {
            if (v < multmin)
                throw parseException("Integer value out of range");
             v = v * 10;
             var d = buf[i++] - '0';
             if (v < limit + d)
                 throw parseException("Integer value out of range");
             v -= d;
        }
        if (buf[i] == '.') {
            i++;
            if (!isDigit(buf[i]))
                throw parseException("Expected digit after decimal point but found: " + buf[i]);
            do {
                i++;
            } while (isDigit(buf[i]));
        }
        syncOffset(i);
        if (!neg) {
            if (v == Integer.MIN_VALUE)
                throw parseException("Integer value out of range");
            return -v;
        } else {
            return v;
        }
    }

    @Override
    public long readLong() {
        ensureAvailable(21);
        var buf = this.buf;
        var i = offset;
        var neg = false;
        if (buf[i] == '-') {
            i++;
            neg = true;
        }
        if (!isDigit(buf[i]))
            throw parseException("Expected digit but found: " + buf[i]);
        long v = -(buf[i++] - '0');
        var limit = Long.MIN_VALUE;
        var multimin = limit / 10;
        while (isDigit(buf[i])) {
            if (v < multimin)
                throw parseException("Integer value out of range");
            v = v * 10;
            var d = buf[i++] - '0';
            if (v < limit + d)
                throw parseException("Integer value out of range");
            v -= d;
        }
        if (buf[i] == '.') {
            i++;
            if (!isDigit(buf[i]))
                throw parseException("Expected digit after decimal point but found: " + buf[i]);
            do {
                i++;
            } while (isDigit(buf[i]));
        }
        syncOffset(i);
        if (!neg) {
            if (v == Long.MIN_VALUE)
                throw parseException("Integer value out of range");
            return -v;
        } else {
            return v;
        }
    }

    @Override
    public float readFloat() {
        var i = scanFloat();
        return FloatingDecimal.parseFloat(buf, i, offset - i);
    }

    @Override
    public double readDouble() {
        var i = scanFloat();
        return FloatingDecimal.parseDouble(buf, i, offset - i);
    }

    private int scanFloat() {
        ensureAvailable(25);
        var buf = this.buf;
        var i = offset;
        var start = i;
        if (buf[i] == '-')
            i++;
        if (!isDigit(buf[i]))
            throw parseException("Expected digit in exponent but found: " + buf[i]);
        do {
            i++;
        } while (isDigit(buf[i]));
        if (buf[i++] == '.') {
            if (!isDigit(buf[i]))
                throw parseException("Expected digit in exponent but found: " + buf[i]);
            do {
                i++;
            } while (isDigit(buf[i]));
        }
        if (buf[i] == 'e' || buf[i] == 'E') {
            i++;
            if (buf[i] == '-')
                i++;
            if (!isDigit(buf[i]))
                throw parseException("Expected digit in exponent but found: " + buf[i]);
            do {
                i++;
            } while (isDigit(buf[i]));
        }
        syncOffset(i);
        return start;
    }

    Number readNumber() {
        ensureAvailable(25);
        var buf = this.buf;
        var i = offset;
        var start = i;
        var neg = false;
        if (buf[i] == '-') {
            neg = true;
            i++;
        }
        if (!isDigit(buf[i]))
            throw parseException("Expected digit but found: " + current());
        var v = 0L;
        do {
            v = v - (buf[i++] - '0');
        } while (isDigit(buf[i]));
        var isFloat = false;
        if (buf[i] == '.') {
            isFloat = true;
            if (!isDigit(buf[i]))
                throw parseException("Expected digit after decimal point but found: " + buf[i]);
            do {
                i++;
            } while (isDigit(buf[i]));
        }
        if (buf[i] == 'e' || buf[i] == 'E') {
            isFloat = true;
            i++;
            if (buf[i] == '-')
                i++;
            while (isDigit(buf[i]))
                i++;
        }
        offset = i;
        if (isFloat)
            return toDouble(start);
        else {
            if (v == Long.MIN_VALUE && !neg)
                throw parseException("Integer value out of range");
            v = neg ? v : -v;
            if (v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE)
                return (int) v;
            else
                return v;
        }
    }

    @Override
    public Map<String, Object> readMap() {
        if (!is('{'))
            throw parseException("Expected '{' but found: " + current());
        var map = new HashMap<String, Object>();
        next();
        do {
            skipWhitespace();
            if (is('}'))
                break;
            var key = readString();
            skipWhitespace();
            accept(':');
            skipWhitespace();
            map.put(key, readValue());
            skipWhitespace();
        } while (skipComma());
        accept('}');
        return map;
    }

    @Override
    public <T> @Nullable T readNullable(Supplier<T> read) {
        if (is('n'))
            return readNull();
        else
            return read.get();
    }

    private List<Object> readList() {
        assert is('[');
        var list = new ArrayList<>();
        next();
        do {
            skipWhitespace();
            if (is(']'))
                break;
            list.add(readValue());
            skipWhitespace();
        } while (skipComma());
        accept(']');
        return list;
    }

    @Override
    public char current() {
        return buf[offset];
    }

    @Override
    public boolean isEof() {
        return current() == EOI;
    }

    @Override
    public void next() {
        if (isEof())
            throw new IllegalStateException("EOF");
        if (++offset == limit)
            load();
    }

    private void ensureAvailable(int size) {
        while (available() < size && !isReaderExhausted()) {
            load();
        }
    }

    private boolean isReaderExhausted() {
        return buf[limit - 1] == EOI;
    }

    private int available() {
        return limit - offset;
    }

    private void compact() {
        var begin = mark != -1 ? mark : offset;
        if (begin >= buf.length / 4) {
            Jsonk.compacts++;
            System.arraycopy(buf, begin, buf, 0, limit - begin);
            offset -= begin;
            limit -= begin;
            lineStartOffset -= begin;
            if (mark != -1)
                mark = 0;
        }
    }

    @SneakyThrows
    private void load() {
        Jsonk.fetches++;
        var begin = mark != -1 ? mark : offset;
        if (limit == begin) {
            limit = 0;
            offset = 0;
            if (mark != -1)
                mark = 0;
        } else {
            compact();
            if (limit >= buf.length / 4 * 3) {
                if (buf.length == BufferPool.BUF_SIZE)
                    BufferPool.instance.ret(buf);
                Jsonk.grows++;
                buf = Arrays.copyOf(buf, buf.length * 2);
            }
        }
        var n = reader.read(buf, limit, buf.length - limit);
        if (n == -1)
            buf[limit++] = EOI;
        else
           limit += n;
    }

    private int row() {
        return row;
    }

    private int column() {
        return offset - lineStartOffset;
    }

    @Override
    public void mark() {
        mark = offset;
    }

    @Override
    public void clearMark() {
        assert mark != -1;
        mark = -1;
    }

    @Override
    public void rollback() {
        assert mark != -1;
        offset = mark;
        mark = -1;
    }

    public String toString(int start, int len) {
        return new String(buf, start, len);
    }

    public double toDouble(int start) {
        return FloatingDecimal.parseDouble(buf, start, offset - start);
    }

    private static int hexDigit(char c) {
        if (c >= '0' && c <= '9') return c - '0';
        if (c >= 'a' && c <= 'f') return c - 'a' + 10;
        if (c >= 'A' && c <= 'F') return c - 'A' + 10;
        return -1;
    }

    public void writeChar(int ch, int start) {
        Character.toChars(ch, buf, start);
    }

    @Override
    public JsonParseException parseException(String message) {
        return new JsonParseException(message, row + 1, column() + 1);
    }

    @Override
    public void skipWhitespace() {
        var buf = this.buf;
        var i = offset;
        var e = limit;
        for (;;) {
            if (i == e) {
                offset = i;
                load();
                i = offset;
                e = limit;
            }
            switch (buf[i]) {
                case ' ', '\t', '\r' -> i++;
                case '\n' -> {
                    i++;
                    row++;
                    lineStartOffset = i;
                }
                default -> {
                    syncOffset(i);
                    return;
                }
            }
        }
    }

    @Override
    public void close() {
        if (buf.length == BufferPool.BUF_SIZE)
            BufferPool.instance.ret(buf);
    }
}
