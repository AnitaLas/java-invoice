package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.*;

import pl.edu.agh.mwo.invoice.invoiceElements.InvoiceProduct;
import pl.edu.agh.mwo.invoice.invoiceElements.PrinterInvoiceElement;
import pl.edu.agh.mwo.invoice.product.Product;

import static pl.edu.agh.mwo.invoice.invoiceElements.PrinterInvoiceElement.*;

public class Invoice {

    private List<InvoiceProduct> products = new LinkedList<>();
    private List<InvoiceProduct> duplicatedProducts = new LinkedList<>();
    private List<String> productsList = new LinkedList<>();
    private String invoiceNumber = "FS/20250429/1";
    private PrinterInvoiceElement printerInvoiceElement;
    private InvoiceProduct invoiceProduct;

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException();
        }
        InvoiceProduct invoiceProduct = new InvoiceProduct(product, 1);
        this.products.add(invoiceProduct);
        InvoiceProduct invoiceProductCopy = new InvoiceProduct(product, 1);
        setDuplicatedProducts(invoiceProductCopy);
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException();
        }
        InvoiceProduct invoiceProduct = new InvoiceProduct(product, quantity);
        this.products.add(invoiceProduct);
        InvoiceProduct invoiceProductCopy = new InvoiceProduct(product, quantity);
        setDuplicatedProducts(invoiceProductCopy);
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

    public void setProductsListToPrint(List<InvoiceProduct> products, PrinterInvoiceElement printerInvoiceElementGetProductValue) {
        String line = "";

        for (InvoiceProduct invoiceProduct : products) {
            line += "Name: " + invoiceProduct.getProduct().getName() + ", ";
            line += "price: " + printerInvoiceElementGetProductValue(invoiceProduct, printerInvoiceElementGetProductValue) + ", ";
            line += "quantity: " + invoiceProduct.getQuantity();
            line += "\n";
            productsList.add(line);
            line = "";
        }
    }

    public void setDuplicatedProducts(InvoiceProduct invoiceProduct) {
        List<InvoiceProduct> newListPD = new LinkedList<>(duplicatedProducts);
        boolean isNewListPDContainsIProduct = false;
//        clone
//        InvoiceProduct newInvoiceProduct = products.get(products.size() - 1);
//        product id instead of product name/ code
        String productName = invoiceProduct.getProduct().getName();

        if (duplicatedProducts.isEmpty()) {
            newListPD.add(invoiceProduct);

        } else {

            for (InvoiceProduct dProduct : newListPD) {
                String duplicatedProductName = dProduct.getProduct().getName();

                if (!isNewListPDContainsIProduct) {
                    //                        if (productName.equals(duplicatedProductName)) {
                    if (productName == duplicatedProductName) {

                        int quantity = dProduct.getQuantity() + invoiceProduct.getQuantity();
                        dProduct.setQuantity(Integer.valueOf(quantity));

                        BigDecimal productNetValue = (dProduct.getGrossValue()).add(invoiceProduct.getGrossValue());
                        dProduct.setNetValue(productNetValue);

                        BigDecimal productGrossValue = (dProduct.getGrossValue()).add(invoiceProduct.getGrossValue());
                        dProduct.setGrossValue(productGrossValue);

                        isNewListPDContainsIProduct = true;

                    } else {
                        isNewListPDContainsIProduct = true;
                    }
                }
            }

            if (isNewListPDContainsIProduct) {
                boolean towarJestJuzDodanyDoListy = false;
                for (InvoiceProduct dProduct : newListPD) {
//                        if(newListPD.contains(iProduct)) {
                    if (invoiceProduct.getProduct().getName() == dProduct.getProduct().getName())
                        towarJestJuzDodanyDoListy = true;
                }

                if (!towarJestJuzDodanyDoListy)
                    newListPD.add(invoiceProduct);
            }
        }

        duplicatedProducts = newListPD;
    }

    public int setProductLinesNumbers(List<InvoiceProduct> productNumbers) {
        return productNumbers.size();
    }

    public int getProductsSumNumberWithDuplicates() {
        return setProductLinesNumbers(products);
    }

    public int getProductsSumNumberWithoutDuplicates() {
        return setProductLinesNumbers(duplicatedProducts);
    }

    public String getPrintTextProductsSumNumber() {
        return "Liczba pozycji: ";
    }

    public String getProductListAsString() {
        String text = "";
        for (String productInfo : productsList) {
            text += productInfo;
        }
        return text;
    }

    public String getProductsList(List<InvoiceProduct> products, PrinterInvoiceElement printerInvoiceElementGetProductValue) {
        setProductsListToPrint(products, printerInvoiceElementGetProductValue);
        return getProductListAsString();
    }

    public String getProductsListWithDuplicatesElements(PrinterInvoiceElement printerInvoiceElementGetProductValue) {
        return getProductsList(this.products, printerInvoiceElementGetProductValue);
    }

    public String getProductsListWithoutDuplicatesElements(PrinterInvoiceElement printerInvoiceElementGetProductValue) {
        return getProductsList(this.duplicatedProducts, printerInvoiceElementGetProductValue);
    }

    public String getProductsListWithDuplicatesElementsGrossPrice() {
//        return getProductsList(this.products, PRODUCT_GROSS_VALUE);
        return getProductsListWithDuplicatesElements(PRODUCT_GROSS_VALUE);
    }

    public String getProductsListWithoutDuplicateElementsGrossPrice() {
//        return getProductsList(this.duplicatedProducts, PRODUCT_GROSS_VALUE);
        return getProductsListWithoutDuplicatesElements(PRODUCT_GROSS_VALUE);
    }

    public String getProductsListWithDuplicateElementsNetPrice() {
        return getProductsListWithDuplicatesElements(PRODUCT_NET_VALUE);
    }

    public String getProductsListWithoutDuplicateElementsNetPrice() {
        return getProductsListWithoutDuplicatesElements(PRODUCT_NET_VALUE);
    }

//    public String getSortedProductsListWithDuplicates() {
//        setProductsListToPrint(products, PRODUCT_GROSS_VALUE);
//        return getProductsList(products);
//    }
//
//    public String getSortedProductsListWithoutDuplicates() {
//        setProductsListToPrint(products, PRODUCT_GROSS_VALUE);
//        return getProductsList(products);
//    }


//    public String getSortedProductsList(List<InvoiceProduct> products) {
//        Collections.sort(productsList);
//        return getProductListAsString();
//    }

//    public String getProductsListWithDuplicates() {
//        setProductsListToPrint(products, PRODUCT_GROSS_VALUE);
//        return getSortedProductsList(products);
//    }

//    public String getSortedProductsListWithDuplicates() {
//        setProductsListToPrint(products, PRODUCT_GROSS_VALUE);
//        return getSortedProductsList(products);
//    }
//
//    public String getSortedProductsListWithoutDuplicates() {
//        setProductsListToPrint(products, PRODUCT_GROSS_VALUE);
//        return getSortedProductsList(products);
//    }


}
