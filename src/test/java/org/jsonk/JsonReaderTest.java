package org.jsonk;

import junit.framework.TestCase;

import java.io.StringReader;

/** @noinspection resource*/
public class JsonReaderTest extends TestCase {

    public void testReadByte() {
        assertEquals((byte) 100, createReader("100").readByte());
    }

    public void testReadShort() {
        assertEquals((short) 100, createReader("100").readShort());
    }

    public void testReadInt() {
        assertEquals(100, createReader("100").readInt());
    }

    public void testNegativeInt() {
        assertEquals(-100, createReader("-100").readInt());
    }

    public void testReadLong() {
        assertEquals(100L, createReader("100").readLong());
    }

    public void testReadMaxLong() {
        assertEquals(Long.MAX_VALUE, createReader(Long.toString(Long.MAX_VALUE)).readLong());
    }

    public void testReadMinLong() {
        assertEquals(Long.MIN_VALUE, createReader(Long.toString(Long.MIN_VALUE)).readLong());
    }

    public void testReadFloat() {
        assertEquals(100.0f, createReader("100.0").readFloat(), 0.001);
    }

    public void testReadDouble() {
        assertEquals(100.0, createReader("100.0").readDouble(), 0.001);
    }

    public void testReadNegativeDouble() {
        assertEquals(-100.0, createReader("-100.0").readDouble(), 0.001);
    }

    public void testReadString() {
        assertEquals("Jsonk", createReader("\"Jsonk\"").readString());
    }

    public void testReadStringWithEscapes() {
        assertEquals("a\tb\nc\u0000", createReader("\"a\\tb\\nc\\u0000\"").readString());
    }

    public void testReadEscapedStringWithRollback() {
        var reader = createReader("\"a\\tb\\nc\\u0000\"");
        reader.mark();
        var expected = "a\tb\nc\u0000";
        var s = reader.readString();
        assertEquals(expected, s);
        reader.rollback();
        reader.readString();
    }

    public void testReadChar() {
        assertEquals('a', createReader("\"a\"").readChar());
    }

    public void testReadEscapedChar() {
        assertEquals('\t', createReader("\"\t\"").readChar());
    }

    public void testReadUnicodeChar() {
        assertEquals('\u0000', createReader("\"\u0000\"").readChar());
    }

    public void testReadBoolean() {
        assertTrue(createReader("true").readBoolean());
        assertFalse(createReader("false").readBoolean());
    }

    public void testReadNull() {
        assertNull(createReader("null").readNull());
    }

    public void testRollback() {
        var r = createReader("\"Jsonk\"");
        r.mark();
        assertEquals("Jsonk", r.readString());
        r.rollback();
        assertEquals("Jsonk", r.readString());
    }

    private JsonReader createReader(String text) {
        return new JsonReader(new StringReader(text));
    }

}