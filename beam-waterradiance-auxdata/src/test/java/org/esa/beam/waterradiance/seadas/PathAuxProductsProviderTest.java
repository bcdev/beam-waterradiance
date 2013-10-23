package org.esa.beam.waterradiance.seadas;

import org.esa.beam.framework.datamodel.Product;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertEquals;

public class PathAuxProductsProviderTest {

    private String auxDirectoryURL;

    @Before
    public void setUp() throws Exception {
        auxDirectoryURL = PathAuxProductsProviderTest.class.getResource("../../../../../auxiliary/seadas/anc").getPath();
    }

    @Test
    public void testCreateWithInvalidAuxPath() {
        String invalidAuxPath = "invalid";
        try {
            PathAuxProductsProvider pathAuxProductsProvider = new PathAuxProductsProvider(invalidAuxPath);
            fail("Auxdata Impl was created although auxdata path was invalid!");
        } catch (Exception expected) {
            //expected
        }
    }

    @Test
    public void testCreateWithValidAuxPath() {
        assertNotNull(auxDirectoryURL);
        try {
            PathAuxProductsProvider pathAuxProductsProvider = new PathAuxProductsProvider(auxDirectoryURL);
            assertNotNull(pathAuxProductsProvider);
        } catch (Exception unexpected) {
            fail("Auxdata Impl was not created although auxdata path was valid!" + unexpected.getMessage());
        }
    }

