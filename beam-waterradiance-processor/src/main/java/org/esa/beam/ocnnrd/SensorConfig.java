package org.esa.beam.ocnnrd;

import org.esa.beam.framework.gpf.pointop.SampleConfigurer;

public interface SensorConfig {
    int getNumSpectralBands();

    Sensor getSensor();

    void configureSourceSamples(SampleConfigurer sampleConfigurer, boolean csvMode);
}
