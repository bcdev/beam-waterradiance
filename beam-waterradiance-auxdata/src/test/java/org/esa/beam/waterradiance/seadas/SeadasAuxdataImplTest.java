package org.esa.beam.waterradiance.seadas;


import junit.framework.Assert;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.VirtualBand;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.fail;
import static org.junit.Assert.*;

@SuppressWarnings("MagicConstant")
public class SeadasAuxdataImplTest {

    private URL auxDirectoryURL;

    public SeadasAuxdataImplTest() {
        auxDirectoryURL = SeadasAuxdataImplTest.class.getResource("../../../../../auxiliary/seadas/anc");
        assertNotNull(auxDirectoryURL);
    }

    @Test
    public void testGetOzoneWithInvalidValue() throws IOException {
        final Calendar calendar = createCalendar(2005, Calendar.JUNE, 15, 6);
        final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());

        try {
            seadasAuxdata.getOzone(calendar.getTime(), 83, 52);
            fail("Exception expected");
        } catch (Exception expected) {
            assertEquals("Could not retrieve ozone for given day", expected.getMessage());
        } finally {
            seadasAuxdata.dispose();
        }
    }

    @Test
    public void testGetOzoneForProductAfterNoon() throws IOException {
        final Calendar calendar = createCalendar(2005, Calendar.JUNE, 15, 18);
        final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());

        try {
            final double ozone = seadasAuxdata.getOzone(calendar.getTime(), -38, -76);
            assertEquals(298.68766021728516, ozone, 1e-8);
        } catch (Exception unexpected) {
            fail("Auxdata Impl was not created although auxdata path was valid!" + unexpected.getMessage());
        } finally {
            seadasAuxdata.dispose();
        }
    }

    @Test
    public void testGetOzoneForProductBeforeNoon() throws IOException {
        final Calendar calendar = createCalendar(2005, Calendar.JUNE, 16, 6);
        final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());

        try {
            final double ozone = seadasAuxdata.getOzone(calendar.getTime(), -38, -76);
            assertEquals(312.5002975463867, ozone, 1e-8);
        } finally {
            seadasAuxdata.dispose();
        }
    }

    @Test
    public void testGetOzoneWhenInterpolationDatesAreFarApart() throws IOException {
        final Product tomsomiStartProduct = getProduct("/2005/166/N200516600_O3_TOMSOMI_24h.hdf");
        final Product tomsomiEndProduct = getProduct("/2012/097/N201209700_O3_TOMSOMI_24h.hdf");
        final Product ncepStartProduct = getProduct("/2005/166/N200516618_MET_NCEPN_6h.hdf");
        final Product ncepEndProduct = getProduct("/2012/097/S201209712_NCEP.MET");
        final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(tomsomiStartProduct, tomsomiEndProduct,
                ncepStartProduct, ncepEndProduct);
        Calendar calendar = createCalendar(2005, Calendar.JUNE, 15, 12);
        double ozone = seadasAuxdata.getOzone(calendar.getTime(), -2, 0);
        Assert.assertEquals(261.5, ozone, 1e-8);

        calendar = createCalendar(2008, Calendar.NOVEMBER, 10, 0);
        ozone = seadasAuxdata.getOzone(calendar.getTime(), -2, 0);
        Assert.assertEquals(260.125, ozone, 1e-8);

        calendar = createCalendar(2012, Calendar.APRIL, 6, 12);
        ozone = seadasAuxdata.getOzone(calendar.getTime(), -2, 0);
        Assert.assertEquals(258.75, ozone, 1e-8);
    }

    @Test
    public void testGetOzone_invalidLonLat() throws IOException {
        final Calendar calendar = createCalendar(2005, Calendar.JUNE, 16, 6);
        final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());

        double ozone;
        try {
            ozone = seadasAuxdata.getOzone(calendar.getTime(), 91, -76);
            assertTrue(Double.isNaN(ozone));

            ozone = seadasAuxdata.getOzone(calendar.getTime(), -28, 182);
            assertTrue(Double.isNaN(ozone));
        } finally {
            seadasAuxdata.dispose();
        }
    }

    @Test
    public void testGetSurfacePressure_auxDataFileNotAccessible() throws IOException {
        final Calendar calendar = createCalendar(2005, Calendar.JUNE, 15, 1);

        final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());
        try {
            seadasAuxdata.getSurfacePressure(calendar.getTime(), 83, 52);
            fail("Exception expected");
        } catch (Exception expected) {
            assertEquals("Could not retrieve surface pressure for given day", expected.getMessage());
        } finally {
            seadasAuxdata.dispose();
        }
    }

    @Test
    public void testGetSurfacePressure() throws Exception {
        final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());
        try {
            Calendar calendar = createCalendar(2005, Calendar.JUNE, 16, 6);
            double surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), 39.5, 2.5);
            assertEquals(1021.364990234375, surfacePressure, 1e-8);

            calendar = createCalendar(2005, Calendar.JUNE, 16, 1);
            surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), -8, -34);
            assertEquals(1015.249979654948, surfacePressure, 1e-8);

            calendar = createCalendar(2005, Calendar.JUNE, 15, 22);
            surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), -8, -34);
            assertEquals(1014.1324564615885, surfacePressure, 1e-8);

            calendar = createCalendar(2005, Calendar.JUNE, 16, 10);
            surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), -2, -21);
            assertEquals(1014.0233154296875, surfacePressure, 1e-8);

            calendar = createCalendar(2005, Calendar.JUNE, 16, 19);
            surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), 4, -2);
            assertEquals(1013.5266723632812, surfacePressure, 1e-8);
        } finally {
            seadasAuxdata.dispose();
        }
    }

    @Test
    public void testGetSurfacePressureAfter2008() throws Exception {
        final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());
        try {
            Calendar calendar = createCalendar(2012, Calendar.APRIL, 6, 18);
            double surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), 57, -33);
            assertEquals(1022.4712829589844, surfacePressure, 1e-8);
        } finally {
            seadasAuxdata.dispose();
        }
    }

    private Calendar createCalendar(int year, int month, int dayOfMonth, int hourOfDay) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(year, month, dayOfMonth, hourOfDay, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    @Test
    public void testGetSurfacePressureWhenInterpolationDatesAreFarApart() throws Exception {
        final Product tomsomiStartProduct = getProduct("/2005/166/N200516600_O3_TOMSOMI_24h.hdf");
        final Product tomsomiEndProduct = getProduct("/2012/097/N201209700_O3_TOMSOMI_24h.hdf");
        final Product ncepStartProduct = getProduct("/2005/166/N200516618_MET_NCEPN_6h.hdf");
        final Product ncepEndProduct = getProduct("/2012/097/S201209712_NCEP.MET");
        final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(tomsomiStartProduct, tomsomiEndProduct,
                ncepStartProduct, ncepEndProduct);
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);

        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(2005, Calendar.JUNE, 15, 21, 0, 0);
        double surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), 10, -9);
        Assert.assertEquals(1012.0399780273438, surfacePressure, 1e-8);

        calendar.set(2008, Calendar.NOVEMBER, 10, 6, 0, 0);
        surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), 10, -9);
        Assert.assertEquals(1011.0234680175781, surfacePressure, 1e-8);

        calendar.set(2012, Calendar.APRIL, 6, 15, 0, 0);
        surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), 10, -9);
        Assert.assertEquals(1010.0069580078125, surfacePressure, 1e-8);
    }

    @Test
    public void testGetDateFraction_varyHour() throws IOException {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        final Product tomsomiStartProduct = getProduct("/2005/166/N200516600_O3_TOMSOMI_24h.hdf");
        final Product tomsomiEndProduct = getProduct("/2005/167/N200516700_O3_TOMSOMI_24h.hdf");

        calendar.set(2005, Calendar.JUNE, 15, 12, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(0.0, SeadasAuxdataImpl.getDateFraction(calendar, 0.5, tomsomiStartProduct, tomsomiEndProduct), 1e-8);

        calendar.set(2005, Calendar.JUNE, 16, 6, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(0.75, SeadasAuxdataImpl.getDateFraction(calendar, 0.5, tomsomiStartProduct, tomsomiEndProduct), 1e-8);

        calendar.set(2005, Calendar.JUNE, 16, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(0.5, SeadasAuxdataImpl.getDateFraction(calendar, 0.5, tomsomiStartProduct, tomsomiEndProduct), 1e-8);

        calendar.set(2005, Calendar.JUNE, 15, 18, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(0.25, SeadasAuxdataImpl.getDateFraction(calendar, 0.5, tomsomiStartProduct, tomsomiEndProduct), 1e-8);

        calendar.set(2005, Calendar.JUNE, 15, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        assertEquals(0.49999998842592597, SeadasAuxdataImpl.getDateFraction(calendar, 0.5, tomsomiStartProduct, tomsomiEndProduct), 1e-8);    // almost 0.5 :-)

        calendar.set(2005, Calendar.JUNE, 16, 11, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        assertEquals(0.9999999884259259, SeadasAuxdataImpl.getDateFraction(calendar, 0.5, tomsomiStartProduct, tomsomiEndProduct), 1e-8); // almost 1
    }

    @Test
    public void testGetDateFraction_varyDays() throws IOException {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);

        Product startProduct = new Product("startProduct", "anyType", 1, 1);
        Calendar startCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        startCalendar.set(2006, Calendar.AUGUST, 6, 0, 0, 0);
        startProduct.setStartTime(ProductData.UTC.create(startCalendar.getTime(), 0));
        Product endProduct = new Product("endProduct", "anyType", 1, 1);
        Calendar endCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        endCalendar.set(2006, Calendar.AUGUST, 7, 0, 0, 0);
        endProduct.setStartTime(ProductData.UTC.create(endCalendar.getTime(), 0));

        calendar.set(2006, Calendar.AUGUST, 7, 11, 52, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(0.9944444444444445, SeadasAuxdataImpl.getDateFraction(calendar, 0.5, startProduct, endProduct), 1e-8);

        startProduct = new Product("startProduct", "anyType", 1, 1);
        startCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        startCalendar.set(2009, Calendar.JANUARY, 26, 0, 0, 0);
        startProduct.setStartTime(ProductData.UTC.create(startCalendar.getTime(), 0));
        endProduct = new Product("endProduct", "anyType", 1, 1);
        endCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        endCalendar.set(2009, Calendar.JANUARY, 27, 0, 0, 0);
        endProduct.setStartTime(ProductData.UTC.create(endCalendar.getTime(), 0));
        calendar.set(2009, Calendar.JANUARY, 27, 11, 52, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(0.9944444444444445, SeadasAuxdataImpl.getDateFraction(calendar, 0.5, startProduct, endProduct), 1e-8);
    }

    @Test
    public void testGetDateFraction_varyOffset() throws IOException {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);

        final Product startProduct = new Product("startProduct", "anyType", 1, 1);
        final Calendar startCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        startCalendar.set(2006, Calendar.AUGUST, 7, 0, 0, 0);
        startProduct.setStartTime(ProductData.UTC.create(startCalendar.getTime(), 0));
        final Product endProduct = new Product("endProduct", "anyType", 1, 1);
        final Calendar endCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        endCalendar.set(2006, Calendar.AUGUST, 8, 0, 0, 0);
        endProduct.setStartTime(ProductData.UTC.create(endCalendar.getTime(), 0));

        calendar.set(2006, Calendar.AUGUST, 7, 17, 22, 11);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(0.22373842592592597, SeadasAuxdataImpl.getDateFraction(calendar, 0.5, startProduct, endProduct), 1e-8);

        assertEquals(0.52373842592592597, SeadasAuxdataImpl.getDateFraction(calendar, 0.2, startProduct, endProduct), 1e-8);

        assertEquals(0.0237384375, SeadasAuxdataImpl.getDateFraction(calendar, 0.7, startProduct, endProduct), 1e-8);
    }

    @Test
    public void testGetDateFractionWhenInterpolationDatesAreFarApart() throws IOException {
        final Product tomsomiStartProduct = getProduct("/2005/166/N200516600_O3_TOMSOMI_24h.hdf");
        final Product tomsomiEndProduct = getProduct("/2012/097/N201209700_O3_TOMSOMI_24h.hdf");
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);

        calendar.set(2005, Calendar.JUNE, 15, 12, 0, 0);
        assertEquals(0.0, SeadasAuxdataImpl.getDateFraction(calendar, 0.5, tomsomiStartProduct, tomsomiEndProduct), 1e-8);

        calendar.set(2008, Calendar.NOVEMBER, 10, 0, 0, 0);
        assertEquals(0.5, SeadasAuxdataImpl.getDateFraction(calendar, 0.5, tomsomiStartProduct, tomsomiEndProduct), 1e-8);

        calendar.set(2012, Calendar.APRIL, 6, 12, 0, 0);
        assertEquals(1.0, SeadasAuxdataImpl.getDateFraction(calendar, 0.5, tomsomiStartProduct, tomsomiEndProduct), 1e-8);
    }

    @Test
    public void testGetTime() throws IOException {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);

        final Product tomsomiStartProduct = getProduct("/2005/166/N200516600_O3_TOMSOMI_24h.hdf");
        calendar.set(2005, Calendar.JUNE, 16, 0, 0, 0);
        Calendar time = SeadasAuxdataImpl.getTime(tomsomiStartProduct);
        assertEquals(0, calendar.compareTo(time), 10);

        final Product ncepStartProduct = getProduct("/2005/166/N200516618_MET_NCEPN_6h.hdf");
        calendar.set(2005, Calendar.JUNE, 15, 18, 0, 0);
        time = SeadasAuxdataImpl.getTime(ncepStartProduct);
        assertEquals(0, calendar.compareTo(time), 10);

        final Product ncepEndProduct = getProduct("/2012/097/S201209712_NCEP.MET");
        calendar.set(2012, Calendar.APRIL, 6, 12, 0, 0);
        time = SeadasAuxdataImpl.getTime(ncepEndProduct);
        assertEquals(0, calendar.compareTo(time), 10);
    }

    @Test
    public void testIsLeapYear() {
        assertFalse(SeadasAuxdataImpl.isLeapYear(1999));
        assertFalse(SeadasAuxdataImpl.isLeapYear(2000));
        assertTrue(SeadasAuxdataImpl.isLeapYear(2004));
        assertFalse(SeadasAuxdataImpl.isLeapYear(2006));
        assertFalse(SeadasAuxdataImpl.isLeapYear(2007));
        assertTrue(SeadasAuxdataImpl.isLeapYear(2008));
    }

    @Test
    public void testLinearInterpolation() {
        Band band = new Band("testBand", ProductData.TYPE_FLOAT32, 4, 4);
        BufferedImage sourceImage = new BufferedImage(4, 4, BufferedImage.TYPE_USHORT_GRAY);
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                sourceImage.getRaster().setSample(x, y, 0, x + (y * 4));
            }
        }
        band.setSourceImage(sourceImage);

        assertEquals(0, SeadasAuxdataImpl.interpolate(band, 0.5, 0.5), 1e-8);
        assertEquals(9, SeadasAuxdataImpl.interpolate(band, 1.5, 2.5), 1e-8);
        assertEquals(2.5, SeadasAuxdataImpl.interpolate(band, 1.0, 1.0), 1e-8);
        assertEquals(5.5, SeadasAuxdataImpl.interpolate(band, 2.0, 1.5), 1e-8);
        assertEquals(11.161919593811035, SeadasAuxdataImpl.interpolate(band, 2.75, 2.75), 1e-8);
    }

    private Product getProduct(String productString) throws IOException {
        File auxDirectory = new File(auxDirectoryURL.getPath());
        ProductReader productReader = ProductIO.getProductReader("NETCDF-CF");
        String tomsomiStartProductPath = auxDirectory.getPath() + productString;
        return productReader.readProductNodes(new File(tomsomiStartProductPath), null);
    }

}
