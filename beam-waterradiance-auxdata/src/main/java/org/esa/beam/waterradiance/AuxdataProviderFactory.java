package org.esa.beam.waterradiance;

import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.waterradiance.levitus.LevitusAuxdataImpl;
import org.esa.beam.waterradiance.no2.NO2AuxdataImpl;
import org.esa.beam.waterradiance.seadas.SeadasAuxdataImpl;

import java.io.IOException;

public class AuxdataProviderFactory {

    public static SalinityTemperatureAuxdata createSalinityTemperatureDataProvider() throws IOException {
        return LevitusAuxdataImpl.create();
    }

    public static AtmosphericAuxdata createAtmosphericDataProvider(String auxPath) throws IOException {
        return SeadasAuxdataImpl.create(auxPath);
    }

    public static AtmosphericAuxdata createAtmosphericDataProvider(Product tomsomiStartProduct,
                                                                   Product tomsomiEndProduct,
                                                                   Product ncepStartProduct,
                                                                   Product ncepEndProduct) throws IOException {
        return SeadasAuxdataImpl.create(tomsomiStartProduct, tomsomiEndProduct, ncepStartProduct, ncepEndProduct);
    }

    public static NO2Auxdata createNO2AuxdataProvider(String auxPath) throws IOException {
        return NO2AuxdataImpl.create(auxPath);
    }
}
