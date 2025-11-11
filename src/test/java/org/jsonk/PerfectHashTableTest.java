package org.jsonk;

import junit.framework.TestCase;
import org.jsonk.util.PerfectHashTable;

import java.util.List;

public class PerfectHashTableTest extends TestCase {

    public void test() {
        var mph = PerfectHashTable.generate(List.of(
                "name", "password", "kind"
        ));
        assertEquals(0, mph.get("name"));
        assertEquals(1, mph.get("password"));
        assertEquals(2, mph.get("kind"));
        assertEquals(-1, mph.get("nonExistent"));
    }

    public void testCollision() {
        var moh = PerfectHashTable.generate(List.of("access", "name", "type", "summary", "label", "numberFormat"));
    }

}