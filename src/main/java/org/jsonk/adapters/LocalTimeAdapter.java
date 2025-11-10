package org.jsonk.adapters;

import org.jsonk.*;

import java.time.LocalTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeAdapter implements Adapter<LocalTime>  {

    private final DateTimeFormatter formatter;

    public LocalTimeAdapter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void init(AdapterRegistry registry) {
    }

    @Override
    public void toJson(LocalTime o, JsonWriter writer) {
        writer.write('"');
        formatter.formatTo(o, writer.getAppendable());
        writer.write('"');
    }

    @Override
    public LocalTime fromJson(JsonReader reader) {
        return LocalTime.parse(reader.readString(), formatter);
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(Type.from(LocalTime.class));
    }
}
