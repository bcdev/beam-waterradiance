package org.esa.beam.waterradiance.seadas;

import org.esa.beam.waterradiance.AtmosphericAuxdata;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class SeadasAuxdataImpl implements AtmosphericAuxdata{

    private final File auxDataDirectory;

    private SeadasAuxdataImpl(File auxDataDirectory) {
        this.auxDataDirectory = auxDataDirectory;
    }

    @Override
    public double getOzone(Date date, double lat, double lon) throws Exception {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double getSurfacePressure(Date date, double lat, double lon) throws Exception {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public static SeadasAuxdataImpl create(String auxPath) throws IOException {
        final File auxDataDirectory = new File(auxPath);
        if(!auxDataDirectory.isDirectory()) {
            throw new IOException();
        }
        return new SeadasAuxdataImpl(auxDataDirectory);
    }
}
