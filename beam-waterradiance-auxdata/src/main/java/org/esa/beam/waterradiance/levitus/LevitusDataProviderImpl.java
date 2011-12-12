package org.esa.beam.waterradiance.levitus;

import org.esa.beam.waterradiance.AuxdataDataProvider;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.GeoCoding;
import org.esa.beam.framework.datamodel.GeoPos;
import org.esa.beam.framework.datamodel.PixelPos;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.util.math.MathUtils;

import java.io.IOException;
import java.util.Calendar;

/**
 * @author Marco Peters
 */
public class LevitusDataProviderImpl implements AuxdataDataProvider {

    public static final int LEVITUS_CENTER_DAY = 16;
    private GeoCoding salinityGeoCoding;
    private GeoCoding temperatureGeoCoding;
    private final Product salinityProduct;
    private Product tempProduct;

    /**
     * Creates an instance of this <code></>LevitusDataProvider</code> implementation.
     *
     * @param salinityProduct the product containing the salinity data
     * @param tempProduct     the product containing the temperature data
     */
    public LevitusDataProviderImpl(Product salinityProduct, Product tempProduct) {
        this.salinityProduct = salinityProduct;
        this.tempProduct = tempProduct;
        salinityGeoCoding = this.salinityProduct.getGeoCoding();
        temperatureGeoCoding = this.tempProduct.getGeoCoding();
    }

    private static class DateDependentFields {

        private double linearFraction;
        private final int lowerMonth;
        private final int upperMonth;

        private DateDependentFields(Calendar date) {
            linearFraction = calculateLinearFraction(date);
            int day = date.get(Calendar.DAY_OF_MONTH);
            int month = date.get(Calendar.MONTH);
            lowerMonth = calculateLowerMonth(day, month);
            upperMonth = calculateUpperMonth(day, month);
        }
    }

    public double getSalinity(Calendar date, double lat, double lon) {
        DateDependentFields dateDependentFields = new DateDependentFields(date);

        PixelPos pixelPos = salinityGeoCoding.getPixelPos(new GeoPos((float) lat, (float) lon), null);
        int x = MathUtils.floorInt(pixelPos.x);
        int y = MathUtils.floorInt(pixelPos.y);
        Band lowerBand = salinityProduct.getBandAt(dateDependentFields.lowerMonth);
        Band upperBand = salinityProduct.getBandAt(dateDependentFields.upperMonth);
        try {
            double[] lowPixel = new double[1];
            double[] upperPixel = new double[1];
            lowerBand.readPixels(x, y, 1, 1, lowPixel);
            upperBand.readPixels(x, y, 1, 1, upperPixel);
            return interpolate(lowPixel[0], upperPixel[0], dateDependentFields.linearFraction);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return Double.NaN;
    }

    public double getTemperature(Calendar date, double lat, double lon) {
        DateDependentFields dateDependentFields = new DateDependentFields(date);

        PixelPos pixelPos = temperatureGeoCoding.getPixelPos(new GeoPos((float) lat, (float) lon), null);
        int x = MathUtils.floorInt(pixelPos.x);
        int y = MathUtils.floorInt(pixelPos.y);
        Band lowerBand = tempProduct.getBandAt(dateDependentFields.lowerMonth);
        Band upperBand = tempProduct.getBandAt(dateDependentFields.upperMonth);
        double lowerValue = lowerBand.getPixelDouble(x, y);
        double upperValue = upperBand.getPixelDouble(x, y);
        return interpolate(lowerValue, upperValue, dateDependentFields.linearFraction);
    }

    static double interpolate(double lowerValue, double upperValue, double fraction) {
        return (lowerValue * (1 - fraction) + upperValue * fraction);
    }

    static double calculateLinearFraction(Calendar date) {
        double day = date.get(Calendar.DAY_OF_MONTH);
        if (day < LEVITUS_CENTER_DAY) {
            day = day + (LEVITUS_CENTER_DAY - 1);
        } else if (day >= LEVITUS_CENTER_DAY) {
            day = day - LEVITUS_CENTER_DAY;
        }
        return day / date.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    static int calculateUpperMonth(int day, int month) {
        int upperMonth = day >= 16 ? month + 1 : month;
        return upperMonth > 11 ? 0 : upperMonth;
    }

    static int calculateLowerMonth(int day, int month) {
        int lowerMonth = day < 16 ? month - 1 : month;
        return lowerMonth < 0 ? 11 : lowerMonth;
    }

}
