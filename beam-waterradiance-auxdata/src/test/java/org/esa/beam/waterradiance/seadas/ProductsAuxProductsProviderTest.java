package org.esa.beam.waterradiance.seadas;

import org.junit.*;

import static junit.framework.Assert.fail;
import static org.junit.Assert.*;

public class ProductsAuxProductsProviderTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testCreateWithInvalidProducts() {
        try {
            final ProductsAuxProductsProvider productsAuxProductsProvider =
                    new ProductsAuxProductsProvider(null, null, null, null);
            fail("Auxdata Impl was created although at least one aux product is not valid");
        } catch (Exception expected) {
            //expected
        }
    }

} 