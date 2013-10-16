package org.esa.beam.ocnnrd;

import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.pointop.Sample;
import org.junit.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.*;

public class SeaWiFSSensorConfigTest {

    private SeaWiFSSensorConfig seaWiFSSensorConfig;

    @Before
    public void setUp() throws Exception {
        seaWiFSSensorConfig = new SeaWiFSSensorConfig();
    }

    @Test
    public void testGetNumSpectralInputBands() {
        assertEquals(8, seaWiFSSensorConfig.getNumSpectralInputBands());
    }

    @Test
    public void testGetNumSpectralOutputBands() {
        assertEquals(8, seaWiFSSensorConfig.getNumSpectralOutputBands());
    }

    @Test
    public void testGetSpectralOutputBandIndices() {
        final int[] expectedIndices = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        final int[] indices = seaWiFSSensorConfig.getSpectralOutputBandIndices();

        assertArrayEquals(expectedIndices, indices);
    }

    @Test
    public void testGetSpectralOutputWavelengths() {
        final float[] expectedWavelengths = new float[]{412.f, 443.f, 490.f, 510.f, 555.f, 670.f, 765.f, 865.f};

        final float[] wavelengths = seaWiFSSensorConfig.getSpectralOutputWavelengths();
        assertArrayEquals(expectedWavelengths, wavelengths, 1e-8f);
    }

    @Test
    public void testGetSpectralInputBandNames() {
        final String[] spectralBandNames = seaWiFSSensorConfig.getSpectralInputBandNames();
        assertNotNull(spectralBandNames);
        assertEquals(8, spectralBandNames.length);

        assertEquals("L_412", spectralBandNames[0]);
        assertEquals("L_443", spectralBandNames[1]);
        assertEquals("L_490", spectralBandNames[2]);
        assertEquals("L_510", spectralBandNames[3]);
        assertEquals("L_555", spectralBandNames[4]);
        assertEquals("L_670", spectralBandNames[5]);
        assertEquals("L_765", spectralBandNames[6]);
        assertEquals("L_865", spectralBandNames[7]);
    }

    @Test
    public void testGetSensorType() {
        assertEquals(Sensor.SEAWIFS, seaWiFSSensorConfig.getSensor());
    }

    @Test
    public void testConfigureSourceSamples() {
        final TestSampleConfigurer testSampleConfigurer = new TestSampleConfigurer();

        seaWiFSSensorConfig.configureSourceSamples(testSampleConfigurer, false);

        assertEquals("solz", testSampleConfigurer.get(0));
        assertEquals("sola", testSampleConfigurer.get(1));
        assertEquals("senz", testSampleConfigurer.get(2));
        assertEquals("sena", testSampleConfigurer.get(3));

        for (int i = 4; i < 8; i++) {
            assertNull(testSampleConfigurer.get(i));
        }

        assertEquals("L_412", testSampleConfigurer.get(8));
        assertEquals("L_443", testSampleConfigurer.get(9));
        assertEquals("L_490", testSampleConfigurer.get(10));
        assertEquals("L_510", testSampleConfigurer.get(11));
        assertEquals("L_555", testSampleConfigurer.get(12));
        assertEquals("L_670", testSampleConfigurer.get(13));
        assertEquals("L_765", testSampleConfigurer.get(14));
        assertEquals("L_865", testSampleConfigurer.get(15));
    }

    @Test
    public void testCopyTiePointData() {
        final double[] inputs = new double[6];
        final TestSample[] sourceSamples = new TestSample[4];
        for (int i = 0; i < sourceSamples.length; i++) {
            sourceSamples[i] = new TestSample();
            sourceSamples[i].set((double)i);
        }

        seaWiFSSensorConfig.copyTiePointData(inputs, sourceSamples);

        for (int i = 0; i < sourceSamples.length; i++) {
            assertEquals(i, inputs[i], 1e-8);
        }
        assertEquals(1019.0, inputs[4], 1e-8);
        assertEquals(330.0, inputs[5], 1e-8);
    }

    @Test
    public void testGetSolarFluxes() {
        final Product product = new Product("test", "type", 5, 5);
        final double[] solarFluxes = seaWiFSSensorConfig.getSolarFluxes(product);
        assertEquals(0, solarFluxes.length);
    }

    @Test
    public void testCopySolarFluxes() {
        double[] input = new double[40];
        final double[] solarFluxes = new double[8];
        for (int i = 0; i < solarFluxes.length; i++) {
            solarFluxes[i] = i;
        }

        input = seaWiFSSensorConfig.copySolarFluxes(input, solarFluxes);
        for (int i = 0; i < solarFluxes.length; i++) {
            assertEquals(solarFluxes[i], input[i + 25], 1e-8);
        }
    }

    @Test
    public void testGetSurfacePressure() {
        Assert.assertEquals(1019.0, seaWiFSSensorConfig.getSurfacePressure(), 1e-8);
    }

    @Test
    public void testGetOzone() {
        Assert.assertEquals(330.0, seaWiFSSensorConfig.getOzone(), 1e-8);
    }

    @Test
    public void testGetDetectorIndex() {
        assertEquals(-1, seaWiFSSensorConfig.getDetectorIndex(new Sample[0]));
    }

} 