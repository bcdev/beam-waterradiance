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
            MerisL1BProduct.assertCorrect_Rl_Tosa_05(savedProduct);

            MerisL1BProduct.assertCorrect_Rl_Path_01(savedProduct);
//
            MerisL1BProduct.assertCorrect_Reflec_01(savedProduct);
            MerisL1BProduct.assertCorrect_Reflec_05(savedProduct);

            MerisL1BProduct.assertCorrect_Trans_Down_01(savedProduct);

            MerisL1BProduct.assertCorrect_Trans_Up_01(savedProduct);

            MerisL1BProduct.assertCorrect_Aot_550(savedProduct);
            MerisL1BProduct.assertCorrect_Ang_864_443(savedProduct);
            MerisL1BProduct.assertCorrect_A_Pig(savedProduct);
            MerisL1BProduct.assertCorrect_A_Ys(savedProduct);
            MerisL1BProduct.assertCorrect_A_Part(savedProduct);
            MerisL1BProduct.assertCorrect_B_Part(savedProduct);
            MerisL1BProduct.assertCorrect_B_Wit(savedProduct);

            MerisL1BProduct.assertCorrect_Sum_Sq(savedProduct);
            MerisL1BProduct.assertCorrect_Num_Iter(savedProduct);
            MerisL1BProduct.assertCorrect_Temperature(savedProduct);
            MerisL1BProduct.assertCorrect_Salinity(savedProduct);
            MerisL1BProduct.assertCorrect_Atm_Press_2(savedProduct);
            MerisL1BProduct.assertCorrect_Ozone_2(savedProduct);
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

            ModisL1BProduct.assertCorrect_Rl_Tosa_01(savedProduct);

//            ModisL1BProduct.assertCorrect_Rl_Path_01(savedProduct);

//            ModisL1BProduct.assertCorrect_Reflec_01(savedProduct);
//            ModisL1BProduct.assertCorrect_Reflec_05(savedProduct);

//            ModisL1BProduct.assertCorrect_Trans_Down_01(savedProduct);

//            ModisL1BProduct.assertCorrect_Trans_Up_01(savedProduct);

//            ModisL1BProduct.assertCorrect_Aot_550(savedProduct);
            ModisL1BProduct.assertCorrect_Ang_864_443(savedProduct);
            ModisL1BProduct.assertCorrect_A_Pig(savedProduct);
            ModisL1BProduct.assertCorrect_A_Ys(savedProduct);
            ModisL1BProduct.assertCorrect_A_Part(savedProduct);
            ModisL1BProduct.assertCorrect_B_Part(savedProduct);
            ModisL1BProduct.assertCorrect_B_Wit(savedProduct);

            ModisL1BProduct.assertCorrect_Sum_Sq(savedProduct);
            ModisL1BProduct.assertCorrect_Num_Iter(savedProduct);
            ModisL1BProduct.assertCorrect_Temperature(savedProduct);
            ModisL1BProduct.assertCorrect_Salinity(savedProduct);
            ModisL1BProduct.assertCorrect_Atm_Press_2(savedProduct);
            ModisL1BProduct.assertCorrect_Ozone_2(savedProduct);
        } finally {
            target.dispose();
            if (savedProduct != null) {
                savedProduct.dispose();
            }
        }
    }

    @Test
    public void testSeaWiFSL1B() throws IOException {
        final Product seawifsL1BProduct = SeaWiFSL1BProduct.create();

        Product savedProduct = null;
        final Product target = GPF.createProduct("OCNNRD", GPF.NO_PARAMS, seawifsL1BProduct);

        try {
            final String targetProductPath = testOutDirectory.getAbsolutePath() + File.separator + "OcNnRd_modis.dim";
            ProductIO.writeProduct(target, targetProductPath, "BEAM-DIMAP");

            savedProduct = ProductIO.readProduct(targetProductPath);
            assertNotNull(savedProduct);

            SeaWiFSL1BProduct.assertCorrect_Rl_Tosa_01(savedProduct);

            SeaWiFSL1BProduct.assertCorrect_Rl_Path_01(savedProduct);

            SeaWiFSL1BProduct.assertCorrect_Reflec_01(savedProduct);
            SeaWiFSL1BProduct.assertCorrect_Reflec_05(savedProduct);

            SeaWiFSL1BProduct.assertCorrect_Trans_Down_01(savedProduct);

            SeaWiFSL1BProduct.assertCorrect_Trans_Up_01(savedProduct);

            SeaWiFSL1BProduct.assertCorrect_Aot_550(savedProduct);
            SeaWiFSL1BProduct.assertCorrect_Ang_864_443(savedProduct);
            SeaWiFSL1BProduct.assertCorrect_A_Pig(savedProduct);
        } finally {
            target.dispose();
            if (savedProduct != null) {
                savedProduct.dispose();
            }
        }
    }
}
