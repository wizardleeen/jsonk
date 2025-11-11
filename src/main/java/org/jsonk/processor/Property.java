package org.jsonk.processor;

import lombok.Getter;
import lombok.Setter;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.Map;
import java.util.Objects;

class Property implements Variable {

    private final Annotations annotations;
    private final CommonNames commonNames;
    private final TypesExt typesExt;
    private final String name;
    @Getter
    @Setter
    private VariableElement field;

    private RecordComponentElement recordComponent;
    @Setter
    @Getter
    private ExecutableElement getter;
    @Setter
    @Getter
    private ExecutableElement setter;
    private PropertyConfig config;
    private Type type;
    private final Env env;

    public Property(Annotations annotations, CommonNames commonNames, TypesExt typesExt, String name, Env env) {
        this.annotations = annotations;
        this.commonNames = commonNames;
        this.typesExt = typesExt;
        this.name = name;
        this.env = env;
    }

    public boolean isWritable() {
        return setter != null || field != null && !field.getModifiers().contains(Modifier.FINAL);
    }

    public boolean isReadable() {
        return getter != null || field != null;
    }

    public boolean includeNull() {
        return getConfig().includeNull();
    }

    public void generateRead(AdapterGenerator adapterGenerator) {
        env.setCurrentElement(getMainElement());
        if (setter != null && ElementUtil.isPublic(setter))
            generateReadBySetter(adapterGenerator);
        else if (field != null && ElementUtil.isPublic(field))
            generateReadByField(adapterGenerator);
        else
            throw new IllegalStateException("Cannot generate read for property: " + name);
    }

    private void generateReadByField(AdapterGenerator adapterGenerator) {
        var fieldName = field.getSimpleName().toString();
        adapterGenerator.write("o.").write(fieldName).write(" = ");
        adapterGenerator.generateRead(field.asType(), getAttributes());
    }

    private void generateReadBySetter(AdapterGenerator adapterGenerator) {
        adapterGenerator.write("o.").write(setter.getSimpleName()).write("(");
        adapterGenerator.generateRead(setter.getParameters().getFirst().asType(), getAttributes());
        adapterGenerator.write(")");
    }

    public void generateRead(String value, AdapterGenerator generator) {
        if (setter != null)
            generator.write("o.").write(setter.getSimpleName()).write("(").write(value).writeln(");");
        else if (field != null)
            generator.write("o.").write(field.getSimpleName()).write(" = ").write(value).writeln(";");
        else
            throw new IllegalStateException("Cannot generate read for property: " + name);
    }

    public Map<String, Object> getAttributes() {
        return getConfig().getAttributes();
    }
    private String getDateFormatField() {
        if (getConfig().dateTimeFormat() != null)
            return name + "Fmt";
        else
            return null;
    }

    public void generateWrite(Runnable writeComma, AdapterGenerator adapterGenerator) {
        env.setCurrentElement(getMainElement());
        if (getter != null && ElementUtil.isPublic(getter))
            generateWriteByGetter(writeComma, adapterGenerator);
        else if (field != null && ElementUtil.isPublic(field))
            generateWriteByField(writeComma, adapterGenerator);
        else
            throw new IllegalStateException("Cannot generate write for property: " + name);
    }

    private void generateWriteByField(Runnable writeComma, AdapterGenerator adapterGenerator) {
        if (isNullable()) {
            var v = adapterGenerator.writeVarDecl(() -> adapterGenerator.write("o.").write(field.getSimpleName()));
            adapterGenerator.write("if (").write(v).writeln(" != null) {");
            adapterGenerator.indent();
            writeComma.run();
            adapterGenerator.generateWriteName(name());
            adapterGenerator.generateWrite(
                    v,
                    field.asType(),
                    isNullable() && includeNull(),
                    getAttributes()
            );
            adapterGenerator.deIndent();
            adapterGenerator.writeln("}");
        } else {
            writeComma.run();
            adapterGenerator.generateWriteName(name());
            adapterGenerator.generateWrite(
                    "o." + field.getSimpleName().toString(),
                    field.asType(),
                    isNullable() && includeNull(),
                    getAttributes()
            );
        }
    }

