package org.jsonk.mocks;

import org.jsonk.Json;

@Json
public record ClassType(String name) implements Type {

    @Override
    public String getType() {
        return "class";
    }

}
