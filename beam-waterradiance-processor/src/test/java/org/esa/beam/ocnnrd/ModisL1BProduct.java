package org.esa.beam.ocnnrd;


import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.TiePointGrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

class ModisL1BProduct {

    // creates an in memory product of Type "Modis L1B" with four pixels from the atlantic ocean, handpicked and cloud-free
    // Product: A2006131132000.L1B_LAC
    //
    // px       original [x,y]  [lon, lat]
    // [0] :    [53, 149]       [-4.297, -17.096]
    // [1] :    [64, 182]       [-3.909, -17.367]
    // [2] :    [0,  171]       [-6.256, -17.488]
    // [3] :    [28, 341]       [-4.866, -18.886]
    //
    static Product create() {
        final Product modisL1BProduct = new Product("Modis L1B", "MODIS Level 1B", 2, 2);

        addEV_RefSB_8(modisL1BProduct);
        addEV_RefSB_9(modisL1BProduct);
        addEV_RefSB_10(modisL1BProduct);
        addEV_RefSB_11(modisL1BProduct);
        addEV_RefSB_12(modisL1BProduct);
        addEV_RefSB_13lo(modisL1BProduct);
        addEV_RefSB_14lo(modisL1BProduct);
        addEV_RefSB_15(modisL1BProduct);
        addEV_RefSB_16(modisL1BProduct);

        addSolarZenith(modisL1BProduct);
        addSolarAzimuth(modisL1BProduct);
        addSensorZenith(modisL1BProduct);
        addSensorAzimuth(modisL1BProduct);

        return modisL1BProduct;
    }

    private static void addEV_RefSB_8(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("EV_1KM_RefSB_8", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.16494925f);
        rasterData.setElemFloatAt(1, 0.16002956f);
        rasterData.setElemFloatAt(2, 0.1858146f);
        rasterData.setElemFloatAt(3, 0.17540193f);
        band.setData(rasterData);
    }

    private static void addEV_RefSB_9(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("EV_1KM_RefSB_9", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.13000539f);
        rasterData.setElemFloatAt(1, 0.12540033f);
        rasterData.setElemFloatAt(2, 0.15010019f);
        rasterData.setElemFloatAt(3, 0.1403755f);
        band.setData(rasterData);
    }

    private static void addEV_RefSB_10(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("EV_1KM_RefSB_10", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.08885628f);
        rasterData.setElemFloatAt(1, 0.086046495f);
        rasterData.setElemFloatAt(2, 0.105446234f);
        rasterData.setElemFloatAt(3, 0.098409556f);
        band.setData(rasterData);
    }

    private static void addEV_RefSB_11(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("EV_1KM_RefSB_11", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.058475647f);
        rasterData.setElemFloatAt(1, 0.056925192f);
        rasterData.setElemFloatAt(2, 0.071732305f);
        rasterData.setElemFloatAt(3, 0.06717627f);
        band.setData(rasterData);
    }

    private static void addEV_RefSB_12(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("EV_1KM_RefSB_12", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.050132222f);
        rasterData.setElemFloatAt(1, 0.048718113f);
        rasterData.setElemFloatAt(2, 0.062036596f);
        rasterData.setElemFloatAt(3, 0.05813898f);
        band.setData(rasterData);
    }

    private static void addEV_RefSB_13lo(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("EV_1KM_RefSB_13lo", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.024006631f);
        rasterData.setElemFloatAt(1, 0.023111792f);
        rasterData.setElemFloatAt(2, 0.03191104f);
        rasterData.setElemFloatAt(3, 0.031167747f);
        band.setData(rasterData);
    }

    private static void addEV_RefSB_14lo(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("EV_1KM_RefSB_14lo", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.022977462f);
        rasterData.setElemFloatAt(1, 0.02202426f);
        rasterData.setElemFloatAt(2, 0.030605521f);
        rasterData.setElemFloatAt(3, 0.03009944f);
        band.setData(rasterData);
    }

    private static void addEV_RefSB_15(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("EV_1KM_RefSB_15", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.016668053f);
        rasterData.setElemFloatAt(1, 0.016030708f);
        rasterData.setElemFloatAt(2, 0.02305809f);
        rasterData.setElemFloatAt(3, 0.023432443f);
        band.setData(rasterData);
    }

    private static void addEV_RefSB_16(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("EV_1KM_RefSB_16", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.010241289f);
        rasterData.setElemFloatAt(1, 0.009856753f);
        rasterData.setElemFloatAt(2, 0.015201397f);
        rasterData.setElemFloatAt(3, 0.016800905f);
        band.setData(rasterData);
    }

    private static void addSolarZenith(Product modisL1BProduct) {
        float[] tiePointData = new float[4];
        tiePointData[0] = 39.154f;
        tiePointData[1] = 39.559498f;
        tiePointData[2] = 38.683697f;
        tiePointData[3] = 40.466f;
        modisL1BProduct.addTiePointGrid(new TiePointGrid("SolarZenith", 2, 2, 0.5f, 0.5f, 1, 1, tiePointData));
    }

    private static void addSolarAzimuth(Product modisL1BProduct) {
        float[] tiePointData = new float[4];
        tiePointData[0] = -27.384f;
        tiePointData[1] = -27.723999f;
        tiePointData[2] = -24.4893f;
        tiePointData[3] = -25.512999f;
        modisL1BProduct.addTiePointGrid(new TiePointGrid("SolarAzimuth", 2, 2, 0.5f, 0.5f, 1, 1, tiePointData));
    }

