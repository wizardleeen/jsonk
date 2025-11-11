package org.jsonk.mocks;

import lombok.Getter;
import lombok.Setter;
import org.jsonk.Json;

import java.util.Objects;

@Json
@Setter
@Getter
public class Material {
    private String name;
    private double amount;

    public Material(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Material material = (Material) object;
        return Double.compare(amount, material.amount) == 0 && Objects.equals(name, material.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, amount);
    }
}
