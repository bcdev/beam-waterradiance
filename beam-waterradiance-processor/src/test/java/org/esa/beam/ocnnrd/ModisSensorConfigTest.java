package org.esa.beam.ocnnrd;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ModisSensorConfigTest {


    private ModisSensorConfig modisSensorConfig;

    @Before
    public void setUp() throws Exception {
        modisSensorConfig = new ModisSensorConfig();
    }

    @Test
    public void testGetNumSpectralBands() {
        assertEquals(9, modisSensorConfig.getNumSpectralBands());
    }

    @Test
    public void testGetSpectralBandNames() {
        final String[] spectralBandNames = modisSensorConfig.getSpectralBandNames();
        assertNotNull(spectralBandNames);
        assertEquals(9, spectralBandNames.length);

        assertEquals("EV_1KM_RefSB.8", spectralBandNames[0]);
        assertEquals("EV_1KM_RefSB.9", spectralBandNames[1]);
        assertEquals("EV_1KM_RefSB.10", spectralBandNames[2]);
        assertEquals("EV_1KM_RefSB.11", spectralBandNames[3]);
        assertEquals("EV_1KM_RefSB.12", spectralBandNames[4]);
        assertEquals("EV_1KM_RefSB.13lo", spectralBandNames[5]);
        assertEquals("EV_1KM_RefSB.14lo", spectralBandNames[6]);
        assertEquals("EV_1KM_RefSB.15", spectralBandNames[7]);
        assertEquals("EV_1KM_RefSB.16", spectralBandNames[8]);
    }

    @Test
    public void testGetSensorType() {
        assertEquals(Sensor.MODIS, modisSensorConfig.getSensor());
    }

    @Test
    public void testConfigureSourceSamples() {
        final TestSampleConfigurer testSampleConfigurer = new TestSampleConfigurer();

        modisSensorConfig.configureSourceSamples(testSampleConfigurer, false);

        assertEquals("SolarZenith", testSampleConfigurer.get(0));
        assertEquals("SolarAzimuth", testSampleConfigurer.get(1));
        assertEquals("SensorZenith", testSampleConfigurer.get(2));
        assertEquals("SensorAzimuth", testSampleConfigurer.get(3));

        for (int i = 4; i < 8; i++) {
            assertNull(testSampleConfigurer.get(i));
        }

        assertEquals("EV_1KM_RefSB.8", testSampleConfigurer.get(8));
        assertEquals("EV_1KM_RefSB.9", testSampleConfigurer.get(9));
        assertEquals("EV_1KM_RefSB.10", testSampleConfigurer.get(10));
        assertEquals("EV_1KM_RefSB.11", testSampleConfigurer.get(11));
        assertEquals("EV_1KM_RefSB.12", testSampleConfigurer.get(12));
        assertEquals("EV_1KM_RefSB.13lo", testSampleConfigurer.get(13));
        assertEquals("EV_1KM_RefSB.14lo", testSampleConfigurer.get(14));
        assertEquals("EV_1KM_RefSB.15", testSampleConfigurer.get(15));
        assertEquals("EV_1KM_RefSB.16", testSampleConfigurer.get(16));
    }

    @Test
    public void testCopyTiePointData() {
        final double[] inputs = new double[6];
        final TestSample[] sourceSamples = new TestSample[4];
        for (int i = 0; i < sourceSamples.length; i++) {
            sourceSamples[i] = new TestSample();
            sourceSamples[i].set((double)i);
        }

        modisSensorConfig.copyTiePointData(inputs, sourceSamples);

        for (int i = 0; i < sourceSamples.length; i++) {
            assertEquals(i, inputs[i], 1e-8);
        }
        assertEquals(1019.0, inputs[4], 1e-8);
        assertEquals(330.0, inputs[5], 1e-8);
    }
} 