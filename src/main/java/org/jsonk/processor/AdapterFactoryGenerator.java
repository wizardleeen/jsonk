package org.jsonk.processor;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class AdapterFactoryGenerator extends AbstractGenerator {

    private final TypeElement clazz;
    private final Elements elements;

    public AdapterFactoryGenerator(TypeElement clazz, Elements elements) {
        this.clazz = clazz;
        this.elements = elements;
    }

    public String generate() {
        assert !clazz.getTypeParameters().isEmpty();
        var pkgName = elements.getPackageOf(clazz).getQualifiedName().toString();
        if (!pkgName.isEmpty()) {
            write("package ").write(pkgName).writeln(";");
            writeln();
        }
        generateImports();
        write("public class ").write(clazz.getSimpleName())
                .writeln("AdapterFactory implements AdapterFactory {");
        indent();
        writeln();
        generateCreate();
        writeln();
        generateIsSupported();
        writeln();
        deIndent();
        writeln("}");
        return toString();
    }

    private void generateImports() {
        writeln("import java.util.Map;");
        writeln("import org.jsonk.Adapter;");
        writeln("import org.jsonk.AdapterFactory;");
        writeln("import org.jsonk.Type;");
        writeln();
    }

    private void generateCreate() {
        writeln("public Adapter<?> create(Type type, Map<String, Object> attributes) {");
        indent();
        writeReturn(() -> write("new ").write(clazz.getQualifiedName()).write("Adapter<>(type)"));
        deIndent();
        writeln("}");
    }

    private void generateIsSupported() {
        writeln("public boolean isSupported(Type type, Map<String, Object> attributes) {");
        indent();
        writeReturn(() -> write("type.clazz() == ").write(clazz.getQualifiedName()).write(".class"));
        deIndent();
        writeln("}");
    }

}
