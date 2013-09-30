package org.esa.beam.waterradiance.seadas;

import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.datamodel.PixelPos;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.waterradiance.AtmosphericAuxdata;

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

    private static final String OZONE_BAND_NAME = "Geophysical Data/ozone";

    private final Calendar utcCalendar;
    private final File auxDataDirectory;
    private final static long MILLI_SECONDS_PER_DAY = 24 * 60 * 60 * 1000;
    public static final long HALF_MILLI_SECONDS_PER_DAY = MILLI_SECONDS_PER_DAY / 2;
    public static final long QUARTER_MILLI_SECONDS_PER_DAY = MILLI_SECONDS_PER_DAY / 4;
    public static final long EIGTH_MILLI_SECONDS_PER_DAY = MILLI_SECONDS_PER_DAY / 8;
    private final static String surface_pressure_band_name = "Geophysical Data/press";
    private final static String[] ncep_hours = {"00", "06", "12", "18"};
    private final Map<String, Product> tomsomiProductMap;
    private final Map<String, Product> ncepProductMap;

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

        final Product startProduct = getTOMSOMIProduct(timeSpan.getStartDay(), timeSpan.getStartYear());
        final Product endProduct = getTOMSOMIProduct(timeSpan.getEndDay(), timeSpan.getEndYear());

        final double startOzone = getOzone((float) lat, lon, startProduct);
        final double endOzone = getOzone((float) lat, lon, endProduct);

        return (1.0 - dateFraction) * startOzone + dateFraction * endOzone;
    }

    @Override
    public double getSurfacePressure(Date date, double lat, double lon) throws Exception {
        setCalendar(date);
        double fraction = getDateFractionForSurfacePressure(utcCalendar);

        final int startDayOffset = getStartDayOffset(getHourOfDay());
        final int endDayOffset = getEndDayOffset(getHourOfDay());

        TimeSpan timeSpan = createTimeSpan(utcCalendar, startDayOffset, endDayOffset);
        timeSpan = adjustForOverlappingYears(timeSpan);

        final Product startProduct = getNCEPProduct(timeSpan.getStartDay(), timeSpan.getStartYear(), timeSpan.getStartInterval());
        final Product endProduct = getNCEPProduct(timeSpan.getEndDay(), timeSpan.getEndYear(), timeSpan.getEndInterval());

        final double startSurfacePressure = getSurfacePressure((float) lat, lon, startProduct);
        final double endSurfacePressure = getSurfacePressure((float) lat, lon, endProduct);

        return (1.0 - fraction) * startSurfacePressure + fraction * endSurfacePressure;
    }

    @Override
    public void dispose() {
        for (Product product : tomsomiProductMap.values()) {
            product.dispose();
        }
        for (Product product : ncepProductMap.values()) {
            product.dispose();
        }
        tomsomiProductMap.clear();
        ncepProductMap.clear();
    }

    static int getDayOffset(int hourOfDay) {
        int dayOffset = 0;
        if (hourOfDay < 12) {
            dayOffset = -1;
        }
        return dayOffset;
    }

    static int getStartDayOffset(int hourOfDay) {
        int startDayOffset = 0;
        if (hourOfDay < 3) {
            startDayOffset = -1;
        }
        return startDayOffset;
    }

    static int getEndDayOffset(int hourOfDay) {
        int endDayOffset = 0;
        if (hourOfDay >= 21) {
            endDayOffset = 1;
        }
        return endDayOffset;
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

    private Product getTOMSOMIProduct(int day, int year) throws IOException {
        final String dayInFittingLength = getDayString(day);
        String productID = "" + year + dayInFittingLength;
        final Product product;
        if (tomsomiProductMap.containsKey(productID)) {
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
        final String dayInFittingLength = getDayString(day);
        String hourInFittingLength = ncep_hours[hour];
        String productID = "" + year + dayInFittingLength + hourInFittingLength;
        final Product product;
        if (ncepProductMap.containsKey(productID)) {
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

    private double getSurfacePressure(float lat, double lon, Product product) throws IOException {
        PixelPos pixelPos = new PixelPos(lat, (float) lon);
        if (product.getSceneRasterWidth() == 288) {
            pixelPos = new PixelPos(lat, (float) (lon * 0.8));
        }
        return Double.parseDouble(product.getBand(surface_pressure_band_name).getPixelString((int) pixelPos.getX(), (int) pixelPos.getY()));
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

    static TimeSpan createTimeSpan(Calendar calendar, int startDayOffset, int endDayOffset) {
        final TimeSpan timeSpan = new TimeSpan();
        final int productYear = calendar.get(Calendar.YEAR);
        timeSpan.setStartYear(productYear);
        final int productDay = calendar.get(Calendar.DAY_OF_YEAR);
        timeSpan.setStartDay(productDay + startDayOffset);
        final int productHour = calendar.get(Calendar.HOUR_OF_DAY);
        int startInterval = ((productHour + 3) / 6) - 1;
        if (startInterval < 0) {
            startInterval = 3;
        }
        int endInterval = (startInterval + 1) % 4;

        timeSpan.setStartInterval(startInterval);
        timeSpan.setEndYear(productYear);
        timeSpan.setEndDay(productDay + endDayOffset);
        timeSpan.setEndInterval(endInterval);
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
        tomsomiProductMap = new HashMap<String, Product>();
        ncepProductMap = new HashMap<String, Product>();
        utcCalendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
    }

    public static class TimeSpan {
        private int startYear;
        private int startDay;
        private int startInterval;
        private int endYear;
        private int endDay;
        private int endInterval;

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

        public int getStartInterval() {
            return startInterval;
        }

        public void setStartInterval(int startInterval) {
            this.startInterval = startInterval;
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

        public int getEndInterval() {
            return endInterval;
        }

        public void setEndInterval(int endInterval) {
            this.endInterval = endInterval;
        }
    }

}