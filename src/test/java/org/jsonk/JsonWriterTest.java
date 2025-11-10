package org.jsonk;

import junit.framework.TestCase;

import java.io.StringWriter;
import java.util.function.Consumer;

public class JsonWriterTest extends TestCase {

    public void testWriteInt() {
        assertEquals("100", write(w -> w.writeInt(100)));
        assertEquals("-100", write(w -> w.writeInt(-100)));
    }

    public void testWriteLong() {
        assertEquals("100", write(w -> w.writeLong(100)));
        assertEquals("-100", write(w -> w.writeLong(-100)));
    }

    public void testWriteDouble() {
        var s = write(jw -> jw.writeDouble(100));
        assertEquals("100.0", s);
    }

    public void testWriteNegativeDouble() {
        var s = write(jw -> jw.writeDouble(-100.1));
        assertEquals("-100.1", s);
    }

    public void testWriteStringWithEscape() {
        var s = write(jw -> jw.writeString("a\tb\nc\u0000"));
        assertEquals("\"a\\tb\\nc\\u0000\"", s);
    }

    public void testWriteLargeString() {
        var s = "Jsonk,".repeat(1024);
        var s1 = write(w -> w.writeString(s));
        assertEquals("\"" +s + "\"", s1);
    }

    public void testWriteChar() {
        var s = write(jw -> jw.writeChar('a'));
        assertEquals("\"a\"", s);
    }

    public void testWriteCharWithEscape() {
        var s = write(jw -> jw.writeChar('\t'));
        assertEquals("\"\\t\"", s);
    }

    public void testWriteBoolean() {
        assertEquals("true", write(jw -> jw.writeBoolean(true)));
        assertEquals("false", write(jw -> jw.writeBoolean(false)));
    }

    private String write(Consumer<JsonWriter> action) {
        var w = new StringWriter();
        var jw = new JsonWriter(AdapterRegistry.instance, w);
        action.accept(jw);
        jw.flush();
        return w.toString();
    }

}