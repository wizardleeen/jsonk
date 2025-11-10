package org.jsonk.adapters;

import org.jsonk.*;
import org.jsonk.Type;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter implements Adapter<LocalDate>  {

    private final DateTimeFormatter formatter;

    public LocalDateAdapter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void init(AdapterRegistry registry) {

    }

    @Override
    public void toJson(LocalDate o, JsonWriter writer) {
        writer.write('"');
        formatter.formatTo(o, writer.getAppendable());
        writer.write('"');
    }

    @Override
    public LocalDate fromJson(JsonReader reader) {
        return LocalDate.parse(reader.readString(), formatter);
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(Type.from(LocalDate.class));
    }
}
