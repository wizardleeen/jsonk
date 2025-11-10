package org.jsonk.mocks;

import lombok.Getter;
import lombok.Setter;
import org.jsonk.Json;

import java.util.Objects;

@Setter
@Getter
@Json
public class Product extends ProductBase {
    private double price;
    private int stock;

    public Product(String name, double price) {
        super(name);
        this.price = price;
    }

    public void setPrice(double price) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        Product product = (Product) object;
        return Double.compare(price, product.price) == 0 && stock == product.stock;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), price, stock);
    }

    @Override
    public String toString() {
        return "Product{" +
                "name=" + getName() +
                ", price=" + price +
                ", stock=" + stock +
                '}';
    }
}
