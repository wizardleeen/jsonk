package org.jsonk.processor;

import javax.lang.model.element.TypeElement;
import java.util.Collection;
import java.util.Set;

class AdapterFactoryMappingGenerator extends AbstractGenerator {

    private final Annotations annotations;
    private final TypesExt typesExt;
    private final MyNames myNames;

    AdapterFactoryMappingGenerator(Annotations annotations, MyNames myNames, TypesExt typesExt) {
        this.annotations = annotations;
        this.typesExt = typesExt;
        this.myNames = myNames;
    }

    public String generate(Set<String> existing, Collection<? extends TypeElement> classes) {
        existing.forEach(this::writeln);
        for (TypeElement clazz : classes) {
            if (clazz.getTypeParameters().isEmpty())
                continue;
            var annotation = annotations.getAnnotation(clazz, myNames.classJson);
            if (annotations.hasCustomAdapter(clazz))
                continue;
            var adapterFactory = annotations.getAdapterFactory(annotation);
            var name = adapterFactory == null || typesExt.getClassName(adapterFactory).equals(myNames.classAdapterFactory)?
                    clazz.getQualifiedName() + "AdapterFactory" : typesExt.getClassName(adapterFactory).toString();
            if (!existing.contains(name))
                writeln(name);
        }
        return toString();
    }

}