    private static void addSensorZenith(Product modisL1BProduct) {
        float[] tiePointData = new float[4];
        tiePointData[0] = 59.068996f;
        tiePointData[1] = 57.864998f;
        tiePointData[2] = 65.289f;
        tiePointData[3] = 61.9391f;
        modisL1BProduct.addTiePointGrid(new TiePointGrid("SensorZenith", 2, 2, 0.5f, 0.5f, 1, 1, tiePointData));
    }

    private static void addSensorAzimuth(Product modisL1BProduct) {
        float[] tiePointData = new float[4];
        tiePointData[0] = 84.05899f;
        tiePointData[1] = 83.78f;
        tiePointData[2] = 84.576f;
        tiePointData[3] = 84.42799f;
        modisL1BProduct.addTiePointGrid(new TiePointGrid("SensorAzimuth", 2, 2, 0.5f, 0.5f, 1, 1, tiePointData));
    }

    static void assertCorrect_Rl_Tosa_01(Product product) {
        final Band band = product.getBand("rl_tosa_1");
        assertNotNull(band);
        assertEquals(0.067852683365345f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.06620963662862778f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.07595263421535492f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.073552705347538f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Path_01(Product product) {
        final Band band = product.getBand("rl_path_1");
        assertNotNull(band);
        assertEquals(0.20610173046588898f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.20356963574886322f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.27903255820274353f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.23324379324913025f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Reflec_01(Product product) {
        final Band band = product.getBand("reflec_1");
        assertNotNull(band);
        assertEquals(0.017709065228700638f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.01580827683210373f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.00025662017287686467f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.014247491955757141f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Reflec_05(Product product) {
        final Band band = product.getBand("reflec_5");
        assertNotNull(band);
        assertEquals(0.0036630176473408937f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.003266403917223215f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.0004935900215059519f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.0026215179823338985f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Trans_Down_01(Product product) {
        final Band band = product.getBand("trans_down_1");
        assertNotNull(band);
        assertEquals(0.8367400765419006f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.834771454334259f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.8377476930618286f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.8323149085044861f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Trans_Up_01(Product product) {
        final Band band = product.getBand("trans_up_1");
        assertNotNull(band);
        assertEquals(0.758436381816864f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.7630296349525452f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.7267043590545654f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.7365984320640564f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Aot_550(Product product) {
        final Band band = product.getBand("aot_550");
        assertNotNull(band);
        assertEquals(0.012107848189771175f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.020218607038259506f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.01005183532834053f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.024830903857946396f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Ang_864_443(Product product) {
        final Band band = product.getBand("ang_864_443");
        assertNotNull(band);
        assertEquals(1.468503475189209f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(1.4132076501846313f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(2.1989939212799072f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(1.3871097564697266f, band.getSampleFloat(1, 1), 1e-8);
        // @todo 1 tb/** values much too high tb 2013-12-04
    }

    static void assertCorrect_A_Pig(Product product) {
        final Band band = product.getBand("a_pig");
        assertNotNull(band);
        assertEquals(0.002240736037492752f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.0026596097741276026f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.6221919655799866f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.0038307695649564266f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_A_Ys(Product product) {
        final Band band = product.getBand("a_ys");
        assertNotNull(band);
        assertEquals(0.0021564215421676636f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.002500100526958704f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(1.4074194431304932f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.003778640413656831f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_A_Part(Product product) {
        final Band band = product.getBand("a_part");
        assertNotNull(band);
        assertEquals(0.011338621377944946f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.011394836939871311f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.06550013273954391f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.009259347803890705f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_B_Part(Product product) {
        final Band band = product.getBand("b_part");
        assertNotNull(band);
        assertEquals(0.04788954555988312f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.036722056567668915f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.0025518611073493958f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.017030920833349228f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_B_Wit(Product product) {
        final Band band = product.getBand("b_wit");
        assertNotNull(band);
        assertEquals(0.026079272851347923f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.022013362497091293f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.005264400038868189f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.014684848487377167f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Sum_Sq(Product product) {
        final Band band = product.getBand("sum_sq");
        assertNotNull(band);
        assertEquals(0.00818241573870182f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.006444675847887993f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.03602083399891853f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.013865271583199501f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Num_Iter(Product product) {
        final Band band = product.getBand("num_iter");
        assertNotNull(band);
        assertEquals(45.f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(11.f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(150.f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(32.f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Temperature(Product product) {
        final Band band = product.getBand("temperature");
        assertNotNull(band);
        assertEquals(15.f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(15.f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(15.f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(15.f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Salinity(Product product) {
        final Band band = product.getBand("salinity");
        assertNotNull(band);
        assertEquals(35.f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(35.f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(35.f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(35.f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Atm_Press_2(Product product) {
        final Band band = product.getBand("atm_press_2");
        assertNotNull(band);
        assertEquals(1019.0f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(1019.0f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(1019.0f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(1019.0f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Ozone_2(Product product) {
        final Band band = product.getBand("ozone_2");
        assertNotNull(band);
        assertEquals(330.f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(330.f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(330.f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(330.f, band.getSampleFloat(1, 1), 1e-8);
    }
}