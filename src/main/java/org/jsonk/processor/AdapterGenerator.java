package org.jsonk.processor;

import org.jsonk.util.PerfectHashTable;
import org.jsonk.util.StringUtil;
import org.jsonk.util.Util;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Parameterizable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;

class AdapterGenerator extends AbstractGenerator {

    private final TypeElement clazz;
    private final Elements elements;
    private final Types types;
    private final TypesExt typesExt;
    private final Introspects introspects;
    private final MyNames myNames;
    private final Annotations annotations;
    private final Env env;
    private final Map<AdapterKey, String> adapters = new HashMap<>();
    private PerfectHashTable mphConfig;
    private final Map<String, String> charsFields = new HashMap<>();

    public AdapterGenerator(TypeElement clazz, Elements elements, Types types, TypesExt typesExt, Introspects introspects, MyNames myNames, Annotations annotations, Env env) {
        this.clazz = clazz;
        this.elements = elements;
        this.types = types;
        this.typesExt = typesExt;
        this.introspects = introspects;
        this.myNames = myNames;
        this.annotations = annotations;
        this.env = env;
    }

   public String generate() {
        setCurrentElement(clazz);
        var element = clazz;
        var clazz = introspects.introspect(element, env);
        var pkgName = elements.getPackageOf(element).getQualifiedName().toString();
        generatePhf(clazz);
        write("package ").write(pkgName).writeln(";").writeln();
        generateImports();
        write("public class ")
                .write(clazz.getName())
                .write("Adapter");
        var hasTypeParams = !clazz.getElement().getTypeParameters().isEmpty();
        if (hasTypeParams)
            writeTypeParams(clazz.getElement());
        write(" implements Adapter").write("<")
                .write(clazz.getQualName());
       if (hasTypeParams)
           writeTypeParams(clazz.getElement());
        writeln("> {");
        indent();
        writeln();
        if (generateFields(clazz))
            writeln();
        if (hasTypeParams) {
            generateConstructor();
            writeln();
        }
        generateInit(clazz);
        writeln();
        generateToJson(clazz);
        writeln();
        generateFromJson(clazz);
        writeln();
        generateGetKey(element);
        writeln();
        deIndent();
        writeln("}");
        return toString();
    }

    private void generateConstructor() {
        write("public ").write(clazz.getSimpleName()).write("Adapter").writeln("(Type type) {");
        indent();
        writeln("this.type = type;");
        deIndent();
        writeln("}");
    }

    private void writeTypeParams(Parameterizable element) {
        write("<");
        var it = element.getTypeParameters().iterator();
        write(it.next().getSimpleName());
        while (it.hasNext()) {
            write(", ");
            write(it.next().getSimpleName());
        }
        write(">");
    }

    private void setCurrentElement(Element element) {
        env.setCurrentElement(element);
    }

    private void generatePhf(Clazz clazz) {
        if (clazz.getConstructor() != null) {
            var keys = new LinkedHashSet<>(Util.map(clazz.getConstructorParams(), Parameter::name));
            for (Property property : clazz.getProperties()) {
                if (property.getSetter() != null)
                    keys.add(property.name());
            }
            mphConfig = PerfectHashTable.generate(new ArrayList<>(keys));
        }
    }

