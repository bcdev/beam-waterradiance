package org.esa.beam.ocnnrd;

import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.pointop.Sample;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;

public class SeaWiFSSensorConfig implements SensorConfig {

    private static String SEAWIFS_L1B_RADIANCE_1_BAND_NAME = "L_412";
    private static String SEAWIFS_L1B_RADIANCE_2_BAND_NAME = "L_443";
    private static String SEAWIFS_L1B_RADIANCE_3_BAND_NAME = "L_490";
    private static String SEAWIFS_L1B_RADIANCE_4_BAND_NAME = "L_510";
    private static String SEAWIFS_L1B_RADIANCE_5_BAND_NAME = "L_555";
    private static String SEAWIFS_L1B_RADIANCE_6_BAND_NAME = "L_670";
    private static String SEAWIFS_L1B_RADIANCE_7_BAND_NAME = "L_765";
    private static String SEAWIFS_L1B_RADIANCE_8_BAND_NAME = "L_865";

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
    public int getNumSpectralBands() {
        return SEAWIFS_L1B_NUM_SPECTRAL_BANDS;
    }

    @Override
    public String[] getSpectralBandNames() {
        return SEAWIFS_L1B_SPECTRAL_BAND_NAMES;
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
        //@todo get the fluxes!
        return new double[0];
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
}
