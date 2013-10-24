package org.esa.beam.waterradiance.seadas;

import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.datamodel.Product;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

public class ProductsAuxProductsProviderTest {

    private File auxDirectory;
    private ProductReader productReader;

    @Before
    public void setUp() throws Exception {
        auxDirectory = new File(PathAuxProductsProviderTest.class.getResource("../../../../../auxiliary/seadas/anc/2005").getPath());
        productReader = ProductIO.getProductReader("NETCDF-CF");
    }

    @Test
    public void testCreateWithInvalidProducts() {
        try {
            new ProductsAuxProductsProvider(null, null, null, null);
            fail("Auxdata Impl was created although at least one aux product is not valid");
        } catch (Exception expected) {
            //expected
        }
    }

    @Test
    public void testCreateWithValidProducts() throws IOException {
        //setup
        final Product tomsomiStartProduct = getProduct("/166/N200516600_O3_TOMSOMI_24h.hdf");
        final Product tomsomiEndProduct = getProduct("/167/N200516700_O3_TOMSOMI_24h.hdf");
        final Product ncepStartProduct = getProduct("/166/N200516618_MET_NCEPN_6h.hdf");
        final Product ncepEndProduct = getProduct("/167/N200516700_MET_NCEPN_6h.hdf");
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(2005, Calendar.JUNE, 16, 6, 0, 0);

        //execution
        final ProductsAuxProductsProvider productsAuxProductsProvider =
                new ProductsAuxProductsProvider(tomsomiStartProduct, tomsomiEndProduct, ncepStartProduct, ncepEndProduct);
        assertNotNull(productsAuxProductsProvider);
        SeadasAuxDataProducts tomsomiProducts = productsAuxProductsProvider.getTOMSOMIProducts(calendar.getTime());
        assertNotNull(tomsomiProducts);
        SeadasAuxDataProducts ncepProducts = productsAuxProductsProvider.getNCEPProducts(calendar.getTime());
        assertNotNull(ncepProducts);

        //assertion
        assertEquals(tomsomiStartProduct, tomsomiProducts.getStartProduct());
        assertEquals(tomsomiEndProduct, tomsomiProducts.getEndProduct());
        assertEquals(ncepStartProduct, ncepProducts.getStartProduct());
        assertEquals(ncepEndProduct, ncepProducts.getEndProduct());
    }

    private Product getProduct(String productString) throws IOException {
        String tomsomiStartProductPath = auxDirectory.getPath() + productString;
        return productReader.readProductNodes(new File(tomsomiStartProductPath), null);
    }

} 