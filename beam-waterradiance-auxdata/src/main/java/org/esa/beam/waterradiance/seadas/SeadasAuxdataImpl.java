package org.esa.beam.waterradiance.seadas;

import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.datamodel.PixelPos;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.util.math.MathUtils;
import org.esa.beam.waterradiance.AtmosphericAuxdata;
import org.esa.beam.waterradiance.util.LatLonToPixelPosConverter;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class SeadasAuxdataImpl implements AtmosphericAuxdata {

    private static final String OZONE_BAND_NAME = "Geophysical_Data/ozone";
    private static final String SURFACE_PRESSURE_BAND_NAME = "Geophysical_Data/press";

    private static final long MILLI_SECONDS_PER_DAY = 24 * 60 * 60 * 1000;
    private static final long HALF_MILLI_SECONDS_PER_DAY = MILLI_SECONDS_PER_DAY / 2;
    private static final long QUARTER_MILLI_SECONDS_PER_DAY = MILLI_SECONDS_PER_DAY / 4;
    private static final long EIGHTH_MILLI_SECONDS_PER_DAY = MILLI_SECONDS_PER_DAY / 8;

    private final Calendar utcCalendar;
    private final Map<String, Double> surfacePressureMap;
    private final Map<String, Double> ozoneMap;
    private Date date_of_last_TOMS_product = new GregorianCalendar(2005, 12, 31).getTime();
    private Date date_of_first_OMI_product = new GregorianCalendar(2006, 1, 1).getTime();
    private AuxProductsProvider auxProductsProvider;

    public static SeadasAuxdataImpl create(String auxPath) throws IOException {
        return new SeadasAuxdataImpl(auxPath);
    }

    public static AtmosphericAuxdata create(Product tomsomiStartProduct, Product tomsomiEndProduct, Product ncepStartProduct, Product ncepEndProduct) throws IOException {
        return new SeadasAuxdataImpl(tomsomiStartProduct, tomsomiEndProduct, ncepStartProduct, ncepEndProduct);
    }

    @Override
    public double getOzone(Date date, double lat, double lon) throws IOException {

        String id = null;
        if (date.before(date_of_last_TOMS_product)) {
            final int xPos = MathUtils.floorInt(lat);
            final int yPos = MathUtils.floorInt(lon * 0.8);
            id = date.toString() + xPos + yPos;
            if (ozoneMap.containsKey(id)) {
                return ozoneMap.get(id);
            }
        } else if (date.after(date_of_first_OMI_product)) {
            final int xPos = MathUtils.floorInt(lat);
            final int yPos = MathUtils.floorInt(lon);
            id = date.toString() + xPos + yPos;
            if (ozoneMap.containsKey(id)) {
                return ozoneMap.get(id);
            }
        }

        setCalendar(date);
        final double dateFraction = getDateFraction(utcCalendar, 0.5);

        final SeadasAuxDataProducts tomsomiProducts = auxProductsProvider.getTOMSOMIProducts(date);
        final double startOzone = getOzone((float) lat, (float) lon, tomsomiProducts.getStartProduct());
        final double endOzone = getOzone((float) lat, (float) lon, tomsomiProducts.getEndProduct());
        final double ozone = (1.0 - dateFraction) * startOzone + dateFraction * endOzone;

        if (id != null) {
            ozoneMap.put(id, ozone);
        }

        return ozone;
    }

    @Override
    public double getSurfacePressure(Date date, double lat, double lon) throws Exception {

        final int xPos = MathUtils.floorInt(lon);
        final int yPos = MathUtils.floorInt(lat);
        String id = date.toString() + xPos + yPos;
        if (surfacePressureMap.containsKey(id)) {
            return surfacePressureMap.get(id);
        }

        setCalendar(date);
        double fraction = getDateFractionForSurfacePressure(utcCalendar);

        final SeadasAuxDataProducts ncepProducts = auxProductsProvider.getNCEPProducts(date);
        final double startSurfacePressure = getSurfacePressure((float) lat, (float) lon, ncepProducts.getStartProduct());
        final double endSurfacePressure = getSurfacePressure((float) lat, (float) lon, ncepProducts.getEndProduct());
        final double surfacePressure = (1.0 - fraction) * startSurfacePressure + fraction * endSurfacePressure;

        if (!surfacePressureMap.containsKey(id)) {
            surfacePressureMap.put(id, surfacePressure);
        }

        return surfacePressure;
    }

    @Override
    public void dispose() {
        ozoneMap.clear();
        surfacePressureMap.clear();
    }

    private double getOzone(float lat, float lon, Product product) throws IOException {
        final int xPos = MathUtils.floorInt(lat);
        final int yPos = MathUtils.floorInt(lon);
        final PixelPos pixelPos = LatLonToPixelPosConverter.getAuxPixelPos(xPos, yPos);
        if (product.getSceneRasterWidth() == 288) {
            pixelPos.setLocation(pixelPos.getX(), pixelPos.getY() * 0.8);
        }
        return Double.parseDouble(product.getBand(OZONE_BAND_NAME).getPixelString((int) pixelPos.getX(), (int) pixelPos.getY()));
    }

    private void setCalendar(Date date) {
        utcCalendar.clear();
        utcCalendar.setTime(date);
    }

    static double getDateFractionForSurfacePressure(Calendar calendar) {
        final int millisOnProductDay = calculateMillisOnDay(calendar);
        final long millisInQuarterDay = (millisOnProductDay + EIGHTH_MILLI_SECONDS_PER_DAY) % QUARTER_MILLI_SECONDS_PER_DAY;
        return ((double) millisInQuarterDay / QUARTER_MILLI_SECONDS_PER_DAY);
    }

    static double getDateFraction(Calendar calendar, double fractionOffset) {
        final int millisOnProductDay = calculateMillisOnDay(calendar);
        double fraction;
        if (millisOnProductDay < HALF_MILLI_SECONDS_PER_DAY) {
            fraction = fractionOffset + ((double) millisOnProductDay / MILLI_SECONDS_PER_DAY);
        } else {
            fraction = -fractionOffset + ((double) millisOnProductDay / MILLI_SECONDS_PER_DAY);
        }
        return fraction;
    }

    private double getSurfacePressure(float lat, float lon, Product product) throws IOException {
        final int xPos = MathUtils.floorInt(lon);
        int yPos = MathUtils.floorInt(lat);
        final PixelPos pixelPos = LatLonToPixelPosConverter.getAuxPixelPos(yPos, xPos);
        return Double.parseDouble(product.getBand(SURFACE_PRESSURE_BAND_NAME).getPixelString((int) pixelPos.getX(), (int) pixelPos.getY()));
    }

    private static int calculateMillisOnDay(Calendar calendar) {
        int millisOnProductDay = calendar.get(Calendar.MILLISECOND);
        millisOnProductDay += 1000.0 * calendar.get(Calendar.SECOND);
        millisOnProductDay += 60000.0 * calendar.get(Calendar.MINUTE);
        millisOnProductDay += 3600000.0 * calendar.get(Calendar.HOUR_OF_DAY);
        return millisOnProductDay;
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
        surfacePressureMap = new HashMap<String, Double>();
        ozoneMap = new HashMap<String, Double>();
    }

    private SeadasAuxdataImpl(Product tomsomiStartProduct, Product tomsomiEndProduct,
                              Product ncepStartProduct, Product ncepEndProduct) throws IOException {
        auxProductsProvider = new ProductsAuxProductsProvider(tomsomiStartProduct, tomsomiEndProduct,
                                                              ncepStartProduct, ncepEndProduct);
        utcCalendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        surfacePressureMap = new HashMap<String, Double>();
        ozoneMap = new HashMap<String, Double>();
    }

}