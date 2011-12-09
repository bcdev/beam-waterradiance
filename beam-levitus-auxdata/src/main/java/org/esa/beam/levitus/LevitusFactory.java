package org.esa.beam.levitus;

import com.bc.ceres.core.ProgressMonitor;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.util.ResourceInstaller;
import org.esa.beam.util.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;

/**
 * @author Marco Peters
 */
public class LevitusFactory {

    private static final File AUXDATA_DIR = new File(SystemUtils.getApplicationDataDir(), "beam-levitus-auxdata/auxdata");
    private static final String SALINITY_FILE_NAME = "Levitus-Annual-Salinity.nc";
    private static final String TEMPERATURE_FILE_NAME = "Levitus-Annual-Temperature.nc";

    static {
        URL sourceUrl = ResourceInstaller.getSourceUrl(LevitusFactory.class);
        ResourceInstaller installer = new ResourceInstaller(sourceUrl, "auxdata/", AUXDATA_DIR);
        try {
            installer.install(".*.nc", ProgressMonitor.NULL);
        } catch (IOException e) {
            throw new RuntimeException("Unable to install auxdata of the beam-levitus-auxdata module");
        }
    }

    public static LevitusDataProvider createDataProvider(int day, int month) throws IOException {
        Calendar calendar = ProductData.UTC.createCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        return createDataProvider(calendar);
    }

    private static LevitusDataProvider createDataProvider(Calendar calendar) throws IOException {
        Product salinityProduct = ProductIO.readProduct(new File(AUXDATA_DIR, SALINITY_FILE_NAME));
        Product temperatureProduct = ProductIO.readProduct(new File(AUXDATA_DIR, TEMPERATURE_FILE_NAME));
        return new LevitusDataProviderImpl(salinityProduct, temperatureProduct, calendar);
    }

}
