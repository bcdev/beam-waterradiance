package org.esa.beam.ocnnrd;

import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.pointop.Sample;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;

public class SeaWiFSSensorConfig implements SensorConfig {

    private static final int[] SPECTRAL_OUTPUT_INDEXES = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
    private static final float[] SPECTRAL_OUTPUT_WAVELENGTHS = new float[]{412.f, 443.f, 490.f, 510.f, 555.f, 670.f, 765.f, 865.f};
//    private static final float[] SPECTRAL_OUTPUT_BANDWIDTHS = new float[]{20.f, 20.f, 20.f, 20.f, 20.f, 20.f, 40.f, 40.f};
    private final static double[] defaultSolarFluxes = {1754.1875, 1894.665263, 1970.7535,
            1863.546111, 1836.141, 1510.442273, 1232.692821, 947.14};
    private static final String SEAWIFS_L1B_RADIANCE_1_BAND_NAME = "L_412";
    private static final String SEAWIFS_L1B_RADIANCE_2_BAND_NAME = "L_443";
    private static final String SEAWIFS_L1B_RADIANCE_3_BAND_NAME = "L_490";
    private static final String SEAWIFS_L1B_RADIANCE_4_BAND_NAME = "L_510";
    private static final String SEAWIFS_L1B_RADIANCE_5_BAND_NAME = "L_555";
    private static final String SEAWIFS_L1B_RADIANCE_6_BAND_NAME = "L_670";
    private static final String SEAWIFS_L1B_RADIANCE_7_BAND_NAME = "L_765";
    private static final String SEAWIFS_L1B_RADIANCE_8_BAND_NAME = "L_865";

    private static String[] SEAWIFS_L1B_SPECTRAL_BAND_NAMES = {
            SEAWIFS_L1B_RADIANCE_1_BAND_NAME,
            SEAWIFS_L1B_RADIANCE_2_BAND_NAME,
            SEAWIFS_L1B_RADIANCE_3_BAND_NAME,
            SEAWIFS_L1B_RADIANCE_4_BAND_NAME,
            SEAWIFS_L1B_RADIANCE_5_BAND_NAME,
            SEAWIFS_L1B_RADIANCE_6_BAND_NAME,
            SEAWIFS_L1B_RADIANCE_7_BAND_NAME,
            SEAWIFS_L1B_RADIANCE_8_BAND_NAME,
    };

    private static int SEAWIFS_L1B_NUM_SPECTRAL_BANDS = SEAWIFS_L1B_SPECTRAL_BAND_NAMES.length;

    private static double surfacePressureDefaultValue = 1019.0;
    private static double ozoneDefaultValue = 330.0;

    @Override
    public int getNumSpectralInputBands() {
        return SEAWIFS_L1B_NUM_SPECTRAL_BANDS;
    }

    @Override
    public String[] getSpectralInputBandNames() {
        return SEAWIFS_L1B_SPECTRAL_BAND_NAMES;
    }

    @Override
    public int getNumSpectralOutputBands() {
        return SEAWIFS_L1B_NUM_SPECTRAL_BANDS;
    }

    @Override
    public int[] getSpectralOutputBandIndices() {
        return SPECTRAL_OUTPUT_INDEXES;
    }

    @Override
    public float[] getSpectralOutputWavelengths() {
        return SPECTRAL_OUTPUT_WAVELENGTHS;
    }

    @Override
    public Sensor getSensor() {
        return Sensor.SEAWIFS;
    }

    @Override
    public void configureSourceSamples(SampleConfigurer sampleConfigurer, boolean csvMode) {
        sampleConfigurer.defineSample(Constants.SRC_SZA, "solz");
        sampleConfigurer.defineSample(Constants.SRC_SAA, "sola");
        sampleConfigurer.defineSample(Constants.SRC_VZA, "senz");
        sampleConfigurer.defineSample(Constants.SRC_VAA, "sena");
        for (int i = 0; i < SEAWIFS_L1B_NUM_SPECTRAL_BANDS; i++) {
            sampleConfigurer.defineSample(Constants.SRC_RAD_OFFSET + i, SEAWIFS_L1B_SPECTRAL_BAND_NAMES[i]);
        }
    }

    @Override
    public void copyTiePointData(double[] inputs, Sample[] sourceSamples) {
        inputs[0] = sourceSamples[Constants.SRC_SZA].getDouble();
        inputs[1] = sourceSamples[Constants.SRC_SAA].getDouble();
        inputs[2] = sourceSamples[Constants.SRC_VZA].getDouble();
        inputs[3] = sourceSamples[Constants.SRC_VAA].getDouble();
        inputs[4] = 1019.0;
        inputs[5] = 330.0;
    }

    @Override
    public double[] getSolarFluxes(Product sourceProduct) {
        return defaultSolarFluxes;
    }

    @Override
    public double[] copySolarFluxes(double[] input, double[] solarFluxes) {
        System.arraycopy(solarFluxes, 0, input, Constants.SRC_SOL_FLUX_OFFSET, SEAWIFS_L1B_NUM_SPECTRAL_BANDS);
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

    @Override
    public double getEarthSunDistance() {
        return 1;
    }

    @Override
    public void init(Product sourceProduct) {
        // do nothing
    }

    @Override
    public int getDetectorIndex(Sample[] samples) {
        return -1;
    }

    @Override
    public int getTargetSampleOffset() {
        return 2;
    }

    @Override
    public double correctAzimuth(double azimuth) {
        if (azimuth < 0.0) {
            return azimuth + 360.0;
        }
        return azimuth;
    }
}
