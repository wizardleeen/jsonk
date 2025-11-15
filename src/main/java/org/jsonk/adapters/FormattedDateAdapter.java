package org.jsonk.adapters;

import lombok.SneakyThrows;
import org.jsonk.*;
import org.jsonk.Type;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

public class FormattedDateAdapter implements Adapter<Date> {

    private final DateFormat dateFormat;

    public FormattedDateAdapter(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public void init(AdapterEnv env) {

    }

    @Override
    public void toJson(Date o, JsonWriter writer) {
        writer.writeString(dateFormat.format(o));
    }

    @SneakyThrows
    @Override
    public Date fromJson(JsonReader reader) {
        return dateFormat.parse(reader.readString());
    }

    @Override
    public AdapterKey getKey() {
        return new AdapterKey(
                Type.from(Date.class),
                Map.of("dateFormat", dateFormat.toString())
        );
    }
}
