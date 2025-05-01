package pl.edu.agh.mwo.invoice.invoiceElements;

import java.math.BigDecimal;

public enum PrinterInvoiceElement {

    PRODUCT_NET_VALUE,
    PRODUCT_GROSS_VALUE;

    public static BigDecimal printerInvoiceElementGetProductValue(InvoiceProduct invoiceProduct, PrinterInvoiceElement printerInvoiceElement) {
        return switch (printerInvoiceElement){
            case PRODUCT_NET_VALUE -> invoiceProduct.geNetValue();
            case PRODUCT_GROSS_VALUE ->invoiceProduct.getGrossValue();
        };
    }

}
