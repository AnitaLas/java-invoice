package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.*;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {

    private Map<Product, Integer> products = new LinkedHashMap<Product, Integer>();
    private List<String> productsList = new LinkedList();
    private String invoiceNumber = "FS/20250429/1";
    private int productsSum;

    public void addProduct(Product product) {
        addProduct(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException();
        }
        products.put(product, quantity);

    }

    public BigDecimal getNetTotal() {
        BigDecimal totalNet = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalNet = totalNet.add(product.getPrice().multiply(quantity));
        }
        return totalNet;
    }

    public BigDecimal getTaxTotal() {
        return getGrossTotal().subtract(getNetTotal());
    }

    public BigDecimal getGrossTotal() {
        BigDecimal totalGross = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalGross = totalGross.add(product.getPriceWithTax().multiply(quantity));
        }
        return totalGross;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public List<String> setBaseProductsList() {
        String line = "";

        for (Product product : products.keySet()) {
            line += "Name: " + product.getName() + ", ";
            line += "price: " + product.getPrice() + ", ";
            line += "quantity: " + products.get(product);
            line += "\n";
            productsList.add(line);
            line = "";
        }
        return productsList;
    }

    public int setProductLinesNumbers() {
//        int quantity = 0;
//
//        for (Integer number : products.values()) {
//            quantity +=  number;

        productsSum = products.size();
        return productsSum;
    }

    public String getProductsSumNumber() {
        return "Liczba pozycji: "+ setProductLinesNumbers();
    }

    public String getProductListAsString() {
        String text = "";
        for (String productInfo : productsList) {
            text += productInfo;
        }
        return text;
    }

    public String getProductsList() {
        productsList = setBaseProductsList();
        return getProductListAsString();
    }

    public String getSortedProductsList() {
        productsList = setBaseProductsList();
        Collections.sort(productsList);
        return getProductListAsString();
    }


}
