package org.esa.beam.ocnnrd;


import org.esa.beam.framework.datamodel.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

class MerisL1BProduct {

    // creates an in memory product of Type MER_RR__1P with four pixels from the atlantic ocean, handpicked and cloud-free
    // Product: MER_RR__1PRACR20060511_094214_000026402047_00337_21934_0000.N1
    //
    // px       original [x,y]
    // [0] :    [787, 11409]
    // [1] :    [831, 11427]
    // [2] :    [599, 11482]
    // [3] :    [769, 11584]
    //
    static Product create() {
        final Product merisL1BProduct = new Product("Meris L1B", "MER_RR__1P", 2, 2);

        addRadiance_01(merisL1BProduct);
        addRadiance_02(merisL1BProduct);
        addRadiance_03(merisL1BProduct);
        addRadiance_04(merisL1BProduct);
        addRadiance_05(merisL1BProduct);
        addRadiance_06(merisL1BProduct);
        addRadiance_07(merisL1BProduct);
        addRadiance_08(merisL1BProduct);
        addRadiance_09(merisL1BProduct);
        addRadiance_10(merisL1BProduct);
        addRadiance_11(merisL1BProduct);
        addRadiance_12(merisL1BProduct);
        addRadiance_13(merisL1BProduct);
        addRadiance_14(merisL1BProduct);
        addRadiance_15(merisL1BProduct);
        addDetectorIndex(merisL1BProduct);
        addFlagBand(merisL1BProduct);

        addSunZenith(merisL1BProduct);
        addSunAzimuth(merisL1BProduct);
        addViewZenith(merisL1BProduct);
        addViewAzimuth(merisL1BProduct);
        addAtmPress(merisL1BProduct);
        addOzone(merisL1BProduct);
        addMeridWind(merisL1BProduct);
        addZonalWind(merisL1BProduct);

        addFlagCoding(merisL1BProduct);

        return merisL1BProduct;
    }

    private static void addFlagCoding(Product merisL1BProduct) {
        final FlagCoding l1_flags = new FlagCoding("l1_flags");
        l1_flags.addFlag("INVALID", 0x01, "No Description.");
        l1_flags.addFlag("LAND_OCEAN", 0x02, "No Description.");
        l1_flags.addFlag("BRIGHT", 0x20, "No Description.");
        merisL1BProduct.getBand("l1_flags").setSampleCoding(l1_flags);
        merisL1BProduct.getFlagCodingGroup().add(l1_flags);
    }

    private static void addRadiance_01(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("radiance_1", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 59.20827f);
        rasterData.setElemFloatAt(1, 58.109188f);
        rasterData.setElemFloatAt(2, 59.85256f);
        rasterData.setElemFloatAt(3, 57.79652f);
        band.setData(rasterData);
        band.setSolarFlux(1682.8041f);
    }

    private static void addRadiance_02(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("radiance_2", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 50.85428f);
        rasterData.setElemFloatAt(1, 50.0166f);
        rasterData.setElemFloatAt(2, 51.40566f);
        rasterData.setElemFloatAt(3, 50.090828f);
        band.setData(rasterData);
        band.setSolarFlux(1843.8704f);
    }

    private static void addRadiance_03(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("radiance_3", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 35.355377f);
        rasterData.setElemFloatAt(1, 34.926758f);
        rasterData.setElemFloatAt(2, 35.71449f);
        rasterData.setElemFloatAt(3, 35.505974f);
        band.setData(rasterData);
        band.setSolarFlux(1892.393f);
    }

    private static void addRadiance_04(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("radiance_4", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 27.684887f);
        rasterData.setElemFloatAt(1, 27.438658f);
        rasterData.setElemFloatAt(2, 28.02747f);
        rasterData.setElemFloatAt(3, 28.02747f);
        band.setData(rasterData);
        band.setSolarFlux(1892.981f);
    }

    private static void addRadiance_05(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("radiance_5", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 16.69019f);
        rasterData.setElemFloatAt(1, 16.587566f);
        rasterData.setElemFloatAt(2, 16.8208f);
        rasterData.setElemFloatAt(3, 17.193974f);
        band.setData(rasterData);
        band.setSolarFlux(1769.467f);
    }

