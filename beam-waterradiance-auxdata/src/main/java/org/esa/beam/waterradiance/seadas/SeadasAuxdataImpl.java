package org.esa.beam.waterradiance.seadas;

import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.datamodel.PixelPos;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.waterradiance.AtmosphericAuxdata;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SeadasAuxdataImpl implements AtmosphericAuxdata {

    private final File auxDataDirectory;
    private final static long MILLI_SECONDS_PER_DAY = 24 * 60 * 60 * 1000;
    public static final long HALF_MILLI_SECONDS_PER_DAY = MILLI_SECONDS_PER_DAY / 2;
    private final static String ozone_band_name = "Geophysical Data/ozone";
    private static final Calendar utcCalendar= GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
    private final Map<String,Product> productMap;

    private SeadasAuxdataImpl(File auxDataDirectory) {
        this.auxDataDirectory = auxDataDirectory;
        productMap = new HashMap<String, Product>();
    }

    @Override
    public double getOzone(Date date, double lat, double lon) throws IOException {

        Calendar calendar = ProductData.UTC.create(date, 0).getAsCalendar();
        utcCalendar.clear();
        utcCalendar.setTime(date);
        double fraction = getDateFraction(utcCalendar, 0.5);
        final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int dayOffset = 0;
        if (hourOfDay < 12) {
            dayOffset = -1;
        }
        int firstDay = calendar.get(Calendar.DAY_OF_YEAR) + dayOffset;
        int firstYear = calendar.get(Calendar.YEAR);
        int secondDay = calendar.get(Calendar.DAY_OF_YEAR) + dayOffset + 1;
        int secondYear = firstYear;
        if (firstDay < 1) {
            firstYear--;
            if (firstYear % 4 == 0) {
                firstDay = 366;
            } else {
                firstDay = 365;
            }
        } else if (secondDay > 365 || (secondYear % 4 == 0 && secondDay > 366)) {
            secondYear++;
            secondDay = 1;
        }
        final double firstOzone = getOzone((float) lat, lon, getProduct(firstDay, firstYear));
        final double secondOzone = getOzone((float) lat, lon, getProduct(secondDay, secondYear));
        return (1 - fraction) * firstOzone + fraction * secondOzone;
    }

    private String getDayInFittingLength(int day) {
        if(day < 10) {
            return "00" + day;
        } else if(day < 100) {
            return "0" + day;
        }
        return "" + day;
    }

    private double getOzone(float lat, double lon, Product product) throws IOException {
        PixelPos pixelPos = new PixelPos(lat, (float) lon);
        if (product.getSceneRasterWidth() == 288) {
            pixelPos = new PixelPos(lat, (float) (lon * 0.8));
        }
        return Double.parseDouble(product.getBand(ozone_band_name).getPixelString((int) pixelPos.getX(), (int) pixelPos.getY()));
    }

    private Product getProduct(int day, int year) throws IOException {
        final String dayInFittingLength = getDayInFittingLength(day);
        String productID = "" + year + dayInFittingLength;
        final Product product;
        if(productMap.containsKey(productID)) {
            product = productMap.get(productID);
        } else {
            String productPath = auxDataDirectory.getPath() +
                    "//" + year + "//" + day + "//N" + year + dayInFittingLength + "00_O3_TOMSOMI_24h.hdf";
            try {
                product = ProductIO.readProduct(new File(productPath));
            } catch (IOException e) {
                throw new IOException("Could not find product for given day");
            }
            productMap.put(productID, product);
        }
        return product;
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

    private static int calculateMillisOnDay(Calendar calendar) {
        int millisOnProductDay = calendar.get(Calendar.MILLISECOND);
        millisOnProductDay += 1000.0 * calendar.get(Calendar.SECOND);
        millisOnProductDay += 60000.0 * calendar.get(Calendar.MINUTE);
        millisOnProductDay += 3600000.0 * calendar.get(Calendar.HOUR_OF_DAY);
        return millisOnProductDay;
    }

    @Override
    public double getSurfacePressure(Date date, double lat, double lon) throws Exception {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void dispose() {
        for (Product product : productMap.values()) {
            product.dispose();
        }
        productMap.clear();
    }

    public static SeadasAuxdataImpl create(String auxPath) throws IOException {
        final File auxDataDirectory = new File(auxPath);
        if (!auxDataDirectory.isDirectory()) {
            throw new IOException();
        }
        return new SeadasAuxdataImpl(auxDataDirectory);
    }
}
