package org.jsonk.processor;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.jsonk.element.ElementFactory;
import org.jsonk.element.MockElements;
import org.jsonk.mocks.Item;

import java.util.List;
import java.util.Set;

@Slf4j
public class AdapterFactoryMappingGeneratorTest extends TestCase {

    public void test() {
        var commonNames = new MyNames(MockElements.instance);
        var typesExt = new TypesExt(commonNames);
        var annotations = new Annotations(commonNames, typesExt);
        var gen = new AdapterFactoryMappingGenerator(annotations, commonNames, typesExt);
        var text = gen.generate(Set.of(), List.of(ElementFactory.instance.buildClass(Item.class)));
        log.debug("\n{}", text);
    }

}