    private boolean generateFields(Clazz clazz) {
        var ref = new Object() {
            boolean anyField;
        };
        if (!clazz.getElement().getTypeParameters().isEmpty()) {
            ref.anyField = true;
            writeln("private final Type type;");
        }
        clazz.forEachReferenceType((t, attrs) -> {
            ref.anyField = true;
            write("private Adapter<").write(t).write("> ");
            writeAdapterFieldName(t, attrs).writeln(";");
        });
        if (mphConfig != null) {
            writeln("private static final char[][] keys = new char[][] {");
            indent();
            writeList(Arrays.asList(mphConfig.getTable()),
                        k -> {
                            if (k != null)
                                writeCharArray(StringUtil.escape(k), false);
                            else
                                write("null");
                        },
                        ",\n"
            );
            deIndent();
            writeln("};");
            write("private static final int[] ordinals = new int[] {")
                    .writeList(Arrays.stream(mphConfig.getOrdinals()).boxed().toList(),
                            d -> write(d + ""),
                            ", "
                            ).writeln("};");
            write("private static final long seed = ").write(mphConfig.getSeed() + "").writeln("L;");
            ref.anyField = true;
        }
        var names = new LinkedHashSet<String>();
        clazz.getTypeNames().forEach(tn -> {
            names.add(tn.property());
            names.add(tn.name());
        });
        if (clazz.getConstructor() != null)
            clazz.getConstructorParams().forEach(p -> names.add(p.name()));
        clazz.getProperties().forEach(p -> names.add(p.name()));
        for (var name : names) {
            var fieldName = "chars" + charsFields.size();
            charsFields.put(name, fieldName);
            write("private static final char[] ").write(fieldName).write(" = ");
            writeCharArray(StringUtil.escape(name), true);
            writeln(";");
            ref.anyField = true;
        }
        return ref.anyField;
    }

    private AdapterGenerator writeCharArray(String s, boolean withQuotes) {
        write("new char[] {");
        if (withQuotes)
            write("'\"'");
        for (int i = 0; i < s.length(); i++) {
            var chars = StringUtil.escapeChar(s.charAt(i));
            for (int j = 0; j < chars.length(); j++) {
                if (withQuotes || i > 0 || j > 0)
                    write(", ");
                write('\'').write(StringUtil.escapeChar(chars.charAt(j))).write('\'');
            }
        }
        if (withQuotes)
            write(", '\"'");
        return (AdapterGenerator) write('}');
    }

    private String getCharsField(String name) {
        return Objects.requireNonNull(charsFields.get(name), () -> "chars field not found for: " + name);
    }

    private void generateInit(Clazz clazz) {
        writeln("@Override");
        writeln("public void init(AdapterEnv env) {");
        indent();
        clazz.forEachReferenceType((t, attrs) -> {
            var adapterName = getAdapterName(t, attrs);
            writeAssignment(
                () -> write(adapterName),
                () -> {
                    if (t.isParameterized() || t instanceof TypeVariable)
                        write("(Adapter) ").write("env.getAdapter(").write(getTypeString(t));
                    else
                        write("env.getAdapter(").write(t.getErasedText()).write(".class");
                    if (!attrs.isEmpty())
                        write(", ").writeAttributes(attrs);
                    write(")");
                });
        });
        deIndent();
        writeln("}");
    }

    private String getTypeString(Type type) {
        return switch (type) {
            case org.jsonk.processor.PrimitiveType primitiveType -> "Type.from(" + primitiveType.name() + ".class)";
            case org.jsonk.processor.ArrayType ignored -> "Type.from(" + type.getErasedText() + ".class)";
            case ClassType dt -> {
                var className = dt.qualName().toString();
                var sb = new StringBuilder();
                sb.append("Type.from(").append(className).append(".class");
                for (var typeArg : dt.typeArgs()) {
                    sb.append(", ");
                    sb.append(getTypeString(typeArg));
                }
                sb.append(")");
                yield sb.toString();
            }
            case TypeVariable typeVar -> {
                var elem = typeVar.element();
                var decl = (Parameterizable) elem.getEnclosingElement();
                var index = decl.getTypeParameters().indexOf(elem);
                yield "type.typeArguments().get(" + index + ")";
            }
            default -> throw new IllegalStateException("Unknown type: " + type);
        };
    }

    private AdapterGenerator writeAttributes(Map<String, Object> attrs) {
        if (attrs.isEmpty()) {
            write("Map.of()");
            return this;
        }
        writeln("Map.ofEntries(");
        indent();
        var first = true;
        for (var e : attrs.entrySet()) {
            var key = e.getKey();
            var value = e.getValue();
            if (first)
                first = false;
            else
                writeln(", ");
            write("Map.entry(").write("\"").write(StringUtil.escape(key)).write("\"")
                    .write(", \"").write(StringUtil.escape(Objects.toString(value))).write("\")");
        }
        writeln();
        deIndent();
        writeln(")");
        return this;
    }

