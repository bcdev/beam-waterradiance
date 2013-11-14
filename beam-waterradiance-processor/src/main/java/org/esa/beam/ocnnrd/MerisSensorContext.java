package org.esa.beam.ocnnrd;

import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.pointop.Sample;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;

class MerisSensorContext implements SensorContext {

    private static final int[] SPECTRAL_OUTPUT_INDEXES = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13};
    private static final float[] SPECTRAL_OUTPUT_WAVELENGTHS = new float[]{412.f, 442.f, 490.f, 510.f, 560.f, 620.f, 665.f, 681.f, 708.f, 753.f, 778.f, 865.f};
    private static final int[] NN_OUTPUT_INDICES = new int[]{1, 2, 4, 6, 11, 12, 15, 19, 20, 22, 24, 25};
    // @todo 2 tb/tb ask RD - out indices are not exactly matching input WLs: 2-> 443nm, 4-> 498nm, 20-> 709nm, 22-> 754nm, 24-> 779nm

    private double surfacePressure;
    private double ozone;

    @Override
    public int getNumSpectralInputBands() {
        return EnvisatConstants.MERIS_L1B_NUM_SPECTRAL_BANDS;
    }

    @Override
    public String[] getSpectralInputBandNames() {
        return EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES;
    }

    @Override
    public int getNumSpectralOutputBands() {
        return SPECTRAL_OUTPUT_INDEXES.length;
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
    public int[] getNnOutputIndices() {
        return NN_OUTPUT_INDICES;
    }

    @Override
    public Sensor getSensor() {
        return Sensor.MERIS;
    }

    @Override
    public void configureSourceSamples(SampleConfigurer sampleConfigurer, boolean csvMode) {
        sampleConfigurer.defineSample(Constants.SRC_SZA, EnvisatConstants.MERIS_SUN_ZENITH_DS_NAME);
        sampleConfigurer.defineSample(Constants.SRC_SAA, EnvisatConstants.MERIS_SUN_AZIMUTH_DS_NAME);
        sampleConfigurer.defineSample(Constants.SRC_VZA, EnvisatConstants.MERIS_VIEW_ZENITH_DS_NAME);
        sampleConfigurer.defineSample(Constants.SRC_VAA, EnvisatConstants.MERIS_VIEW_AZIMUTH_DS_NAME);
        sampleConfigurer.defineSample(Constants.SRC_PRESS, "atm_press");
        sampleConfigurer.defineSample(Constants.SRC_OZ, "ozone");
        sampleConfigurer.defineSample(Constants.SRC_MWIND, "merid_wind");
        sampleConfigurer.defineSample(Constants.SRC_ZWIND, "zonal_wind");

        for (int i = 0; i < EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES.length; i++) {
            sampleConfigurer.defineSample(Constants.SRC_RAD_OFFSET + i, EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES[i]);
        }

        sampleConfigurer.defineSample(Constants.SRC_DETECTOR, EnvisatConstants.MERIS_DETECTOR_INDEX_DS_NAME);
        sampleConfigurer.defineSample(Constants.SRC_MASK, "_mask_");

        if (csvMode) {
            for (int i = 0; i < EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES.length; i++) {
                sampleConfigurer.defineSample(Constants.SRC_SOL_FLUX_OFFSET + i, "solar_flux_" + (i + 1));
            }
            sampleConfigurer.defineSample(Constants.SRC_LAT, EnvisatConstants.MERIS_LAT_DS_NAME);
            sampleConfigurer.defineSample(Constants.SRC_LON, EnvisatConstants.MERIS_LON_DS_NAME);
        }
    }

    @Override
    public void copyTiePointData(double[] inputs, Sample[] sourceSamples) {
        inputs[0] = sourceSamples[Constants.SRC_SZA].getDouble();
        inputs[1] = sourceSamples[Constants.SRC_SAA].getDouble();
        inputs[2] = sourceSamples[Constants.SRC_VZA].getDouble();
        inputs[3] = sourceSamples[Constants.SRC_VAA].getDouble();
        surfacePressure = sourceSamples[Constants.SRC_PRESS].getDouble();
        inputs[4] = surfacePressure;
        ozone = sourceSamples[Constants.SRC_OZ].getDouble();
        inputs[5] = ozone;
        inputs[6] = sourceSamples[Constants.SRC_MWIND].getDouble();
        inputs[7] = sourceSamples[Constants.SRC_ZWIND].getDouble();
    }

    @Override
    public double[] getSolarFluxes(Product sourceProduct) {
        final int numBands = getNumSpectralInputBands();
        final double[] solarFluxes = new double[numBands];
        final String[] spectralBandNames = getSpectralInputBandNames();

        for (int i = 0; i < numBands; i++) {
            final Band band = sourceProduct.getBand(spectralBandNames[i]);
            solarFluxes[i] = band.getSolarFlux();
        }
        return solarFluxes;
    }

    @Override
    public double[] copySolarFluxes(double[] input, double[] solarFluxes) {
        System.arraycopy(solarFluxes, 0, input, Constants.SRC_SOL_FLUX_OFFSET,
                EnvisatConstants.MERIS_L1B_NUM_SPECTRAL_BANDS);
        return input;
    }

    @Override
    public double getSurfacePressure() {
        return surfacePressure;
    }

    @Override
    public double getOzone() {
        return ozone;
    }

    @Override
    public double getEarthSunDistanceInAE() {
        return 1;
    }

    @Override
    public void init(Product sourceProduct) {
        // do nothing
    }

    @Override
    public int getDetectorIndex(Sample[] samples) {
        return samples[Constants.SRC_DETECTOR].getInt();
    }

    @Override
    public int getTargetSampleOffset() {
        return 0;
    }

    @Override
    public double correctSunAzimuth(double sunAzimuth) {
        return sunAzimuth;  //nothing to correct here tb 2013-10-16
    }

    @Override
    public double correctViewAzimuth(double viewAzimuth) {
        return viewAzimuth; // nothing to correct here tb 2013-11-14
    }
}
