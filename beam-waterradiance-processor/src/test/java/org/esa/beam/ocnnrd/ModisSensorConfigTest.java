package org.esa.beam.ocnnrd;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

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
} 