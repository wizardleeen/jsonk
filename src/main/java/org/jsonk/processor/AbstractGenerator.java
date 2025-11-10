package org.jsonk.processor;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.function.Consumer;

public class AbstractGenerator {
    private int nextVariable;
    private boolean newLine = true;
    private int indent;
    private final StringBuilder buf = new StringBuilder();

    public void indent() {
        indent++;
    }

    public void deIndent() {
        indent--;
    }

    public AbstractGenerator writeln(String s) {
        write(s);
        writeln();
        return this;
    }

    public AbstractGenerator writeln() {
        buf.append('\n');
        newLine = true;
        return this;
    }

    public AbstractGenerator writeImport(String imp) {
        write("import ").write(imp).writeln(";");
        return this;
    }

    public AbstractGenerator write(Name name) {
        return write(name.toString());
    }

    public AbstractGenerator writeReturn(String value) {
        return writeReturn(() -> write(value));
    }

    public AbstractGenerator writeReturn(Runnable writeValue) {
        write("return ");
        writeValue.run();
        writeln(";");
        return this;
    }

    public AbstractGenerator writeThrow(String exception) {
        writeThrow(() -> write(exception));
        return this;
    }

    public AbstractGenerator writeThrow(Runnable writeException) {
        write("throw ");
        writeException.run();
        writeln(";");
        return this;
    }

    public AbstractGenerator write(TypeMirror type) {
        return write(type.toString());
    }

    AbstractGenerator write(Type type) {
        return write(type.getText());
    }

    public AbstractGenerator write(int i) {
        return write(Integer.toString(i));
    }

    public AbstractGenerator write(char c) {
        return write(Character.toString(c));
    }

    public AbstractGenerator write(String s) {
        if (newLine) {
            newLine = false;
            appendIndents();
        }
        var len = s.length();
        for (int i = 0; i < len; i++) {
            var c = s.charAt(i);
            buf.append(c);
            if (c == '\n') {
                if (i == len - 1)
                    newLine = true;
                else
                    appendIndents();
            }
        }
        return this;
    }

    public void writePackage(String name) {
        write("package ").write(name).writeln(";");
        writeln();
    }

    public String writeVarDecl(Runnable writeInitial) {
        var v = nextVariable();
        writeVarDecl(v, writeInitial);
        return v;
    }

    public AbstractGenerator writeAssignment(Runnable writeLhs, Runnable writeRhs) {
        writeLhs.run();
        write(" = ");
        writeRhs.run();
        writeln(";");
        return this;
    }

    public AbstractGenerator writeClassLiteral(DeclaredType type) {
        var clazz = (TypeElement) type.asElement();
        return write(clazz.getQualifiedName()).write(".class");
    }

    public void writeVarDecl(String name, Runnable writeInitial) {
        write("var ").write(name).write(" = ");
        writeInitial.run();
        writeln(";");
    }

    private void appendIndents() {
        buf.append("    ".repeat(Math.max(0, indent)));
    }

    public <T> AbstractGenerator writeList(Iterable<T> iterable, Consumer<? super T> action, String delimiter) {
        var it = iterable.iterator();
        if (!it.hasNext())
            return this;
        action.accept(it.next());
        while (it.hasNext()) {
            write(delimiter);
            action.accept(it.next());
        }
        return this;
    }

    public AbstractGenerator writeIfElse(Runnable writeCond, Runnable writeThen, Runnable writeElse) {
        write("if (");
        writeCond.run();
        writeln(") {");
        indent();
        writeThen.run();
        deIndent();
        writeln("} else {");
        indent();
        writeElse.run();
        deIndent();
        writeln("}");
        return this;
    }

    public AbstractGenerator writeIf(Runnable writeCond, Runnable writeThen) {
        write("if (");
        writeCond.run();
        writeln(") {");
        indent();
        writeThen.run();
        deIndent();
        writeln("}");
        return this;
    }

    public AbstractGenerator writeInstanceOf(String value, String type, String bindVar) {
        write(value).write(" instanceof ").write(type).write(" ").write(bindVar);
        return this;
    }

    protected String nextVariable() {
        return "v" + nextVariable++;
    }

    @Override
    public String toString() {
        return buf.toString();
    }

}
