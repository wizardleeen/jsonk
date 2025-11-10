package org.jsonk.element;

import org.jsonk.util.Util;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.UnionType;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MockUnionType extends MockType implements UnionType {

    private final List<MockType> alternatives;

    public MockUnionType(Set<MockType> alternatives) {
        this.alternatives = new ArrayList<>(alternatives);
    }

    @Override
    public List<? extends TypeMirror> getAlternatives() {
        return Collections.unmodifiableList(alternatives);
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.UNION;
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return List.of();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return null;
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        return null;
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        return v.visitUnion(this, p);
    }

    @Override
    public void write(ElementWriter writer) {
        writer.writeList(alternatives, writer::write, " | ");
    }

    @Override
    public boolean isAssignableFrom(MockType that) {
        return Util.anyMatch(alternatives, alt -> alt.isAssignableFrom(that));
    }
}
