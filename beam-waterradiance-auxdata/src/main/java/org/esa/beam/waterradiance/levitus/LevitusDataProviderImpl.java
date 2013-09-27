package org.esa.beam.waterradiance.levitus;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.datamodel.*;
import org.esa.beam.util.ResourceInstaller;
import org.esa.beam.util.SystemUtils;
import org.esa.beam.util.math.MathUtils;
import org.esa.beam.waterradiance.AuxdataProvider;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Marco Peters
 */
public class LevitusDataProviderImpl implements AuxdataProvider {

    private static File AUXDATA_DIR;

    private static final String SALINITY_FILE_NAME = "Levitus-Annual-Salinity.nc";
    private static final String TEMPERATURE_FILE_NAME = "Levitus-Annual-Temperature.nc";

    private static final int LEVITUS_CENTER_DAY = 16;
    private static boolean isAuxDataResourceInitialized = false;

    private GeoCoding salinityGeoCoding;
    private GeoCoding temperatureGeoCoding;
    private final Product salinityProduct;
    private Product temperatureProduct;


    public static LevitusDataProviderImpl create() throws IOException {
        if (!isAuxDataResourceInitialized) {
            initializeResourceAccess();
        }

        final Product salinityProduct = ProductIO.readProduct(new File(AUXDATA_DIR, SALINITY_FILE_NAME));
        final Product temperatureProduct = ProductIO.readProduct(new File(AUXDATA_DIR, TEMPERATURE_FILE_NAME));
        return new LevitusDataProviderImpl(salinityProduct, temperatureProduct);
    }

    public double getSalinity(Date date, double lat, double lon) throws Exception {
        DateDependentValues dateDependentValues = new DateDependentValues(date);

        PixelPos pixelPos = salinityGeoCoding.getPixelPos(new GeoPos((float) lat, (float) lon), null);
        int x = MathUtils.floorInt(pixelPos.x);
        int y = MathUtils.floorInt(pixelPos.y);
        if (!productContainsPixel(salinityProduct, x, y)) {
            return Double.NaN;
        }
        Band lowerBand = salinityProduct.getBandAt(dateDependentValues.lowerMonth);
        Band upperBand = salinityProduct.getBandAt(dateDependentValues.upperMonth);
        double[] lowPixel = new double[1];
        double[] upperPixel = new double[1];
        lowerBand.readPixels(x, y, 1, 1, lowPixel);
        upperBand.readPixels(x, y, 1, 1, upperPixel);
        return interpolate(lowPixel[0], upperPixel[0], dateDependentValues.linearFraction);
    }

    public double getTemperature(Date date, double lat, double lon) throws Exception {
        DateDependentValues dateDependentValues = new DateDependentValues(date);

        PixelPos pixelPos = temperatureGeoCoding.getPixelPos(new GeoPos((float) lat, (float) lon), null);
        int x = MathUtils.floorInt(pixelPos.x);
        int y = MathUtils.floorInt(pixelPos.y);
        if (!productContainsPixel(temperatureProduct, x, y)) {
            return Double.NaN;
        }

        Band lowerBand = temperatureProduct.getBandAt(dateDependentValues.lowerMonth);
        Band upperBand = temperatureProduct.getBandAt(dateDependentValues.upperMonth);
        double[] lowPixel = new double[1];
        double[] upperPixel = new double[1];
        lowerBand.readPixels(x, y, 1, 1, lowPixel);
        upperBand.readPixels(x, y, 1, 1, upperPixel);
        return interpolate(lowPixel[0], upperPixel[0], dateDependentValues.linearFraction);
    }

    @Override
    public void dispose() {
        if (salinityProduct != null)  {
            salinityProduct.dispose();
        }

        if (temperatureProduct != null) {
            temperatureProduct.dispose();
        }
    }

    /**
     * Creates an instance of this <code></>LevitusDataProvider</code> implementation.
     *
     * @param salinityProduct the product containing the salinity data
     * @param temperatureProduct     the product containing the temperature data
     */
    private LevitusDataProviderImpl(Product salinityProduct, Product temperatureProduct) {
        this.salinityProduct = salinityProduct;
        this.temperatureProduct = temperatureProduct;
        salinityGeoCoding = salinityProduct.getGeoCoding();
        temperatureGeoCoding = temperatureProduct.getGeoCoding();
    }

    private static void initializeResourceAccess() {
        AUXDATA_DIR = new File(SystemUtils.getApplicationDataDir(), "beam-waterradiance-auxdata/auxdata");
        final URL sourceUrl = ResourceInstaller.getSourceUrl(LevitusDataProviderImpl.class);
        final ResourceInstaller installer = new ResourceInstaller(sourceUrl, "../auxdata/", AUXDATA_DIR);
        try {
            installer.install(".*.nc", ProgressMonitor.NULL);
        } catch (IOException e) {
            throw new RuntimeException("Unable to install auxdata of the beam-levitus-auxdata module");
        }
        isAuxDataResourceInitialized = true;
    }

    static double interpolate(double lowerValue, double upperValue, double fraction) {
        return (lowerValue * (1 - fraction) + upperValue * fraction);
    }

    static boolean productContainsPixel(Product product, int x, int y) {
        return x >= 0 && x < product.getSceneRasterWidth() &&
                y >= 0 && y < product.getSceneRasterHeight();
    }

    static double calculateLinearFraction(Calendar calendar) {
        double day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day < LEVITUS_CENTER_DAY) {
            day = day + (LEVITUS_CENTER_DAY - 1);
        } else if (day >= LEVITUS_CENTER_DAY) {
            day = day - LEVITUS_CENTER_DAY;
        }
        return day / calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    static int calculateUpperMonth(int day, int month) {
        int upperMonth = day >= 16 ? month + 1 : month;
        return upperMonth > 11 ? 0 : upperMonth;
    }

    static int calculateLowerMonth(int day, int month) {
        int lowerMonth = day < 16 ? month - 1 : month;
        return lowerMonth < 0 ? 11 : lowerMonth;
    }

    private static class DateDependentValues {

        private double linearFraction;
        private final int lowerMonth;
        private final int upperMonth;

        private DateDependentValues(Date date) {
            Calendar calendar = ProductData.UTC.create(date, 0).getAsCalendar();
            linearFraction = calculateLinearFraction(calendar);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            lowerMonth = calculateLowerMonth(day, month);
            upperMonth = calculateUpperMonth(day, month);
        }
    }
}
