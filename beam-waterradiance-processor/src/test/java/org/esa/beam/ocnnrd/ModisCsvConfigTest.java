package org.esa.beam.ocnnrd;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ModisCsvConfigTest {

    private ModisCsvConfig modisCsvConfig;

    @Before
    public void setUp() {
        modisCsvConfig = new ModisCsvConfig();
    }

    @Test
    public void testGetSensor() {
        assertEquals(Sensor.MODIS, modisCsvConfig.getSensor());
    }

    @Test
    public void testGetNumSpectralInputBands() {
        assertEquals(8, modisCsvConfig.getNumSpectralInputBands());
    }

    @Test
    public void testGetSpectralInputBandNames() {
        final String[] expectedNames = {"Radiance_TOA_412",
                "Radiance_TOA_443",
                "Radiance_TOA_488",
                "Radiance_TOA_531",
                "Radiance_TOA_547",
                "Radiance_TOA_645",
                "Radiance_TOA_748",
                "Radiance_TOA_869"};
        assertArrayEquals(expectedNames, modisCsvConfig.getSpectralInputBandNames());
    }
}
