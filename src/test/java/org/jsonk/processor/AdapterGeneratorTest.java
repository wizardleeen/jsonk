package org.jsonk.processor;

import junit.framework.TestCase;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsonk.element.ElementFactory;
import org.jsonk.element.MockElements;
import org.jsonk.element.MockTypes;
import org.jsonk.mocks.*;
import org.jsonk.mocks.ArrayType;
import org.jsonk.mocks.ClassType;
import org.jsonk.mocks.PrimitiveType;
import org.jsonk.mocks.Type;

import javax.lang.model.element.TypeElement;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class AdapterGeneratorTest extends TestCase {

    @SneakyThrows
    public void test() {
//        var cls = ElementFactory.instance.buildClass(User.class);
//        log.debug("\n{}", cls);
        var clazz = ElementFactory.instance.buildClass(User.class);
        var gen = createGenerator(clazz);
        var code = gen.generate();
        log.info("\n{}", code);
        var path = "/Users/leen/workspace/jsonk/src/test/java/org/jsonk/mocks/UserAdapter.java";
        Files.writeString(Path.of(path), code);
    }

    @SneakyThrows
    public void testEmpty() {
//        var cls = ElementFactory.instance.buildClass(User.class);
//        log.debug("\n{}", cls);
        var clazz = ElementFactory.instance.buildClass(Role.class);
        var gen = createGenerator(clazz);
        var code = gen.generate();
        log.info("\n{}", code);
        var path = "/Users/leen/workspace/jsonk/src/test/java/org/jsonk/mocks/RoleAdapter.java";
        Files.writeString(Path.of(path), code);
    }

    @SneakyThrows
    public void testListField() {
        var code1 = createGenerator(ElementFactory.instance.buildClass(Order.class)).generate();
        var code2 = createGenerator(ElementFactory.instance.buildClass(OrderItem.class)).generate();
        log.info("\n{}", code1);
        log.info("\n{}", code2);
        var path1 = "/Users/leen/workspace/jsonk/src/test/java/org/jsonk/mocks/OrderAdapter.java";
        var path2 = "/Users/leen/workspace/jsonk/src/test/java/org/jsonk/mocks/OrderItemAdapter.java";
        Files.writeString(Path.of(path1), code1);
        Files.writeString(Path.of(path2), code2);
    }

    @SneakyThrows
    public void testPolymorphic() {
        var gen = createGenerator(ElementFactory.instance.buildClass(Type.class));
        var code1 = gen.generate();
        logCode(code1);
        var path = "/Users/leen/workspace/jsonk/src/test/java/org/jsonk/mocks/TypeAdapter.java";
        Files.writeString(Path.of(path), code1);
        generate(ClassType.class);
        generate(ArrayType.class);
        generate(PrimitiveType.class);
    }

    public void testInheritance() {
        generate(Product.class);
    }

    public void testPropName() {
        generate(MockClazz.class);
    }

    public void testLocalDateTime() {
        generate(LocalDateTimeFoo.class);
    }

    public void testArray() {
        generate(ArrayFoo.class);
    }

    public void testCyclicRef() {
        generate(CyclicRefFoo.class);
    }

    public void testGeneric() {
        generate(Item.class);
    }

    @SneakyThrows
    private String generateWithoutSave(Class<?> clazz) {
        var code = createGenerator(ElementFactory.instance.buildClass(clazz)).generate();
        log.info("\n{}", code);
        return code;
    }

    @SneakyThrows
    private void generate(Class<?> clazz) {
        var code = createGenerator(ElementFactory.instance.buildClass(clazz)).generate();
        var path = "/Users/leen/workspace/jsonk/src/test/java/" + clazz.getName().replace('.', '/') + "Adapter.java";
        Files.writeString(Path.of(path), code);
    }

    private void logCode(String code) {
        log.info("\n{}", code);
    }

    private AdapterGenerator createGenerator(TypeElement clazz) {
        var env = new Env();

        var commonNames = new CommonNames(MockElements.instance);
        var typeExt = new TypesExt(commonNames);
        return new AdapterGenerator(
                clazz,
                MockElements.instance,
                MockTypes.instance,
                typeExt,
                new Introspects(
                        new Annotations(commonNames, typeExt),
                        commonNames,
                        typeExt,
                        env
                ),
                commonNames,
                new Annotations(commonNames, typeExt),
                env
        );

    }
}
