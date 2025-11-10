package org.jsonk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record Type(
        Class<?> clazz,
        List<Type> typeArguments
) {

    public static Type from(Class<?> clazz) {
        List<Type> typeArgs;
        if (clazz.getTypeParameters().length == 0)
            typeArgs = List.of();
        else {
            typeArgs = new ArrayList<Type>();
            for (int i = 0; i < clazz.getTypeParameters().length; i++) {
                typeArgs.add(Type.from(Object.class));
            }
        }
        return new Type(clazz, typeArgs);
    }

    public static Type from(Class<?> clazz, Type...typeArgs) {
        return new Type(clazz, Arrays.asList(typeArgs));
    }

    public static Type from(Class<?> clazz, Class<?>...typeArgs) {
        var types = new ArrayList<Type>();
        for (Class<?> t : typeArgs) {
            types.add(Type.from(t));
        }
        return new Type(clazz, types);
    }

}
