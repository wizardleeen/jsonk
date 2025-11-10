package org.jsonk;

import junit.framework.TestCase;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsonk.mocks.*;
import org.jsonk.mocks.Type;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonkTest extends TestCase {

    @SneakyThrows
    public void test() {
        var register = AdapterRegistry.instance;
        register.addAdapter(new UserAdapter());
        register.initAdapters();
        var user = new User(
                "leen",
                UserKind.CUSTOMER,
                "123456",
                true,
                null,
                Map.of()
        );
        var json = Jsonk.toJson(user);
        log.info("\n{}", json);
        var user1 = Jsonk.fromJson(User.class, new StringReader(json));
        log.info("\n{}", user1);
        assertEquals(user, user1);
    }

    public void testIgnore() {
        var user = new User("leen", UserKind.CUSTOMER, "123456", true, null, Map.of());
        var json = Jsonk.toJson(user);
        assertFalse(json.contains("admin"));
    }

    public void testListField() {
        var registry = AdapterRegistry.instance;
        registry.addAdapter(new OrderAdapter());
        registry.addAdapter(new OrderItemAdapter());
        registry.initAdapters();
        var order = new Order(List.of(
                new OrderItem("001", 1, 100),
                new OrderItem("001", 1, 100),
                new OrderItem("001", 1, 100)
        ));
        var json = Jsonk.toJson(order);
        log.info("\n{}", json);
        var order1 = Jsonk.fromJson(Order.class, json);
        assertEquals(order, order1);
    }

    public void testLargeJson() {
        var registry = AdapterRegistry.instance;
        registry.addAdapter(new OrderAdapter());
        registry.addAdapter(new OrderItemAdapter());
        registry.initAdapters();
        var items = new ArrayList<OrderItem>();
        for (int i = 0; i < 1000; i++) {
            items.add(new OrderItem("001", 1, 100));
        }
        var order = new Order(items);
        var json = Jsonk.toJson(order);
//        log.info("\n{}", json);
        var order1 = Jsonk.fromJson(Order.class, json);
        assertEquals(order, order1);
    }

    public void testPolymorphism() {
        var registry = AdapterRegistry.instance;
        registry.addAdapter(new TypeAdapter());
        registry.addAdapter(new ClassTypeAdapter());
        registry.addAdapter(new PrimitiveTypeAdapter());
        registry.addAdapter(new ArrayTypeAdapter());
        registry.initAdapters();
        var arrayType = new ArrayType(new ClassType("Foo"));
        var json = Jsonk.toJson(arrayType);
        assertEquals(arrayType, Jsonk.fromJson(org.jsonk.mocks.Type.class, json));
        var json1 = """
                {
                    "kind": "int",
                    "type": "primitive",
                }
                """;
        assertEquals(new PrimitiveType("int"), Jsonk.fromJson(Type.class, json1));
    }

    public void testExistingDiscriminator() {
        var registry = AdapterRegistry.instance;
        registry.addAdapter(new TypeAdapter());
        registry.addAdapter(new PrimitiveTypeAdapter());
        registry.addAdapter(new ClassTypeAdapter());
        registry.addAdapter(new ArrayTypeAdapter());
        registry.initAdapters();
        var json = Jsonk.toJson(new ClassType("Foo"), Option.create().indent());
        log.info("\n{}", json);
        assertEquals(json.indexOf("type"), json.lastIndexOf("type"));
    }

    public void testInheritance() {
        var registry = AdapterRegistry.instance;
        registry.addAdapter(new ProductAdapter());
        registry.initAdapters();
        var product = new Product("Shoes", 100);
        product.setStock(100);
        var json = Jsonk.toJson(product);
        log.debug("\n{}", json);
        var product1 = Jsonk.fromJson(Product.class, json);
        assertEquals(product, product1);
    }

    public void testPrettyPrinting() {
        var registry = AdapterRegistry.instance;
        registry.addAdapter(new OrderAdapter());
        registry.addAdapter(new OrderItemAdapter());
        registry.initAdapters();
        var order = new Order(List.of(
                new OrderItem("001", 1, 100)
        ));
        var json = Jsonk.toJson(order, Option.create().indent());
        log.debug("\n{}", json);
        var order1 = Jsonk.fromJson(Order.class, json);
        assertEquals(order, order1);
    }

    public void testPropName() {
        var registry = AdapterRegistry.instance;
        registry.addAdapter(new MockClazzAdapter());
        var clazz = new MockClazz(true);
        var json = Jsonk.toJson(clazz);
        log.debug("\n{}", json);
        var clazz1 = Jsonk.fromJson(MockClazz.class, json);
        assertEquals(clazz, clazz1);
    }

    public void testMap() {
        var json = """
                {
                    "name": "Jsonk"
                }
                """;
        var map = Jsonk.fromJson(Map.class, json);
        assertEquals(Map.of("name", "Jsonk"), map);
    }

    public void testLocalDateTime() {
        var registry = AdapterRegistry.instance;
        registry.addAdapter(new LocalDateTimeFooAdapter());
        registry.initAdapters();
        var foo = new LocalDateTimeFoo(
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
        var json = Jsonk.toJson(foo);
        log.debug("\n{}", json);
        var foo1 = Jsonk.fromJson(LocalDateTimeFoo.class, json);
        assertEquals(foo, foo1);
    }

    public void testArray() {
        var registry = AdapterRegistry.instance;
        registry.addAdapter(new ArrayFooAdapter());
        registry.initAdapters();
        var foo = new ArrayFoo(new String[] {"t1", "t2", "t3"});
        var json = Jsonk.toJson(foo);
        var foo1 = Jsonk.fromJson(ArrayFoo.class, json);
        assertEquals(foo, foo1);
    }

    public void testCyclicRef() {
        register(new CyclicRefFooAdapter());
        var foo = new CyclicRefFoo();
        foo.setValue(foo);
        try {
            Jsonk.toJson(foo);
            fail("Should have failed");
        } catch (JsonWriteException e) {
            assertTrue(e.getMessage().startsWith("Cyclic reference detected"));
        }
    }

    public void testGeneric() {
        var reg = AdapterRegistry.instance;
        reg.addAdapterFactory(new ItemAdapterFactory());
        var item = new Item<>("jsonk");
        var json = Jsonk.toJson(item);
        log.debug(json);
        var item1 = Jsonk.fromJson(org.jsonk.Type.from(Item.class, String.class), json);
        assertEquals(item, item1);
    }

    private void register(Adapter<?>...adapters) {
        for (Adapter<?> adapter : adapters) {
            AdapterRegistry.instance.addAdapter(adapter);
        }
    }

}
