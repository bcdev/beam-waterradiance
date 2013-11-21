package org.esa.beam.waterradiance.seadas;

import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.PixelPos;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.waterradiance.AtmosphericAuxdata;
import org.esa.beam.waterradiance.util.LatLonToPixelPosConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SeadasAuxdataImpl implements AtmosphericAuxdata {

    private static final String OZONE_BAND_NAME = "Geophysical_Data/ozone";
    private static final String SURFACE_PRESSURE_BAND_NAME = "Geophysical_Data/press";

    private static final long MILLI_SECONDS_PER_DAY = 24 * 60 * 60 * 1000;

    private final Calendar utcCalendar;
    private AuxProductsProvider auxProductsProvider;
    private static final int[] months = new int[]{Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL,
            Calendar.MAY, Calendar.JUNE, Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER,
            Calendar.NOVEMBER, Calendar.DECEMBER};

    public static SeadasAuxdataImpl create(String auxPath) throws IOException {
        return new SeadasAuxdataImpl(auxPath);
    }

    public static SeadasAuxdataImpl create(Product tomsomiStartProduct, Product tomsomiEndProduct, Product ncepStartProduct, Product ncepEndProduct) throws IOException {
        return new SeadasAuxdataImpl(tomsomiStartProduct, tomsomiEndProduct, ncepStartProduct, ncepEndProduct);
    }

    @Override
    public double getOzone(Date date, double lat, double lon) throws IOException {
        setCalendar(date);
        final SeadasAuxDataProducts tomsomiProducts = auxProductsProvider.getTOMSOMIProducts(date);
        final double dateFraction = getDateFraction(utcCalendar, 0.5,
                                                    tomsomiProducts.getStartProduct(), tomsomiProducts.getEndProduct());

        final double startOzone = getOzone((float) lat, (float) lon, tomsomiProducts.getStartProduct());
        final double endOzone = getOzone((float) lat, (float) lon, tomsomiProducts.getEndProduct());
        return (1.0 - dateFraction) * startOzone + dateFraction * endOzone;
    }

    @Override
    public double getSurfacePressure(Date date, double lat, double lon) throws Exception {
        setCalendar(date);
        final SeadasAuxDataProducts ncepProducts = auxProductsProvider.getNCEPProducts(date);
        final double fraction = getDateFraction(utcCalendar, 0.125, ncepProducts.getStartProduct(),
                                                ncepProducts.getEndProduct());
        final double startSurfacePressure = getSurfacePressure((float) lat, (float) lon, ncepProducts.getStartProduct());
        final double endSurfacePressure = getSurfacePressure((float) lat, (float) lon, ncepProducts.getEndProduct());
        final double surfacePressure = (1.0 - fraction) * startSurfacePressure + fraction * endSurfacePressure;
        return surfacePressure;
    }

    @Override
    public void dispose() {
        auxProductsProvider.dispose();
    }

    private double getOzone(float lat, float lon, Product product) throws IOException {
        final PixelPos pixelPos = LatLonToPixelPosConverter.getAuxPixelPos(lat, lon, true);
        if (product.getSceneRasterWidth() == 288) {
            pixelPos.setLocation(pixelPos.getX() * 0.8, pixelPos.getY());
        }
        if (product.containsPixel(pixelPos)) {
            final Band ozoneBand = product.getBand(OZONE_BAND_NAME);
            return interpolate(ozoneBand, pixelPos.getX(), pixelPos.getY());
        }
        return Double.NaN;
    }

    // package access for testing only tf 2013-11-20
    static float interpolate(Band band, double pixelX, double pixelY) {
        List<Float> pixelValues = new ArrayList<Float>();
        List<Double> weights = new ArrayList<Double>();
        final double xFloor = Math.floor(pixelX);
        final double yFloor = Math.floor(pixelY);
        int xStart = -1;
        int xEnd = 1;
        int yStart = -1;
        int yEnd = 1;
        double totalSumOfWeights = 0;
        for (int i = xStart; i <= xEnd; i++) {
            int origX = (int) xFloor + i;
            int x = origX % band.getSceneRasterWidth();
            if (x < 0) {
                x = band.getSceneRasterWidth() - 1;
            }
            for (int j = yStart; j <= yEnd; j++) {
                int y = (int) yFloor + j;
                if (y >= 0 && y < band.getSceneRasterHeight()) {
                    pixelValues.add(band.getSampleFloat(x, y));
                    final double distanceToPixelCenter = Math.pow(pixelX - (origX + 0.5), 2) +
                            Math.pow(pixelY - (y + 0.5), 2);
                    final double weight = Math.exp(-Math.pow(distanceToPixelCenter / 0.5, 2));
                    weights.add(weight);
                    totalSumOfWeights += weight;
                }
            }
        }
        float interpolatedValue = 0;
        for (int i = 0; i < weights.size(); i++) {
            interpolatedValue += pixelValues.get(i) * (weights.get(i) / totalSumOfWeights);
        }
        return interpolatedValue;
    }

    private void setCalendar(Date date) {
        utcCalendar.clear();
        utcCalendar.setTime(date);
    }

    static double getDateFraction(Calendar productTime, double fractionOffset, Product startProduct, Product endProduct) {
        Calendar startTime = getTime(startProduct);
        Calendar endTime = getTime(endProduct);
        startTime.setTimeInMillis(startTime.getTimeInMillis() + (long) (fractionOffset * MILLI_SECONDS_PER_DAY));
        endTime.setTimeInMillis(endTime.getTimeInMillis() + (long) (fractionOffset * MILLI_SECONDS_PER_DAY));
        return (double) (productTime.getTimeInMillis() - startTime.getTimeInMillis()) / (endTime.getTimeInMillis() - startTime.getTimeInMillis());
    }

    static Calendar getTime(Product product) {
        final ProductData.UTC startTime = product.getStartTime();
        if (startTime != null) {
            return startTime.getAsCalendar();
        }
        final String productName = product.getName();
        int year = Integer.parseInt(productName.substring(1, 5));
        int dayInYear = Integer.parseInt(productName.substring(5, 8));
        int[] firstDaysOfMonths = {1, 32, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};
        if (isLeapYear(year)) {
            firstDaysOfMonths = new int[]{1, 32, 61, 92, 122, 153, 183, 214, 245, 275, 306, 336};
        }
        int month = months[11];
        int dayInMonth = dayInYear - firstDaysOfMonths[11];
        for (int i = 1; i < firstDaysOfMonths.length; i++) {
            int firstDayOfMonth = firstDaysOfMonths[i];
            if (dayInYear <= firstDayOfMonth) {
                month = months[i - 1];
                dayInMonth = dayInYear - firstDaysOfMonths[i - 1] + 1;
                break;
            }
        }
        int hour = Integer.parseInt(productName.substring(8, 10));
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(year, month, dayInMonth, hour, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        product.setStartTime(ProductData.UTC.create(calendar.getTime(), 0));
        return calendar;
    }

    static boolean isLeapYear(int year) {
        return !(year % 4 != 0 || year % 400 == 0);
    }

    private double getSurfacePressure(float lat, float lon, Product product) throws IOException {
        final PixelPos pixelPos = LatLonToPixelPosConverter.getAuxPixelPos(lat, lon, false);
        final float surfacePressure = interpolate(product.getBand(SURFACE_PRESSURE_BAND_NAME),
                                                  pixelPos.getX(), pixelPos.getY());
        return surfacePressure;
    }

    // package access for testing only tb 2013-10-01
    static String getProductId(int year, String dayString) {
        final StringBuilder stringBuilder = new StringBuilder(16);
        stringBuilder.append(year);
        stringBuilder.append(dayString);
        return stringBuilder.toString();
    }

    // package access for testing only tb 2013-10-01
    static String getProductId(int year, String dayString, String hourString) {
        final StringBuilder stringBuilder = new StringBuilder(16);
        stringBuilder.append(year);
        stringBuilder.append(dayString);
        stringBuilder.append(hourString);
        return stringBuilder.toString();
    }

    private SeadasAuxdataImpl(String auxPath) throws IOException {
        auxProductsProvider = new PathAuxProductsProvider(auxPath);
        utcCalendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
    }

    private SeadasAuxdataImpl(Product tomsomiStartProduct, Product tomsomiEndProduct,
                              Product ncepStartProduct, Product ncepEndProduct) throws IOException {
        auxProductsProvider = new ProductsAuxProductsProvider(tomsomiStartProduct, tomsomiEndProduct,
                                                              ncepStartProduct, ncepEndProduct);
        utcCalendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
    }

}