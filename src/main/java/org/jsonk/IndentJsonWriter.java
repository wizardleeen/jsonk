package org.jsonk;

import java.io.Writer;
import java.util.function.Consumer;

public class IndentJsonWriter extends JsonWriter {

    private int indents;
    private boolean newLine;

    public IndentJsonWriter(AdapterRegistry adapterRegistry, Writer writer) {
        super(adapterRegistry, writer);
    }

    @Override
    public void writeColon() {
        write(':');
        write(' ');
    }

    @Override
    public void writeComma() {
        writeln(',');
    }

    @Override
    public void writeLBrace() {
        writeln('{');
        indent();
    }

    @Override
    public void writeRBrace() {
        writeln();
        deIndent();
        writeIndents();
        write('}');
    }

    @Override
    public void writeLBracket() {
        writeln('[');
        indent();
    }

    @Override
    public void writeRBracket() {
        writeln();
        deIndent();
        writeIndents();
        write(']');
    }

    private void writeln(char c) {
        write(c);
        writeln();
    }

    private void writeln() {
        write('\n');
        newLine = true;
    }

    @Override
    public void writeName(String name) {
        writeIndents();
        super.writeName(name);
    }

    @Override
    protected <E> void writeElement(E element, Consumer<? super E> writeElement) {
        writeIndents();
        super.writeElement(element, writeElement);
    }

    private void writeIndents() {
        if (newLine) {
            for (int i = 0; i < indents; i++) {
                write(' ');
                write(' ');
            }
            newLine = false;
        }
    }

    private void indent() {
        indents++;
    }

    private void deIndent() {
        indents--;
    }

}
