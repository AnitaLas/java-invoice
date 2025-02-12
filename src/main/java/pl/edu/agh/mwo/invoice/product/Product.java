package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public abstract class Product {
    private final String name;

    private final BigDecimal price;

    private final BigDecimal taxPercent;

    protected Product(String name, BigDecimal price, BigDecimal tax) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Product name cannot be null  or empty");

        if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Price cannot be null or negative");

        if (tax == null || tax.signum() < 0)
            throw new IllegalArgumentException("Tax cannot be null or empty");

        this.name = name;
        this.price = price;
        this.taxPercent = tax;
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getNetPrice() {
        return this.price;
    }

    public BigDecimal getTaxPercent() {
        return this.taxPercent;
    }

    public BigDecimal getPriceWithTax() {
        return this.getNetPrice().add(this.getNetPrice().multiply(this.getTaxPercent()));
    }


}
