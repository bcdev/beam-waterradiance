package org.esa.beam.waterradiance;

import org.esa.beam.waterradiance.levitus.LevitusAuxdataImpl;
import org.esa.beam.waterradiance.seadas.SeadasAuxdataImpl;

import java.io.IOException;

public class AuxdataProviderFactory {

    public static SalinityTemperatureAuxdata createSalinityTemperatureDataProvider() throws IOException {
        return LevitusAuxdataImpl.create();
    }

    public static AtmosphericAuxdata createAtmosphericDataProvider(String auxPath) throws IOException {
        return SeadasAuxdataImpl.create(auxPath);
    }
}
