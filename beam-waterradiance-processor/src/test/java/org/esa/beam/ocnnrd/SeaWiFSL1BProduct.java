package org.esa.beam.ocnnrd;

import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.PixelGeoCoding;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

class SeaWiFSL1BProduct {

    // creates an in memory product of Type "Generic Level 1B" (SeaWiFS) with four pixels from the atlantic ocean, handpicked
    // and cloud-free
    // Product: S2006131120520.L1B_LAC
    //
    // px       original [x,y]  [lon, lat]
    // [0] :    [59, 2764]      [-4.251, -17.088]
    // [1] :    [66, 2770]      [-3.943, -17.381]
    // [2] :    [29, 2780]      [-6.234, -17.474]
    // [3] :    [56, 2809]      [-4.867, -18.872]
    //
    static Product create() {
        final Product seawifsL1BProduct = new Product("SeaWiFS L1B", "Generic Level 1B", 2, 2);

        addL_412(seawifsL1BProduct);
        addL_443(seawifsL1BProduct);
        addL_490(seawifsL1BProduct);
        addL_510(seawifsL1BProduct);
        addL_555(seawifsL1BProduct);
        addL_670(seawifsL1BProduct);
        addL_765(seawifsL1BProduct);
        addL_865(seawifsL1BProduct);

        addLongitude(seawifsL1BProduct);
        addLatitude(seawifsL1BProduct);
        addSolz(seawifsL1BProduct);
        addSola(seawifsL1BProduct);
        addSenz(seawifsL1BProduct);
        addSena(seawifsL1BProduct);

        final PixelGeoCoding pixelGeoCoding = new PixelGeoCoding(seawifsL1BProduct.getBand("latitude"), seawifsL1BProduct.getBand("longitude"), "", 1);
        seawifsL1BProduct.setGeoCoding(pixelGeoCoding);

        return seawifsL1BProduct;
    }

    private static void addL_412(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("L_412", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 7.7473636f);
        rasterData.setElemFloatAt(1, 7.6275034f);
        rasterData.setElemFloatAt(2, 7.8351574f);
        rasterData.setElemFloatAt(3, 7.6167817f);
        band.setData(rasterData);
    }

    private static void addL_443(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("L_443", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 6.576024f);
        rasterData.setElemFloatAt(1, 6.5023556f);
        rasterData.setElemFloatAt(2, 6.694297f);
        rasterData.setElemFloatAt(3, 6.5087557f);
        band.setData(rasterData);
    }

    private static void addL_490(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("L_490", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 4.574263f);
        rasterData.setElemFloatAt(1, 4.5513277f);
        rasterData.setElemFloatAt(2, 4.657561f);
        rasterData.setElemFloatAt(3, 4.5430064f);
        band.setData(rasterData);
    }

    private static void addL_510(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("L_510", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 3.550375f);
        rasterData.setElemFloatAt(1, 3.5479484f);
        rasterData.setElemFloatAt(2, 3.6173937f);
        rasterData.setElemFloatAt(3, 3.5236957f);
        band.setData(rasterData);
    }

    private static void addL_555(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("L_555", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 2.2783818f);
        rasterData.setElemFloatAt(1, 2.2851682f);
        rasterData.setElemFloatAt(2, 2.3282785f);
        rasterData.setElemFloatAt(3, 2.2559612f);
        band.setData(rasterData);
    }

    private static void addL_670(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("L_670", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.8626618f);
        rasterData.setElemFloatAt(1, 0.87060755f);
        rasterData.setElemFloatAt(2, 0.8825606f);
        rasterData.setElemFloatAt(3, 0.85437685f);
        band.setData(rasterData);
    }

    private static void addL_765(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("L_765", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.37928122f);
        rasterData.setElemFloatAt(1, 0.38228065f);
        rasterData.setElemFloatAt(2, 0.38626856f);
        rasterData.setElemFloatAt(3, 0.37307215f);
        band.setData(rasterData);
    }

    private static void addL_865(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("L_865", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.21277075f);
        rasterData.setElemFloatAt(1, 0.21525031f);
        rasterData.setElemFloatAt(2, 0.21872231f);
        rasterData.setElemFloatAt(3, 0.21546218f);
        band.setData(rasterData);
    }

