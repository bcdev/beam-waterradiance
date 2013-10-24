package org.esa.beam.waterradiance.seadas;


import junit.framework.Assert;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SeadasAuxdataImplTest {

    private URL auxDirectoryURL;

    @Before
    public void setUp() {
        auxDirectoryURL = SeadasAuxdataImplTest.class.getResource("../../../../../auxiliary/seadas/anc");
    }

    @Test
    public void testGetOzoneWithInvalidValue() throws IOException {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(2005, Calendar.JUNE, 15, 6, 0, 0);
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
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(2005, Calendar.JUNE, 15, 18, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());

        try {
            final double ozone = seadasAuxdata.getOzone(calendar.getTime(), 38, -76);
            assertEquals(297.0, ozone, 1e-8);
        } catch (Exception unexpected) {
            fail("Auxdata Impl was not created although auxdata path was valid!" + unexpected.getMessage());
        } finally {
            seadasAuxdata.dispose();
        }
    }

    @Test
    public void testGetOzoneForProductBeforeNoon() throws IOException {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(2005, Calendar.JUNE, 16, 6, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());

        try {
            final double ozone = seadasAuxdata.getOzone(calendar.getTime(), 38, -76);
            assertEquals(309.0, ozone, 1e-8);
        } catch (Exception unexpected) {
            fail("Auxdata Impl was not created although auxdata path was valid!" + unexpected.getMessage());
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
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);

        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(2005, Calendar.JUNE, 15, 12, 0, 0);
        double ozone = seadasAuxdata.getOzone(calendar.getTime(), 2, 0);
        Assert.assertEquals(261, ozone, 1e-8);

        calendar.set(2008, Calendar.NOVEMBER, 10, 0, 0, 0);
        ozone = seadasAuxdata.getOzone(calendar.getTime(), 2, 0);
        Assert.assertEquals(260, ozone, 1e-8);

        calendar.set(2012, Calendar.APRIL, 6, 12, 0, 0);
        ozone = seadasAuxdata.getOzone(calendar.getTime(), 2, 0);
        Assert.assertEquals(259, ozone, 1e-8);
    }

    @Test
    public void testGetSurfacePressureWithInvalidValue() throws IOException {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(2005, Calendar.JUNE, 15, 1, 30, 0);
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
    public void testGetSurfacePressure() throws IOException {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(Calendar.MILLISECOND, 0);
        final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());

        try {
            calendar.set(2005, Calendar.JUNE, 16, 6, 0, 0);
            double surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), 39, 2);
            assertEquals(1021.36, surfacePressure, 1e-8);

            calendar.set(2005, Calendar.JUNE, 16, 1, 30, 0);
            surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), -8, -34);
            assertEquals(1015.49, surfacePressure, 1e-8);

            calendar.set(2005, Calendar.JUNE, 15, 22, 30, 0);
            surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), -8, -34);
            assertEquals(1014.49, surfacePressure, 1e-8);

            calendar.set(2005, Calendar.JUNE, 16, 10, 30, 0);
            surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), -2, -21);
            assertEquals(1014.2, surfacePressure, 1e-8);

            calendar.set(2005, Calendar.JUNE, 16, 19, 30, 0);
            surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), 4, -2);
            assertEquals(1013.28, surfacePressure, 1e-8);

        } catch (Exception unexpected) {
            fail("Auxdata Impl was not created although auxdata path was valid!" + unexpected.getMessage());
        } finally {
            seadasAuxdata.dispose();
        }
    }

    @Test
    public void testGetSurfacePressureAfter2008() throws IOException {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(Calendar.MILLISECOND, 0);
        final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());

        try {
            calendar.set(2012, Calendar.APRIL, 6, 18, 0, 0);
            double surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), 57, -33);
            assertEquals(1023.71218, surfacePressure, 1e-8);
        } catch (Exception unexpected) {
            fail("Auxdata Impl was not created although auxdata path was valid!" + unexpected.getMessage());
        } finally {
            seadasAuxdata.dispose();
        }
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
        Assert.assertEquals(1012.17, surfacePressure, 1e-8);

        calendar.set(2008, Calendar.NOVEMBER, 10, 6, 0, 0);
        surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), 10, -9);
        Assert.assertEquals(1011.17, surfacePressure, 1e-8);

        calendar.set(2012, Calendar.APRIL, 6, 15, 0, 0);
        surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), 10, -9);
        Assert.assertEquals(1010.17, surfacePressure, 1e-8);
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

    private Product getProduct(String productString) throws IOException {
        File auxDirectory = new File(auxDirectoryURL.getPath());
        ProductReader productReader = ProductIO.getProductReader("NETCDF-CF");
        String tomsomiStartProductPath = auxDirectory.getPath() + productString;
        return productReader.readProductNodes(new File(tomsomiStartProductPath), null);
    }

}