    private AdapterGenerator writeAdapterFieldName(Type type, Map<String, Object> attributes) {
        if (type instanceof ClassType ct && ct.element() == clazz)
            write("this");
        else {
            // StringUtil.decapitalize(type.asElement().getSimpleName().toString())
            var adapterName = getAdapterName(type, attributes);
            write(adapterName);
        }
        return this;
    }

    private String getAdapterName(Type type, Map<String, Object> attributes) {
        var key = new AdapterKey(type, attributes);
        return adapters.computeIfAbsent(key, k -> "adapter" + adapters.size());
    }

    private AdapterGenerator writeDateTimeFormatFieldName(String fieldName) {
        write(fieldName).write("Fmt");
        return this;
    }

    private void generateGetKey(TypeElement clazz) {
        writeln("@Override");
        writeln("public AdapterKey getKey() {");
        indent();
        if (clazz.getTypeParameters().isEmpty())
            writeReturn(() -> write("AdapterKey.of(").write(clazz.getQualifiedName()).write(".class)"));
        else
            writeReturn(() -> write("AdapterKey.of(type)"));
        deIndent();
        writeln("}");
    }

    private void generateImports() {
        writeln("import java.util.Map;");
        writeln("import java.util.List;");
        writeln("import org.jsonk.JsonWriter;");
        writeln("import org.jsonk.JsonReader;");
        writeln("import org.jsonk.Adapter;");
        writeln("import org.jsonk.Type;");
        writeln("import org.jsonk.AdapterKey;");
        writeln("import org.jsonk.AdapterEnv;");
        writeln();
    }

    private void generateToJson(Clazz clazz) {
        if (clazz.isPolymorphic())
            generatePolymorphicToJson(clazz);
        else {
            writeln("@Override");
            generateToJson0(clazz, "toJson");
        }
    }

    private void generatePolymorphicToJson(Clazz clazz) {
        writeln("@Override");
        write("public void toJson(").write(clazz).writeln(" o, JsonWriter writer) {");
        indent();
        writeln("switch(o) {");
        indent();
        clazz.forEachSubType((name, type) -> {
            var v = nextVariable();
            write("case ").write(type).write(" ").write(v).write(" -> ");
            if (type.equals(clazz.asType()))
                write("toJson0(").write(v).write(", writer);");
            else
                writeAdapterFieldName(Type.from(type), Map.of()).write(".toJson(").write(v).writeln(", writer);");
        });
        write("default -> ").writeThrow("new IllegalStateException(\"Unexpected value: \" + o)");
        deIndent();
        writeln("}");
        deIndent();
        writeln("}");
        if (clazz.isConcreteSuperType()) {
            writeln();
            generateToJson0(clazz, "toJson0");
        }
    }

    private void generateToJson0(Clazz clazz, String methodName) {
        write("public void ").write(methodName).write("(").write(clazz).writeln(" o, JsonWriter writer) {");
        indent();
        writeln("writer.writeLBrace();");
        if (clazz.getTypeNames().isEmpty()) {
            var cnt = clazz.numWritableProps();
            if (cnt == 1) {
                clazz.firstWritableProp().generateWrite(() -> {
                }, this);
            } else if (cnt > 1) {
                var it = clazz.getProperties().iterator();
                var first = clazz.firstWritableProp();
                while (it.hasNext() && it.next() != first) ;
                if (first.canSkip()) {
                    writeln("var first = true;");
                    first.generateWrite(() -> writeln("first = false;"), this);
                    while (it.hasNext()) {
                        var prop = it.next();
                        if (prop.isReadable()) {
                            if (prop.canSkip()) {
                                prop.generateWrite(() -> writeIfElse(
                                        () -> write("first"),
                                        () -> writeln("first = false;"),
                                        () -> writeln("writer.writeComma();")
                                ), this);
                            } else {
                                prop.generateWrite(() -> writeIf(
                                        () -> write("!first"),
                                        () -> writeln("writer.writeComma();")
                                ), this);
                                break;
                            }
                        }
                    }
                } else
                    first.generateWrite(() -> {
                    }, this);
                while (it.hasNext()) {
                    var prop = it.next();
                    if (prop.isReadable())
                        prop.generateWrite(() -> writeln("writer.writeComma();"), this);
                }
            }
        } else {
            for (TypeName typeName : clazz.getTypeNames()) {
                write("writer.write(").write(getCharsField(typeName.property())).writeln(");");
                writeln("writer.writeColon();");
                write("writer.write(").write(getCharsField(typeName.name())).writeln(");");
            }
            for (Property prop : clazz.getProperties()) {
                prop.generateWrite(() -> writeln("writer.writeComma();"), this);
            }
        }
        writeln("writer.writeRBrace();");
        deIndent();
        writeln("}");

    }

