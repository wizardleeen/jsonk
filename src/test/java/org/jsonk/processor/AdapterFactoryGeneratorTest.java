package org.jsonk.processor;

import junit.framework.TestCase;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsonk.element.ElementFactory;
import org.jsonk.element.MockElements;
import org.jsonk.mocks.Item;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class AdapterFactoryGeneratorTest extends TestCase {

    public void test() {
        generate(Item.class);
    }

    @SneakyThrows
    private void generate(Class<?> clazz) {
        var gen = new AdapterFactoryGenerator(ElementFactory.instance.buildClass(Item.class), MockElements.instance);
        var text = gen.generate();
        log.debug("\n{}", text);
        var path = "/Users/leen/workspace/jsonk/src/test/java/" + clazz.getName().replace('.', '/') + "AdapterFactory.java";
        Files.writeString(Path.of(path), text);

    }


}