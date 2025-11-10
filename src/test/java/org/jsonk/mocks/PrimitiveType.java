package org.jsonk.mocks;

import org.jsonk.Json;

@Json
public record PrimitiveType(String kind) implements Type {
    @Override
    public String getType() {
        return "primitive";
    }
}
