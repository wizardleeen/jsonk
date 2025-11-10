package org.jsonk.element;

import lombok.Getter;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.*;
import java.util.*;

public class TypeFactory {

    public static final TypeFactory instance = new TypeFactory() ;
    private final Map<ClassTypeKey, MockDeclaredType> classTypes = new HashMap<>();
    private final Map<MockType, MockArrayType> arrayTypes = new HashMap<>();
    private final Map<Set<MockType>, MockUnionType> unionTypes = new HashMap<>();
    private final Map<Set<MockType>, MockIntersectionType> intersectionTypes = new HashMap<>();
    private final Map<WildcardTypeKey, MockWildcardType> wildcardTypes = new HashMap<>();
    private final Map<TypeParameterElement, MockTypeVariable> typeVariables = new HashMap<>();
    private final Map<ExecTypeKey, MockExecType> executableTypes = new HashMap<>();
    @Getter
    private final MockPrimType booleanType = new MockPrimType(TypeKind.BOOLEAN);
    @Getter
    private final MockPrimType byteType = new MockPrimType(TypeKind.BYTE);
    @Getter
    private final MockPrimType shortType = new MockPrimType(TypeKind.SHORT);
    @Getter
    private final MockPrimType intType = new MockPrimType(TypeKind.INT);
    @Getter
    private final MockPrimType longType = new MockPrimType(TypeKind.LONG);
    @Getter
    private final MockPrimType charType = new MockPrimType(TypeKind.CHAR);
    @Getter
    private final MockPrimType floatType = new MockPrimType(TypeKind.FLOAT);
    @Getter
    private final MockPrimType doubleType = new MockPrimType(TypeKind.DOUBLE);
    @Getter
    private final MockPrimType voidType = new MockPrimType(TypeKind.VOID);
    private MockDeclaredType objectType;
    private final MockNoType noType = new MockNoType();
    private final MockNullType nullType = new MockNullType();

    public MockDeclaredType getObjectType() {
        if (objectType == null) {
            objectType = new MockDeclaredType(null, ElementFactory.instance.buildClass(Object.class), List.of()) {
                @Override
                public boolean isAssignableFrom(MockType that) {
                    return true;
                }
            };
        }
        return objectType;
    }

    public NoType getNoType() {
        return noType;
    }

    public NullType getNullType() {
        return nullType;
    }

    public MockDeclaredType getClassType(MockDeclaredType owner, MockClass clazz, List<MockType> typeArgs) {
        var key = new ClassTypeKey(owner, clazz, typeArgs);
        return classTypes.computeIfAbsent(key, k -> new MockDeclaredType(owner, clazz, typeArgs));
    }

    public MockArrayType getArrayType(MockType componentType) {
        return arrayTypes.computeIfAbsent(componentType, k -> new MockArrayType(componentType));
    }

    public MockType getUnionType(Set<MockType> alternatives) {
        if (alternatives.isEmpty())
            return noType;
        if (alternatives.size() == 1)
            return alternatives.iterator().next();
        return unionTypes.computeIfAbsent(alternatives, k -> new MockUnionType(alternatives));
    }

    public MockType getIntersectionType(Set<MockType> bounds) {
        if (bounds.isEmpty())
            return getObjectType();
        if (bounds.size() == 1)
            return bounds.iterator().next();
        return intersectionTypes.computeIfAbsent(bounds, k -> new MockIntersectionType(bounds));
    }

    public MockWildcardType getWildcardType(MockType lowerBound, MockType upperBound) {
        var key = new WildcardTypeKey(lowerBound, upperBound);
        return wildcardTypes.computeIfAbsent(key, k -> new MockWildcardType(lowerBound, upperBound));
    }

    public MockTypeVariable getTypeVariable(MockTypeParamElement element) {
        return typeVariables.computeIfAbsent(element, MockTypeVariable::new);
    }

    public MockExecType getExecutableType(
            List<MockTypeVariable> typeVars,
            MockType receiverType,
            List<? extends MockType> paramTypes,
            MockType retType,
            List<? extends MockType> throwTypes
    ) {
        var key = new ExecTypeKey(typeVars, paramTypes, retType, throwTypes);
        return executableTypes.computeIfAbsent(key, k -> new MockExecType(typeVars, paramTypes, retType, receiverType, throwTypes));
    }

    private record ClassTypeKey(
            DeclaredType owner,
            TypeElement clazz,
            List<MockType> typeArgs
    ) {}

    private record WildcardTypeKey(
            MockType lowerBound,
            MockType upperBound
    ) {}

    private record ExecTypeKey(
            List<? extends TypeVariable> typeVars,
            List<? extends MockType> paramTypes,
            MockType retType,
            List<? extends MockType> throwTypes
            ) {

    }

}
