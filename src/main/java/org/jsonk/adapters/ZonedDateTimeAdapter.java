package org.jsonk.adapters;

import org.jsonk.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeAdapter implements Adapter<ZonedDateTime>  {

    private final DateTimeFormatter formatter;

    public ZonedDateTimeAdapter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void init(AdapterRegistry registry) {
    }

    @Override
    public void toJson(ZonedDateTime o, JsonWriter writer) {
        writer.write('"');
        formatter.formatTo(o, writer.getAppendable());
        writer.write('"');
    }

    @Override
    public ZonedDateTime fromJson(JsonReader reader) {
        return ZonedDateTime.parse(reader.readString(), formatter);
    }

    @Override
    public AdapterKey getKey() {
        return AdapterKey.of(Type.from(ZonedDateTime.class));
    }
}
