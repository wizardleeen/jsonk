package org.jsonk.adapters;

import org.jsonk.*;
import org.jsonk.Type;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter implements Adapter<LocalDateTime>  {

    private final DateTimeFormatter formatter;

    public LocalDateTimeAdapter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void init(AdapterEnv env) {
    }

    @Override
    public void toJson(LocalDateTime o, JsonWriter writer) {
        writer.write('"');
        formatter.formatTo(o, writer.getAppendable());
        writer.write('"');
    }

    @Override
    public LocalDateTime fromJson(JsonReader reader) {
        return LocalDateTime.parse(reader.readString(), formatter);
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(Type.from(LocalDateTime.class));
    }
}
