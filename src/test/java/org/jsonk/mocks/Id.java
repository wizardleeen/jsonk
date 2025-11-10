package org.jsonk.mocks;

import org.jsonk.Json;

@Json(adapter = IdAdapter.class)
public record Id(String id) {


}
