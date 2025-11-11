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
        var user1 = Jsonk.fromJson(new StringReader(json), User.class);
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
        var order1 = Jsonk.fromJson(json, Order.class);
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
        var order1 = Jsonk.fromJson(json, Order.class);
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
        assertEquals(arrayType, Jsonk.fromJson(json, org.jsonk.mocks.Type.class));
        var json1 = """
                {
                    "kind": "int",
                    "type": "primitive",
                }
                """;
        assertEquals(new PrimitiveType("int"), Jsonk.fromJson(json1, Type.class));
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
        var product1 = Jsonk.fromJson(json, Product.class);
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
        var order1 = Jsonk.fromJson(json, Order.class);
        assertEquals(order, order1);
    }

    public void testPropName() {
        var registry = AdapterRegistry.instance;
        registry.addAdapter(new MockClazzAdapter());
        var clazz = new MockClazz(true);
        var json = Jsonk.toJson(clazz);
        log.debug("\n{}", json);
        var clazz1 = Jsonk.fromJson(json, MockClazz.class);
        assertEquals(clazz, clazz1);
    }

    public void testMap() {
        var json = """
                {
                    "name": "Jsonk"
                }
                """;
        var map = Jsonk.fromJson(json, Map.class);
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
        var foo1 = Jsonk.fromJson(json, LocalDateTimeFoo.class);
        assertEquals(foo, foo1);
    }

    public void testArray() {
        var registry = AdapterRegistry.instance;
        registry.addAdapter(new ArrayFooAdapter());
        registry.initAdapters();
        var foo = new ArrayFoo(new String[] {"t1", "t2", "t3"});
        var json = Jsonk.toJson(foo);
        var foo1 = Jsonk.fromJson(json, ArrayFoo.class);
        assertEquals(foo, foo1);
    }

    public void testGeneric() {
        var reg = AdapterRegistry.instance;
        reg.addAdapterFactory(new ItemAdapterFactory());
        var item = new Item<>("jsonk");
        var json = Jsonk.toJson(item);
        log.debug(json);
        var item1 = Jsonk.fromJson(json, org.jsonk.Type.from(Item.class, String.class));
        assertEquals(item, item1);
    }

    public void testNestedConstruction() {
        var reg = AdapterRegistry.instance;
        reg.addAdapter(new MaterialAdapter());
        reg.addAdapter(new BOMAdapter());
        reg.initAdapters();
        var material = new Material("Steel");
        material.setAmount(1);
        var bom = new BOM(material);
        bom.setVersion(1);
        var json = Jsonk.toJson(bom);
        var bom1 = Jsonk.fromJson(json, BOM.class);
        assertEquals(bom, bom1);
    }

    public void testUnmodifiableMap() {
        var map = Map.of("name", "leen");
        var json = Jsonk.toJson(map);
        var map1 = Jsonk.fromJson(json, org.jsonk.Type.from(Map.class, String.class, String.class));
        assertEquals(map1, map);
    }

    public void testImmutableLit() {
        var list = List.of(1, 2, 3);
        var json = Jsonk.toJson(list);
        var list1 = Jsonk.fromJson(json, org.jsonk.Type.from(List.class, Integer.class));
        assertEquals(list, list1);
    }

    private void register(Adapter<?>...adapters) {
        for (Adapter<?> adapter : adapters) {
            AdapterRegistry.instance.addAdapter(adapter);
        }
    }

}
