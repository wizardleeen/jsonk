package org.jsonk.mocks;

import org.jsonk.Json;

import java.util.Arrays;

@Json
public record ArrayFoo(
        String[] tags
) {

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ArrayFoo that;
    }
}