    @Test
    public void testGetTOMSOMIProducts() {
        try {
            PathAuxProductsProvider pathAuxProductsProvider = new PathAuxProductsProvider(auxDirectoryURL);
            final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
            calendar.set(2005, Calendar.JUNE, 16, 6, 0, 0);
            final SeadasAuxDataProducts tomsomiProducts = pathAuxProductsProvider.getTOMSOMIProducts(calendar.getTime());
            final Product startProduct = tomsomiProducts.getStartProduct();
            final Product endProduct = tomsomiProducts.getEndProduct();
            assertNotNull(startProduct);
            assertNotNull(endProduct);
            assertEquals(startProduct.getName(), "N200516600_O3_TOMSOMI_24h");
            assertEquals(endProduct.getName(), "N200516700_O3_TOMSOMI_24h");
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetNCEPProducts() {
        try {
            PathAuxProductsProvider pathAuxProductsProvider = new PathAuxProductsProvider(auxDirectoryURL);
            final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
            calendar.set(2005, Calendar.JUNE, 16, 0, 0, 0);
            final SeadasAuxDataProducts necpProducts = pathAuxProductsProvider.getNCEPProducts(calendar.getTime());
            final Product startProduct = necpProducts.getStartProduct();
            final Product endProduct = necpProducts.getEndProduct();
            assertNotNull(startProduct);
            assertNotNull(endProduct);
            assertEquals(startProduct.getName(), "N200516618_MET_NCEPN_6h");
            assertEquals(endProduct.getName(), "N200516700_MET_NCEPN_6h");
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetDayOffset() {
        assertEquals(-1, PathAuxProductsProvider.getDayOffset(0));
        assertEquals(-1, PathAuxProductsProvider.getDayOffset(11));
        assertEquals(0, PathAuxProductsProvider.getDayOffset(12));
        assertEquals(0, PathAuxProductsProvider.getDayOffset(23));
    }

    @Test
    public void testGetStartDayOffset() {
        assertEquals(-1, PathAuxProductsProvider.getStartDayOffset(0));
        assertEquals(0, PathAuxProductsProvider.getStartDayOffset(11));
        assertEquals(0, PathAuxProductsProvider.getStartDayOffset(12));
        assertEquals(0, PathAuxProductsProvider.getStartDayOffset(23));
    }

    @Test
    public void testGetEndDayOffset() {
        assertEquals(0, PathAuxProductsProvider.getEndDayOffset(0));
        assertEquals(0, PathAuxProductsProvider.getEndDayOffset(11));
        assertEquals(0, PathAuxProductsProvider.getEndDayOffset(12));
        assertEquals(1, PathAuxProductsProvider.getEndDayOffset(23));
    }

    @Test
    public void testGetDayString() {
        assertEquals("000", PathAuxProductsProvider.getDayString(0));
        assertEquals("009", PathAuxProductsProvider.getDayString(9));
        assertEquals("010", PathAuxProductsProvider.getDayString(10));
        assertEquals("099", PathAuxProductsProvider.getDayString(99));
        assertEquals("100", PathAuxProductsProvider.getDayString(100));
        assertEquals("114", PathAuxProductsProvider.getDayString(114));
    }

    @Test
    public void testGetTOMSOMIProductPath() {
        assertEquals("auxdata//2008//005//N200800500_O3_TOMSOMI_24h.hdf",
                     PathAuxProductsProvider.getTomsomiProductPath("auxdata", 2008, "005"));
        assertEquals("seadas//2010//116//N201011600_O3_TOMSOMI_24h.hdf",
                     PathAuxProductsProvider.getTomsomiProductPath("seadas", 2010, "116"));
    }

    @Test
    public void testGetNCEPProductPath() {
        assertEquals("auxdata//2008//005//N200800523_MET_NCEPN_6h.hdf",
                     PathAuxProductsProvider.getNCEPProductPath("auxdata", 2008, "005", "23"));
        assertEquals("seadas//2010//116//S201011604_NCEP.MET",
                     PathAuxProductsProvider.getNCEPProductPath("seadas", 2010, "116", "04"));
    }

    @Test
    public void testCreateTimeSpan() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(2007, Calendar.SEPTEMBER, 17, 17, 22, 11);

        final PathAuxProductsProvider.TimeSpan timeSpan = PathAuxProductsProvider.createTimeSpan(calendar, 0);
        assertNotNull(timeSpan);
        assertEquals(2007, timeSpan.getStartYear());
        assertEquals(260, timeSpan.getStartDay());
        assertEquals(2007, timeSpan.getEndYear());
        assertEquals(261, timeSpan.getEndDay());
    }

    @Test
    public void testCreateTimeSpan_varyOffset() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(2008, Calendar.OCTOBER, 18, 17, 22, 11);

        final PathAuxProductsProvider.TimeSpan timeSpan = PathAuxProductsProvider.createTimeSpan(calendar, -1);
        assertNotNull(timeSpan);
        assertEquals(2008, timeSpan.getStartYear());
        assertEquals(291, timeSpan.getStartDay());
        assertEquals(2008, timeSpan.getEndYear());
        assertEquals(292, timeSpan.getEndDay());
    }

    @Test
    public void testCreateTimeSpanForSurfacePressure() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(2007, Calendar.SEPTEMBER, 17, 17, 22, 11);

        final PathAuxProductsProvider.TimeSpan timeSpan = PathAuxProductsProvider.createTimeSpan(calendar, 0, 0);
        assertNotNull(timeSpan);
        assertEquals(2007, timeSpan.getStartYear());
        assertEquals(260, timeSpan.getStartDay());
        assertEquals(2, timeSpan.getStartInterval());
        assertEquals(2007, timeSpan.getEndYear());
        assertEquals(260, timeSpan.getEndDay());
        assertEquals(3, timeSpan.getEndInterval());
    }

    @Test
    public void testCreateTimeSpanForSurfacePressureWithDateOverlap_1() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(2007, Calendar.SEPTEMBER, 17, 2, 2, 11);

        final PathAuxProductsProvider.TimeSpan timeSpan = PathAuxProductsProvider.createTimeSpan(calendar, -1, 0);
        assertNotNull(timeSpan);
        assertEquals(2007, timeSpan.getStartYear());
        assertEquals(259, timeSpan.getStartDay());
        assertEquals(3, timeSpan.getStartInterval());
        assertEquals(2007, timeSpan.getEndYear());
        assertEquals(260, timeSpan.getEndDay());
        assertEquals(0, timeSpan.getEndInterval());
    }