    private static void addRadiance_06(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("radiance_6", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 9.842125f);
        rasterData.setElemFloatAt(1, 9.743867f);
        rasterData.setElemFloatAt(2, 9.727491f);
        rasterData.setElemFloatAt(3, 10.3416f);
        band.setData(rasterData);
        band.setSolarFlux(1620.0034f);
    }

    private static void addRadiance_07(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("radiance_7", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 7.32744f);
        rasterData.setElemFloatAt(1, 7.2315664f);
        rasterData.setElemFloatAt(2, 7.1493897f);
        rasterData.setElemFloatAt(3, 7.902678f);
        band.setData(rasterData);
        band.setSolarFlux(1503.077f);
    }

    private static void addRadiance_08(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("radiance_8", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 6.528631f);
        rasterData.setElemFloatAt(1, 6.4245615f);
        rasterData.setElemFloatAt(2, 6.3829336f);
        rasterData.setElemFloatAt(3, 7.1044827f);
        band.setData(rasterData);
        band.setSolarFlux(1444.7269f);
    }

    private static void addRadiance_09(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("radiance_9", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 5.3815536f);
        rasterData.setElemFloatAt(1, 5.3058457f);
        rasterData.setElemFloatAt(2, 5.1355033f);
        rasterData.setElemFloatAt(3, 5.8673444f);
        band.setData(rasterData);
        band.setSolarFlux(1381.6986f);
    }

    private static void addRadiance_10(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("radiance_10", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 4.0290546f);
        rasterData.setElemFloatAt(1, 3.9857316f);
        rasterData.setElemFloatAt(2, 3.8904207f);
        rasterData.setElemFloatAt(3, 4.592256f);
        band.setData(rasterData);
        band.setSolarFlux(1242.4438f);
    }

    private static void addRadiance_11(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("radiance_11", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 2.0585244f);
        rasterData.setElemFloatAt(1, 2.0585244f);
        rasterData.setElemFloatAt(2, 1.9964136f);
        rasterData.setElemFloatAt(3, 2.1383808f);
        band.setData(rasterData);
        band.setSolarFlux(1231.1958f);
    }

    private static void addRadiance_12(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("radiance_12", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 3.287515f);
        rasterData.setElemFloatAt(1, 3.1894345f);
        rasterData.setElemFloatAt(2, 3.0949864f);
        rasterData.setElemFloatAt(3, 3.8069787f);
        band.setData(rasterData);
        band.setSolarFlux(1155.3154f);
    }

    private static void addRadiance_13(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("radiance_13", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 1.947779f);
        rasterData.setElemFloatAt(1, 1.8695836f);
        rasterData.setElemFloatAt(2, 1.791388f);
        rasterData.setElemFloatAt(3, 2.3885174f);
        band.setData(rasterData);
        band.setSolarFlux(940.52094f);
    }

    private static void addRadiance_14(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("radiance_14", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 1.8250111f);
        rasterData.setElemFloatAt(1, 1.7334554f);
        rasterData.setElemFloatAt(2, 1.6235886f);
        rasterData.setElemFloatAt(3, 2.2461677f);
        band.setData(rasterData);
        band.setSolarFlux(912.50586f);
    }

    private static void addRadiance_15(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("radiance_15", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 1.3847926f);
        rasterData.setElemFloatAt(1, 1.3576398f);
        rasterData.setElemFloatAt(2, 1.2653203f);
        rasterData.setElemFloatAt(3, 1.6291678f);
        band.setData(rasterData);
        band.setSolarFlux(878.76825f);
    }

    private static void addDetectorIndex(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("detector_index", ProductData.TYPE_INT16);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemIntAt(0, 238);
        rasterData.setElemIntAt(1, 200);
        rasterData.setElemIntAt(2, 423);
        rasterData.setElemIntAt(3, 255);
        band.setData(rasterData);
    }

    private static void addFlagBand(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("l1_flags", ProductData.TYPE_UINT8);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemIntAt(0, 0);
        rasterData.setElemIntAt(1, 0);
        rasterData.setElemIntAt(2, 0);
        rasterData.setElemIntAt(3, 0);
        band.setData(rasterData);
    }

    private static void addSunZenith(Product merisL1BProduct) {
        float[] tiePointData = new float[4];
        tiePointData[0] = 45.452816f;
        tiePointData[1] = 45.414864f;
        tiePointData[2] = 46.960255f;
        tiePointData[3] = 47.088203f;
        merisL1BProduct.addTiePointGrid(new TiePointGrid("sun_zenith", 2, 2, 0, 0, 1, 1, tiePointData));
    }

