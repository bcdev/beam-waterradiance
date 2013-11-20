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
        assertEquals(0.021598128601908684f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.021075131371617317f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.02417641691863537f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.023412497714161873f, band.getSampleFloat(1, 1), 1e-8);
    }

    static void assertCorrect_Rl_Path_01(Product product) {
        final Band band = product.getBand("rl_path_1");
        assertNotNull(band);
        assertEquals(0.2077283412218094f, band.getSampleFloat(0, 0), 1e-8);
        assertEquals(0.2039971798658371f, band.getSampleFloat(1, 0), 1e-8);
        assertEquals(0.28996893763542175f, band.getSampleFloat(0, 1), 1e-8);
        assertEquals(0.23655562102794647f, band.getSampleFloat(1, 1), 1e-8);
    }
}