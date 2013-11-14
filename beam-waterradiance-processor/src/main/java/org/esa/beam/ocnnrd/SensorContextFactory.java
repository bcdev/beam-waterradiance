package org.esa.beam.ocnnrd;

import org.esa.beam.dataio.envisat.EnvisatConstants;

public class SensorContextFactory {

    public static SensorContext fromTypeString(String productTypeName) {
        if (productTypeName.equalsIgnoreCase(EnvisatConstants.MERIS_RR_L1B_PRODUCT_TYPE_NAME)
                || productTypeName.equalsIgnoreCase(EnvisatConstants.MERIS_FR_L1B_PRODUCT_TYPE_NAME)
                || productTypeName.equalsIgnoreCase(EnvisatConstants.MERIS_FRS_L1B_PRODUCT_TYPE_NAME)
                || productTypeName.equalsIgnoreCase(EnvisatConstants.MERIS_FSG_L1B_PRODUCT_TYPE_NAME)
                || productTypeName.equalsIgnoreCase(EnvisatConstants.MERIS_FRG_L1B_PRODUCT_TYPE_NAME)) {
            return new MerisSensorContext();
        } else if (productTypeName.equalsIgnoreCase("MOD021KM")
                || productTypeName.equalsIgnoreCase("MYD021KM")
                || productTypeName.equalsIgnoreCase("MODIS Level 1B")) {
            return new ModisSensorContext();
        } else if (productTypeName.equalsIgnoreCase("Generic Level 1B")) {
            return new SeaWiFSSensorContext();
        } else if (productTypeName.equalsIgnoreCase("MODIS_CSV")) {
            return new ModisCsvContext();
        }
        throw new IllegalArgumentException("Invalid Product Type: " + productTypeName);
    }
}
