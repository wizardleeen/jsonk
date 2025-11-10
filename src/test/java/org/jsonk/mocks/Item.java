package org.jsonk.mocks;

import org.jsonk.Json;

@Json
public record Item<T>(T value) {
}
