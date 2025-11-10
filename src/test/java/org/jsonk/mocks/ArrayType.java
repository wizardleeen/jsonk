package org.jsonk.mocks;

import org.jsonk.Json;

@Json
public record ArrayType(Type componentType) implements Type {
    @Override
    public String getType() {
        return "array";
    }
}
