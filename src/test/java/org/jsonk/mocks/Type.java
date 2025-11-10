package org.jsonk.mocks;

import org.jsonk.Json;
import org.jsonk.SubType;

@Json(
        typeProperty = "type",
        subTypes = {
                @SubType(value = "class", type = ClassType.class),
                @SubType(value = "primitive", type = PrimitiveType.class),
                @SubType(value = "array", type = ArrayType.class)
        }
)
public interface Type {

        String getType();

}
