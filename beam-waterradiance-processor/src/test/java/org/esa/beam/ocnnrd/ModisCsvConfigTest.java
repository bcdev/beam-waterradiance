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

    @Test
    public void testGetNumSpectralOutputBands() {
        assertEquals(8, modisCsvConfig.getNumSpectralOutputBands());
    }

    @Test
    public void testGetSpectralOutputBandIndices() {
        final int[] expectedIndices = {1, 2, 3, 4, 5, 6, 7, 8};

        assertArrayEquals(expectedIndices, modisCsvConfig.getSpectralOutputBandIndices());
    }

    @Test
    public void testGetSpectralOutputWavelengths() {
        final float[] expectedWavelengths = {412.f, 443.f, 488.f, 531.f, 547.f, 645.f, 748.f, 869.f};

        assertArrayEquals(expectedWavelengths, modisCsvConfig.getSpectralOutputWavelengths(), 1e-8f);
    }

    @Test
    public void testConfigureSourceSamples() {
        final TestSampleConfigurer testSampleConfigurer = new TestSampleConfigurer();

        modisCsvConfig.configureSourceSamples(testSampleConfigurer, false);

        assertEquals("Solar_Zenith", testSampleConfigurer.get(0));
        assertEquals("Relative_Azimuth", testSampleConfigurer.get(1));
        assertEquals("Viewing_Zenith", testSampleConfigurer.get(2));
        assertEquals("Relative_Azimuth", testSampleConfigurer.get(3));

        assertEquals("Pressure", testSampleConfigurer.get(4));
        assertEquals("Ozone", testSampleConfigurer.get(5));
        assertEquals("WindSpeedM", testSampleConfigurer.get(6));
        assertEquals("WindSpeedZ", testSampleConfigurer.get(7));

        assertEquals("Radiance_TOA_412", testSampleConfigurer.get(8));
        assertEquals("Radiance_TOA_443", testSampleConfigurer.get(9));
        assertEquals("Radiance_TOA_488", testSampleConfigurer.get(10));
        assertEquals("Radiance_TOA_531", testSampleConfigurer.get(11));
        assertEquals("Radiance_TOA_547", testSampleConfigurer.get(12));
        assertEquals("Radiance_TOA_645", testSampleConfigurer.get(13));
        assertEquals("Radiance_TOA_748", testSampleConfigurer.get(14));
        assertEquals("Radiance_TOA_869", testSampleConfigurer.get(15));
    }

    @Test
    public void testCorrectSunAzimuth() {
        assertEquals(19.23, modisCsvConfig.correctSunAzimuth(19.23), 1e-8);
        assertEquals(-11.5, modisCsvConfig.correctSunAzimuth(-11.5), 1e-8);
    }

    @Test
    public void testCorrectViewAzimuth() {
        assertEquals(2.0 * 8.9, modisCsvConfig.correctViewAzimuth(8.9), 1e-8);
        assertEquals(2.0 * -3.77, modisCsvConfig.correctViewAzimuth(-3.77), 1e-8);
    }
}
