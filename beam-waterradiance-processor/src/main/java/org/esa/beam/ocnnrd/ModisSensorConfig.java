package org.esa.beam.ocnnrd;

import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.gpf.pointop.Sample;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;

class ModisSensorConfig implements SensorConfig {

    private static String MODIS_L1B_RADIANCE_1_BAND_NAME = "EV_1KM_RefSB.8";
    private static String MODIS_L1B_RADIANCE_2_BAND_NAME = "EV_1KM_RefSB.9";
    private static String MODIS_L1B_RADIANCE_3_BAND_NAME = "EV_1KM_RefSB.10";
    private static String MODIS_L1B_RADIANCE_4_BAND_NAME = "EV_1KM_RefSB.11";
    private static String MODIS_L1B_RADIANCE_5_BAND_NAME = "EV_1KM_RefSB.12";
    private static String MODIS_L1B_RADIANCE_6_BAND_NAME = "EV_1KM_RefSB.13lo";
    private static String MODIS_L1B_RADIANCE_7_BAND_NAME = "EV_1KM_RefSB.14lo";
    private static String MODIS_L1B_RADIANCE_8_BAND_NAME = "EV_1KM_RefSB.15";
    private static String MODIS_L1B_RADIANCE_9_BAND_NAME = "EV_1KM_RefSB.16";

    private static String[] MODIS_L1B_SPECTRAL_BAND_NAMES = {
            MODIS_L1B_RADIANCE_1_BAND_NAME,
            MODIS_L1B_RADIANCE_2_BAND_NAME,
            MODIS_L1B_RADIANCE_3_BAND_NAME,
            MODIS_L1B_RADIANCE_4_BAND_NAME,
            MODIS_L1B_RADIANCE_5_BAND_NAME,
            MODIS_L1B_RADIANCE_6_BAND_NAME,
            MODIS_L1B_RADIANCE_7_BAND_NAME,
            MODIS_L1B_RADIANCE_8_BAND_NAME,
            MODIS_L1B_RADIANCE_9_BAND_NAME,
    };
    private static int MODIS_L1B_NUM_SPECTRAL_BANDS = MODIS_L1B_SPECTRAL_BAND_NAMES.length;
    private static double surfacePressureDefaultValue = 1019.0;
    private static double ozoneDefaultValue = 330.0;

    @Override
    public int getNumSpectralBands() {
        return MODIS_L1B_NUM_SPECTRAL_BANDS;
    }

    @Override
    public String[] getSpectralBandNames() {
        return MODIS_L1B_SPECTRAL_BAND_NAMES;
    }

    @Override
    public Sensor getSensor() {
        return Sensor.MODIS;
    }

    @Override
    public void configureSourceSamples(SampleConfigurer sampleConfigurer, boolean csvMode) {
        sampleConfigurer.defineSample(Constants.SRC_SZA, "SolarZenith");
        sampleConfigurer.defineSample(Constants.SRC_SAA, "SolarAzimuth");
        sampleConfigurer.defineSample(Constants.SRC_VZA, "SensorZenith");
        sampleConfigurer.defineSample(Constants.SRC_VAA, "SensorAzimuth");
        for (int i = 0; i < MODIS_L1B_NUM_SPECTRAL_BANDS; i++) {
            sampleConfigurer.defineSample(Constants.SRC_RAD_OFFSET + i, MODIS_L1B_SPECTRAL_BAND_NAMES[i]);
        }
    }

    @Override
    public void copyTiePointData(double[] inputs, Sample[] sourceSamples) {
        inputs[0] = sourceSamples[Constants.SRC_SZA].getDouble();
        inputs[1] = sourceSamples[Constants.SRC_SAA].getDouble();
        inputs[2] = sourceSamples[Constants.SRC_VZA].getDouble();
        inputs[3] = sourceSamples[Constants.SRC_VAA].getDouble();
        inputs[4] = surfacePressureDefaultValue;
        inputs[5] = ozoneDefaultValue;
    }

    @Override
    public double[] getSolarFluxes(Product sourceProduct) {
        double[] solarFluxes;
        final String globalMetadataName = "GLOBAL_METADATA";
        final String solarFluxesName = "Solar_Irradiance_on_RSB_Detectors_over_pi";
        final ProductData productData =
                sourceProduct.getMetadataRoot().getElement(globalMetadataName).getAttribute(solarFluxesName).getData();
        solarFluxes = new double[MODIS_L1B_NUM_SPECTRAL_BANDS];
        int[] startPositionInProductData = new int[]{180, 190, 200, 210, 220, 230, 250, 270, 280};
        for(int i = 0; i < MODIS_L1B_NUM_SPECTRAL_BANDS; i++) {
            for(int j = 0; j < 10; j++) {
                solarFluxes[i] += productData.getElemDoubleAt(startPositionInProductData[i] + j);
            }
            solarFluxes[i] /= 10;
        }
        return solarFluxes;
    }

    @Override
    public double[] copySolarFluxes(double[] input, double[] solarFluxes) {
        System.arraycopy(solarFluxes, 0, input, Constants.SRC_SOL_FLUX_OFFSET, MODIS_L1B_NUM_SPECTRAL_BANDS);
        return input;
    }

    @Override
    public double getSurfacePressure() {
        return surfacePressureDefaultValue;
    }

    @Override
    public double getOzone() {
        return ozoneDefaultValue;
    }
}
