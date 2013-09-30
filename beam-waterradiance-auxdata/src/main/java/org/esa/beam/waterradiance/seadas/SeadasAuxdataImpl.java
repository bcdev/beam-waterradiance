package org.esa.beam.waterradiance.seadas;

import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.datamodel.PixelPos;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.waterradiance.AtmosphericAuxdata;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SeadasAuxdataImpl implements AtmosphericAuxdata {

    private static final long MILLI_SECONDS_PER_DAY = 24 * 60 * 60 * 1000;
    private static final long HALF_MILLI_SECONDS_PER_DAY = MILLI_SECONDS_PER_DAY / 2;
    private static final String OZONE_BAND_NAME = "Geophysical Data/ozone";
    private static final Calendar utcCalendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);

    private final File auxDataDirectory;
    private final Map<String, Product> productMap;

    public static SeadasAuxdataImpl create(String auxPath) throws IOException {
        final File auxDataDirectory = new File(auxPath);
        if (!auxDataDirectory.isDirectory()) {
            throw new IOException();
        }
        return new SeadasAuxdataImpl(auxDataDirectory);
    }

    @Override
    public double getOzone(Date date, double lat, double lon) throws IOException {
        setCalendar(date);
        final double dateFraction = getDateFraction(utcCalendar, 0.5);

        int dayOffset = getDayOffset(getHourOfDay());

        TimeSpan timeSpan = createTimeSpan(utcCalendar, dayOffset);
        timeSpan = adjustForOverlappingYears(timeSpan);

        final Product startProduct = getProduct(timeSpan.getStartDay(), timeSpan.getStartYear());
        final Product endproduct = getProduct(timeSpan.getEndDay(), timeSpan.getEndYear());

        final double firstOzone = getOzone((float) lat, lon, startProduct);
        final double secondOzone = getOzone((float) lat, lon, endproduct);

        return (1.0 - dateFraction) * firstOzone + dateFraction * secondOzone;
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

    static int getDayOffset(int hourOfDay) {
        int dayOffset = 0;
        if (hourOfDay < 12) {
            dayOffset = -1;
        }
        return dayOffset;
    }

    // package access for testing only tb 2013-09-30
    static String getDayString(int day) {
        if (day < 10) {
            return "00" + day;
        } else if (day < 100) {
            return "0" + day;
        }
        return "" + day;
    }

    private double getOzone(float lat, double lon, Product product) throws IOException {
        PixelPos pixelPos = new PixelPos(lat, (float) lon);
        if (product.getSceneRasterWidth() == 288) {
            pixelPos = new PixelPos(lat, (float) (lon * 0.8));
        }
        return Double.parseDouble(product.getBand(OZONE_BAND_NAME).getPixelString((int) pixelPos.getX(), (int) pixelPos.getY()));
    }

    private int getHourOfDay() {
        return utcCalendar.get(Calendar.HOUR_OF_DAY);
    }

    private void setCalendar(Date date) {
        utcCalendar.clear();
        utcCalendar.setTime(date);
    }

    private Product getProduct(int day, int year) throws IOException {
        final String dayInFittingLength = getDayString(day);
        String productID = "" + year + dayInFittingLength;
        final Product product;
        if (productMap.containsKey(productID)) {
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

    static TimeSpan createTimeSpan(Calendar calendar, int dayOffset) {
        final TimeSpan timeSpan = new TimeSpan();
        final int startYear = calendar.get(Calendar.YEAR);
        timeSpan.setStartYear(startYear);

        int startDay = calendar.get(Calendar.DAY_OF_YEAR) + dayOffset;
        timeSpan.setStartDay(startDay);
        timeSpan.setEndYear(startYear);
        timeSpan.setEndDay(startDay + 1);
        return timeSpan;
    }

    static TimeSpan adjustForOverlappingYears(TimeSpan timeSpan) {
        int startDay = timeSpan.getStartDay();
        int startYear = timeSpan.getStartYear();
        int endDay = timeSpan.getEndDay();
        int endYear = timeSpan.getEndYear();

        if (startDay < 1) {
            startYear--;
            if (startYear % 4 == 0) {
                startDay = 366;
            } else {
                startDay = 365;
            }
        } else if (endDay > 365 || (endYear % 4 == 0 && endDay > 366)) {
            endYear++;
            endDay = 1;
        }

        timeSpan.setStartDay(startDay);
        timeSpan.setStartYear(startYear);
        timeSpan.setEndDay(endDay);
        timeSpan.setEndYear(endYear);
        return timeSpan;
    }

    private SeadasAuxdataImpl(File auxDataDirectory) {
        this.auxDataDirectory = auxDataDirectory;
        productMap = new HashMap<String, Product>();
    }

    public static class TimeSpan {
        private int startYear;
        private int startDay;
        private int endYear;
        private int endDay;

        int getStartYear() {
            return startYear;
        }

        void setStartYear(int startYear) {
            this.startYear = startYear;
        }

        int getStartDay() {
            return startDay;
        }

        void setStartDay(int startDay) {
            this.startDay = startDay;
        }

        int getEndYear() {
            return endYear;
        }

        void setEndYear(int endYear) {
            this.endYear = endYear;
        }

        int getEndDay() {
            return endDay;
        }

        void setEndDay(int endDay) {
            this.endDay = endDay;
        }
    }
}
