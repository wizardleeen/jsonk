package org.jsonk;

import junit.framework.TestCase;
import org.jsonk.adapters.ArrayListAdapter;
import org.jsonk.adapters.StringAdapter;

import java.util.List;

public class AdapterRegistryTest extends TestCase {

    public void testLoad() {
        var reg = AdapterRegistry.instance;
        var strAdapter = reg.getAdapter(String.class);
        assertTrue(strAdapter instanceof StringAdapter);
    }

    public void testFactory() {
        var reg = AdapterRegistry.instance;
        var type = Type.from(List.class, Type.from(String.class));
        var adapter = reg.getAdapter(type);
        assertTrue(adapter instanceof ArrayListAdapter<?>);
        assertEquals(type, adapter.getKey().type());
    }

}