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
    public static final long QUARTER_MILLI_SECONDS_PER_DAY = MILLI_SECONDS_PER_DAY / 4;
    public static final long EIGTH_MILLI_SECONDS_PER_DAY = MILLI_SECONDS_PER_DAY / 8;
    private final static String ozone_band_name = "Geophysical Data/ozone";
    private final static String surface_pressure_band_name = "Geophysical Data/press";
    private static final Calendar utcCalendar= GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
    private final static String[] ncep_hours = {"00", "06", "12", "18"};
    private final Map<String,Product> tomsomiProductMap;
    private final Map<String,Product> ncepProductMap;

    private SeadasAuxdataImpl(File auxDataDirectory) {
        this.auxDataDirectory = auxDataDirectory;
        tomsomiProductMap = new HashMap<String, Product>();
        ncepProductMap = new HashMap<String, Product>();
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
        final double firstOzone = getOzone((float) lat, lon, getTOMSOMIProduct(firstDay, firstYear));
        final double secondOzone = getOzone((float) lat, lon, getTOMSOMIProduct(secondDay, secondYear));
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

    private Product getTOMSOMIProduct(int day, int year) throws IOException {
        final String dayInFittingLength = getDayInFittingLength(day);
        String productID = "" + year + dayInFittingLength;
        final Product product;
        if(tomsomiProductMap.containsKey(productID)) {
            product = tomsomiProductMap.get(productID);
        } else {
            String productPath = auxDataDirectory.getPath() +
                    "//" + year + "//" + day + "//N" + year + dayInFittingLength + "00_O3_TOMSOMI_24h.hdf";
            try {
                product = ProductIO.readProduct(new File(productPath));
            } catch (IOException e) {
                throw new IOException("Could not retrieve ozone for given day");
            }
            tomsomiProductMap.put(productID, product);
        }
        return product;
    }

    private Product getNCEPProduct(int day, int year, int hour) throws IOException {
        final String dayInFittingLength = getDayInFittingLength(day);
        String hourInFittingLength = ncep_hours[hour];
        String productID = "" + year + dayInFittingLength + hourInFittingLength;
        final Product product;
        if(ncepProductMap.containsKey(productID)) {
            product = ncepProductMap.get(productID);
        } else {
            String productPath = auxDataDirectory.getPath() +
                    "//" + year + "//" + day + "//N" + year + dayInFittingLength + hourInFittingLength +
                    "_MET_NCEPN_6h.hdf";
            try {
                product = ProductIO.readProduct(new File(productPath));
            } catch (IOException e) {
                throw new IOException("Could not retrieve surface pressure for given day");
            }
            ncepProductMap.put(productID, product);
        }
        return product;
    }

    static double getDateFractionForSurfacePressure(Calendar calendar) {
        final int millisOnProductDay = calculateMillisOnDay(calendar);
        final long millisInQuarterDay = (millisOnProductDay + EIGTH_MILLI_SECONDS_PER_DAY) % QUARTER_MILLI_SECONDS_PER_DAY;
        return ((double)millisInQuarterDay / QUARTER_MILLI_SECONDS_PER_DAY);
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

        Calendar calendar = ProductData.UTC.create(date, 0).getAsCalendar();
        utcCalendar.clear();
        utcCalendar.setTime(date);
        double fraction = getDateFractionForSurfacePressure(utcCalendar);
        final int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int firstDayOffset = 0;
        int secondDayOffset = 0;
        if (hourOfDay < 3) {
            firstDayOffset = -1;
        } else if(hourOfDay >= 21) {
            secondDayOffset = 1;
        }
        int firstDay = calendar.get(Calendar.DAY_OF_YEAR) + firstDayOffset;
        int firstYear = calendar.get(Calendar.YEAR);
        int secondDay = calendar.get(Calendar.DAY_OF_YEAR) + secondDayOffset;
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
        int firstHour = ((hourOfDay + 3) / 6) - 1;
        if(firstHour < 0) {
            firstHour = 3;
        }
        int secondHour = (firstHour + 1) % 4;

        final double firstOzone = getSurfacePressure((float) lat, lon, getNCEPProduct(firstDay, firstYear, firstHour));
        final double secondOzone = getSurfacePressure((float) lat, lon, getNCEPProduct(secondDay, secondYear, secondHour));
        return (1 - fraction) * firstOzone + fraction * secondOzone;
    }

    private double getSurfacePressure(float lat, double lon, Product product) throws IOException {
        PixelPos pixelPos = new PixelPos(lat, (float) lon);
        if (product.getSceneRasterWidth() == 288) {
            pixelPos = new PixelPos(lat, (float) (lon * 0.8));
        }
        return Double.parseDouble(product.getBand(surface_pressure_band_name).getPixelString((int) pixelPos.getX(), (int) pixelPos.getY()));
    }

    @Override
    public void dispose() {
        for (Product product : tomsomiProductMap.values()) {
            product.dispose();
        }
        tomsomiProductMap.clear();
    }

    public static SeadasAuxdataImpl create(String auxPath) throws IOException {
        final File auxDataDirectory = new File(auxPath);
        if (!auxDataDirectory.isDirectory()) {
            throw new IOException();
        }
        return new SeadasAuxdataImpl(auxDataDirectory);
    }
}