    private void generateFromJson(Clazz clazz) {
        if (clazz.isPolymorphic()) {
            generatePolymorphicFromJson(clazz);
            if (Util.anyMatch(clazz.getSubTypes(), t -> t.type().equals(clazz.asType()))) {
                writeln();
                generateFromJson0(clazz, "fromJson0");
            }
        } else {
            writeln("@Override");
            generateFromJson0(clazz, "fromJson");
        }
    }

    private void generateFromJson0(Clazz clazz, String methodName) {
        write("public ").write(clazz).write(" ").write(methodName).writeln("(JsonReader reader) {");
        indent();
        generateDefaultFromJson(clazz);
        writeln("return o;");
        deIndent();
        writeln("}");

    }

    private void generatePolymorphicFromJson(Clazz clazz) {
        writeln("@Override");
        write("public ").write(clazz).writeln(" fromJson(JsonReader reader) {");
        indent();
        writeln("reader.mark();");
        writeln("reader.accept('{');");
        writeln("do {");
        indent();
        write("""
                reader.skipWhitespace();
                if (reader.is('}'))
                    break;
                var name = reader.readString();
                reader.skipWhitespace();
                reader.accept(':');
                reader.skipWhitespace();
                """);
        var typeProp = clazz.getTypeProperty();
        writeIfElse(
                () -> write("name.equals(\"").write(typeProp).write("\")"),
                () -> {
                    writeVarDecl("typeProp", () -> write("reader.readString()"));
                    writeln("switch (typeProp) {");
                    indent();
                    for (SubType subType : clazz.getSubTypes()) {
                        write("case \"").write(subType.value()).writeln("\" -> {");
                        indent();
                        writeln("reader.rollback();");
                        if (subType.type().equals(clazz.asType()))
                            writeReturn("fromJson0(reader)");
                        else
                            writeReturn(() -> writeAdapterFieldName(Type.from(subType.type()), Map.of()).write(".fromJson(reader)"));
                        deIndent();
                        writeln("}");
                    }
                    writeln("default -> throw reader.parseException(\"Unknown type type property: \" + typeProp);");
                    deIndent();
                    writeln("}");
                },
                () -> writeln("reader.skipValue();")
        );
        writeln("reader.skipWhitespace();");
        deIndent();
        writeln("} while (reader.skip(','));");
        writeThrow(() -> write("reader.parseException(\"Type property '")
                .write(typeProp)
                .write("' not found\")"));
        deIndent();
        writeln("}");
    }

    private AdapterGenerator write(Clazz clazz) {
        write(clazz.getQualName());
        if (clazz.isParameterized())
            writeTypeParams(clazz.getElement());
        return this;
    }

