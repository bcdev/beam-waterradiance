package org.esa.beam.ocnnrd;


import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.GPF;
import org.esa.beam.util.io.FileUtils;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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

        Product savedProduct = null;
        final Product target = GPF.createProduct("OCNNRD", GPF.NO_PARAMS, merisL1BProduct);

        try {
            final String targetProductPath = testOutDirectory.getAbsolutePath() + File.separator + "OcNnRd_meris.dim";
            ProductIO.writeProduct(target, targetProductPath, "BEAM-DIMAP");

            savedProduct = ProductIO.readProduct(targetProductPath);
            assertNotNull(savedProduct);

            MerisL1BProduct.assertCorrect_Rl_Tosa_01(savedProduct);
            MerisL1BProduct.assertCorrect_Rl_Tosa_02(savedProduct);
            MerisL1BProduct.assertCorrect_Rl_Tosa_03(savedProduct);
            MerisL1BProduct.assertCorrect_Rl_Tosa_04(savedProduct);
            MerisL1BProduct.assertCorrect_Rl_Tosa_05(savedProduct);
            MerisL1BProduct.assertCorrect_Rl_Tosa_06(savedProduct);
            MerisL1BProduct.assertCorrect_Rl_Tosa_07(savedProduct);
            MerisL1BProduct.assertCorrect_Rl_Tosa_08(savedProduct);
            MerisL1BProduct.assertCorrect_Rl_Tosa_09(savedProduct);
            MerisL1BProduct.assertCorrect_Rl_Tosa_10(savedProduct);
            MerisL1BProduct.assertCorrect_Rl_Tosa_12(savedProduct);
            MerisL1BProduct.assertCorrect_Rl_Tosa_13(savedProduct);

            MerisL1BProduct.assertCorrect_Rl_Path_01(savedProduct);
            MerisL1BProduct.assertCorrect_Rl_Path_02(savedProduct);
            MerisL1BProduct.assertCorrect_Rl_Path_03(savedProduct);
            MerisL1BProduct.assertCorrect_Rl_Path_04(savedProduct);
        } finally {
            target.dispose();
            if (savedProduct != null) {
                savedProduct.dispose();
            }
        }
    }

    @Test
    public void testModisL1B() throws IOException {
        final Product modisL1BProduct = ModisL1BProduct.create();

        Product savedProduct = null;
        final Product target = GPF.createProduct("OCNNRD", GPF.NO_PARAMS, modisL1BProduct);

        try {
            final String targetProductPath = testOutDirectory.getAbsolutePath() + File.separator + "OcNnRd_modis.dim";
            ProductIO.writeProduct(target, targetProductPath, "BEAM-DIMAP");

            savedProduct = ProductIO.readProduct(targetProductPath);
            assertNotNull(savedProduct);

            //ModisL1BProduct.assertCorrect_Rl_Tosa_01(savedProduct);

            ModisL1BProduct.assertCorrect_Rl_Path_01(savedProduct);
        } finally {
            target.dispose();
            if (savedProduct != null) {
                savedProduct.dispose();
            }
        }
    }
}
