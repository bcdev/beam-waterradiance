package org.esa.beam.waterradiance.seadas;

import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.datamodel.Product;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

class PathAuxProductsProvider implements AuxProductsProvider {

    private final File auxDataDirectory;
    private final Calendar utcCalendar;
    private final Map<String, Product> tomsomiProductMap;
    private final Map<String, Product> ncepProductMap;
    private static final String[] NCEP_HOURS = {"00", "06", "12", "18"};

    PathAuxProductsProvider(String auxPath) throws IOException {
        auxDataDirectory = new File(auxPath);
        if (!auxDataDirectory.isDirectory()) {
            throw new IOException();
        }
        utcCalendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        tomsomiProductMap = new HashMap<String, Product>();
        ncepProductMap = new HashMap<String, Product>();
    }

    @Override
    public SeadasAuxDataProducts getTOMSOMIProducts(Date date) throws IOException {
        setCalendar(date);

        int dayOffset = getDayOffset(getHourOfDay());

        TimeSpan timeSpan = createTimeSpan(utcCalendar, dayOffset);
        timeSpan = adjustForOverlappingYears(timeSpan);

        final Product startProduct = getTOMSOMIProduct(timeSpan.getStartDay(), timeSpan.getStartYear());
        final Product endProduct = getTOMSOMIProduct(timeSpan.getEndDay(), timeSpan.getEndYear());

        return new SeadasAuxDataProducts(startProduct, endProduct);

    }

    @Override
    public SeadasAuxDataProducts getNCEPProducts(Date date) throws IOException {
        setCalendar(date);

        final int startDayOffset = getStartDayOffset(getHourOfDay());
        final int endDayOffset = getEndDayOffset(getHourOfDay());

        TimeSpan timeSpan = createTimeSpan(utcCalendar, startDayOffset, endDayOffset);
        timeSpan = adjustForOverlappingYears(timeSpan);

        final Product startProduct = getNCEPProduct(timeSpan.getStartDay(), timeSpan.getStartYear(), timeSpan.getStartInterval());
        final Product endProduct = getNCEPProduct(timeSpan.getEndDay(), timeSpan.getEndYear(), timeSpan.getEndInterval());

        return new SeadasAuxDataProducts(startProduct, endProduct);
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
                throw new IOException("Could not retrieve surface pressure for given day");
            }
            ncepProductMap.put(productID, product);
        }
        return product;
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

    private void setCalendar(Date date) {
        utcCalendar.clear();
        utcCalendar.setTime(date);
    }

    static int getDayOffset(int hourOfDay) {
        int dayOffset = 0;
        if (hourOfDay < 12) {
            dayOffset = -1;
        }
        return dayOffset;
    }

    private int getHourOfDay() {
        return utcCalendar.get(Calendar.HOUR_OF_DAY);
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
