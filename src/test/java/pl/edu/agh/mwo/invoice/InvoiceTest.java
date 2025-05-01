package pl.edu.agh.mwo.invoice;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.mwo.invoice.product.DairyProduct;
import pl.edu.agh.mwo.invoice.product.OtherProduct;
import pl.edu.agh.mwo.invoice.product.Product;
import pl.edu.agh.mwo.invoice.product.TaxFreeProduct;

public class InvoiceTest  {
    private Invoice invoice;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Before
    public void createEmptyInvoiceForTheTest() {
        invoice = new Invoice();
        System.setOut(new PrintStream(outputStream));
    }

    @After
    public void restoreSystemOut() {
        System.setOut(originalOut);
    }

    @Test
    public void testEmptyInvoiceHasEmptySubtotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTaxAmount() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithTwoDifferentProducts() {
        Product onions = new TaxFreeProduct("Warzywa", new BigDecimal("10"));
        Product apples = new TaxFreeProduct("Owoce", new BigDecimal("10"));
        invoice.addProduct(onions);
        invoice.addProduct(apples);
        Assert.assertThat(new BigDecimal("20"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithManySameProducts() {
        Product onions = new TaxFreeProduct("Warzywa", BigDecimal.valueOf(10));
        invoice.addProduct(onions, 100);
        Assert.assertThat(new BigDecimal("1000"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {
        // price with tax: 200
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
        // price with tax: 108
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
        // price with tax: 12.30
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.40
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        // 1000x pinezka - price with tax: 12.30
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddingNullProduct() {
        invoice.addProduct(null);
    }


//    XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    @Test
    public void testInvoiceWithDuplicateElementsGrossPriceProductsGetProductList() {
        invoice.addProduct(new TaxFreeProduct("Chlebek", new BigDecimal("10")));
        System.out.println(invoice.getProductsListWithDuplicatesElementsGrossPrice());
        String expectedOutput = "Name: Chlebek, price: 10, quantity: 1\n"
                + System.lineSeparator();
        Assert.assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void testInvoiceWithoutDuplicateElementsGrossPriceProductsGetProductList() {
        invoice.addProduct(new TaxFreeProduct("Chlebek", new BigDecimal("10")));
        System.out.println(invoice.getProductsListWithoutDuplicateElementsGrossPrice());
        String expectedOutput = "Name: Chlebek, price: 10, quantity: 1\n"
                + System.lineSeparator();
        Assert.assertEquals(expectedOutput, outputStream.toString());
    }


    @Test
    public void testInvoiceWithDuplicateElementsGrossPriceProductsInSeparateLine() {
        Product p1 = new TaxFreeProduct("Chedar", new BigDecimal("10"));
        Product p2 = new TaxFreeProduct("Masełko", new BigDecimal("200"));
        Product p3 = new TaxFreeProduct("Chlebek", new BigDecimal("200"));
        invoice.addProduct(p1, 3);
        invoice.addProduct(p2);
        invoice.addProduct(p3);
        System.out.println(invoice.getProductsListWithDuplicatesElementsGrossPrice());
        String expectedOutput = "Name: Chedar, price: 30, quantity: 3\n"
                + "Name: Masełko, price: 200, quantity: 1\n"
                + "Name: Chlebek, price: 200, quantity: 1\n"
                + System.lineSeparator();
        Assert.assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void testInvoiceWithoutDuplicateElementsGrossPriceProductsInSeparateLine() {
        Product p1 = new TaxFreeProduct("Chedar", new BigDecimal("10"));
        Product p2 = new TaxFreeProduct("Masełko", new BigDecimal("200"));
        Product p3 = new TaxFreeProduct("Chlebek", new BigDecimal("200"));
        invoice.addProduct(p1, 3);
        invoice.addProduct(p2);
        invoice.addProduct(p3);
        System.out.println(invoice.getProductsListWithoutDuplicateElementsGrossPrice());
        String expectedOutput = "Name: Chedar, price: 30, quantity: 3\n"
                + "Name: Masełko, price: 200, quantity: 1\n"
                + "Name: Chlebek, price: 200, quantity: 1\n"
                + System.lineSeparator();
        Assert.assertEquals(expectedOutput, outputStream.toString());
    }




    @Test
    public void testInvoiceWithDuplicateElementsGrossPriceProductsSortedProductsByProductName() {
        Product p1 = new TaxFreeProduct("Chedar", new BigDecimal("10"));
        Product p2 = new TaxFreeProduct("Masełko", new BigDecimal("200"));
        Product p3 = new TaxFreeProduct("Chlebek", new BigDecimal("200"));
        invoice.addProduct(p1, 3);
        invoice.addProduct(p2);
        invoice.addProduct(p3);
        System.out.println(invoice.getProductsListWithDuplicatesElementsGrossPrice());
        String expectedOutput = "Name: Chedar, price: 30, quantity: 3\n"
                + "Name: Masełko, price: 200, quantity: 1\n"
                + "Name: Chlebek, price: 200, quantity: 1\n"
                + System.lineSeparator();
        Assert.assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void testInvoiceWithoutDuplicateElementsGrossPriceProductsSortedProductsByProductName() {
        Product p1 = new TaxFreeProduct("Chedar", new BigDecimal("10"));
        Product p2 = new TaxFreeProduct("Masełko", new BigDecimal("200"));
        Product p3 = new TaxFreeProduct("Chlebek", new BigDecimal("200"));
        invoice.addProduct(p1, 3);
        invoice.addProduct(p2);
        invoice.addProduct(p3);
        System.out.println(invoice.getProductsListWithoutDuplicateElementsGrossPrice());
        String expectedOutput = "Name: Chedar, price: 30, quantity: 3\n"
                + "Name: Masełko, price: 200, quantity: 1\n"
                + "Name: Chlebek, price: 200, quantity: 1\n"
                + System.lineSeparator();
        Assert.assertEquals(expectedOutput, outputStream.toString());
    }



    @Test
    public void testInvoiceGetInvoiceNumber() {
        System.out.println(invoice.getInvoiceNumber());
        String expectedOutput = "FS/20250429/1" + System.lineSeparator();
        Assert.assertEquals(expectedOutput, outputStream.toString());
    }



    @Test
    public void testInvoiceWithoutDuplicateElementsGrossPriceProductsNumberPlusProduct() {
        Product p1 = new TaxFreeProduct("Chedar", new BigDecimal("10"));
        invoice.addProduct(p1, 4);
        System.out.println(invoice.getInvoiceNumber()
                + System.lineSeparator()
                + invoice.getProductsListWithDuplicatesElementsGrossPrice());
        String expectedOutput = "FS/20250429/1"
                + System.lineSeparator() +
                "Name: Chedar, price: 40, quantity: 4\n"
                + System.lineSeparator();
        Assert.assertEquals(expectedOutput, (outputStream.toString()));
    }

    @Test
    public void testInvoiceWithDuplicateElementsGrossPriceProductsNumberPlusProduct() {
        Product p1 = new TaxFreeProduct("Chedar", new BigDecimal("10"));
        invoice.addProduct(p1, 4);
        System.out.println(invoice.getInvoiceNumber() + System.lineSeparator() + invoice.getProductsListWithoutDuplicateElementsGrossPrice());
        String expectedOutput = "FS/20250429/1"
                + System.lineSeparator()
                + "Name: Chedar, price: 40, quantity: 4\n"
                + System.lineSeparator();
        Assert.assertEquals(expectedOutput, (outputStream.toString()));
    }


    @Test
    public void testInvoiceWithDuplicateElementsGrossPriceProductsNumberPlusProducts() {
        Product p1 = new TaxFreeProduct("Chedar", new BigDecimal("10"));
        Product p2 = new TaxFreeProduct("Masełko", new BigDecimal("200"));
        invoice.addProduct(p1, 10);
        invoice.addProduct(p2);
        System.out.println(invoice.getInvoiceNumber() + System.lineSeparator() + invoice.getProductsListWithDuplicatesElementsGrossPrice());
        String expectedOutput = "FS/20250429/1"
                + System.lineSeparator()
                + "Name: Chedar, price: 100, quantity: 10\n"
                + "Name: Masełko, price: 200, quantity: 1\n"
                + System.lineSeparator();
        Assert.assertEquals(expectedOutput, (outputStream.toString()));
    }

    @Test
    public void testInvoiceWithoutDuplicateElementsGrossPriceProductsNumberPlusProducts() {
        Product p1 = new TaxFreeProduct("Chedar", new BigDecimal("10"));
        Product p2 = new TaxFreeProduct("Masełko", new BigDecimal("200"));
        invoice.addProduct(p1, 10);
        invoice.addProduct(p2);
        System.out.println(invoice.getInvoiceNumber() + System.lineSeparator() + invoice.getProductsListWithoutDuplicateElementsGrossPrice());
        String expectedOutput = "FS/20250429/1"
                + System.lineSeparator()
                + "Name: Chedar, price: 100, quantity: 10\n"
                + "Name: Masełko, price: 200, quantity: 1\n"
                + System.lineSeparator();
        Assert.assertEquals(expectedOutput, (outputStream.toString()));
    }





    @Test
    public void testInvoiceWithDuplicatesGetSumOfLineElementsOneProduct() {
        Product p1 = new TaxFreeProduct("Chedar", new BigDecimal("10"));
        invoice.addProduct(p1, 3);
        System.out.println(invoice.getPrintTextProductsSumNumber() + invoice.getProductsSumNumberWithDuplicates());
        String expectedOutput = "Liczba pozycji: 1" + System.lineSeparator();
        Assert.assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void testInvoiceWithoutDuplicatesGetSumOfLineElementsOneProduct() {
        Product p1 = new TaxFreeProduct("Chedar", new BigDecimal("10"));
        invoice.addProduct(p1, 3);
        System.out.println(invoice.getPrintTextProductsSumNumber() + invoice.getProductsSumNumberWithoutDuplicates());
        String expectedOutput = "Liczba pozycji: 1" + System.lineSeparator();
        Assert.assertEquals(expectedOutput, outputStream.toString());
    }




    @Test
    public void testInvoiceWithDuplicatesGetSumOfLineElementsTwoDifferentProducts() {
        Product p1 = new DairyProduct("Chedar", new BigDecimal("10"));
        Product p2 = new DairyProduct("Masełko", new BigDecimal("200"));
        invoice.addProduct(p1);
        invoice.addProduct(p2);
        System.out.println(invoice.getPrintTextProductsSumNumber()
                + invoice.getProductsSumNumberWithDuplicates());
        String expectedOutput = "Liczba pozycji: 2" + System.lineSeparator();
        Assert.assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void testInvoiceWithoutDuplicatesGetSumOfLineElementsTwoDifferentProducts() {
        Product p1 = new DairyProduct("Chedar", new BigDecimal("10"));
        Product p2 = new DairyProduct("Masełko", new BigDecimal("200"));
        invoice.addProduct(p1);
        invoice.addProduct(p2);
        System.out.println(invoice.getPrintTextProductsSumNumber()
                + invoice.getProductsSumNumberWithoutDuplicates());
        String expectedOutput = "Liczba pozycji: 2" + System.lineSeparator();
        Assert.assertEquals(expectedOutput, outputStream.toString());
    }




    @Test
    public void testInvoiceWithDuplicatesGetSumOfLineElementsTwoIdenticalProducts() {
        Product p1 = new DairyProduct("Chedar", new BigDecimal("10"));
        Product p1a = new DairyProduct("Chedar", new BigDecimal("10"));
        invoice.addProduct(p1);
        invoice.addProduct(p1a);
        System.out.println(invoice.getPrintTextProductsSumNumber()
                + invoice.getProductsSumNumberWithDuplicates());
        String expectedOutput = "Liczba pozycji: 2" + System.lineSeparator();
        Assert.assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void testInvoiceWithoutDuplicatesGetSumOfLineElementsTwoIdenticalProducts() {
        Product p1 = new DairyProduct("Chedar", new BigDecimal("10"));
        Product p1a = new DairyProduct("Chedar", new BigDecimal("10"));
        invoice.addProduct(p1);
        invoice.addProduct(p1a);
        System.out.println(invoice.getPrintTextProductsSumNumber()
                + invoice.getProductsSumNumberWithoutDuplicates());
        String expectedOutput = "Liczba pozycji: 1" + System.lineSeparator();
        Assert.assertEquals(expectedOutput, outputStream.toString());
    }



    @Test
    public void testInvoiceWithDuplicatesGetSumOfLineElementsTwoIdenticalProductsPlusOneDifferentProduct() {
        Product p1 = new DairyProduct("towar", new BigDecimal("10"));
        Product p1a = new DairyProduct("towar", new BigDecimal("10"));
        Product p1b = new DairyProduct("Masełko", new BigDecimal("10"));
        invoice.addProduct(p1,2);
        invoice.addProduct(p1a);
        invoice.addProduct(p1b,3);
        System.out.println(invoice.getPrintTextProductsSumNumber()
                + invoice.getProductsSumNumberWithDuplicates());
        String expectedOutput = "Liczba pozycji: 3" + System.lineSeparator();
        Assert.assertEquals(expectedOutput, outputStream.toString());
    }
    
    @Test
    public void testInvoiceWithoutDuplicatesGetSumOfLineElementsTwoIdenticalProductsPlusOneDifferentProduct() {
        Product p1 = new DairyProduct("towar", new BigDecimal("10"));
        Product p1a = new DairyProduct("towar", new BigDecimal("10"));
        Product p1b = new DairyProduct("Masełko", new BigDecimal("10"));
        invoice.addProduct(p1,2);
        invoice.addProduct(p1a);
        invoice.addProduct(p1b,3);
        System.out.println(invoice.getPrintTextProductsSumNumber()
                + invoice.getProductsSumNumberWithoutDuplicates());
        String expectedOutput = "Liczba pozycji: 2" + System.lineSeparator();
        Assert.assertEquals(expectedOutput, outputStream.toString());
    }



    @Test
    public void testInvoiceWithDuplicatesGetSumOfLineElementsTwoIdenticalProductsPlusOneDifferentProductAddedAlternately() {
        Product p1 = new DairyProduct("towar", new BigDecimal("10"));
        Product p1a = new DairyProduct("Masełko", new BigDecimal("10"));
        Product p1b = new DairyProduct("towar", new BigDecimal("10"));
        invoice.addProduct(p1,2);
        invoice.addProduct(p1a);
        invoice.addProduct(p1b,3);
        System.out.println(invoice.getPrintTextProductsSumNumber()
                + invoice.getProductsSumNumberWithDuplicates());
        String expectedOutput = "Liczba pozycji: 3" + System.lineSeparator();
        Assert.assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void testInvoiceWithoutDuplicatesGetSumOfLineElementsTwoIdenticalProductsPlusOneDifferentProductAddedAlternately() {
        Product p1 = new DairyProduct("towar", new BigDecimal("10"));
        Product p1a = new DairyProduct("Masełko", new BigDecimal("10"));
        Product p1b = new DairyProduct("towar", new BigDecimal("10"));
        invoice.addProduct(p1,2);
        invoice.addProduct(p1a);
        invoice.addProduct(p1b,3);
        System.out.println(invoice.getPrintTextProductsSumNumber()
                + invoice.getProductsSumNumberWithoutDuplicates());
        String expectedOutput = "Liczba pozycji: 2" + System.lineSeparator();
        Assert.assertEquals(expectedOutput, outputStream.toString());
    }




    @Test
    public void testInvoiceWithDuplicatesGetSumOfLineElementsTwoIdenticalProductsPlusTwoDifferentProductAddedAlternately() {
        Product p1 = new DairyProduct("towar", new BigDecimal("10"));
        Product p1a = new DairyProduct("Masełko", new BigDecimal("10"));
        Product p1b = new DairyProduct("towar", new BigDecimal("10"));
        Product p1c = new DairyProduct("Masełko", new BigDecimal("10"));
        invoice.addProduct(p1,2);
        invoice.addProduct(p1a);
        invoice.addProduct(p1b,3);
        invoice.addProduct(p1c,4);
        System.out.println(invoice.getPrintTextProductsSumNumber()
                + invoice.getProductsSumNumberWithDuplicates());
        String expectedOutput = "Liczba pozycji: 4" + System.lineSeparator();
        Assert.assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void testInvoiceWithoutDuplicatesGetSumOfLineElementsTwoIdenticalProductsPlusTwoDifferentProductAddedAlternately() {
        Product p1 = new DairyProduct("towar", new BigDecimal("10"));
        Product p1a = new DairyProduct("Masełko", new BigDecimal("10"));
        Product p1b = new DairyProduct("towar", new BigDecimal("10"));
        Product p1c = new DairyProduct("Masełko", new BigDecimal("10"));
        invoice.addProduct(p1,2);
        invoice.addProduct(p1a);
        invoice.addProduct(p1b,3);
        invoice.addProduct(p1c,4);
        System.out.println(invoice.getPrintTextProductsSumNumber()
                + invoice.getProductsSumNumberWithoutDuplicates());
        String expectedOutput = "Liczba pozycji: 2" + System.lineSeparator();
        Assert.assertEquals(expectedOutput, outputStream.toString());
    }




    @Test
    public void testInvoiceWithDuplicatesElementsGrossPriceTwoDifferentProducts() {
        Product p1 = new DairyProduct("Chedar", new BigDecimal("10"));
        Product p2 = new DairyProduct("Masełko", new BigDecimal("200"));
        invoice.addProduct(p1, 2);
        invoice.addProduct(p2);
        System.out.println(invoice.getInvoiceNumber()
                + System.lineSeparator()
                + invoice.getProductsListWithDuplicatesElementsGrossPrice()
                + invoice.getPrintTextProductsSumNumber()
                + invoice.getProductsSumNumberWithDuplicates());
        String expectedOutput = "FS/20250429/1"
                + System.lineSeparator()
                + "Name: Chedar, price: 21.60, quantity: 2\n"
                + "Name: Masełko, price: 216.00, quantity: 1\n"
                + "Liczba pozycji: 2"
                + System.lineSeparator();
        Assert.assertEquals(expectedOutput, (outputStream.toString()));
    }

    @Test
    public void testInvoiceWithoutDuplicatesElementsGrossPriceTwoDifferentProducts() {
        Product p1 = new DairyProduct("Chedar", new BigDecimal("10"));
        Product p2 = new DairyProduct("Masełko", new BigDecimal("200"));
        invoice.addProduct(p1, 2);
        invoice.addProduct(p2);
        System.out.println(invoice.getInvoiceNumber()
                + System.lineSeparator()
                + invoice.getProductsListWithoutDuplicateElementsGrossPrice()
                + invoice.getPrintTextProductsSumNumber()
                + invoice.getProductsSumNumberWithoutDuplicates());
        String expectedOutput = "FS/20250429/1"
                + System.lineSeparator()
                + "Name: Chedar, price: 21.60, quantity: 2\n"
                + "Name: Masełko, price: 216.00, quantity: 1\n"
                + "Liczba pozycji: 2"
                + System.lineSeparator();
        Assert.assertEquals(expectedOutput, (outputStream.toString()));
    }





    @Test
    public void testInvoiceWithDuplicatesElementsGrossPriceTwoIdenticalProducts() {
        Product p1 = new DairyProduct("Masełko", new BigDecimal("10"));
        Product p1a = new DairyProduct("Masełko", new BigDecimal("10"));
        Product p1b = new DairyProduct("Masełko", new BigDecimal("10"));
        invoice.addProduct(p1);
        invoice.addProduct(p1a);
        invoice.addProduct(p1b);

        System.out.println(invoice.getInvoiceNumber()
                + System.lineSeparator()
                + invoice.getProductsListWithDuplicatesElementsGrossPrice()
                + invoice.getPrintTextProductsSumNumber()
                + invoice.getProductsSumNumberWithDuplicates());
        String expectedOutput = "FS/20250429/1"
                + System.lineSeparator()
                + "Name: Masełko, price: 10.80, quantity: 1\n"
                + "Name: Masełko, price: 10.80, quantity: 1\n"
                + "Name: Masełko, price: 10.80, quantity: 1\n"
                + "Liczba pozycji: 3"
                + System.lineSeparator();
        Assert.assertEquals(expectedOutput, (outputStream.toString()));
    }


    @Test // nii work
    public void testInvoiceWithoutDuplicatesElementsGrossPriceTwoIdenticalProducts() {
        Product p1 = new DairyProduct("Masełko", new BigDecimal("10"));
        Product p1a = new DairyProduct("Masełko", new BigDecimal("10"));
        Product p1b = new DairyProduct("Masełko", new BigDecimal("10"));
        invoice.addProduct(p1);
        invoice.addProduct(p1a);
        invoice.addProduct(p1b);

        System.out.println(invoice.getInvoiceNumber()
                + System.lineSeparator()
                + invoice.getProductsListWithoutDuplicateElementsGrossPrice()
                + invoice.getPrintTextProductsSumNumber()
                + invoice.getProductsSumNumberWithoutDuplicates());
        String expectedOutput = "FS/20250429/1"
                + System.lineSeparator()
                + "Name: Masełko, price: 32.40, quantity: 3\n"
                + "Liczba pozycji: 1"
                + System.lineSeparator();
        Assert.assertEquals(expectedOutput, (outputStream.toString()));
    }





    @Test
    public void testInvoiceWithDuplicatesElementsGrossPriceTwoIdenticalProducts2() {
        Product p1 = new DairyProduct("Masełko", new BigDecimal("20"));
        Product p1a = new DairyProduct("Masełko", new BigDecimal("10"));
        invoice.addProduct(p1, 3);
        invoice.addProduct(p1a, 10);

        System.out.println(invoice.getInvoiceNumber()
                + System.lineSeparator()
                + invoice.getProductsListWithDuplicatesElementsGrossPrice()
                + invoice.getPrintTextProductsSumNumber()
                + invoice.getProductsSumNumberWithDuplicates());
        String expectedOutput = "FS/20250429/1"
                + System.lineSeparator()
                + "Name: Masełko, price: 64.80, quantity: 3\n"
                + "Name: Masełko, price: 108.00, quantity: 10\n"
                + "Liczba pozycji: 2"
                + System.lineSeparator();
        Assert.assertEquals(expectedOutput, (outputStream.toString()));
    }

    @Test
    public void testInvoiceWithoutDuplicatesElementsGrossPriceTwoIdenticalProducts2() {
        Product p1 = new DairyProduct("Masełko", new BigDecimal("20"));
        Product p1a = new DairyProduct("Masełko", new BigDecimal("10"));
        invoice.addProduct(p1, 3);
        invoice.addProduct(p1a, 10);

        System.out.println(invoice.getInvoiceNumber()
                + System.lineSeparator()
                + invoice.getProductsListWithoutDuplicateElementsGrossPrice()
                + invoice.getPrintTextProductsSumNumber()
                + invoice.getProductsSumNumberWithoutDuplicates());
        String expectedOutput = "FS/20250429/1"
                + System.lineSeparator()
                + "Name: Masełko, price: 172.80, quantity: 13\n"
                + "Liczba pozycji: 1"
                + System.lineSeparator();
        Assert.assertEquals(expectedOutput, (outputStream.toString()));
    }






    @Test
    public void testInvoiceHasDuplicateProducts2() {
        Product p1 = new DairyProduct("Masełko", new BigDecimal("20"));
        Product p1a = new DairyProduct("Masełko", new BigDecimal("10"));
        Product p2 = new DairyProduct("Chleb", new BigDecimal("100"));
        Product p2a = new DairyProduct("Masełko", new BigDecimal("4"));
        invoice.addProduct(p1, 3);
        invoice.addProduct(p1a, 10);
        invoice.addProduct(p2);
        invoice.addProduct(p2a, 2);

        System.out.println(invoice.getInvoiceNumber()
                + System.lineSeparator()
                + invoice.getProductsListWithoutDuplicateElementsGrossPrice()
                + invoice.getPrintTextProductsSumNumber()
                + invoice.getProductsSumNumberWithoutDuplicates());
        String expectedOutput = "FS/20250429/1"
                + System.lineSeparator()
                + "Name: Masełko, price: 181.44, quantity: 15\n"
                + "Name: Chleb, price: 108.00, quantity: 1\n"
                + "Liczba pozycji: 2"
                + System.lineSeparator();
        Assert.assertEquals(expectedOutput, (outputStream.toString()));
    }


    //    @Test // method sorted
//    public void testInvoiceWithDuplicateElementsSortedProductsSortedProductsByProductName() {
//        Product p1 = new TaxFreeProduct("Chedar", new BigDecimal("10"));
//        Product p2 = new TaxFreeProduct("Masełko", new BigDecimal("200"));
//        Product p3 = new TaxFreeProduct("Chlebek", new BigDecimal("200"));
//        invoice.addProduct(p1, 3);
//        invoice.addProduct(p2);
//        invoice.addProduct(p3);
//        System.out.println(invoice.getProductsListWithDuplicates());
//        String expectedOutput = "Name: Chedar, price: 30, quantity: 3\n"
//                + "Name: Chlebek, price: 200, quantity: 1\n"
//                + "Name: Masełko, price: 200, quantity: 1\n"
//                + System.lineSeparator();
//        Assert.assertEquals(expectedOutput, outputStream.toString());
//    }
//
//    @Test
//    public void testInvoiceWithoutDuplicateElementsSortedProductsSortedProductsByProductName() {
//        Product p1 = new TaxFreeProduct("Chedar", new BigDecimal("10"));
//        Product p2 = new TaxFreeProduct("Masełko", new BigDecimal("200"));
//        Product p3 = new TaxFreeProduct("Chlebek", new BigDecimal("200"));
//        invoice.addProduct(p1, 3);
//        invoice.addProduct(p2);
//        invoice.addProduct(p3);
//        System.out.println(invoice.getProductsListWithoutDuplicates());
//        String expectedOutput = "Name: Chedar, price: 30, quantity: 3\n"
//                + "Name: Chlebek, price: 200, quantity: 1\n"
//                + "Name: Masełko, price: 200, quantity: 1\n"
//                + System.lineSeparator();
//        Assert.assertEquals(expectedOutput, outputStream.toString());
//    }


}