    private void generateDefaultFromJson(Clazz clazz) {
        var variables = new HashMap<String, String>();
        var paramNames = Util.mapToSet(clazz.getConstructorParams(), Parameter::name);
        for (var param : clazz.getConstructorParams()) {
            var v = nextVariable();
            generateVariableDecl(param, v);
            variables.put(param.name(), v);
        }
        for (Property property : clazz.getProperties()) {
            if (paramNames.contains(property.name()) || !property.isWritable())
                continue;
            var v = nextVariable();
            generateVariableDecl(property, v);
            variables.put(property.name(), v);
        }
        writeln("reader.accept('{');");
        writeln("do {");
        indent();
        var ph = Objects.requireNonNull(mphConfig);
        write("""
                 reader.skipWhitespace();
                if (reader.is('}'))
                    break;
                var name = reader.readName(keys, ordinals, seed);
                reader.skipWhitespace();
                reader.accept(':');
                reader.skipWhitespace();
                switch(name) {
                """);
        indent();
        for (var param : clazz.getConstructorParams()) {
            write("case ").write(ph.get(param.name())).write(" -> ");
//            write("case \"").write(param.name()).write("\" -> ");
            write(variables.get(param.name())).write(" = ");
            generateRead(param.type(), param.getAttributes());
            writeln(";");
        }
        for (Property property : clazz.getProperties()) {
            if (paramNames.contains(property.name()) || !property.isWritable())
                continue;
            write("case ").write(ph.get(property.name())).write(" -> ");
//            write("case \"").write(property.name()).write("\" -> ");
            write(variables.get(property.name())).write(" = ");
            generateRead(property.type(), property.getAttributes());
            writeln(";");
        }
        writeln("default -> reader.skipValue();");
        deIndent();
        writeln("}");
        writeln("reader.skipWhitespace();");
        deIndent();
        writeln("} while (reader.skip(','));");
        writeln("reader.accept('}');");
        writeVarDecl("o", () -> {
            write("new ").write(clazz.getElement().getSimpleName()).write("(");
            writeList(clazz.getConstructorParams(), p -> write(variables.get(p.name())), ", ");
            write(")");
        });
        for (Property property : clazz.getProperties()) {
            if (paramNames.contains(property.name()) || !property.isWritable())
                continue;
            property.generateRead(variables.get(property.name()), this);
        }
    }

    private void generateVariableDecl(Variable variable, String name) {
        write(variable.type()).write(" ").write(name).write(" = ")
                .write(getInitialValue(variable.type())).writeln(";");
    }

    private void generateReadProps(Clazz clazz) {
        var ignoredPropNames = Util.mapToSet(clazz.getConstructorParams(), Parameter::name);
        writeln("reader.accept('{');");
        writeln("do {");
        indent();
        write("""
                reader.skipWhitespace();
                if (reader.is('}'))
                    break;
                var name = reader.readString();
                reader.skipWhitespace();
                reader.accept(':');
                reader.skipWhitespace();
                switch(name) {
                """);
        indent();
//        var phf = Objects.requireNonNull(propConfig).createPhf();
        for (var prop : clazz.getProperties()) {
            if (ignoredPropNames.contains(prop.name()) || !prop.isWritable())
                continue;
//            write("case ").write(phf.get(prop.name())).write(" -> ");
            write("case \"").write(prop.name()).write("\" -> ");
            prop.generateRead(this);
            writeln(";");
        }
        writeln("default -> reader.skipValue();");
        deIndent();
        writeln("}");
        writeln("reader.skipWhitespace();");
        deIndent();
        writeln("} while (reader.skip(','));");
        writeln("reader.accept('}');");
    }

    void generateWrite(String value, TypeMirror type, boolean nullable, Map<String, Object> attrs) {
        switch (type.getKind()) {
            case BYTE -> write("writer.writeByte(").write(value).write(");");
            case SHORT -> write("writer.writeShort(").write(value).writeln(");");
            case INT -> write("writer.writeInt(").write(value).writeln(");");
            case LONG -> write("writer.writeLong(").write(value).writeln(");");
            case FLOAT -> write("writer.writeFloat(").write(value).writeln(");");
            case DOUBLE -> write("writer.writeDouble(").write(value).writeln(");");
            case CHAR -> write("writer.writeChar(").write(value).writeln(");");
            case BOOLEAN -> write("writer.writeBoolean(").write(value).writeln(");");
            case TYPEVAR, ARRAY -> write("writer.").write(getWriteMethod(type, nullable)).write("(")
                    .write(value).write(", ").writeAdapterFieldName(Type.from(type), attrs).writeln(");");
            case DECLARED -> {
                var dt = (DeclaredType) type;
                var cl = (TypeElement) dt.asElement();
                if (cl.getKind() == ElementKind.ENUM)
                    write("writer.writeString(").write(value).writeln(".name());");
                else if (cl.getQualifiedName() == myNames.classString)
                    write("writer.").write(nullable ? "writeStringOrNull(" : "writeString(").write(value).writeln(");");
                else
                    write("writer.").write(getWriteMethod(type, nullable)).write("(")
                            .write(value).write(", ").writeAdapterFieldName(Type.from(type), attrs).writeln(");");
            }
            default -> throw new IllegalStateException("Unknown type kind: " + type.getKind());
                    //write("// TODO: handle ").write(type.getKind().toString()).writeln("");
        }
    }

