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
    private final static long milli_seconds_per_day = 24 * 60 * 60 * 1000;
    private final static String ozone_band_name = "Geophysical Data/ozone";
    private static final Calendar utcCalendar= GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);

    private SeadasAuxdataImpl(File auxDataDirectory) {
        this.auxDataDirectory = auxDataDirectory;
    }

    @Override
    public double getOzone(Date date, double lat, double lon) throws Exception {

        //@todo try to do most of the work in the constructor
        Calendar calendar = ProductData.UTC.create(date, 0).getAsCalendar();
        double fraction = getDateFraction(date, 0.5);
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

        final double firstOzone = getOzone((float) lat, lon, firstDay, firstYear);
        final double secondOzone = getOzone((float) lat, lon, secondDay, secondYear);
        return (1 - fraction) * firstOzone + fraction * secondOzone;
    }

    private double getOzone(float lat, double lon, int day, int year) throws IOException {
        //@todo ensure that each product is only read once for one operator call
        //@todo consider case when day is < 100
        String productPath = auxDataDirectory.getPath() +
                "//" + year + "//" + day + "//N" + year + day + "00_O3_TOMSOMI_24h.hdf";
        final Product product = ProductIO.readProduct(new File(productPath));
        PixelPos pixelPos = new PixelPos(lat, (float) lon);
        if (product.getSceneRasterWidth() == 288) {
            pixelPos = new PixelPos(lat, (float) (lon * 0.8));
        }
        return Double.parseDouble(product.getBand(ozone_band_name).getPixelString((int) pixelPos.getX(), (int) pixelPos.getY()));
    }

    static double getDateFraction(Date date, double fractionOffset) {
        //@todo this can be done better
//        final long productTimeInMillis = date.getTime();
//        final long millisOnProductDay = productTimeInMillis % milli_seconds_per_day;
        utcCalendar.clear();
        utcCalendar.setTime(date);
        int millisOnProductDay = utcCalendar.get(Calendar.MILLISECOND);
        millisOnProductDay += 1000.0 * utcCalendar.get(Calendar.SECOND);
        millisOnProductDay += 60000.0 * utcCalendar.get(Calendar.MINUTE);
        millisOnProductDay += 3600000.0 * utcCalendar.get(Calendar.HOUR_OF_DAY);
        double fraction;
        if (millisOnProductDay < (milli_seconds_per_day / 2)) {
            fraction = fractionOffset + ((double) millisOnProductDay / milli_seconds_per_day);
        } else {
            fraction = -fractionOffset + ((double) millisOnProductDay / milli_seconds_per_day);
        }
        return fraction;
    }

    @Override
    public double getSurfacePressure(Date date, double lat, double lon) throws Exception {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public static SeadasAuxdataImpl create(String auxPath) throws IOException {
        final File auxDataDirectory = new File(auxPath);
        if (!auxDataDirectory.isDirectory()) {
            throw new IOException();
        }
        return new SeadasAuxdataImpl(auxDataDirectory);
    }
}
