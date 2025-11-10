package org.jsonk.mocks;

import org.jsonk.JsonProperty;
import org.jsonk.Json;

@Json
public record MockClazz(
        @JsonProperty("abstract")
        boolean isAbstract
) {
}
