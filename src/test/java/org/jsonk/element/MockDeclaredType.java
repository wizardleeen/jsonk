package org.jsonk.element;

import jakarta.annotation.Nullable;
import org.jsonk.util.Util;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import java.lang.annotation.Annotation;
import java.util.*;

public class MockDeclaredType extends MockType implements DeclaredType {

    private final MockDeclaredType owner;
    private final MockClass element;
    private final List<MockType> typeArgs;
    private MockDeclaredType superType;
    private List<MockDeclaredType> interfaces;

    public MockDeclaredType(MockDeclaredType owner, MockClass element, List<MockType> typeArgs) {
        this.owner = owner;
        this.element = element;
        this.typeArgs = new ArrayList<>(typeArgs);
    }

    @Override
    public Element asElement() {
        return element;
    }

    @Override
    public TypeMirror getEnclosingType() {
        return owner;
    }

    @Override
    public List<? extends TypeMirror> getTypeArguments() {
        return Collections.unmodifiableList(typeArgs);
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.DECLARED;
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
        return v.visitDeclared(this, p);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object instanceof DeclaredType that)
            return Objects.equals(owner, that.getEnclosingType()) && Objects.equals(element, that.asElement()) && Objects.equals(typeArgs, that.getTypeArguments());
        else
            return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, element, typeArgs);
    }

    @Override
    public void write(ElementWriter writer) {
        if (owner != null) {
            writer.write(owner).write(".");
            writer.write(element.getSimpleName());
        } else
            writer.write(element.getQualifiedName());
        if (!typeArgs.isEmpty())
            writer.write("<").writeList(typeArgs, writer::write, ",").write(">");
    }

    public @Nullable MockDeclaredType getSuperClass() {
        ensureSuperTypesAvailable();
        return superType;
    }

    public List<MockDeclaredType> getInterfaces() {
        ensureSuperTypesAvailable();
        return Objects.requireNonNull(interfaces);
    }

    private void ensureSuperTypesAvailable() {
        if (interfaces != null)
            return;
        var allTypeArgs = new HashMap<MockTypeVariable, MockType>();
        collectAllTypeArgs(allTypeArgs);
        var subst = TypeSubstitutor.create(allTypeArgs);
        if (element.getSuperclass() != null)
            superType = (MockDeclaredType) element.getSuperclass().accept(subst, null);
        interfaces = Util.map(element.getInterfaces(), t -> (MockDeclaredType) t.accept(subst, null));
    }

    private void collectAllTypeArgs(Map<MockTypeVariable, MockType> typeArgs) {
        if (owner != null)
            owner.collectAllTypeArgs(typeArgs);
        if (!this.typeArgs.isEmpty())
            Util.biForEach(element.getTypeParameters(), this.typeArgs, (tp, tv) -> typeArgs.put((MockTypeVariable) tp.asType(), tv));
    }

    @Override
    public boolean isAssignableFrom(MockType that) {
        if (this == that.getUpperBound())
            return true;
        if (that.getUpperBound() instanceof MockDeclaredType thatCt) {
            if (thatCt.element == element) {
                if ((owner == null) != (thatCt.owner == null))
                    return false;
                if (owner != null && !owner.isAssignableFrom(thatCt.owner))
                    return false;
                if (typeArgs.isEmpty())
                    return true;
                if (thatCt.typeArgs.isEmpty())
                    return false;
                return Util.allMatch(typeArgs, thatCt.typeArgs, MockType::isAssignableFrom);
            } else {
                if (thatCt.getSuperClass() != null && isAssignableFrom(thatCt.getSuperClass()))
                    return true;
                for (MockDeclaredType it : thatCt.getInterfaces()) {
                    if (isAssignableFrom(it))
                        return true;
                }
                return false;
            }
        }
        else
            return false;
    }
}
