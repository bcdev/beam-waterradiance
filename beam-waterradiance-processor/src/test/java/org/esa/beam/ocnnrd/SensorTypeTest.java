package org.esa.beam.ocnnrd;

import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.junit.*;

import static org.junit.Assert.*;

public class SensorTypeTest {

    @Test
    public void testFromTypeString() {
        assertEquals(SensorType.MERIS, SensorType.fromTypeString(EnvisatConstants.MERIS_RR_L1B_PRODUCT_TYPE_NAME));
        assertEquals(SensorType.MERIS, SensorType.fromTypeString(EnvisatConstants.MERIS_FR_L1B_PRODUCT_TYPE_NAME));
        assertEquals(SensorType.MERIS, SensorType.fromTypeString(EnvisatConstants.MERIS_FRS_L1B_PRODUCT_TYPE_NAME));
        assertEquals(SensorType.MERIS, SensorType.fromTypeString(EnvisatConstants.MERIS_FSG_L1B_PRODUCT_TYPE_NAME));
        assertEquals(SensorType.MERIS, SensorType.fromTypeString(EnvisatConstants.MERIS_FRG_L1B_PRODUCT_TYPE_NAME));

        assertEquals(SensorType.MODIS, SensorType.fromTypeString("MOD021KM"));
        assertEquals(SensorType.MODIS, SensorType.fromTypeString("MYD021KM"));
    }

    @Test
    public void testFromTypeString_invalidType() {
        try {
            SensorType.fromTypeString("Tonios private sensor");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
            assertEquals("Invalid Product Type: Tonios private sensor", expected.getMessage());
        }
    }
} 