package org.esa.beam.ocnnrd;

import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.pointop.Sample;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;

class ModisCsvConfig implements SensorConfig {

    private static final String[] SPECTRAL_INPUT_BANDS_NAMES = {"Radiance_TOA_412",
            "Radiance_TOA_443",
            "Radiance_TOA_488",
            "Radiance_TOA_531",
            "Radiance_TOA_547",
            "Radiance_TOA_645",
            "Radiance_TOA_748",
            "Radiance_TOA_869"};
    private static final int[] SPECTRAL_OUTPUT_INDEXES = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
    private static final float[] SPECTRAL_OUTPUT_WAVELENGTHS = new float[]{412.f, 443.f, 488.f, 531.f, 547.f, 645.f, 748.f, 869.f};

    @Override
    public Sensor getSensor() {
        return Sensor.MODIS;
    }

    @Override
    public int getNumSpectralInputBands() {
        return SPECTRAL_INPUT_BANDS_NAMES.length;
    }

    @Override
    public String[] getSpectralInputBandNames() {
        return SPECTRAL_INPUT_BANDS_NAMES;
    }

    @Override
    public int getNumSpectralOutputBands() {
        return SPECTRAL_INPUT_BANDS_NAMES.length;
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
    public void configureSourceSamples(SampleConfigurer sampleConfigurer, boolean csvMode) {
        sampleConfigurer.defineSample(Constants.SRC_SZA, "Solar_Zenith");
        sampleConfigurer.defineSample(Constants.SRC_SAA, "Relative_Azimuth");
        sampleConfigurer.defineSample(Constants.SRC_VZA, "Viewing_Zenith");
        sampleConfigurer.defineSample(Constants.SRC_VAA, "Relative_Azimuth");
        // the error in the azimuth angles introduced here is corrected in correctViewAzimuth() below tb 2013-11-14

        sampleConfigurer.defineSample(Constants.SRC_PRESS, "Pressure");
        sampleConfigurer.defineSample(Constants.SRC_OZ, "Ozone");
        sampleConfigurer.defineSample(Constants.SRC_MWIND, "WindSpeedM");
        sampleConfigurer.defineSample(Constants.SRC_ZWIND, "WindSpeedZ");

        for (int i = 0; i < SPECTRAL_INPUT_BANDS_NAMES.length; i++) {
            sampleConfigurer.defineSample(Constants.SRC_RAD_OFFSET + i, SPECTRAL_INPUT_BANDS_NAMES[i]);
        }
    }

    @Override
    public void copyTiePointData(double[] inputs, Sample[] sourceSamples) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double[] getSolarFluxes(Product sourceProduct) {
        return new double[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double[] copySolarFluxes(double[] input, double[] solarFluxes) {
        return new double[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double getSurfacePressure() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double getOzone() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double getEarthSunDistanceInAE() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void init(Product sourceProduct) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getDetectorIndex(Sample[] samples) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getTargetSampleOffset() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double correctSunAzimuth(double sunAzimuth) {
        return sunAzimuth;
    }

    @Override
    public double correctViewAzimuth(double viewAzimuth) {
        // we need to cope with the fact that the csv-file contains only the relative azimuth angle. Which is also the
        // one we need in the algorithm. To have this value in the end, we count double the raa here.
        // saa = raa_in
        // vaa = 2 * raa_in
        // raa = abs(vaa - saa) = abs(raa_in) ... which is what we want. tb 2013-11-14
        return 2.0 * viewAzimuth;
    }
}
