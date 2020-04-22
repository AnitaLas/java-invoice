package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public abstract class Product {
    private final String name;

    private final BigDecimal price;

    private final BigDecimal taxPercent;

    protected Product(String name, BigDecimal price, BigDecimal tax) {
        this.name = name;
        this.price = price;
        this.taxPercent = tax;
    }

    public String getName() {
        return null;
    }

    public BigDecimal getPrice() {
        return null;
    }

    public BigDecimal getTaxPercent() {
        return null;
    }

    public BigDecimal getPriceWithTax() {
        return null;
    }
}
