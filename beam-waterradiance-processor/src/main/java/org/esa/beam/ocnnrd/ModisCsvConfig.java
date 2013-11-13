package org.esa.beam.ocnnrd;

import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.pointop.Sample;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;

class ModisCsvConfig implements SensorConfig {

    private static final String[] SPECTRAL_INPUT_BANDS_NAMES = {"Radiance_TOA_412", "Radiance_TOA_443", "Radiance_TOA_488", "Radiance_TOA_531", "Radiance_TOA_547", "Radiance_TOA_645", "Radiance_TOA_748", "Radiance_TOA_869"};

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
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int[] getSpectralOutputBandIndices() {
        return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float[] getSpectralOutputWavelengths() {
        return new float[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void configureSourceSamples(SampleConfigurer sampleConfigurer, boolean csvMode) {
        //To change body of implemented methods use File | Settings | File Templates.
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
    public double getEarthSunDistance() {
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
    public double correctAzimuth(double azimuth) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
