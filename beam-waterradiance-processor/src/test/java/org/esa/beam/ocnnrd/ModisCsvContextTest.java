package org.esa.beam.ocnnrd;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ModisCsvContextTest {

    private ModisCsvContext modisCsvConfig;

    @Before
    public void setUp() {
        modisCsvConfig = new ModisCsvContext();
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
    public void testGetNnOutputIndices() {
        final int[] expectedIndices = {1, 2, 4, 8, 9, 13, 21, 26};

        assertArrayEquals(expectedIndices, modisCsvConfig.getNnOutputIndices());
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
    public void testCopyTiePointData() {
        final double[] inputs = new double[8];
        final TestSample[] sourceSamples = new TestSample[8];
        for (int i = 0; i < 8; i++) {
            sourceSamples[i] = new TestSample();
            sourceSamples[i].set((double) i + 22);
        }

        modisCsvConfig.copyTiePointData(inputs, sourceSamples);

        for (int i = 0; i < 8; i++) {
            assertEquals(i + 22, inputs[i], 1e-8);
        }
    }

    @Test
    public void testGetSolarFluxes() {
        final double[] expectedFluxes = new double[]{1740.458085, 1844.698571, 1949.723913, 1875.394737, 1882.428333,
                1597.176923, 1277.037, 945.3382727};

        final double[] solarFluxes = modisCsvConfig.getSolarFluxes(null);
        assertArrayEquals(expectedFluxes, solarFluxes, 1e-8);
    }

    @Test
    public void testCopySolarFluxes() {
        double[] input = new double[40];
        final double[] solarFluxes = new double[8];
        for (int i = 0; i < solarFluxes.length; i++) {
            solarFluxes[i] = i + 4;
        }

        input = modisCsvConfig.copySolarFluxes(input, solarFluxes);
        for (int i = 0; i < solarFluxes.length; i++) {
            assertEquals(solarFluxes[i], input[i + 25], 1e-8);
        }
    }

    @Test
    public void testGetSurfacePressure() {
        final TestSample[] sourceSamples = new TestSample[8];
        for (int i = 0; i < 8; i++) {
            sourceSamples[i] = new TestSample();
            sourceSamples[i].set((double) i + 23);
        }

        modisCsvConfig.copyTiePointData(new double[8], sourceSamples);

        assertEquals(27.0, modisCsvConfig.getSurfacePressure(), 1e-8);
    }

    @Test
    public void testGetSurfacePressure_uninitialized() {
        Assert.assertTrue(Double.isNaN(modisCsvConfig.getSurfacePressure()));
    }

    @Test
    public void testGetOzone() {
        final TestSample[] sourceSamples = new TestSample[8];
        for (int i = 0; i < 8; i++) {
            sourceSamples[i] = new TestSample();
            sourceSamples[i].set((double) i + 24);
        }

        modisCsvConfig.copyTiePointData(new double[8], sourceSamples);

        assertEquals(29.0, modisCsvConfig.getOzone(), 1e-8);
    }

    @Test
    public void testGetOzone_uninitialized() {
        Assert.assertTrue(Double.isNaN(modisCsvConfig.getOzone()));
    }

    @Test
    public void testEarthSunDistanceInAU() {
        assertEquals(1.0, modisCsvConfig.getEarthSunDistanceInAU(), 1e-8);
    }

    @Test
    public void testGetDetectorIndex() {
        assertEquals(-1, modisCsvConfig.getDetectorIndex(null));
    }

    @Test
    public void testGetTargetSampleOffset() {
         assertEquals(0, modisCsvConfig.getTargetSampleOffset());
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
