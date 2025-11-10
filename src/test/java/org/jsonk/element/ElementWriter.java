package org.jsonk.element;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import java.util.Collection;
import java.util.function.Consumer;

public class ElementWriter {
    private int indent;
    private boolean newLine = true;
    private final StringBuilder buf = new StringBuilder();

    public ElementWriter writeModifiers(Collection<Modifier> mods) {
        if (mods.isEmpty())
            return this;
        return writeList(mods, m -> write(m.toString()), " ").write(" ");
    }

    public ElementWriter write(MockElement element) {
        element.write(this);
        return this;
    }

    public ElementWriter write(Name name) {
        return write(name.toString());
    }

    public <T> ElementWriter writeList(Collection<T> list, Consumer<T> write, String delimiter) {
        if (list.isEmpty())
            return this;
        var it = list.iterator();
        write.accept(it.next());
        while (it.hasNext()) {
            write(delimiter);
            write.accept(it.next());
        }
        return this;
    }

    public ElementWriter write(MockType type) {
        type.write(this);
        return this;
    }

    public ElementWriter write(String s) {
        if (newLine) {
            buf.append("  ".repeat(Math.max(0, indent)));
            newLine = false;
        }
        buf.append(s);
        return this;
    }

    public ElementWriter writeln(String s) {
        write(s);
        return writeln();
    }

    public ElementWriter writeln() {
        buf.append('\n');
        newLine = true;
        return this;
    }

    public void indent() {
        indent++;
    }

    public void deIndent() {
        indent--;
    }

    public String toString() {
        return buf.toString();
    }

}
