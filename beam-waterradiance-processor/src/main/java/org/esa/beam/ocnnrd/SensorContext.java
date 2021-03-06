package org.esa.beam.ocnnrd;

import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.pointop.Sample;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;

public interface SensorContext {

    Sensor getSensor();

    int getNumSpectralInputBands();

    String[] getSpectralInputBandNames();

    int getNumSpectralOutputBands();

    int[] getSpectralOutputBandIndices();

    /**
     * Retrieves the center wavelengths for the output spectral bands in [nm]
     *
     * @return the array of wavelengths
     */
    float[] getSpectralOutputWavelengths();

    int[] getNnOutputIndices();

    void configureSourceSamples(SampleConfigurer sampleConfigurer, boolean csvMode);

    /**
     * Scales the input spectral data to be consistent with the MERIS case. Resulting data should be TOA radiance in
     *      [mW/(m^2 * sr * nm)] or [LU], i.e. Luminance Unit
     * Scaling is performed "in place", if necessary
     *
     * @param inputs input data vector
     */
    void scaleInputSpectralData(double[] inputs);

    void copyTiePointData(double[] inputs, Sample[] sourceSamples);

    double[] getSolarFluxes(Product sourceProduct);

    double[] copySolarFluxes(double[] input, double[] solarFluxes);

    double getSurfacePressure();

    double getOzone();

    double getEarthSunDistanceInAU();

    void init(Product sourceProduct);

    int getDetectorIndex(Sample[] samples);

    int getTargetSampleOffset();

    double correctSunAzimuth(double azimuth);

    double correctViewAzimuth(double azimuth);
}
