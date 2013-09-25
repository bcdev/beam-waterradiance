package org.esa.beam.ocnnrd;

import org.esa.beam.framework.gpf.pointop.Sample;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;

public interface SensorConfig {
    int getNumSpectralBands();

    String[] getSpectralBandNames();

    Sensor getSensor();

    void configureSourceSamples(SampleConfigurer sampleConfigurer, boolean csvMode);

    void copyTiePointData(double[] inputs, Sample[] sourceSamples);

    double[] copySolarFluxes(double[] input, double[] solarFluxes);
}
