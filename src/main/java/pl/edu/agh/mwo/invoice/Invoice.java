package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.HashMap;
import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
    private HashMap<Product, Integer> productQuantity = new HashMap<>();

    public void addProduct(Product product) {
        this.addProduct(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null || quantity <= 0)
            throw new IllegalArgumentException("Quantity must be greater than 0");
        productQuantity.put(product, quantity);
    }

    public BigDecimal getSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for(Product product : productQuantity.keySet()) {
           subtotal = subtotal.add(product.getNetPrice().multiply(new BigDecimal(productQuantity.get(product))));
        }
        return subtotal;
    }

    public BigDecimal getTax() {
        BigDecimal tax = BigDecimal.ZERO;
        for(Product product : productQuantity.keySet()) {
            tax = tax.add(product.getNetPrice().multiply(product.getTaxPercent()).multiply(new BigDecimal(productQuantity.get(product))));
        }
        return tax;
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for(Product product : productQuantity.keySet()) {
            total = total.add(product.getPriceWithTax().multiply(new BigDecimal(productQuantity.get(product))));
        }
        return total;
    }
}
