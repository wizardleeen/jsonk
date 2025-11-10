package org.jsonk.element;

import org.jsonk.util.Util;

import javax.lang.model.type.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeSubstitutor implements TypeVisitor<MockType, Void> {

    public static TypeSubstitutor create(Map<? extends TypeVariable, ? extends MockType> map) {
        if (map.isEmpty())
            return EMPTY;
        else
            return new TypeSubstitutor(map);
    }

    private final TypeFactory typeFactory = TypeFactory.instance;
    private final Map<? extends TypeVariable, MockType> map;

    private TypeSubstitutor(Map<? extends TypeVariable, ? extends MockType> map) {
        this.map = new HashMap<>(map);
    }

    @Override
    public MockType visit(TypeMirror t, Void unused) {
        throw new UnsupportedOperationException("Should not reach here");
    }

    @Override
    public MockType visitPrimitive(PrimitiveType t, Void unused) {
        return (MockType) t;
    }

    @Override
    public MockType visitNull(NullType t, Void unused) {
        return (MockType) t;
    }

    @Override
    public MockType visitArray(ArrayType t, Void unused) {
        return typeFactory.getArrayType(
                (MockType) t.getComponentType().accept(this, null)
        );
    }

    @Override
    public MockType visitDeclared(DeclaredType t, Void unused) {
        if (t.getEnclosingType() == null && t.getTypeArguments().isEmpty())
            return (MockType) t;
        return typeFactory.getClassType(
                Util.safeCall(t.getEnclosingType(), owner -> (MockDeclaredType) owner.accept(this, null)),
                (MockClass) t.asElement(),
                Util.map(
                        t.getTypeArguments(), typeArg -> (MockType) typeArg.accept(this, null)
                )
        );
    }

    @Override
    public MockType visitError(ErrorType t, Void unused) {
        return (MockType) t;
    }

    @Override
    public MockType visitTypeVariable(TypeVariable t, Void unused) {
        return map.getOrDefault(t, (MockType) t);
    }

    @Override
    public MockType visitWildcard(WildcardType t, Void unused) {
        if (t.getSuperBound() == null && t.getExtendsBound() == null)
            return (MockType) t;
        return typeFactory.getWildcardType(
                Util.safeCall(t.getSuperBound(), lb -> lb.accept(this, null)),
                Util.safeCall(t.getExtendsBound(), up -> up.accept(this, null))
        );
    }

    @Override
    public MockType visitExecutable(ExecutableType t, Void unused) {
        //noinspection unchecked,rawtypes
        return typeFactory.getExecutableType(
                (List) t.getTypeVariables(),
                t.getReceiverType().accept(this, null),
                Util.map(t.getParameterTypes(), paramType -> (MockType) paramType.accept(this, null)),
                t.getReturnType().accept(this, null),
                Util.map(t.getThrownTypes(), throwType -> (MockType) throwType.accept(this, null))
        );
    }

    @Override
    public MockType visitNoType(NoType t, Void unused) {
        return (MockType) t;
    }

    @Override
    public MockType visitUnknown(TypeMirror t, Void unused) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MockType visitUnion(UnionType t, Void unused) {
        return typeFactory.getUnionType(
                Util.mapToSet(t.getAlternatives(), alt -> (MockType) alt.accept(this, null))
        );
    }

    @Override
    public MockType visitIntersection(IntersectionType t, Void unused) {
        return typeFactory.getIntersectionType(
                Util.mapToSet(t.getBounds(), alt -> (MockType) alt.accept(this, null))
        );
    }

    public static final TypeSubstitutor EMPTY = new TypeSubstitutor(Map.of()) {

        @Override
        public MockType visitDeclared(DeclaredType t, Void unused) {
            return (MockType) t;
        }

        @Override
        public MockType visitIntersection(IntersectionType t, Void unused) {
            return (MockType) t;
        }

        @Override
        public MockType visitUnion(UnionType t, Void unused) {
            return (MockType) t;
        }

        @Override
        public MockType visitExecutable(ExecutableType t, Void unused) {
            return (MockType) t;
        }

        @Override
        public MockType visitWildcard(WildcardType t, Void unused) {
            return (MockType) t;
        }
    };

}
