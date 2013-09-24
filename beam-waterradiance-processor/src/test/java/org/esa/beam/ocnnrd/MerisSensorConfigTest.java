package org.esa.beam.ocnnrd;

import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.junit.*;

import static org.junit.Assert.*;

public class MerisSensorConfigTest {


    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGetNumSpectralBands() {
        final MerisSensorConfig merisSensorConfig = new MerisSensorConfig();

        assertEquals(EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES.length, merisSensorConfig.getNumSpectralBands());
    }
} 