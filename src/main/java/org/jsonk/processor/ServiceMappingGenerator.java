package org.jsonk.processor;

import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.Set;

class ServiceMappingGenerator extends AbstractGenerator {

    private final Annotations annotations;
    private final MyNames myNames;
    private final TypesExt typesExt;

    public ServiceMappingGenerator(Annotations annotations, MyNames myNames, TypesExt typesExt) {
        this.annotations = annotations;
        this.myNames = myNames;
        this.typesExt = typesExt;
    }

    public String generate(Set<String> existing, Collection<? extends TypeElement> classes) {
        existing.forEach(this::writeln);
        for (TypeElement cls : classes) {
            if (!cls.getTypeParameters().isEmpty())
                continue;
            var adapter = annotations.getAdapter(annotations.getAnnotation(cls, myNames.classJson));
            var adapterName = adapter == null || typesExt.getClassName(adapter).equals(myNames.classAdapter)?
                cls.getQualifiedName().toString() + "Adapter" : typesExt.getClassName(adapter).toString();
            if (!existing.contains(adapterName))
                writeln(adapterName);

        }
        return toString();
    }

}
