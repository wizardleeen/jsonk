package org.jsonk;

import junit.framework.TestCase;
import org.jsonk.mocks.User;
import org.jsonk.mocks.UserAdapter;

public class ParseExceptionTest extends TestCase {

    @Override
    protected void setUp() {
        var registry = AdapterRegistry.instance;
        registry.addAdapter(new UserAdapter());
    }

    public void testUnterminatedString() {
        try {
            userFromJson("""
                    {"name": "leen}""");
        } catch (JsonParseException e) {
            assertEquals("Unterminated string literal. line: 1, column: 16", e.getMessage());
        }
    }

    public void testIllegalCharInString() {
        try {
            userFromJson("""
                   {"name": "
                   "}
                   """);
        } catch (JsonParseException e) {
            assertEquals("Illegal character in string: U+000A. line: 1, column: 11", e.getMessage());
        }
    }

    private void userFromJson(String json) {
        Jsonk.fromJson(json, User.class);

    }

}
