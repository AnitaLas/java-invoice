package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.*;

import pl.edu.agh.mwo.invoice.invoiceElements.InvoiceProduct;
import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {

    private List<InvoiceProduct> products = new LinkedList<>();
    private List<InvoiceProduct> duplicatedProducts;
    private List<String> productsList = new LinkedList();
    private String invoiceNumber = "FS/20250429/1";
    private int productLineNumbers;


    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException();
        }
        InvoiceProduct invoiceProduct = new InvoiceProduct(product, 1);
        products.add(invoiceProduct);
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException();
        }
        InvoiceProduct invoiceProduct = new InvoiceProduct(product, quantity);
        products.add(invoiceProduct);
    }

    public BigDecimal getNetTotal() {
        BigDecimal totalNet = BigDecimal.ZERO;
        for (InvoiceProduct invoiceProduct : products) {
            totalNet = totalNet.add(invoiceProduct.geNetValue());
        }
        return totalNet;
    }

    public BigDecimal getTaxTotal() {
        return getGrossTotal().subtract(getNetTotal());
    }

    public BigDecimal getGrossTotal() {
        BigDecimal totalGross = BigDecimal.ZERO;
        for (InvoiceProduct invoiceProduct : products) {
            totalGross = totalGross.add(invoiceProduct.getGrossValue());
        }
        return totalGross;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public List<String> setBaseProductsList(List<InvoiceProduct> products) {
        String line = "";

        for (InvoiceProduct invoiceProduct : products) {
            line += "Name: " + invoiceProduct.getProduct().getName() + ", ";
            line += "value: " + invoiceProduct.getGrossValue()+ ", ";
            line += "quantity: " + invoiceProduct.getQuantity();
            line += "\n";
            productsList.add(line);
            line = "";
        }
        return productsList;
    }

    public List<InvoiceProduct> setDuplicatedProducts(List<InvoiceProduct> products) {

        LinkedList<InvoiceProduct> newListPD = new LinkedList<>();

        for (InvoiceProduct iProduct : products) {
            String productName = iProduct.getProduct().getName();

            for (InvoiceProduct dProduct : newListPD) {
                String duplicatedProductName = dProduct.getProduct().getName();

                // future? -> if -( ... || UOM || VAT rate)
                if (productName.equals(duplicatedProductName)) {
                    int quantity = dProduct.getQuantity() + iProduct.getQuantity();
                    dProduct.setQuantity(Integer.valueOf(quantity));

                    BigDecimal productNetValue = (dProduct.getGrossValue()).add(iProduct.getGrossValue());
                    dProduct.setNetValue(productNetValue);

                    BigDecimal productGrossValue = (dProduct.getGrossValue()).add(iProduct.getGrossValue());
                    dProduct.setGrossValue(productGrossValue);
                }

                if (!productName.equals(duplicatedProductName))
                    newListPD.add(iProduct);
            }

            if (newListPD.isEmpty()){
                newListPD.add(iProduct);
            }
        }
        return newListPD;
    }

    public int setProductLinesNumbers(List<InvoiceProduct> products) {
        return products.size();
    }

    public String getProductsSumNumberWithDuplicates() {
        return "Liczba pozycji: " + setProductLinesNumbers(products);
    }

    public String getProductsSumNumberWithoutDuplicates() {
        return "Liczba pozycji: " + setProductLinesNumbers(duplicatedProducts);
    }

    public String getProductListAsString() {
        String text = "";
        for (String productInfo : productsList) {
            text += productInfo;
        }
        return text;
    }

    public String getProductsList(List<InvoiceProduct> products) {
        productsList = setBaseProductsList(products);
        return getProductListAsString();
    }

    public String getProductListWithDuplicates() {
        return getProductsList(products);
    }


    public String getProductsListWithoutDuplicates() {
        duplicatedProducts = setDuplicatedProducts(products);
        getProductsList(duplicatedProducts);
        return getProductListAsString();
    }

    public String getSortedProductsList(List<InvoiceProduct> products) {
        productsList = setBaseProductsList(products);
        Collections.sort(productsList);
        return getProductListAsString();
    }

    public String getSortedProductsListWithDuplicates() {
        return getSortedProductsList(products);
    }


}
