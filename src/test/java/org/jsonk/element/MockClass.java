package org.jsonk.element;

import lombok.Getter;
import lombok.Setter;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Consumer;

public class MockClass extends MockElement implements TypeElement {
    private final ElementKind kind;
    @Getter
    private final MockPackageElement pkg;
    @Setter
    private Element owner;
    private final Name qualName;
    private final Name simpleName;
    private final Set<Modifier> modifiers;
    private final List<MockElement> members = new ArrayList<>();
    @Setter
    private MockDeclaredType superClass;
    private final List<TypeMirror> interfaces = new ArrayList<>();
    private final List<MockTypeParamElement> typeParameters = new ArrayList<>();
    private final List<RecordComponentElement> recordComponents = new ArrayList<>();
    @Setter
    private MockDeclaredType type;

    public MockClass(
            ElementKind kind,
            MockPackageElement pkg,
            Name simpleName,
            Set<Modifier> modifiers) {
        this.kind = kind;
        this.pkg = pkg;
        qualName = pkg.getQualifiedName().isEmpty() ? simpleName :
                MockName.of(pkg.getQualifiedName() + "." + simpleName);
        this.simpleName = simpleName;
        this.modifiers = new HashSet<>(modifiers);
        pkg.addClass(this);
    }

    @Override
    public MockType asType() {
        return type;
    }

    @Override
    public ElementKind getKind() {
        return kind;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return Collections.unmodifiableSet(modifiers);
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        return Collections.unmodifiableList(members);
    }

    @Override
    public void forEachChild(Consumer<MockElement> action) {
        typeParameters.forEach(action);
        members.forEach(action);
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
    public <R, P> R accept(ElementVisitor<R, P> v, P p) {
        return v.visitType(this, p);
    }

    @Override
    public NestingKind getNestingKind() {
        return null;
    }

    @Override
    public Name getQualifiedName() {
        return qualName;
    }

    @Override
    public Name getSimpleName() {
        return simpleName;
    }

    @Override
    public MockDeclaredType getSuperclass() {
        return superClass;
    }

    @Override
    public List<? extends TypeMirror> getInterfaces() {
        return Collections.unmodifiableList(interfaces);
    }

    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        return Collections.unmodifiableList(typeParameters);
    }

    public void setTypeParameters(List<? extends MockTypeParamElement> typeParams) {
        this.typeParameters.clear();
        this.typeParameters.addAll(typeParams);
    }

    @Override
    public List<RecordComponentElement> getRecordComponents() {
        return recordComponents;
    }

    public void setRecordComponents(List<RecordComponentElement> recordComponents) {
        this.recordComponents.clear();
        this.recordComponents.addAll(recordComponents);
    }

    public void setInterfaces(List<TypeMirror> interfaces) {
        this.interfaces.clear();
        this.interfaces.addAll(interfaces);
    }

    @Override
    public Element getEnclosingElement() {
        return owner;
    }

    public void setMembers(List<MockElement> members) {
        this.members.clear();
        this.members.addAll(members);
    }

    public List<MockElement> getMembers() {
        return Collections.unmodifiableList(members);
    }

    @Override
    public void write(ElementWriter writer) {
        writer.writeModifiers(modifiers);
        var keyword = switch (kind) {
            case RECORD -> "record";
            case ENUM -> "enum";
            case INTERFACE -> "interface";
            default -> "class";
        };
        writer.write(keyword).write(" ").write(simpleName.toString());
        if (!typeParameters.isEmpty())
            writer.write("<").writeList(typeParameters, writer::write, ", ").write(">");
        writer.writeln(" {");
        writer.indent();
        writer.writeln();
        for (var member : members) {
            member.write(writer);
            writer.writeln();
        }
        writer.deIndent();
        writer.writeln("}");
    }
}