    private static void addSunAzimuth(Product merisL1BProduct) {
        float[] tiePointData = new float[4];
        tiePointData[0] = 41.056522f;
        tiePointData[1] = 40.484688f;
        tiePointData[2] = 42.686634f;
        tiePointData[3] = 40.39209f;
        merisL1BProduct.addTiePointGrid(new TiePointGrid("sun_azimuth", 2, 2, 0, 0, 1, 1, tiePointData));
    }

    private static void addViewZenith(Product merisL1BProduct) {
        float[] tiePointData = new float[4];
        tiePointData[0] = 18.647131f;
        tiePointData[1] = 21.993055f;
        tiePointData[2] = 3.2978954f;
        tiePointData[3] = 17.234716f;
        merisL1BProduct.addTiePointGrid(new TiePointGrid("view_zenith", 2, 2, 0, 0, 1, 1, tiePointData));
    }

    private static void addViewAzimuth(Product merisL1BProduct) {
        float[] tiePointData = new float[4];
        tiePointData[0] = 282.0428f;
        tiePointData[1] = 281.91473f;
        tiePointData[2] = 282.58694f;
        tiePointData[3] = 282.07883f;
        merisL1BProduct.addTiePointGrid(new TiePointGrid("view_azimuth", 2, 2, 0, 0, 1, 1, tiePointData));
    }

    private static void addAtmPress(Product merisL1BProduct) {
        float[] tiePointData = new float[4];
        tiePointData[0] = 1015.21875f;
        tiePointData[1] = 1015.3f;
        tiePointData[2] = 1015.1375f;
        tiePointData[3] = 1015.0f;
        merisL1BProduct.addTiePointGrid(new TiePointGrid("atm_press", 2, 2, 0, 0, 1, 1, tiePointData));
    }

    private static void addOzone(Product merisL1BProduct) {
        float[] tiePointData = new float[4];
        tiePointData[0] = 241.51414f;
        tiePointData[1] = 241.82562f;
        tiePointData[2] = 240.97311f;
        tiePointData[3] = 241.85562f;
        merisL1BProduct.addTiePointGrid(new TiePointGrid("ozone", 2, 2, 0, 0, 1, 1, tiePointData));
    }

    private static void addMeridWind(Product merisL1BProduct) {
        float[] tiePointData = new float[4];
        tiePointData[0] = -0.61757815f;
        tiePointData[1] = -0.6761719f;
        tiePointData[2] = 0.25625002f;
        tiePointData[3] = -1.0f;
        merisL1BProduct.addTiePointGrid(new TiePointGrid("merid_wind", 2, 2, 0, 0, 1, 1, tiePointData));
    }

    private static void addZonalWind(Product merisL1BProduct) {
        float[] tiePointData = new float[4];
        tiePointData[0] = -0.46875f;
        tiePointData[1] = -0.08125f;
        tiePointData[2] = 2.65f;
        tiePointData[3] = 3.0937502f;
        merisL1BProduct.addTiePointGrid(new TiePointGrid("zonal_wind", 2, 2, 0, 0, 1, 1, tiePointData));
    }

