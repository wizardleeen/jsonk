package org.jsonk;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.jsonk.element.ElementFactory;
import org.jsonk.mocks.User;
import org.jsonk.mocks.UserKind;

@Slf4j
public class MockElementTest extends TestCase {

    public void test() {
        var factory = ElementFactory.instance;
        var elem = factory.buildClass(User.class);
        log.info("\n{}", elem);
        log.info("\n{}", factory.buildClass(UserKind.class));
        log.info("\n{}", factory.buildClass(Comparable.class));
    }

}
