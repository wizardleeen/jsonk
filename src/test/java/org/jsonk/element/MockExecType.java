package org.jsonk.element;

import org.jsonk.util.Util;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class MockExecType extends MockType implements ExecutableType {

    private final List<? extends MockTypeVariable> typeVars;
    private final List<? extends MockType> paramTypes;
    private final MockType retType;
    private final MockType receiverType;
    private final List<? extends MockType> throwTypes;

    public MockExecType(List<? extends MockTypeVariable> typeVars,
                        List<? extends MockType> paramTypes,
                        MockType retType,
                        MockType receiverType,
                        List<? extends MockType> throwTypes) {
        this.typeVars = typeVars;
        this.paramTypes = paramTypes;
        this.retType = retType;
        this.receiverType = receiverType;
        this.throwTypes = throwTypes;
    }

    @Override
    public List<? extends TypeVariable> getTypeVariables() {
        return Collections.unmodifiableList(typeVars);
    }

    @Override
    public MockType getReturnType() {
        return retType;
    }

    @Override
    public List<? extends MockType> getParameterTypes() {
        return Collections.unmodifiableList(paramTypes);
    }

    @Override
    public MockType getReceiverType() {
        return receiverType;
    }

    @Override
    public List<? extends MockType> getThrownTypes() {
        return Collections.unmodifiableList(throwTypes);
    }

    @Override
    public TypeKind getKind() {
        return TypeKind.EXECUTABLE;
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return null;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return null;
    }

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
        return null;
    }

    @Override
    public <R, P> R accept(TypeVisitor<R, P> v, P p) {
        return v.visitExecutable(this, p);
    }

    @Override
    public void write(ElementWriter writer) {
        writer.write("(").writeList(paramTypes, writer::write, ", ").write(") -> ").write(retType);
        if (!throwTypes.isEmpty())
            writer.write(" throws ").writeList(throwTypes, writer::write, ", ");
    }

    @Override
    public boolean isAssignableFrom(MockType that) {
        if (this == that.getUpperBound())
            return true;
        if (that.getUpperBound() instanceof MockExecType thatEt) {
            if (typeVars.size() != thatEt.typeVars.size())
                return false;
            var subst = typeVars.isEmpty() ?
                    TypeSubstitutor.EMPTY :
                    TypeSubstitutor.create(Util.toMap(typeVars, thatEt.typeVars));
            return retType.accept(subst).isAssignableFrom(thatEt.retType)
                    && Util.allMatch(throwTypes, thatEt.throwTypes, (tt1, tt2) -> tt1.accept(subst).isAssignableFrom(tt2))
                    && thatEt.receiverType.isAssignableFrom(receiverType.accept(subst))
                    && Util.allMatch(paramTypes, thatEt.paramTypes, (pt1, pt2) -> pt2.isAssignableFrom(pt1.accept(subst)));
        }
        else
            return false;
    }
}
