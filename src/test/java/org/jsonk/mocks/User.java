package org.jsonk.mocks;

import org.jsonk.JsonIgnore;
import org.jsonk.JsonProperty;
import org.jsonk.Json;

import java.util.Map;

@Json
public record User(
        String name,
        UserKind kind,
        String password,
        boolean activated,
        @JsonProperty(includeNull = true)
        User creator,
        Map<String, Object> attributes
) {

    @JsonIgnore
    public boolean isAdmin() {
        return kind == UserKind.ADMIN;
    }

}
