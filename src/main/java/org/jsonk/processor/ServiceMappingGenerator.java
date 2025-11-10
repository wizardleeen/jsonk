package org.jsonk.processor;

import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.Set;

class ServiceMappingGenerator extends AbstractGenerator {

    private final Annotations annotations;
    private final CommonNames commonNames;
    private final TypesExt typesExt;

    public ServiceMappingGenerator(Annotations annotations, CommonNames commonNames, TypesExt typesExt) {
        this.annotations = annotations;
        this.commonNames = commonNames;
        this.typesExt = typesExt;
    }

    public String generate(Set<String> existing, Collection<? extends TypeElement> classes) {
        existing.forEach(this::writeln);
        for (TypeElement cls : classes) {
            if (!cls.getTypeParameters().isEmpty())
                continue;
            var adapter = annotations.getAdapter(annotations.getAnnotation(cls, commonNames.classJson));
            var adapterName = adapter == null || typesExt.getClassName(adapter).equals(commonNames.classAdapter)?
                cls.getQualifiedName().toString() + "Adapter" : typesExt.getClassName(adapter).toString();
            if (!existing.contains(adapterName))
                writeln(adapterName);

        }
        return toString();
    }

}
