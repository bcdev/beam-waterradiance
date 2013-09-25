package org.esa.beam.ocnnrd;

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
            MODIS_L1B_RADIANCE_1_BAND_NAME, // 0
            MODIS_L1B_RADIANCE_2_BAND_NAME, // 1
            MODIS_L1B_RADIANCE_3_BAND_NAME, // 2
            MODIS_L1B_RADIANCE_4_BAND_NAME, // 3
            MODIS_L1B_RADIANCE_5_BAND_NAME, // 4
            MODIS_L1B_RADIANCE_6_BAND_NAME, // 5
            MODIS_L1B_RADIANCE_7_BAND_NAME, // 6
            MODIS_L1B_RADIANCE_8_BAND_NAME, // 7
            MODIS_L1B_RADIANCE_9_BAND_NAME, // 8
    };
    private static int MODIS_L1B_NUM_SPECTRAL_BANDS = MODIS_L1B_SPECTRAL_BAND_NAMES.length;

    @Override
    public int getNumSpectralBands() {
        return MODIS_L1B_NUM_SPECTRAL_BANDS;
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
}
