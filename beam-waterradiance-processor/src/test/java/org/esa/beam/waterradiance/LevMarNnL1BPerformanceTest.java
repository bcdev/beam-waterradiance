package org.esa.beam.waterradiance;

import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.GPF;
import org.esa.beam.framework.gpf.graph.GraphException;
import org.esa.beam.ocnnrd.OcNnRdOperator;
import org.esa.beam.util.io.FileUtils;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class LevMarNnL1BPerformanceTest {

    private NanoTimer nanoTimer;
    private WaterRadianceOperator.Spi waterRadianceOpSpi;
    private OcNnRdOperator.Spi ocNnRdSpi;
    private File targetDirectory;

    private static String c_elapsed;
    private static String java_elapsed;

    @Before
    public void setUp() {
        nanoTimer = new NanoTimer();
        waterRadianceOpSpi = new WaterRadianceOperator.Spi();
        ocNnRdSpi = new OcNnRdOperator.Spi();

        targetDirectory = new File("test_out");
        if (!targetDirectory.mkdirs()) {
            fail("Unable to create test target directory");
        }
    }

    @After
    public void tearDown() {
        if (targetDirectory.isDirectory()) {
            if (!FileUtils.deleteTree(targetDirectory)) {
                fail("Unable to delete test directory");
            }
        }
    }

    @AfterClass
    public static void destruct() {
        System.out.println(c_elapsed);
        System.out.println(java_elapsed);
    }

    @Test
    @Ignore
    public void testNnInC() throws Exception {
        GPF.getDefaultInstance().getOperatorSpiRegistry().addOperatorSpi(waterRadianceOpSpi);
        final String testProductPath = getTestProductPath();
        final Product product = ProductIO.readProduct(testProductPath);

        try {
            nanoTimer.start();
            final Product ocProduct = GPF.createProduct("Meris.WaterRadiance",
                    createDefaultParameterMap(),
                    new Product[]{product});

            ProductIO.writeProduct(ocProduct, targetDirectory.getPath(), "BEAM-DIMAP");
            nanoTimer.stop();
            c_elapsed = "C   : " + nanoTimer.getElapsedTime();
        } finally {
            GPF.getDefaultInstance().getOperatorSpiRegistry().removeOperatorSpi(waterRadianceOpSpi);
            if (product != null) {
                product.dispose();
            }
        }
    }

    @Test
    @Ignore
    public void testNnInJava() throws GraphException, IOException {
        GPF.getDefaultInstance().getOperatorSpiRegistry().addOperatorSpi(ocNnRdSpi);
        final String testProductPath = getTestProductPath();
        final Product product = ProductIO.readProduct(testProductPath);

        try {
            nanoTimer.start();
            final Product ocProduct = GPF.createProduct("Meris.OCNNRD",
                    createDefaultParameterMap(),
                    new Product[]{product});
            ProductIO.writeProduct(ocProduct, targetDirectory.getPath(), "BEAM-DIMAP");
            nanoTimer.stop();
            java_elapsed = "Java: " + nanoTimer.getElapsedTime();
        } finally {
            GPF.getDefaultInstance().getOperatorSpiRegistry().removeOperatorSpi(ocNnRdSpi);
            if (product != null) {
                product.dispose();
            }
        }

    }

    private String getTestProductPath() {
        final URL resource = LevMarNnL1BPerformanceTest.class.getResource("../../../../subset_0_of_MER_RR__1PRACR20060116_201233_000026092044_00200_20294_0000.dim");
        final String resourcePath = resource.getPath();
        assertTrue(new File(resourcePath).isFile());
        return resourcePath;
    }

    private HashMap<String, Object> createDefaultParameterMap() {
        final HashMap<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("targetDirectory", targetDirectory);
        return parameterMap;
    }

    private class NanoTimer {

        private long start;
        private long stop;

        public void start() {
            start = System.nanoTime();
        }

        public void stop() {
            stop = System.nanoTime();
        }

        public String getElapsedTime() {
            final long elapsedTime = stop - start;
            final long milliSecs = TimeUnit.MILLISECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
            return "" + milliSecs + " millis";
        }
    }
}
