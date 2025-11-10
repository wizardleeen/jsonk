package org.jsonk;

import junit.framework.TestCase;
import org.jsonk.util.StringUtil;

public class StringUtilTest extends TestCase {

    public void testEscape() {
        var s = "\u001AHello\tJsonk\nYou are the best\u001F!\u0000";
        var buf = new char[1024];
        s.getChars(0, s.length(), buf, 0);
        var n = StringUtil.escape(buf, 0, s.length());
        var s1 = new String(buf, 0, n);
        assertEquals(
                "\\u001AHello\\tJsonk\\nYou are the best\\u001F!\\u0000",
                s1
        );
    }

}