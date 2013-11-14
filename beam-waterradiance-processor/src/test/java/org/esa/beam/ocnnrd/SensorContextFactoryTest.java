package org.esa.beam.ocnnrd;

import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.junit.*;

import static org.junit.Assert.*;

public class SensorContextFactoryTest {

    @Test
    public void testFromTypeString_MERIS() {
        assertTrue(SensorContextFactory.fromTypeString(EnvisatConstants.MERIS_RR_L1B_PRODUCT_TYPE_NAME) instanceof MerisSensorContext);
        assertTrue(SensorContextFactory.fromTypeString(EnvisatConstants.MERIS_FR_L1B_PRODUCT_TYPE_NAME) instanceof MerisSensorContext);
        assertTrue(SensorContextFactory.fromTypeString(EnvisatConstants.MERIS_FRS_L1B_PRODUCT_TYPE_NAME) instanceof MerisSensorContext);
        assertTrue(SensorContextFactory.fromTypeString(EnvisatConstants.MERIS_FSG_L1B_PRODUCT_TYPE_NAME) instanceof MerisSensorContext);
        assertTrue(SensorContextFactory.fromTypeString(EnvisatConstants.MERIS_FRG_L1B_PRODUCT_TYPE_NAME) instanceof MerisSensorContext);
    }

    @Test
    public void testFromTypeString_MODIS() {
        assertTrue(SensorContextFactory.fromTypeString("MOD021KM") instanceof ModisSensorContext);
        assertTrue(SensorContextFactory.fromTypeString("MYD021KM") instanceof ModisSensorContext);
        assertTrue(SensorContextFactory.fromTypeString("MODIS Level 1B") instanceof ModisSensorContext);
    }

    @Test
    public void testFromTypeString_MODIS_CSV() {
        assertTrue(SensorContextFactory.fromTypeString("MODIS_CSV") instanceof ModisCsvContext);
    }

    @Test
    public void testFromTypeString_SEAWIFS() {
        assertTrue(SensorContextFactory.fromTypeString("Generic Level 1B") instanceof SeaWiFSSensorContext);
    }

    @Test
    public void testFromTypeString_invalidType() {
        try {
            SensorContextFactory.fromTypeString("Tonios private sensor");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            assertEquals("Invalid Product Type: Tonios private sensor", expected.getMessage());
        }
    }
}