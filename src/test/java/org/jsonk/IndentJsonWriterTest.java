package org.jsonk;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.jsonk.mocks.*;

import java.util.List;
import java.util.Map;

@Slf4j
public class IndentJsonWriterTest extends TestCase {

    @Override
    protected void setUp() {
        AdapterRegistry.instance.addAdapter(new UserAdapter());
        AdapterRegistry.instance.addAdapter(new OrderAdapter());
        AdapterRegistry.instance.addAdapter(new OrderItemAdapter());
        AdapterRegistry.instance.initAdapters();
    }

    public void test() {
        var json = Jsonk.toJson(
                new User("leen", UserKind.CUSTOMER, "123456", true, null, Map.of()),
                Option.create().indent()
        );
        log.info("\n{}", json);
    }

    public void testArray() {
        var json = Jsonk.toJson(
                new Order(List.of(
                        new OrderItem("001", 1, 100),
                        new OrderItem("002", 1, 100)
                )),
                Option.create().indent()
        );
        log.info("\n{}", json);
    }

}