    private String getWriteMethod(TypeMirror type, boolean nullable) {
        if (isValueType(type))
            return nullable ? "writeValueOrNull" : "writeValue";
        else
            return nullable ? "writeObjectOrNull" : "writeObject";
    }

    private boolean isValueType(TypeMirror type) {
        return switch (type) {
            case PrimitiveType ignored -> true;
            case ArrayType arrayType -> isValueType(arrayType.getComponentType());
            case DeclaredType declaredType -> switch (typesExt.getClassName(declaredType).toString()) {
                case "java.lang.Byte", "java.lang.Short", "java.lang.Integer", "java.lang.Long",
                        "java.lang.Float", "java.lang.Double", "java.lang.Boolean", "java.lang.Character",
                        "java.lang.String", "java.time.LocalDateTime", "java.time.LocalDate", "java.time.LocalTime",
                        "java.time.OffsetDateTime", "java.time.OffsetTime", "java.time.ZonedDateTime",
                        "java.util.Date" -> true;
                default -> false;
            };
            default -> false;
        };
    }

    AdapterGenerator generateRead(TypeMirror type, Map<String, Object> attrs) {
        switch (type.getKind()) {
            case BYTE -> write("reader.readByte()");
            case SHORT -> write("reader.readShort()");
            case INT -> write("reader.readInt()");
            case LONG -> write("reader.readLong()");
            case FLOAT -> write("reader.readFloat()");
            case DOUBLE -> write("reader.readDouble()");
            case BOOLEAN -> write("reader.readBoolean()");
            case CHAR -> write("reader.readChar()");
            case TYPEVAR, ARRAY -> {
                write("reader.readObject(").writeAdapterFieldName(Type.from(type), attrs).write(")");
//                var arrayType = (ArrayType) type;
//                write("reader.readArray(").write(typesExt.getErasedText(type)).write("::new, () -> ");
//                generateRead(arrayType.getComponentType(), attrs);
//                write(")");
            }
            case DECLARED -> {
                var dt = (DeclaredType) type;
                var cl = (TypeElement) dt.asElement();
                if (cl.getKind() == ElementKind.ENUM)
                    write("reader.readNullable(() -> ").write(cl.getQualifiedName()).write(".valueOf(reader.readString()))");
                else if (cl.getQualifiedName() == myNames.classString)
                    write("reader.readStringOrNull()");
                else
                    write("reader.readObject(").writeAdapterFieldName(Type.from(type), attrs).write(")");
            }
            default -> throw new IllegalStateException("Unknown type kind: " + type.getKind());
        }
        return this;
    }

    private void addError(String message) {
        env.addError(message);
    }

    @Override
    public AdapterGenerator write(String s) {
        return (AdapterGenerator) super.write(s);
    }

    private String getInitialValue(TypeMirror type) {
        return switch (type.getKind()) {
            case BOOLEAN -> "false";
            case BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, CHAR -> "0";
            default -> "null";
        };
    }

    void generateWriteName(String name) {
        write("writer.write(").write(getCharsField(name)).writeln(");");
        writeln("writer.writeColon();");
    }

    private record AdapterKey(Type type, Map<String, Object> attributes) {

        static AdapterKey of(Type type) {
            return new AdapterKey(type, Map.of());
        }

    }

}