    private static void addLongitude(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("longitude", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, -4.251f);
        rasterData.setElemFloatAt(1, -3.943f);
        rasterData.setElemFloatAt(2, -6.234f);
        rasterData.setElemFloatAt(3, -4.867f);
        band.setData(rasterData);
    }

    private static void addLatitude(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("latitude", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, -17.088f);
        rasterData.setElemFloatAt(1, -17.381f);
        rasterData.setElemFloatAt(2, -17.474f);
        rasterData.setElemFloatAt(3, -18.872f);
        band.setData(rasterData);
    }

    private static void addSolz(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("solz", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 35.45554f);
        rasterData.setElemFloatAt(1, 35.796307f);
        rasterData.setElemFloatAt(2, 35.589764f);
        rasterData.setElemFloatAt(3, 37.14766f);
        band.setData(rasterData);
    }

    private static void addSola(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("sola", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 350.66183f);
        rasterData.setElemFloatAt(1, 350.20615f);
        rasterData.setElemFloatAt(2, 353.87964f);
        rasterData.setElemFloatAt(3, 351.81122f);
        band.setData(rasterData);
    }

    private static void addSenz(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("senz", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 33.1715f);
        rasterData.setElemFloatAt(1, 31.097263f);
        rasterData.setElemFloatAt(2, 43.319252f);
        rasterData.setElemFloatAt(3, 34.1327f);
        band.setData(rasterData);
    }

    private static void addSena(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("sena", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 60.086914f);
        rasterData.setElemFloatAt(1, 56.245773f);
        rasterData.setElemFloatAt(2, 72.82555f);
        rasterData.setElemFloatAt(3, 61.81661f);
        band.setData(rasterData);
    }


    static void assertCorrect_Rl_Tosa_01(Product product) {
        final Band band = product.getBand("rl_tosa_1");
        assertNotNull(band);
        assertEquals(0.05482029542326927f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.05420253425836563f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.0555356927216053f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.055078815668821335f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Path_01(Product product) {
        final Band band = product.getBand("rl_path_1");
        assertNotNull(band);
        assertEquals(0.15172167122364044f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.15015892684459686f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.1545717418193817f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.1521068662405014f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Reflec_01(Product product) {
        final Band band = product.getBand("reflec_1");
        assertNotNull(band);
        assertEquals(0.028195686638355255f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.028843343257904053f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.027871686965227127f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.03022538684308529f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Reflec_05(Product product) {
        final Band band = product.getBand("reflec_5");
        assertNotNull(band);
        assertEquals(0.004554662387818098f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.005389802623540163f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.0034011811949312687f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.005292304791510105f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Trans_Down_01(Product product) {
        final Band band = product.getBand("trans_down_1");
        assertNotNull(band);
        assertEquals(0.8417088985443115f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.8424979448318481f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.8413354754447937f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.840110182762146f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Trans_Up_01(Product product) {
        final Band band = product.getBand("trans_up_1");
        assertNotNull(band);
        assertEquals(0.8371795415878296f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.8417350053787231f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.8165712356567383f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.8368776440620422f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Aot_550(Product product) {
        final Band band = product.getBand("aot_550");
        assertNotNull(band);
        assertEquals(0.02175370790064335f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.012118607759475708f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.021470965817570686f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.011611931957304478f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Ang_864_443(Product product) {
        final Band band = product.getBand("ang_864_443");
        assertNotNull(band);
        assertEquals(2.0081160068511963f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.08352840691804886f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(1.219462513923645f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.8114418983459473f, band.getSampleFloat(1, 1), 1e-8);
        // @todo 1 tb/** too high and inconsistent values! tb 2013-11-26
    }

    static void assertCorrect_A_Pig(Product product) {
        final Band band = product.getBand("a_pig");
        assertNotNull(band);
        assertEquals(0.0001492759765824303f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(3.816504889897487E-9f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.0004800829046871513f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(4.890403459967274E-8f, band.getSampleFloat(1, 1), 1e-8);
        // @todo 1 tb/** too low and inconsistent values! tb 2013-11-26
    }
}


