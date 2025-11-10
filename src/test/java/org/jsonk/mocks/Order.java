package org.jsonk.mocks;

import org.jsonk.Json;

import java.util.List;

@Json
public record Order(
        List<OrderItem> items
) {


}