    private void generateWriteByGetter(Runnable writeComma, AdapterGenerator adapterGenerator) {
        if (isNullable()) {
            var v = adapterGenerator.writeVarDecl(() -> adapterGenerator.write("o.").write(getter.getSimpleName()).write("()"));
            adapterGenerator.write("if (").write(v).writeln(" != null) {");
            adapterGenerator.indent();
            writeComma.run();
            adapterGenerator.generateWriteName(name);
            adapterGenerator.generateWrite(
                    v,
                    getter.getReturnType(),
                    isNullable() && includeNull(),
                    getAttributes()
            );
            adapterGenerator.deIndent();
            adapterGenerator.writeln("}");
        } else {
            writeComma.run();
            adapterGenerator.generateWriteName(name);
            adapterGenerator.generateWrite(
                    "o." + getter.getSimpleName() + "()",
                    getter.getReturnType(),
                    isNullable() && includeNull(),
                    getAttributes()
            );
        }
    }

    public Type getType() {
        if (type == null)
            type = Type.from(type());
        return type;
    }

    @Override
    public TypeMirror type() {
        if (recordComponent != null)
            return recordComponent.asType();
        else if (getter != null)
            return getter.getReturnType();
        else if (setter != null)
            return setter.getParameters().getFirst().asType();
        else if (field != null)
            return field.asType();
        else
            throw new IllegalStateException("Cannot determine property type for property: " + name);
    }

    public void setRecordComponent(RecordComponentElement recordComponent) {
        this.recordComponent = recordComponent;
        getter = recordComponent.getAccessor();
    }

    private PropertyConfig getConfig() {
        if (config == null) {
            if (recordComponent != null && annotations.isAnnotationPresent(recordComponent, commonNames.classJsonProperty))
                config = annotations.getPropertyConfig(recordComponent);
            if (getter != null && annotations.isAnnotationPresent(getter, commonNames.classJsonProperty))
                config = annotations.getPropertyConfig(getter);
            else if (setter != null && annotations.isAnnotationPresent(setter, commonNames.classJsonProperty))
                config = annotations.getPropertyConfig(setter);
            else if (field != null && annotations.isAnnotationPresent(field, commonNames.classJsonProperty))
                config =  annotations.getPropertyConfig(field);
            else
                config = PropertyConfig.DEFAULT;
        }
        return config;
    }

    @Override
    public String name() {
        var config = getConfig();
        return config.name() != null ? config.name() : name;
    }

    public String getDateTimeFormat() {
        return getConfig().dateTimeFormat();
    }

    public boolean isIgnored() {
        return recordComponent != null && annotations.isIgnorePresent(recordComponent)
                || getter != null && annotations.isIgnorePresent(getter)
                || setter != null && annotations.isIgnorePresent(setter)
                || field != null && annotations.isIgnorePresent(field);
    }

    public boolean isNullable() {
        return getType().isNullable();
    }

    public Object getValueType() {
        return getType().getValueType();
    }

    public boolean canSkip() {
        return isNullable() && !includeNull();
    }

    public Element getMainElement() {
        if (recordComponent != null)
            return recordComponent;
        if (field != null)
            return field;
        return Objects.requireNonNullElseGet(getter, () -> Objects.requireNonNull(setter));
    }

//    public Tree getNode() {
//        if (recordComponent != null)
//            return trees.getTree(recordComponent);
//        if (field != null)
//            return trees.getTree(field);
//        if (getter != null)
//            return trees.getTree(getter);
//        if (setter != null)
//            return trees.getTree(setter);
//        throw new IllegalStateException("Cannot get node for property: " + name);
//    }

}
