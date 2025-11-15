package org.jsonk.processor;

import com.sun.source.util.Trees;
import lombok.SneakyThrows;
import org.jsonk.Adapter;
import org.jsonk.Json;
import org.jsonk.AdapterFactory;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.StandardLocation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@SupportedAnnotationTypes("org.jsonk.Json")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class JsonkProcessor extends AbstractProcessor {

    private Elements elements;
    private Types types;
    private Trees trees;
    private MyNames myNames;
    private Annotations annotations;
    private TypesExt typesExt;
    private JsonApiUsageScanner scanner;
    private Introspects introspects;
    private final Env env = new Env();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        processingEnv = jbUnwrap(ProcessingEnvironment.class, processingEnv);
        elements = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
        trees = Trees.instance(processingEnv);
        myNames = new MyNames(elements);
        typesExt = new TypesExt(myNames);
        annotations = new Annotations(myNames, typesExt);
        introspects = new Introspects(annotations, myNames, typesExt, env);
        scanner = new JsonApiUsageScanner(trees, myNames, typesExt, annotations);
    }

    private static <T> T jbUnwrap(Class<? extends T> iface, T wrapper) {
        T unwrapped = null;
        try {
            final Class<?> apiWrappers = wrapper.getClass().getClassLoader().loadClass("org.jetbrains.jps.javac.APIWrappers");
            final Method unwrapMethod = apiWrappers.getDeclaredMethod("unwrap", Class.class, Object.class);
            unwrapped = iface.cast(unwrapMethod.invoke(null, iface, wrapper));
        }
        catch (Throwable ignored) {}
        return unwrapped != null? unwrapped : wrapper;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //noinspection unchecked
        var classes = (Set<? extends TypeElement>) roundEnv.getElementsAnnotatedWith(Json.class);
        if (!classes.isEmpty()) {
            generateAdapters(classes);
            generateAdapterFactories(classes);
            generateMapping(classes);
            generateFactoryMapping(classes);
        }
        if (roundEnv.processingOver()) {
            for (Element rootElement : roundEnv.getRootElements()) {
                var path = trees.getPath(rootElement);
                scanner.scan(path, null);
            }
        }
        return false;
    }

    @SneakyThrows
    private void generateAdapters(Set<? extends TypeElement> classes) {
        for (var clazz : classes) {
            if (annotations.hasCustomAdapter(clazz))
                continue;
            var builderFile = processingEnv.getFiler().createSourceFile(clazz.getQualifiedName() + "Adapter");
            var adapterGenerator = new AdapterGenerator(clazz, elements, types, typesExt, introspects, myNames, annotations, env);
            var text = adapterGenerator.generate();
            if (!env.hasError()) {
                try (var writer = builderFile.openWriter()) {
                    writer.write(text);
                }
            }
            flushDiags(clazz, env);
        }
    }

    @SneakyThrows
    private void generateAdapterFactories(Set<? extends TypeElement> classes) {
        for (var clazz : classes) {
            if (clazz.getTypeParameters().isEmpty() || annotations.hasCustomAdapter(clazz) || annotations.hasCustomAdapterFactory(clazz))
                continue;
            var builderFile = processingEnv.getFiler().createSourceFile(clazz.getQualifiedName() + "AdapterFactory");
            var gen = new AdapterFactoryGenerator(clazz, elements);
            var text = gen.generate();
            if (!env.hasError()) {
                try (var writer = builderFile.openWriter()) {
                    writer.write(text);
                }
            }
            flushDiags(clazz, env);
        }
    }

    private void flushDiags(TypeElement clazz, Env env) {
        var root = trees.getPath(clazz).getCompilationUnit();
        for (var diag : env.getDiags()) {
            trees.printMessage(
                    diag.kind().toKind(),
                    diag.message(),
                    trees.getTree(diag.element()),
                    root
            );
        }
        env.clearDiags();
    }

    @SneakyThrows
    private void generateMapping(Set<? extends TypeElement> classes) {
        var resource = "META-INF/services/" + Adapter.class.getName();
        var existing = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", resource);
        var content = existing.getLastModified() != 0 ? existing.getCharContent(true).toString() : "";
        var file = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", resource);
        var serviceMappingGenerator = new ServiceMappingGenerator(annotations, myNames, typesExt);
        try (var writer = file.openWriter()) {
            writer.write(serviceMappingGenerator.generate(readExisting(content), classes));
        }
    }

    @SneakyThrows
    private void generateFactoryMapping(Set<? extends TypeElement> classes) {
        var resource = "META-INF/services/" + AdapterFactory.class.getName();
        var existing = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", resource);
        var content = existing.getLastModified() != 0 ? existing.getCharContent(true).toString() : "";
        var file = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", resource);
        var gen = new AdapterFactoryMappingGenerator(annotations, myNames, typesExt);
        try (var writer = file.openWriter()) {
            writer.write(gen.generate(readExisting(content), classes));
        }
    }

    private Set<String> readExisting(String content) {
        var lines = content.split("\n");
        var set = new HashSet<String>();
        for (String line : lines) {
            if (!line.isEmpty())
                set.add(line);
        }
        return set;
    }

}