    static void assertCorrect_Rl_Tosa_01(Product product) {
        final Band band = product.getBand("rl_tosa_1");
        assertNotNull(band);
        assertEquals(0.050456270575523376f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.05268685147166252f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.049439869821071625f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.05097590759396553f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Tosa_02(Product product) {
        final Band band = product.getBand("rl_tosa_2");
        assertNotNull(band);
        assertEquals(0.0393572673201561f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.04120594635605812f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.03854338079690933f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.04018130525946617f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Tosa_03(Product product) {
        final Band band = product.getBand("rl_tosa_3");
        assertNotNull(band);
        assertEquals(0.02713007666170597f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.028325466439127922f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.02674200013279915f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.028237035498023033f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Tosa_04(Product product) {
        final Band band = product.getBand("rl_tosa_4");
        assertNotNull(band);
        assertEquals(0.021631352603435516f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.022563502192497253f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.021478239446878433f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.022677475586533546f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Tosa_05(Product product) {
        final Band band = product.getBand("rl_tosa_5");
        assertNotNull(band);
        assertEquals(0.014469144865870476f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.015033824369311333f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.014410327188670635f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.015470479615032673f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Tosa_06(Product product) {
        final Band band = product.getBand("rl_tosa_6");
        assertNotNull(band);
        assertEquals(0.00933693628758192f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.00951949879527092f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.00925732497125864f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.010187982581555843f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Tosa_07(Product product) {
        final Band band = product.getBand("rl_tosa_7");
        assertNotNull(band);
        assertEquals(0.007233708165585995f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.007278947625309229f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.007144390605390072f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.0080880057066679f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Tosa_08(Product product) {
        final Band band = product.getBand("rl_tosa_8");
        assertNotNull(band);
        assertEquals(0.006648535840213299f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.006700865924358368f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.006547912023961544f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.007496106904000044f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Tosa_09(Product product) {
        final Band band = product.getBand("rl_tosa_9");
        assertNotNull(band);
        assertEquals(0.005823937710374594f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.0057051461189985275f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.005722174886614084f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.006619449704885483f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Tosa_10(Product product) {
        final Band band = product.getBand("rl_tosa_10");
        assertNotNull(band);
        assertEquals(0.004694788716733456f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.0046753049828112125f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.004643902648240328f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.005541970953345299f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Tosa_12(Product product) {
        final Band band = product.getBand("rl_tosa_12");
        assertNotNull(band);
        assertEquals(0.004122256767004728f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.003998724278062582f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.004002106375992298f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.0049418555572628975f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Tosa_13(Product product) {
        final Band band = product.getBand("rl_tosa_13");
        assertNotNull(band);
        assertEquals(0.002986186882480979f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.0028321947902441025f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.0028659545350819826f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.003792061936110258f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Path_01(Product product) {
        final Band band = product.getBand("rl_path_1");
        assertNotNull(band);
        assertEquals(0.1460312008857727f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.1418505311012268f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.1535067856311798f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.15570786595344543f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Path_02(Product product) {
        final Band band = product.getBand("rl_path_2");
        assertNotNull(band);
        assertEquals(0.1123882606625557f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.10912559926509857f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.11868877708911896f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.12105292826890945f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Path_03(Product product) {
        final Band band = product.getBand("rl_path_3");
        assertNotNull(band);
        assertEquals(0.07614742964506149f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.07401028275489807f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.08081348985433578f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.08320828527212143f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Path_04(Product product) {
        final Band band = product.getBand("rl_path_4");
        assertNotNull(band);
        assertEquals(0.06532929092645645f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.06352116167545319f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.06942687928676605f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.07179448008537292f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Path_05(Product product) {
        final Band band = product.getBand("rl_path_5");
        assertNotNull(band);
        assertEquals(0.04547905921936035f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.04430543631315231f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.048548802733421326f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.050813592970371246f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Path_06(Product product) {
        final Band band = product.getBand("rl_path_6");
        assertNotNull(band);
        assertEquals(0.030827602371573448f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.030104171484708786f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.033051833510398865f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.03510051965713501f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Path_07(Product product) {
        final Band band = product.getBand("rl_path_7");
        assertNotNull(band);
        assertEquals(0.02365374006330967f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.02314145490527153f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.025477800518274307f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.027370767667889595f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Path_08(Product product) {
        final Band band = product.getBand("rl_path_8");
        assertNotNull(band);
        assertEquals(0.021592577919363976f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.02112475037574768f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.023314012214541435f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.025162074714899063f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Path_09(Product product) {
        final Band band = product.getBand("rl_path_9");
        assertNotNull(band);
        assertEquals(0.01867799647152424f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.018274515867233276f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.02022469788789749f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.022007649764418602f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Path_10(Product product) {
        final Band band = product.getBand("rl_path_10");
        assertNotNull(band);
        assertEquals(0.014980772510170937f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.014641830697655678f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.016297191381454468f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.01800575479865074f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Path_12(Product product) {
        final Band band = product.getBand("rl_path_12");
        assertNotNull(band);
        assertEquals(0.013359303586184978f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.013025998137891293f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.014585016295313835f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.016278089955449104f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Path_13(Product product) {
        final Band band = product.getBand("rl_path_13");
        assertNotNull(band);
        assertEquals(0.009559405967593193f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.00920199602842331f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.010582491755485535f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.012254506349563599f, band.getSampleFloat(1, 1), 1e-8);
    }
}

