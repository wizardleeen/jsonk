package org.jsonk.adapters;

import org.jsonk.*;
import org.jsonk.Type;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeAdapter implements Adapter<OffsetDateTime>  {

    private final DateTimeFormatter formatter;

    public OffsetDateTimeAdapter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void init(AdapterRegistry registry) {
    }

    @Override
    public void toJson(OffsetDateTime o, JsonWriter writer) {
        writer.write('"');
        formatter.formatTo(o, writer.getAppendable());
        writer.write('"');
    }

    @Override
    public OffsetDateTime fromJson(JsonReader reader) {
        return OffsetDateTime.parse(reader.readString(), formatter);
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(Type.from(OffsetDateTime.class));
    }
}
