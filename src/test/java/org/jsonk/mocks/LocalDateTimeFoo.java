package org.jsonk.mocks;

import org.jsonk.Json;
import org.jsonk.JsonProperty;

import java.time.LocalDateTime;

@Json
public record LocalDateTimeFoo(
        @JsonProperty(
                dateTimeFormat = "yyyy-MM-dd HH:mm:ss"
        )
        LocalDateTime time,
        LocalDateTime time1
) {
}
