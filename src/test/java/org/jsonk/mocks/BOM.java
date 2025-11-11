package org.jsonk.mocks;

import lombok.Getter;
import lombok.Setter;
import org.jsonk.Json;

import java.util.Objects;

@Json
@Getter
@Setter
public class BOM {
    private Material product;
    private long version;

    public BOM(Material product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BOM bom = (BOM) object;
        return version == bom.version && Objects.equals(product, bom.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, version);
    }
}
