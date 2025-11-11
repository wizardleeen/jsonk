package org.jsonk;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.jsonk.util.PerfectHashTable;
import org.jsonk.util.StringUtil;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class JsonReaderImplTest extends TestCase {

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

    public void testReadIntOverflow() {
        var r = createReader("-2147483649");
        try {
            r.readInt();
            fail("Should have failed");
        } catch (JsonParseException e) {
            assertEquals("Integer value out of range. line: 1, column: 1", e.getMessage());
        }
    }

    public void testReadLongOverflow() {
        var r = createReader("-9223372036854775809");
        try {
            r.readLong();
            fail("Should have failed");
        } catch (JsonParseException e) {
            assertEquals("Integer value out of range. line: 1, column: 1", e.getMessage());
        }
    }

    public void testReadName() {
        var names = List.of("id", "name", "activated", "roles", "jsonk\n");
        var ph = PerfectHashTable.generate(names);
        System.out.println(Arrays.toString(ph.getTable()));
        for (String s : names) {
            var index = ph.get(s);
            var r = createReader("\"" + StringUtil.escape(s) + "\"");
            var readIndex = r.readName(ph.keyCharArrays(), ph.getOrdinals(), ph.getSeed());
            assertEquals("Index not match for name: " + s, index, readIndex);
        }
        var r = createReader("\"nonExistent\"");
        var nameIndex = r.readName(ph.keyCharArrays(), ph.getOrdinals(), ph.getSeed());
        assertEquals(-1, nameIndex);
    }

    private JsonReader createReader(String text) {
        return new JsonReaderImpl(new StringReader(text));
    }

}