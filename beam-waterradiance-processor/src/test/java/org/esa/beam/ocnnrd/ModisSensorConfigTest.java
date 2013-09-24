package org.esa.beam.ocnnrd;

import org.junit.*;

import static org.junit.Assert.*;

public class ModisSensorConfigTest {


    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGetNumSpectralBands() {
        final ModisSensorConfig modisSensorConfig = new ModisSensorConfig();

        assertEquals(9, modisSensorConfig.getNumSpectralBands());
    }
} 