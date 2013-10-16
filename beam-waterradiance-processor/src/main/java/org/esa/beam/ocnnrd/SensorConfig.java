package org.esa.beam.ocnnrd;

import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.pointop.Sample;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;

public interface SensorConfig {

    Sensor getSensor();

    int getNumSpectralInputBands();

    String[] getSpectralInputBandNames();

    int getNumSpectralOutputBands();

    int[] getSpectralOutputBandIndices();

    float[] getSpectralOutputWavelengths();

    void configureSourceSamples(SampleConfigurer sampleConfigurer, boolean csvMode);

    void copyTiePointData(double[] inputs, Sample[] sourceSamples);

    double[] getSolarFluxes(Product sourceProduct);

    double[] copySolarFluxes(double[] input, double[] solarFluxes);

    double getSurfacePressure();

    double getOzone();

    double getEarthSunDistance();

    void init(Product sourceProduct);

    int getDetectorIndex(Sample[] samples);
}
