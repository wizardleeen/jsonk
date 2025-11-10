package org.jsonk;

import junit.framework.TestCase;
import org.jsonk.util.MinimalPerfectHash;

import java.util.List;

public class MinimalPerfectHashTest extends TestCase {

    public void test() {
        var mph = MinimalPerfectHash.generate(List.of(
                "name", "password", "kind"
        ));
        assertTrue(mph.get("name") >= 0);
        assertTrue(mph.get("password") >= 0);
        assertTrue(mph.get("kind") >= 0);
        assertEquals(-1, mph.get("nonExistent"));
    }

    public void testCollision() {
        var moh = MinimalPerfectHash.generateConfig(List.of("productId", "quantity", "price", "createdAt"));
    }

}