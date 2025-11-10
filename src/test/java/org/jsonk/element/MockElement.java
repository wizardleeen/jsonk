package org.jsonk.element;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class MockElement implements Element {

    protected final List<AnnotationMirror> annotations = new ArrayList<>();

    protected abstract void write(ElementWriter writer);

    @Override
    public abstract MockType asType();

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return Collections.unmodifiableList(annotations);
    }

    void setAnnotations(List<? extends AnnotationMirror> annotations) {
        this.annotations.clear();
        this.annotations.addAll(annotations);
    }

    void addAnnotation(AnnotationMirror annotation) {
        this.annotations.add(annotation);
    }

    @Override
    public String toString() {
        var writer = new ElementWriter();
        write(writer);
        return writer.toString();
    }

    public abstract void forEachChild(Consumer<MockElement> action);

}
