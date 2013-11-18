package org.esa.beam.ocnnrd;


import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.GPF;
import org.esa.beam.util.io.FileUtils;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import static org.junit.Assert.*;

public class OcNnRdOperatorAcceptanceTest {

    private File testOutDirectory;

    @BeforeClass
    public static void beforeClass() throws ParseException {
        GPF.getDefaultInstance().getOperatorSpiRegistry().addOperatorSpi(new OcNnRdOperator.Spi());
    }

    @AfterClass
    public static void afterClass() throws ParseException {
        GPF.getDefaultInstance().getOperatorSpiRegistry().removeOperatorSpi(new OcNnRdOperator.Spi());
    }

    @Before
    public void setUp() {
        testOutDirectory = new File("output");
        if (!testOutDirectory.mkdirs()) {
            fail("unable to create test directory: " + testOutDirectory);
        }
    }

    @After
    public void tearDown() {
        if (testOutDirectory != null) {
            if (!FileUtils.deleteTree(testOutDirectory)) {
                fail("Unable to delete test directory: " + testOutDirectory);
            }
        }
    }

    @Test
    public void testMerisL1B() throws IOException {
        final Product merisL1BProduct = MerisL1BProduct.create();

        final Product target = GPF.createProduct("OCNNRD", GPF.NO_PARAMS, merisL1BProduct);

        final String targetProductPath = testOutDirectory.getAbsolutePath() + File.separator + "OcNnRd_meris.dim";
        ProductIO.writeProduct(target, targetProductPath, "BEAM-DIMAP");

        final Product product = ProductIO.readProduct(targetProductPath);
        assertNotNull(product);
        try {
            MerisL1BProduct.assertCorrect_Rl_Tosa_01(product);
            MerisL1BProduct.assertCorrect_Rl_Tosa_02(product);
            MerisL1BProduct.assertCorrect_Rl_Tosa_03(product);
            MerisL1BProduct.assertCorrect_Rl_Tosa_04(product);
            MerisL1BProduct.assertCorrect_Rl_Tosa_05(product);
        } finally {
            target.dispose();
            product.dispose();
        }
    }

    // @todo 1 tb/tb continue here tb 2013-11-18
//    @Test
//    public void testModisL1B() throws IOException {
//        final Product modisL1BProduct = ModisL1BProduct.create();
//
//        final Product target = GPF.createProduct("OCNNRD", GPF.NO_PARAMS, modisL1BProduct);
//
//        final String targetProductPath = testOutDirectory.getAbsolutePath() + File.separator + "OcNnRd_modis.dim";
//        ProductIO.writeProduct(target, targetProductPath, "BEAM-DIMAP");
//    }
}
