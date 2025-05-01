package pl.edu.agh.mwo.invoice.invoiceElements;

import pl.edu.agh.mwo.invoice.product.Product;

import java.math.BigDecimal;

public class InvoiceProduct {

    private Product product;
    private Integer quantity;
    private BigDecimal netValue;
    private BigDecimal VATValue;
    private BigDecimal grossValue;

    public InvoiceProduct(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
        setNetValue(product.getPrice(), quantity);
        setGrossValue(product.getPrice(), quantity);
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setNetValue(BigDecimal price) {
        this.netValue = price;
    }

    public void setNetValue(BigDecimal price, Integer quantity) {
        this.netValue = price.multiply(new BigDecimal(quantity));
    }

    public BigDecimal geNetValue() {
        return this.netValue;
    }

    public void setGrossValue(BigDecimal price, Integer quantity) {
         this.grossValue = product.getPriceWithTax().multiply(BigDecimal.valueOf(quantity));
    }

    public void setGrossValue(BigDecimal price) {
        this.grossValue = price;
    }

    public BigDecimal getGrossValue() {
        return grossValue;
    }

    public Product getProduct() {
        return product;
    }

public InvoiceProduct cloneInvoiceProduct() throws CloneNotSupportedException {
        return (InvoiceProduct) super.clone();
}





}
