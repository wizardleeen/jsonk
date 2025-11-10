package org.jsonk.mocks;

import org.jsonk.Json;

@Json
public record OrderItem(
        String productId,
        int quantity,
        long price
) {
}