    @Test
    public void testCreateTimeSpanForSurfacePressureWithDateOverlap_2() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(2007, Calendar.SEPTEMBER, 17, 22, 22, 11);

        final PathAuxProductsProvider.TimeSpan timeSpan = PathAuxProductsProvider.createTimeSpan(calendar, 0, 1);
        assertNotNull(timeSpan);
        assertEquals(2007, timeSpan.getStartYear());
        assertEquals(260, timeSpan.getStartDay());
        assertEquals(3, timeSpan.getStartInterval());
        assertEquals(2007, timeSpan.getEndYear());
        assertEquals(261, timeSpan.getEndDay());
        assertEquals(0, timeSpan.getEndInterval());
    }

    @Test
    public void testAdjustForOverlappingYears_startYear() {
        PathAuxProductsProvider.TimeSpan timeSpan = new PathAuxProductsProvider.TimeSpan();
        timeSpan.setStartDay(0);
        timeSpan.setStartYear(2006);

        timeSpan = PathAuxProductsProvider.adjustForOverlappingYears(timeSpan);
        assertEquals(365, timeSpan.getStartDay());
        assertEquals(2005, timeSpan.getStartYear());
    }

    @Test
    public void testAdjustForOverlappingYears_startYear_leapYear() {
        PathAuxProductsProvider.TimeSpan timeSpan = new PathAuxProductsProvider.TimeSpan();
        timeSpan.setStartDay(0);
        timeSpan.setStartYear(2005);

        timeSpan = PathAuxProductsProvider.adjustForOverlappingYears(timeSpan);
        assertEquals(366, timeSpan.getStartDay());
        assertEquals(2004, timeSpan.getStartYear());
    }

    @Test
    public void testAdjustForOverlappingYears_endYear() {
        PathAuxProductsProvider.TimeSpan timeSpan = new PathAuxProductsProvider.TimeSpan();
        timeSpan.setStartDay(365);
        timeSpan.setEndDay(366);
        timeSpan.setEndYear(2007);

        timeSpan = PathAuxProductsProvider.adjustForOverlappingYears(timeSpan);
        assertEquals(1, timeSpan.getEndDay());
        assertEquals(2008, timeSpan.getEndYear());
    }

    @Test
    public void testAdjustForOverlappingYears_endYear_leapYear() {
        PathAuxProductsProvider.TimeSpan timeSpan = new PathAuxProductsProvider.TimeSpan();
        timeSpan.setStartDay(365);
        timeSpan.setEndDay(367);
        timeSpan.setEndYear(2000);

        timeSpan = PathAuxProductsProvider.adjustForOverlappingYears(timeSpan);
        assertEquals(1, timeSpan.getEndDay());
        assertEquals(2001, timeSpan.getEndYear());
    }

    @Test
    public void testAdjustForOverlappingYears_noAdjustments() {
        PathAuxProductsProvider.TimeSpan timeSpan = new PathAuxProductsProvider.TimeSpan();
        timeSpan.setStartDay(219);
        timeSpan.setStartYear(2012);
        timeSpan.setEndDay(220);
        timeSpan.setEndYear(2012);

        timeSpan = PathAuxProductsProvider.adjustForOverlappingYears(timeSpan);
        assertEquals(219, timeSpan.getStartDay());
        assertEquals(2012, timeSpan.getStartYear());
        assertEquals(220, timeSpan.getEndDay());
        assertEquals(2012, timeSpan.getEndYear());
    }

    @Test
    public void testGetProductId() {
        assertEquals("2012005", SeadasAuxdataImpl.getProductId(2012, "005"));
        assertEquals("2003178", SeadasAuxdataImpl.getProductId(2003, "178"));
    }

    @Test
    public void testGetProductId_withHour() {
        assertEquals("201200505", SeadasAuxdataImpl.getProductId(2012, "005", "05"));
        assertEquals("200317818", SeadasAuxdataImpl.getProductId(2003, "178", "18"));
    }

}