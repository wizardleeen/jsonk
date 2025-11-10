package org.jsonk.element;

import lombok.SneakyThrows;

import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MockElements implements Elements {

    public static final MockElements instance = new MockElements();

    private final ElementFactory elementFactory = ElementFactory.instance;

    @Override
    public PackageElement getPackageElement(CharSequence name) {
        return elementFactory.getPackage(name.toString());
    }

    @SneakyThrows
    @Override
    public TypeElement getTypeElement(CharSequence name) {
        return elementFactory.buildClass(Class.forName(name.toString()));
    }

    @Override
    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(AnnotationMirror a) {
        return null;
    }

    @Override
    public String getDocComment(Element e) {
        return null;
    }

    @Override
    public boolean isDeprecated(Element e) {
        return false;
    }

    @Override
    public Name getBinaryName(TypeElement type) {
        return null;
    }

    @Override
    public PackageElement getPackageOf(Element e) {
        return switch (e) {
            case PackageElement pkg -> pkg;
            case MockClass cls -> cls.getPkg();
            default -> getPackageOf(Objects.requireNonNull(e.getEnclosingElement()));
        };
    }

    @Override
    public List<? extends Element> getAllMembers(TypeElement type) {
        var cls = (MockClass) type;
        return cls.getMembers();
    }

    @Override
    public List<? extends AnnotationMirror> getAllAnnotationMirrors(Element e) {
        return null;
    }

    @Override
    public boolean hides(Element hider, Element hidden) {
        return false;
    }

    @Override
    public boolean overrides(ExecutableElement overrider, ExecutableElement overridden, TypeElement type) {
        return false;
    }

    @Override
    public String getConstantExpression(Object value) {
        return null;
    }

    @Override
    public void printElements(Writer w, Element... elements) {

    }

    @Override
    public Name getName(CharSequence cs) {
        return MockName.of(cs.toString());
    }

    @Override
    public boolean isFunctionalInterface(TypeElement type) {
        return false;
    }
}
