package org.esa.beam.ocnnrd;

import org.esa.beam.dataio.envisat.EnvisatConstants;

public class SensorConfigFactory {

    public static SensorConfig fromTypeString(String productTypeName) {
        if (productTypeName.equalsIgnoreCase(EnvisatConstants.MERIS_RR_L1B_PRODUCT_TYPE_NAME)
                || productTypeName.equalsIgnoreCase(EnvisatConstants.MERIS_FR_L1B_PRODUCT_TYPE_NAME)
                || productTypeName.equalsIgnoreCase(EnvisatConstants.MERIS_FRS_L1B_PRODUCT_TYPE_NAME)
                || productTypeName.equalsIgnoreCase(EnvisatConstants.MERIS_FSG_L1B_PRODUCT_TYPE_NAME)
                || productTypeName.equalsIgnoreCase(EnvisatConstants.MERIS_FRG_L1B_PRODUCT_TYPE_NAME)) {
            return new MerisSensorConfig();
        } else if (productTypeName.equalsIgnoreCase("MOD021KM")
                || productTypeName.equalsIgnoreCase("MYD021KM")
                || productTypeName.equalsIgnoreCase("MODIS Level 1B")) {
            return new ModisSensorConfig();
        } else if (productTypeName.equalsIgnoreCase("Generic Level 1B")) {
            return new SeaWiFSSensorConfig();
        }
        throw new IllegalArgumentException("Invalid Product Type: " + productTypeName);
    }
}
