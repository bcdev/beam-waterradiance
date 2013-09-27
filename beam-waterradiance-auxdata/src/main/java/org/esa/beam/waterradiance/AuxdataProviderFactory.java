package org.esa.beam.waterradiance;

import org.esa.beam.waterradiance.levitus.LevitusDataProviderImpl;

import java.io.IOException;

public class AuxdataProviderFactory {

    public static AuxdataProvider createDataProvider() throws IOException {
        return LevitusDataProviderImpl.create();
    }
}
