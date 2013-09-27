package org.esa.beam.waterradiance;

import org.esa.beam.waterradiance.levitus.LevitusAuxdataImpl;

import java.io.IOException;

public class AuxdataProviderFactory {

    public static SalinityTemperatureAuxdata createSalinityTemperatureDataProvider() throws IOException {
        return LevitusAuxdataImpl.create();
    }
}
