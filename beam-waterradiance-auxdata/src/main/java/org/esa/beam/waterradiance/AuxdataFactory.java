package org.esa.beam.waterradiance;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.waterradiance.levitus.LevitusDataProviderImpl;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.util.ResourceInstaller;
import org.esa.beam.util.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author Marco Peters
 */
public class AuxdataFactory {

    private static final File AUXDATA_DIR = new File(SystemUtils.getApplicationDataDir(), "beam-waterradiance-auxdata/auxdata");
    private static final String SALINITY_FILE_NAME = "Levitus-Annual-Salinity.nc";
    private static final String TEMPERATURE_FILE_NAME = "Levitus-Annual-Temperature.nc";

    static {
        URL sourceUrl = ResourceInstaller.getSourceUrl(AuxdataFactory.class);
        ResourceInstaller installer = new ResourceInstaller(sourceUrl, "auxdata/", AUXDATA_DIR);
        try {
            installer.install(".*.nc", ProgressMonitor.NULL);
        } catch (IOException e) {
            throw new RuntimeException("Unable to install auxdata of the beam-levitus-auxdata module");
        }
    }

    public static AuxdataDataProvider createDataProvider() throws IOException {
        Product salinityProduct = ProductIO.readProduct(new File(AUXDATA_DIR, SALINITY_FILE_NAME));
        Product temperatureProduct = ProductIO.readProduct(new File(AUXDATA_DIR, TEMPERATURE_FILE_NAME));
        return new LevitusDataProviderImpl(salinityProduct, temperatureProduct);
    }

}
