package org.jsonk.processor;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.jsonk.element.ElementFactory;
import org.jsonk.element.MockElements;
import org.jsonk.mocks.User;

@Slf4j
public class ClazzTest extends TestCase {

    public void test() {
        var cls = ElementFactory.instance.buildClass(User.class);
        var elements = MockElements.instance;
        var commonNames = new CommonNames(elements);
        var typeExt = new TypesExt(commonNames);
        var introspects = new Introspects(
                new Annotations(commonNames, typeExt),
                commonNames,
                typeExt,
                new Env()
        );
        var clazz = introspects.introspect(cls, new Env());
        log.debug("Fields:");
        for (Field field : clazz.getProperties()) {
            log.debug("{}: {}", field.name(), field.type());
        }
        clazz.forEachReferenceType((t, attrs) -> {
            System.out.println(t);
        });
    }

}