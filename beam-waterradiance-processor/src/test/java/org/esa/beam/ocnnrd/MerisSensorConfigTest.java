package org.esa.beam.ocnnrd;

import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MerisSensorConfigTest {

    private MerisSensorConfig merisSensorConfig;

    @Before
    public void setUp() throws Exception {
        merisSensorConfig = new MerisSensorConfig();
    }

    @Test
    public void testGetNumSpectralInputBands() {
        assertEquals(EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES.length, merisSensorConfig.getNumSpectralInputBands());
    }

    @Test
    public void testGetNumSpectralOutputBands() {
        assertEquals(12, merisSensorConfig.getNumSpectralOutputBands());
    }

    @Test
    public void testGetSpectralOutputBandIndices() {
        final int[] expectedIndices = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13};

        final int[] indices = merisSensorConfig.getSpectralOutputBandIndices();
        assertArrayEquals(expectedIndices, indices);
    }

    @Test
    public void testGetSpectralOutputWavelengths() {
        final float[] expectedWavelengths = new float[]{412.f, 442.f, 449.f, 510.f, 560.f, 620.f, 665.f, 681.f, 708.f, 753.f, 778.f, 865.f};

        final float[] wavelengths = merisSensorConfig.getSpectralOutputWavelengths();
        assertArrayEquals(expectedWavelengths, wavelengths, 1e-8f);
    }

    @Test
    public void testGetSensorType() {
        assertEquals(Sensor.MERIS, merisSensorConfig.getSensor());
    }

    @Test
    public void testConfigureSourceSamples_noCsvMode() {
        final TestSampleConfigurer testSampleConfigurer = new TestSampleConfigurer();

        merisSensorConfig.configureSourceSamples(testSampleConfigurer, false);

        assertBasicSamples(testSampleConfigurer);
    }

    @Test
    public void testConfigureSourceSamples_csvMode() {
        final TestSampleConfigurer testSampleConfigurer = new TestSampleConfigurer();

        merisSensorConfig.configureSourceSamples(testSampleConfigurer, true);

        assertBasicSamples(testSampleConfigurer);

        for (int i = 1; i < 16; i++) {
            assertEquals("solar_flux_" + i, testSampleConfigurer.get(24 + i));
        }

        assertEquals(EnvisatConstants.MERIS_LAT_DS_NAME, testSampleConfigurer.get(40));
        assertEquals(EnvisatConstants.MERIS_LON_DS_NAME, testSampleConfigurer.get(41));
    }

    @Test
    public void testGetSpectralInputBandNames() {
        final String[] spectralBandNames = merisSensorConfig.getSpectralInputBandNames();
        assertNotNull(spectralBandNames);
        assertEquals(15, spectralBandNames.length);

        for (int i = 0; i < 15; i++) {
            assertEquals("radiance_" + (i + 1), spectralBandNames[i]);
        }
    }

    @Test
    public void testCopyTiePointData() {
        final double[] inputs = new double[8];
        final TestSample[] sourceSamples = new TestSample[8];
        for (int i = 0; i < 8; i++) {
            sourceSamples[i] = new TestSample();
            sourceSamples[i].set((double) i);
        }

        merisSensorConfig.copyTiePointData(inputs, sourceSamples);

        for (int i = 0; i < 8; i++) {
            assertEquals(i, inputs[i], 1e-8);
        }
    }

    @Test
    public void testGetSolarFluxes() {
        final Product product = new Product("test", "type", 5, 5);
        final float[] testFluxes = new float[]{23.6f, 34.7f, 102.3f, 14.7f, 71.5f, 63.4f, 102.87f, 94.5f, 61f, 12.3f, 14.1f, 29.7f, 1023f, 60.1f, 51.9f};
        for (int i = 0; i < merisSensorConfig.getNumSpectralInputBands(); i++) {
            product.addBand(createBandWithSolarFlux(merisSensorConfig.getSpectralInputBandNames()[i], testFluxes[i]));
        }

        final Band schnick = createBandWithSolarFlux("schnick", 23.6f);
        final Band schnack = createBandWithSolarFlux("schnack", 34.7f);
        product.addBand(schnick);
        product.addBand(schnack);

        final double[] solarFluxes = merisSensorConfig.getSolarFluxes(product);
        assertEquals(15, solarFluxes.length);
        for (int i = 0; i < merisSensorConfig.getNumSpectralInputBands(); i++) {
            assertEquals(testFluxes[i], solarFluxes[i], 1e-8);
        }
    }

    @Test
    public void testCopySolarFluxes() {
        double[] input = new double[40];
        final double[] solarFluxes = new double[15];
        for (int i = 0; i < solarFluxes.length; i++) {
            solarFluxes[i] = i;
        }

        input = merisSensorConfig.copySolarFluxes(input, solarFluxes);
        for (int i = 0; i < solarFluxes.length; i++) {
            assertEquals(solarFluxes[i], input[i + 25], 1e-8);
        }
    }

    @Test
    public void testGetSurfacePressure() {
        assertEquals(0, merisSensorConfig.getSurfacePressure(), 1e-8);

        final double[] inputs = new double[8];
        final TestSample[] sourceSamples = new TestSample[8];
        for (int i = 0; i < 8; i++) {
            sourceSamples[i] = new TestSample();
            sourceSamples[i].set((double) i);
        }
        merisSensorConfig.copyTiePointData(inputs, sourceSamples);

        assertEquals(sourceSamples[Constants.SRC_PRESS].getDouble(), merisSensorConfig.getSurfacePressure(), 1e-8);
    }

    @Test
    public void testGetOzone() {
        assertEquals(0, merisSensorConfig.getOzone(), 1e-8);

        final double[] inputs = new double[8];
        final TestSample[] sourceSamples = new TestSample[8];
        for (int i = 0; i < 8; i++) {
            sourceSamples[i] = new TestSample();
            sourceSamples[i].set((double) i);
        }
        merisSensorConfig.copyTiePointData(inputs, sourceSamples);

        assertEquals(sourceSamples[Constants.SRC_OZ].getDouble(), merisSensorConfig.getOzone(), 1e-8);
    }

    @Test
    public void testGetDetectorIndex() {
        final TestSample[] samples = new TestSample[24];
        samples[23] = new TestSample();
        samples[23].set(776);

        final int detectorIndex = merisSensorConfig.getDetectorIndex(samples);
        assertEquals(776, detectorIndex);
    }

    @Test
    public void testGetTargetSamplesOffset() {
        assertEquals(0, merisSensorConfig.getTargetSampleOffset());
    }

    private void assertBasicSamples(TestSampleConfigurer testSampleConfigurer) {
        assertEquals(EnvisatConstants.MERIS_SUN_ZENITH_DS_NAME, testSampleConfigurer.get(0));
        assertEquals(EnvisatConstants.MERIS_SUN_AZIMUTH_DS_NAME, testSampleConfigurer.get(1));
        assertEquals(EnvisatConstants.MERIS_VIEW_ZENITH_DS_NAME, testSampleConfigurer.get(2));
        assertEquals(EnvisatConstants.MERIS_VIEW_AZIMUTH_DS_NAME, testSampleConfigurer.get(3));
        assertEquals("atm_press", testSampleConfigurer.get(4));
        assertEquals("ozone", testSampleConfigurer.get(5));
        assertEquals("merid_wind", testSampleConfigurer.get(6));
        assertEquals("zonal_wind", testSampleConfigurer.get(7));

        for (int i = 1; i < 16; i++) {
            assertEquals("radiance_" + i, testSampleConfigurer.get(7 + i));
        }

        assertEquals(EnvisatConstants.MERIS_DETECTOR_INDEX_DS_NAME, testSampleConfigurer.get(23));
        assertEquals("_mask_", testSampleConfigurer.get(24));
    }

    private Band createBandWithSolarFlux(String schnick1, float solarFlux) {
        final Band schnick = new Band(schnick1, ProductData.TYPE_INT16, 5, 5);
        schnick.setSolarFlux(solarFlux);
        return schnick;
    }

}