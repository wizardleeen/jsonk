package org.jsonk.processor;

import junit.framework.TestCase;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsonk.element.ElementFactory;
import org.jsonk.element.MockElements;
import org.jsonk.mocks.*;
import org.jsonk.mocks.ArrayType;
import org.jsonk.mocks.ClassType;
import org.jsonk.mocks.PrimitiveType;
import org.jsonk.mocks.Type;
import org.jsonk.util.Util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@Slf4j
public class ServiceMappingGeneratorTest extends TestCase {

    private ServiceMappingGenerator gen;

    @Override
    protected void setUp() {
        var commonNames = new MyNames(MockElements.instance);
        var typesExt = new TypesExt(commonNames);
        gen = new ServiceMappingGenerator(new Annotations(commonNames, typesExt), commonNames, typesExt);
    }

    @SneakyThrows
    public void test() {
        var classes = List.of(
                User.class, Role.class, ArrayFoo.class, Type.class, PrimitiveType.class, ClassType.class, ArrayType.class,
                BOM.class, Material.class, CyclicRefFoo.class, Id.class, Item.class, LocalDateTimeFoo.class,
                MockClazz.class, Order.class, OrderItem.class, Product.class
        );
        var code = gen.generate(Set.of(), Util.map(classes, ElementFactory.instance::buildClass));
        log.info("\n{}", code);
        var path = "/Users/leen/workspace/jsonk/src/test/resources/META-INF/services/org.jsonk.Adapter";
        Files.writeString(Path.of(path), code);
    }

    public void testCustomAdapter() {
        var code = gen.generate(Set.of(), List.of(
                ElementFactory.instance.buildClass(Id.class)
        ));
        assertEquals("org.jsonk.mocks.IdAdapter\n", code);
    }

    public void testModify() {
        var code = gen.generate(Set.of("org.jsonk.mocks.UserAdapter"), List.of(
                ElementFactory.instance.buildClass(User.class),
                ElementFactory.instance.buildClass(Id.class)
        ));
        assertEquals("org.jsonk.mocks.UserAdapter\norg.jsonk.mocks.IdAdapter\n", code);
    }


}
