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
        assertEquals(0.3804172873497009f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.35432690382003784f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.48044803738594055f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.4654878079891205f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Reflec_01(Product product) {
        final Band band = product.getBand("reflec_norm_1");
        assertNotNull(band);
        assertEquals(0.015177609398961067f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.01519598625600338f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.011117346584796906f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.00017023543477989733f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Reflec_05(Product product) {
        final Band band = product.getBand("reflec_norm_5");
        assertNotNull(band);
        assertEquals(0.0023378152400255203f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.002348458394408226f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.0022205067798495293f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.00038845211383886635f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Trans_Down_01(Product product) {
        final Band band = product.getBand("trans_down_1");
        assertNotNull(band);
        assertEquals(0.8095505833625793f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.8193200826644897f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.7055725455284119f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.6982688307762146f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Trans_Up_01(Product product) {
        final Band band = product.getBand("trans_up_1");
        assertNotNull(band);
        assertEquals(0.7100877165794373f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.7349326014518738f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.5518251061439514f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.5580682158470154f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Aot_550(Product product) {
        final Band band = product.getBand("aot_550");
        assertNotNull(band);
        assertEquals(0.1920083612203598f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.11819527298212051f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(1.0f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(1.0f, band.getSampleFloat(1, 1), 1e-8);
        // @todo 1 tb/** too high and inconsistent values! tb 2013-12-06
    }

    static void assertCorrect_Ang_864_443(Product product) {
        final Band band = product.getBand("ang_864_443");
        assertNotNull(band);
        assertEquals(2.1329030990600586f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(2.022620439529419f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(2.1989939212799072f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(2.1989939212799072f, band.getSampleFloat(1, 1), 1e-8);
        // @todo 1 tb/** values much too high tb 2013-12-04
    }

    static void assertCorrect_A_Pig(Product product) {
        final Band band = product.getBand("a_pig");
        assertNotNull(band);
        assertEquals(0.005582201760262251f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.00560918590053916f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.007245012558996677f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.16977369785308838f, band.getSampleFloat(1, 1), 1e-8);
        // @todo 1 tb/** inconsistent tb 2013-12-06
    }

    static void assertCorrect_A_Ys(Product product) {
        final Band band = product.getBand("a_ys");
        assertNotNull(band);
        assertEquals(0.005621954333037138f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.005637760739773512f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.007694250904023647f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(8.480278015136719f, band.getSampleFloat(1, 1), 1e-8);
        // @todo 1 tb/** inconsistent tb 2013-12-06
    }

    static void assertCorrect_A_Part(Product product) {
        final Band band = product.getBand("a_part");
        assertNotNull(band);
        assertEquals(0.0055155581794679165f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.0055581945925951f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.008459586650133133f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(4.953032493591309f, band.getSampleFloat(1, 1), 1e-8);
        // @todo 1 tb/** inconsistent tb 2013-12-09
    }

    static void assertCorrect_B_Part(Product product) {
        final Band band = product.getBand("b_part");
        assertNotNull(band);
        assertEquals(0.009469808079302311f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.009497999213635921f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.008607530035078526f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.003108347300440073f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_B_Wit(Product product) {
        final Band band = product.getBand("b_wit");
        assertNotNull(band);
        assertEquals(0.009756953455507755f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.009780358523130417f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.009412834420800209f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.007820493541657925f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Sum_Sq(Product product) {
        final Band band = product.getBand("sum_sq");
        assertNotNull(band);
        assertEquals(1.0162994861602783f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.818674623966217f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(1.8091602325439453f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(1.5762733221054077f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Num_Iter(Product product) {
        final Band band = product.getBand("num_iter");
        assertNotNull(band);
        assertEquals(41.f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(31.f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(150.f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(150.f, band.getSampleFloat(1, 1), 1e-8);
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