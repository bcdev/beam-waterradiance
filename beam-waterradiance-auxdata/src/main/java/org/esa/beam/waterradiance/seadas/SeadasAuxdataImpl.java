package org.esa.beam.waterradiance.seadas;

import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.datamodel.PixelPos;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.util.math.MathUtils;
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

    private static final String OZONE_BAND_NAME = "Geophysical_Data/ozone";
    private static final String SURFACE_PRESSURE_BAND_NAME = "Geophysical_Data/press";

    private static final long MILLI_SECONDS_PER_DAY = 24 * 60 * 60 * 1000;
    private static final long HALF_MILLI_SECONDS_PER_DAY = MILLI_SECONDS_PER_DAY / 2;
    private static final long QUARTER_MILLI_SECONDS_PER_DAY = MILLI_SECONDS_PER_DAY / 4;
    private static final long EIGHTH_MILLI_SECONDS_PER_DAY = MILLI_SECONDS_PER_DAY / 8;
    private static final String[] NCEP_HOURS = {"00", "06", "12", "18"};

    private final Calendar utcCalendar;
    private final File auxDataDirectory;
    private final Map<String, Product> tomsomiProductMap;
    private final Map<String, Product> ncepProductMap;
    private final Map<String, Double> surfacePressureMap;
    private final Map<String, Double> ozoneMap;
    private Date date_of_last_TOMS_product = new GregorianCalendar(2005, 12, 31).getTime();
    private Date date_of_first_OMI_product = new GregorianCalendar(2006, 1, 1).getTime();

    public static SeadasAuxdataImpl create(String auxPath) throws IOException {
        final File auxDataDirectory = new File(auxPath);
        if (!auxDataDirectory.isDirectory()) {
            throw new IOException();
        }
        return new SeadasAuxdataImpl(auxDataDirectory);
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

        int dayOffset = getDayOffset(getHourOfDay());

        TimeSpan timeSpan = createTimeSpan(utcCalendar, dayOffset);
        timeSpan = adjustForOverlappingYears(timeSpan);

        final Product startProduct = getTOMSOMIProduct(timeSpan.getStartDay(), timeSpan.getStartYear());
        final Product endProduct = getTOMSOMIProduct(timeSpan.getEndDay(), timeSpan.getEndYear());

        final double startOzone = getOzone((float) lat, (float) lon, startProduct);
        final double endOzone = getOzone((float) lat, (float) lon, endProduct);

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

        final int startDayOffset = getStartDayOffset(getHourOfDay());
        final int endDayOffset = getEndDayOffset(getHourOfDay());

        TimeSpan timeSpan = createTimeSpan(utcCalendar, startDayOffset, endDayOffset);
        timeSpan = adjustForOverlappingYears(timeSpan);

        final Product startProduct = getNCEPProduct(timeSpan.getStartDay(), timeSpan.getStartYear(), timeSpan.getStartInterval());
        final Product endProduct = getNCEPProduct(timeSpan.getEndDay(), timeSpan.getEndYear(), timeSpan.getEndInterval());

        final double startSurfacePressure = getSurfacePressure((float) lat, (float) lon, startProduct);
        final double endSurfacePressure = getSurfacePressure((float) lat, (float) lon, endProduct);

        final double surfacePressure = (1.0 - fraction) * startSurfacePressure + fraction * endSurfacePressure;

        if (!surfacePressureMap.containsKey(id)) {
            surfacePressureMap.put(id, surfacePressure);
        }

        return surfacePressure;
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
        ozoneMap.clear();
        surfacePressureMap.clear();
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

    // package access for testing only tb 2013-10-01
    static String getTomsomiProductPath(String auxdataPath, int year, String dayString) {
        final StringBuilder stringBuilder = createPreFilledStringBuilder(auxdataPath, year, dayString);
        stringBuilder.append("//N");
        stringBuilder.append(year);
        stringBuilder.append(dayString);
        stringBuilder.append("00_O3_TOMSOMI_24h.hdf");
        return stringBuilder.toString();
    }

    // package access for testing only tb 2013-10-01
    static String getNCEPProductPath(String auxdataPath, int year, String dayString, String hourString) {
        final StringBuilder stringBuilder;
        stringBuilder = createPreFilledStringBuilder(auxdataPath, year, dayString);
        if (year > 2008) {
            stringBuilder.append("//S");
            stringBuilder.append(year);
            stringBuilder.append(dayString);
            stringBuilder.append(hourString);
            stringBuilder.append("_NCEP.MET");
        } else {
            stringBuilder.append("//N");
            stringBuilder.append(year);
            stringBuilder.append(dayString);
            stringBuilder.append(hourString);
            stringBuilder.append("_MET_NCEPN_6h.hdf");
        }
        return stringBuilder.toString();
    }

    private static StringBuilder createPreFilledStringBuilder(String auxdataPath, int year, String dayString) {
        final StringBuilder stringBuilder = new StringBuilder(128);
        stringBuilder.append(auxdataPath);
        stringBuilder.append("//");
        stringBuilder.append(year);
        stringBuilder.append("//");
        stringBuilder.append(dayString);
        return stringBuilder;
    }

    private double getOzone(float lat, float lon, Product product) throws IOException {
        final int xPos = MathUtils.floorInt(lat);
        final int yPos = MathUtils.floorInt(lon);
        final PixelPos pixelPos = getAuxPixelPos(xPos, yPos);
        if (product.getSceneRasterWidth() == 288) {
            pixelPos.setLocation(pixelPos.getX(), pixelPos.getY() * 0.8);
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
        final String productID = getProductId(year, dayInFittingLength);
        final Product product;
        if (tomsomiProductMap.containsKey(productID)) {
            product = tomsomiProductMap.get(productID);
        } else {
            final String productPath = getTomsomiProductPath(auxDataDirectory.getPath(), year, dayInFittingLength);
            try {
                final ProductReader productReader = ProductIO.getProductReader("NETCDF-CF");
                product = productReader.readProductNodes(new File(productPath), null);
            } catch (IOException e) {
                throw new IOException("Could not retrieve ozone for given day");
            }
            tomsomiProductMap.put(productID, product);
        }
        return product;
    }

    private Product getNCEPProduct(int day, int year, int hour) throws IOException {
        final String dayInFittingLength = getDayString(day);
        final String hourInFittingLength = NCEP_HOURS[hour];
        final String productID = getProductId(year, dayInFittingLength, hourInFittingLength);
        final Product product;
        if (ncepProductMap.containsKey(productID)) {
            product = ncepProductMap.get(productID);
        } else {
            final String productPath = getNCEPProductPath(auxDataDirectory.getPath(), year, dayInFittingLength, hourInFittingLength);
            try {
                final ProductReader productReader = ProductIO.getProductReader("NETCDF-CF");
                product = productReader.readProductNodes(new File(productPath), null);
            } catch (IOException e) {
                throw new IOException("Could not retrieve surface pressure for given day", e);
            }
            ncepProductMap.put(productID, product);
        }
        return product;
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
        final PixelPos pixelPos = getAuxPixelPos(yPos, xPos);
        return Double.parseDouble(product.getBand(SURFACE_PRESSURE_BAND_NAME).getPixelString((int) pixelPos.getX(), (int) pixelPos.getY()));
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

    static PixelPos getAuxPixelPos(int lat, int lon) {
        PixelPos pixelPos = new PixelPos();
        float pixelY = 180 - (lat + 90);
        float pixelX = lon + 180;
        pixelPos.setLocation(pixelX, pixelY);
        return pixelPos;
    }

    private SeadasAuxdataImpl(File auxDataDirectory) {
        this.auxDataDirectory = auxDataDirectory;
        tomsomiProductMap = new HashMap<String, Product>();
        ncepProductMap = new HashMap<String, Product>();
        utcCalendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        surfacePressureMap = new HashMap<String, Double>();
        ozoneMap = new HashMap<String, Double>();
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