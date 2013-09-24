package org.esa.beam.ocnnrd;

import org.esa.beam.dataio.envisat.EnvisatConstants;

public enum SensorType {
    MERIS, MODIS;

    public static SensorType fromTypeString(String productTypeName) {
        if (productTypeName.equalsIgnoreCase(EnvisatConstants.MERIS_RR_L1B_PRODUCT_TYPE_NAME)
                || productTypeName.equalsIgnoreCase(EnvisatConstants.MERIS_FR_L1B_PRODUCT_TYPE_NAME)
                || productTypeName.equalsIgnoreCase(EnvisatConstants.MERIS_FRS_L1B_PRODUCT_TYPE_NAME)
                || productTypeName.equalsIgnoreCase(EnvisatConstants.MERIS_FSG_L1B_PRODUCT_TYPE_NAME)
                || productTypeName.equalsIgnoreCase(EnvisatConstants.MERIS_FRG_L1B_PRODUCT_TYPE_NAME)) {
            return MERIS;
        }else if (productTypeName.equalsIgnoreCase("MOD021KM")
                || productTypeName.equalsIgnoreCase("MYD021KM")) {
            return MODIS;
        }
        throw new IllegalArgumentException("Invalid Product Type: " + productTypeName);
    }
}
