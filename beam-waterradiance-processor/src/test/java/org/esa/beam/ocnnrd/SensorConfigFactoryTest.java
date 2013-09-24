package org.esa.beam.ocnnrd;

import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.junit.*;

import static org.junit.Assert.*;

public class SensorConfigFactoryTest {

    @Test
    public void testFromTypeString() {
        assertTrue(SensorConfigFactory.fromTypeString(EnvisatConstants.MERIS_RR_L1B_PRODUCT_TYPE_NAME) instanceof MerisSensorConfig);
        assertTrue(SensorConfigFactory.fromTypeString(EnvisatConstants.MERIS_FR_L1B_PRODUCT_TYPE_NAME) instanceof MerisSensorConfig);
        assertTrue(SensorConfigFactory.fromTypeString(EnvisatConstants.MERIS_FRS_L1B_PRODUCT_TYPE_NAME) instanceof MerisSensorConfig);
        assertTrue(SensorConfigFactory.fromTypeString(EnvisatConstants.MERIS_FSG_L1B_PRODUCT_TYPE_NAME) instanceof MerisSensorConfig);
        assertTrue(SensorConfigFactory.fromTypeString(EnvisatConstants.MERIS_FRG_L1B_PRODUCT_TYPE_NAME) instanceof MerisSensorConfig);

        assertTrue(SensorConfigFactory.fromTypeString("MOD021KM") instanceof ModisSensorConfig);
        assertTrue(SensorConfigFactory.fromTypeString("MYD021KM") instanceof ModisSensorConfig);
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