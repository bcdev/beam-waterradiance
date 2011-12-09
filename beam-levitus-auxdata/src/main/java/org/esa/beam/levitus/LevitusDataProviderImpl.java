package org.esa.beam.levitus;

import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;

import java.util.Calendar;

/**
* @author Marco Peters
*/
class LevitusDataProviderImpl implements LevitusDataProvider {

    public static final int LEVITUS_CENTER_DAY = 16;
    private Band salinityLower;
    private Band salinityUpper;
    private Band temperatureLower;
    private Band temperatureUpper;
    private double interpolFraction;

    /**
     * Creates an instance of this <code></>LevitusDataProvider</code> implementation.
     * @param salinityProduct the product containing the salinity data
     * @param tempProduct the product containing the temperature data
     * @param date the date to retrieve the data for
     */
    LevitusDataProviderImpl(Product salinityProduct, Product tempProduct, Calendar date) {
        int day = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH);
        interpolFraction = (day + LEVITUS_CENTER_DAY) / date.getActualMaximum(Calendar.DAY_OF_MONTH);
        int lowerMonth = calculateLowerMonth(day, month);
        int upperMonth = calculateUpperMonth(day, month);
        salinityLower = salinityProduct.getBandAt(lowerMonth);
        salinityUpper = salinityProduct.getBandAt(upperMonth);
        temperatureLower = tempProduct.getBandAt(lowerMonth);
        temperatureUpper = tempProduct.getBandAt(upperMonth);
    }

    public double getSalinity(double lat, double lon) {
        return 0;
    }

    public double getTemperature(double lat, double lon) {
        return 0;
    }


    static int calculateUpperMonth(int day, int month) {
        int upperMonth = day >= 16 ? month + 1 : month;
        return upperMonth > 11 ? 0: upperMonth;
    }

    static int calculateLowerMonth(int day, int month) {
        int lowerMonth = day < 16 ? month - 1 : month;
        return lowerMonth < 0 ? 11: lowerMonth;
    }

}
