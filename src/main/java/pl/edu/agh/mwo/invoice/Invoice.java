package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
    //private Collection<Product> products = new ArrayList<Product>();
    private HashMap<String, Integer> productQuantity = new HashMap<>();

    public void addProduct(Product product) {
       //if(this.products == null) {
           //this.products = new ArrayList<Product>();
      //}
        productQuantity.put(product.getName(), 1);
    }

    public void addProduct(Product product, Integer quantity) {
        productQuantity.put(product.getName(), quantity);

    }

    public BigDecimal getSubtotal() {
        //return BigDecimal.ZERO;
        BigDecimal subtotal = BigDecimal.ZERO;
        for(String produktName : productQuantity.keySet()) {
           subtotal =subtotal.add();
        }
        return subtotal;
    }

    public BigDecimal getTax() {
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotal() {
        return BigDecimal.ZERO;
    }
}
