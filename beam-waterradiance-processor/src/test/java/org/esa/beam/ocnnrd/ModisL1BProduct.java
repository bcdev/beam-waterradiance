package org.esa.beam.ocnnrd;


import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.TiePointGrid;

class ModisL1BProduct {

    // creates an in memory product of Type "Modis L1B" with four pixels from the atlantic ocean, handpicked and cloud-free
    // Product: A2006302181500.L1B_LAC
    //
    // px       original [x,y]
    // [0] :    [537, 800]
    // [1] :    [545, 831]
    // [2] :    [693, 903]
    // [3] :    [808, 582]

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
        rasterData.setElemFloatAt(0, 0.10293106f);
        rasterData.setElemFloatAt(1, 0.10484232f);
        rasterData.setElemFloatAt(2, 0.11404275f);
        rasterData.setElemFloatAt(3, 0.11209194f);
        band.setData(rasterData);
    }

    private static void addEV_RefSB_9(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("EV_1KM_RefSB_9", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.08083943f);
        rasterData.setElemFloatAt(1, 0.08215659f);
        rasterData.setElemFloatAt(2, 0.09006805f);
        rasterData.setElemFloatAt(3, 0.08847896f);
        band.setData(rasterData);
    }

    private static void addEV_RefSB_10(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("EV_1KM_RefSB_10", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.057613704f);
        rasterData.setElemFloatAt(1, 0.058612272f);
        rasterData.setElemFloatAt(2, 0.0634268f);
        rasterData.setElemFloatAt(3, 0.06347435f);
        band.setData(rasterData);
    }

    private static void addEV_RefSB_11(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("EV_1KM_RefSB_11", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.03888675f);
        rasterData.setElemFloatAt(1, 0.03944589f);
        rasterData.setElemFloatAt(2, 0.04179913f);
        rasterData.setElemFloatAt(3, 0.04329179f);
        band.setData(rasterData);
    }

    private static void addEV_RefSB_12(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("EV_1KM_RefSB_12", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.033735927f);
        rasterData.setElemFloatAt(1, 0.034214936f);
        rasterData.setElemFloatAt(2, 0.036203213f);
        rasterData.setElemFloatAt(3, 0.0375452f);
        band.setData(rasterData);
    }

    private static void addEV_RefSB_13lo(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("EV_1KM_RefSB_13lo", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.015850909f);
        rasterData.setElemFloatAt(1, 0.016265588f);
        rasterData.setElemFloatAt(2, 0.01726501f);
        rasterData.setElemFloatAt(3, 0.018259773f);
        band.setData(rasterData);
    }

    private static void addEV_RefSB_14lo(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("EV_1KM_RefSB_14lo", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.015179039f);
        rasterData.setElemFloatAt(1, 0.01560035f);
        rasterData.setElemFloatAt(2, 0.016500099f);
        rasterData.setElemFloatAt(3, 0.017499821f);
        band.setData(rasterData);
    }

    private static void addEV_RefSB_15(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("EV_1KM_RefSB_15", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.011262999f);
        rasterData.setElemFloatAt(1, 0.011674728f);
        rasterData.setElemFloatAt(2, 0.0123815285f);
        rasterData.setElemFloatAt(3, 0.013095192f);
        band.setData(rasterData);
    }

    private static void addEV_RefSB_16(Product merisL1BProduct) {
        final Band band = merisL1BProduct.addBand("EV_1KM_RefSB_16", ProductData.TYPE_FLOAT32);
        final ProductData rasterData = band.createCompatibleRasterData();
        rasterData.setElemFloatAt(0, 0.0071801194f);
        rasterData.setElemFloatAt(1, 0.007587253f);
        rasterData.setElemFloatAt(2, 0.008134709f);
        rasterData.setElemFloatAt(3, 0.008551724f);
        band.setData(rasterData);
    }

    private static void addSolarZenith(Product modisL1BProduct) {
        float[] tiePointData = new float[4];
        tiePointData[0] = 50.231f;
        tiePointData[1] = 50.050995f;
        tiePointData[2] = 50.379997f;
        tiePointData[3] = 53.290997f;
        modisL1BProduct.addTiePointGrid(new TiePointGrid("SolarZenith", 2, 2, 0, 0, 1, 1, tiePointData));
    }

    private static void addSolarAzimuth(Product modisL1BProduct) {
        float[] tiePointData = new float[4];
        tiePointData[0] = -152.95699f;
        tiePointData[1] = -152.688f;
        tiePointData[2] = -150.57701f;
        tiePointData[3] = -150.9715f;
        modisL1BProduct.addTiePointGrid(new TiePointGrid("SolarAzimuth", 2, 2, 0, 0, 1, 1, tiePointData));
    }

    private static void addSensorZenith(Product modisL1BProduct) {
        float[] tiePointData = new float[4];
        tiePointData[0] = 12.4345f;
        tiePointData[1] = 11.712f;
        tiePointData[2] = 1.6778986f;
        tiePointData[3] = 12.0634985f;
        modisL1BProduct.addTiePointGrid(new TiePointGrid("SensorZenith", 2, 2, 0, 0, 1, 1, tiePointData));
    }

    private static void addSensorAzimuth(Product modisL1BProduct) {
        float[] tiePointData = new float[4];
        tiePointData[0] = 81.05548f;
        tiePointData[1] = 80.81529f;
        tiePointData[2] = -102.08755f;
        tiePointData[3] = -99.980995f;
        modisL1BProduct.addTiePointGrid(new TiePointGrid("SensorAzimuth", 2, 2, 0, 0, 1, 1, tiePointData));
    }
}
