package org.jsonk.element;

import lombok.Setter;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class MockExecElement extends MockElement implements ExecutableElement {

    private final Element owner;
    private final Set<Modifier> modifiers;
    private final Name name;
    private final MockType receiverType;
    private final List<MockVarElement> params = new ArrayList<>();
    private final List<MockTypeParamElement> typeParams = new ArrayList<>();
    private final List<MockType> throwTypes = new ArrayList<>();
    @Setter
    private MockType retType;
    private  final ElementKind kind;
    @Setter
    private MockExecType type;
    private final boolean isDefault;
    private final boolean varargs;

    public MockExecElement(Element owner,
                           Set<Modifier> modifiers,
                           Name name,
                           MockType receiverType,
                           ElementKind kind,
                           boolean isDefault,
                           boolean varargs) {
        this.owner = owner;
        this.modifiers = modifiers;
        this.name = name;
        this.receiverType = receiverType;
        this.kind = kind;
        this.isDefault = isDefault;
        this.varargs = varargs;
    }

    public void setParams(List<MockVarElement> params) {
        this.params.clear();
        this.params.addAll(params);
    }

    public void setTypeParams(List<MockTypeParamElement> typeParams) {
        this.typeParams.clear();
        this.typeParams.addAll(typeParams);
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
        return modifiers;
    }

    @Override
    public List<? extends TypeParameterElement> getTypeParameters() {
        return Collections.unmodifiableList(typeParams);
    }

    @Override
    public MockType getReturnType() {
        return retType;
    }

    @Override
    public List<? extends MockVarElement> getParameters() {
        return Collections.unmodifiableList(params);
    }

    @Override
    public TypeMirror getReceiverType() {
        return receiverType;
    }

    @Override
    public boolean isVarArgs() {
        return varargs;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public List<? extends MockType> getThrownTypes() {
        return Collections.unmodifiableList(throwTypes);
    }

    @Override
    public AnnotationValue getDefaultValue() {
        return null;
    }

    @Override
    public Element getEnclosingElement() {
        return owner;
    }

    @Override
    public List<? extends Element> getEnclosedElements() {
        return List.of();
    }

    @Override
    public void forEachChild(Consumer<MockElement> action) {
        typeParams.forEach(action);
        params.forEach(action);
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
        return v.visitExecutable(this, p);
    }

    @Override
    public Name getSimpleName() {
        return name;
    }

    public void setThrowTypes(List<MockType> throwTypes) {
        this.throwTypes.clear();
        this.throwTypes.addAll(throwTypes);
    }

    @Override
    public void write(ElementWriter writer) {
        writer.writeModifiers(modifiers);
        if (!typeParams.isEmpty()) {
            writer.write("<")
                    .writeList(typeParams, writer::write, ", ")
                    .write("> ");
        }
        if (getKind() != ElementKind.CONSTRUCTOR)
            writer.write(retType).write(" ");
        writer.write(name)
                .write("(")
                .writeList(params, writer::write, ", ")
                .write(")");
        if (!throwTypes.isEmpty()) {
            writer.write(" throws ")
                    .writeList(throwTypes, writer::write, ", ");
        }
        writer.writeln(";");
    }
}
