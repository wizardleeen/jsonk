package org.jsonk.adapters;

import org.jsonk.Adapter;
import org.jsonk.AdapterFactory;
import org.jsonk.util.Util;
import org.jsonk.Type;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DateTimeAdapterFactory implements AdapterFactory {

    private static final Set<Class<?>> temporalClasses = Set.of(
            java.time.LocalDateTime.class,
            java.time.LocalDate.class,
            java.time.LocalTime.class,
            java.time.OffsetDateTime.class,
            java.time.ZonedDateTime.class,
            java.time.Instant.class
    );

    @Override
    public Adapter<?> create(Type type, Map<String, Object> attributes) {
        var className = type.clazz().getName();
        var df = Util.safeCall(attributes.get("dateFormat"), v -> DateTimeFormatter.ofPattern((String) v));
        return switch (className) {
            case "java.time.LocalDateTime" -> new LocalDateTimeAdapter(
                    Objects.requireNonNullElse(df, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
            case "java.util.LocalDate" -> new LocalDateAdapter(
                    Objects.requireNonNullElse(df, DateTimeFormatter.ISO_LOCAL_DATE)
            );
            case "java.util.LocalTime" -> new LocalTimeAdapter(
                    Objects.requireNonNullElse(df, DateTimeFormatter.ISO_LOCAL_TIME)
            );
            case "java.util.OffsetDateTime" -> new OffsetDateTimeAdapter(
                    Objects.requireNonNullElse(df, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            );
            case "java.util.ZonedDateTime" -> new ZonedDateTimeAdapter(
                    Objects.requireNonNullElse(df, DateTimeFormatter.ISO_ZONED_DATE_TIME)
            );
            case "java.util.Instant" -> new InstantAdapter();
            default -> throw new IllegalArgumentException("Unsupported date/time type: " + className);
        };
    }

    @Override
    public boolean isSupported(Type type, Map<String, Object> attributes) {
        return temporalClasses.contains(type.clazz());
    }
}
