package org.jsonk.processor;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;

class JsonApiUsageScanner extends TreePathScanner<Void, Void> {

    private final Trees trees;
    private final CommonNames commonNames;
    private final TypesExt typesExt;
    private final Annotations annotations;


    JsonApiUsageScanner(Trees trees, CommonNames commonNames, TypesExt typesExt, Annotations annotations) {
        this.trees = trees;
        this.commonNames = commonNames;
        this.typesExt = typesExt;
        this.annotations = annotations;
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree node, Void unused) {
        if (trees.getElement(getCurrentPath()) instanceof ExecutableElement exec) {
            var clazz = (TypeElement) exec.getEnclosingElement();
            if (clazz.getQualifiedName().equals(commonNames.classJsonk)) {
                if (exec.getSimpleName().equals(commonNames.toJson))
                    checkToJson(node);
                else if (exec.getSimpleName().equals(commonNames.fromJson))
                    checkFromJson(node);
            }
        }
        return super.visitMethodInvocation(node, unused);
    }

    private void checkToJson(MethodInvocationTree tree) {
        var arg = tree.getArguments().getFirst();
        var type = trees.getTypeMirror(new TreePath(getCurrentPath(), arg));
        if (typesExt.getValueType(type) instanceof DeclaredType declType) {
            var clazz = (TypeElement) declType.asElement();
            if (clazz.getQualifiedName().equals(commonNames.classObject))
                return;
            if (!annotations.isAnnotationPresent(clazz, commonNames.classJson)) {
                trees.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Type " + clazz.getQualifiedName() + " must be annotated with @org.jsonk.Json to be used with this API.",
                        tree,
                        getCurrentPath().getCompilationUnit()
                );
            }
        }
    }

    private void checkFromJson(MethodInvocationTree tree) {
        var arg = tree.getArguments().getFirst();
        var type = trees.getTypeMirror(new TreePath(getCurrentPath(), arg));
        if (type instanceof DeclaredType declaredType) {
            var clazz = (TypeElement) declaredType.asElement();
            if (clazz.getQualifiedName().equals(commonNames.classClass) && !declaredType.getTypeArguments().isEmpty()) {
                var typeArg = declaredType.getTypeArguments().getFirst();
                if (typeArg instanceof DeclaredType declType1) {
                    var clazz1 = (TypeElement) declType1.asElement();
                    if (!clazz1.getQualifiedName().equals(commonNames.classObject)
                            && !annotations.isAnnotationPresent(clazz1, commonNames.classJson)) {
                        trees.printMessage(
                                Diagnostic.Kind.ERROR,
                                "Type " + clazz1.getQualifiedName() + " must be annotated with @org.jsonk.Json to be used with this API.",
                                tree,
                                getCurrentPath().getCompilationUnit()
                        );
                    }
                }
            }
        }
    }


}
