package org.esa.beam.ocnnrd;

import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.junit.*;

import static org.junit.Assert.*;

public class MerisSensorConfigTest {


    private MerisSensorConfig merisSensorConfig;

    @Before
    public void setUp() throws Exception {
        merisSensorConfig = new MerisSensorConfig();
    }

    @Test
    public void testGetNumSpectralBands() {
        assertEquals(EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES.length, merisSensorConfig.getNumSpectralBands());
    }

    @Test
    public void testGetSensorType() {
        assertEquals(Sensor.MERIS, merisSensorConfig.getSensor());
    }
} 