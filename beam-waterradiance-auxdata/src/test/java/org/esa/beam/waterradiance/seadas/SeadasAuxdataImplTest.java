package org.esa.beam.waterradiance.seadas;


import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static junit.framework.Assert.*;

public class SeadasAuxdataImplTest {

    private URL auxDirectoryURL;

    @Before
    public void setUp() {
        auxDirectoryURL = SeadasAuxdataImplTest.class.getResource("../../../../../auxiliary/seadas/anc");
    }

    @Test
    public void testCreateWithInvalidAuxPath() {
        String invalidAuxPath = "invalid";
        try {
            SeadasAuxdataImpl.create(invalidAuxPath);
            fail("Auxdata Impl was created although auxdata path was invalid!");
        } catch (Exception expected) {
            //expected
        }
    }

    @Test
    public void testCreateWithValidAuxPath() {
        assertNotNull(auxDirectoryURL);
        final String auxDirectoryPath = auxDirectoryURL.getPath();
        try {
            SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryPath);
            assertNotNull(seadasAuxdata);
        } catch (Exception unexpected) {
            fail("Auxdata Impl was not created although auxdata path was valid!" + unexpected.getMessage());
        }
    }

//    @todo what to do when date is out of validity range?
    @Test
    public void testGetOzoneWithInvalidValue() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(2005, Calendar.JUNE, 15, 6, 0, 0);
        try {
            final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());
            final double ozone = seadasAuxdata.getOzone(calendar.getTime(), 83, 52);
            fail("Exception expected");
        } catch (Exception expected) {
            assertEquals("Could not find product for given day", expected.getMessage());
        }
    }

    @Test
    public void testGetOzoneForProductAfterNoon() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(2005, Calendar.JUNE, 15, 18, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        try {
            final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());
            final double ozone = seadasAuxdata.getOzone(calendar.getTime(), 83, 65);
            assertEquals(297.0, ozone, 1e-8);
        } catch (Exception unexpected) {
            fail("Auxdata Impl was not created although auxdata path was valid!" + unexpected.getMessage());
        }
    }

    @Test
    public void testGetOzoneForProductBeforeNoon() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(2005, Calendar.JUNE, 16, 6, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        try {
            final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());
            final double ozone = seadasAuxdata.getOzone(calendar.getTime(), 83, 65);
            assertEquals(309.0, ozone, 1e-8);
        } catch (Exception unexpected) {
            fail("Auxdata Impl was not created although auxdata path was valid!" + unexpected.getMessage());
        }
    }

    @Test
    public void testGetDateFraction_varyHour() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);

        calendar.set(2012, Calendar.JULY, 16, 12, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(0.0, SeadasAuxdataImpl.getDateFraction(calendar, 0.5), 1e-8);

        calendar.set(2012, Calendar.JULY, 16, 6, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(0.75, SeadasAuxdataImpl.getDateFraction(calendar, 0.5), 1e-8);

        calendar.set(2012, Calendar.JULY, 16, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(0.5, SeadasAuxdataImpl.getDateFraction(calendar, 0.5), 1e-8);

        calendar.set(2012, Calendar.JULY, 16, 18, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(0.25, SeadasAuxdataImpl.getDateFraction(calendar, 0.5), 1e-8);

        calendar.set(2012, Calendar.JULY, 16, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        assertEquals(0.49999998842592597, SeadasAuxdataImpl.getDateFraction(calendar, 0.5), 1e-8);    // almost 0.5 :-)

        calendar.set(2012, Calendar.JULY, 16, 11, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        assertEquals(0.9999999884259259, SeadasAuxdataImpl.getDateFraction(calendar, 0.5), 1e-8); // almost 1
    }

    @Test
    public void testGetDateFraction_varyDays() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);

        calendar.set(2006, Calendar.AUGUST, 7, 11, 52, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(0.9944444444444445, SeadasAuxdataImpl.getDateFraction(calendar, 0.5), 1e-8);

        calendar.set(2009, Calendar.JANUARY, 27, 11, 52, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(0.9944444444444445, SeadasAuxdataImpl.getDateFraction(calendar, 0.5), 1e-8);
    }

    @Test
    public void testGetDateFraction_varyOffset() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);

        calendar.set(2006, Calendar.AUGUST, 7, 17, 22, 11);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(0.22373842592592597, SeadasAuxdataImpl.getDateFraction(calendar, 0.5), 1e-8);

        assertEquals(0.52373842592592597, SeadasAuxdataImpl.getDateFraction(calendar, 0.2), 1e-8);

        assertEquals(0.02373842592592597, SeadasAuxdataImpl.getDateFraction(calendar, 0.7), 1e-8);
    }

    @Test
    public void testGetDayString() {
         assertEquals("000", SeadasAuxdataImpl.getDayString(0));
         assertEquals("009", SeadasAuxdataImpl.getDayString(9));
         assertEquals("010", SeadasAuxdataImpl.getDayString(10));
         assertEquals("099", SeadasAuxdataImpl.getDayString(99));
         assertEquals("100", SeadasAuxdataImpl.getDayString(100));
         assertEquals("114", SeadasAuxdataImpl.getDayString(114));
    }

    @Test
    public void testGetDayOffset() {
         assertEquals(-1, SeadasAuxdataImpl.getDayOffset(0));
         assertEquals(-1, SeadasAuxdataImpl.getDayOffset(11));
         assertEquals(0, SeadasAuxdataImpl.getDayOffset(12));
         assertEquals(0, SeadasAuxdataImpl.getDayOffset(23));
    }

    @Test
    public void testCreateTimeSpan() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(2007, Calendar.SEPTEMBER, 17, 17, 22, 11);

        final SeadasAuxdataImpl.TimeSpan timeSpan = SeadasAuxdataImpl.createTimeSpan(calendar, 0);
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

        final SeadasAuxdataImpl.TimeSpan timeSpan = SeadasAuxdataImpl.createTimeSpan(calendar, -1);
        assertNotNull(timeSpan);
        assertEquals(2008, timeSpan.getStartYear());
        assertEquals(291, timeSpan.getStartDay());
        assertEquals(2008, timeSpan.getEndYear());
        assertEquals(292, timeSpan.getEndDay());
    }

    @Test
    public void testAdjustForOverlappingYears_startYear() {
        SeadasAuxdataImpl.TimeSpan timeSpan = new SeadasAuxdataImpl.TimeSpan();
        timeSpan.setStartDay(0);
        timeSpan.setStartYear(2006);

        timeSpan = SeadasAuxdataImpl.adjustForOverlappingYears(timeSpan);
        assertEquals(365, timeSpan.getStartDay());
        assertEquals(2005, timeSpan.getStartYear());
    }

    @Test
    public void testAdjustForOverlappingYears_startYear_leapYear() {
        SeadasAuxdataImpl.TimeSpan timeSpan = new SeadasAuxdataImpl.TimeSpan();
        timeSpan.setStartDay(0);
        timeSpan.setStartYear(2005);

        timeSpan = SeadasAuxdataImpl.adjustForOverlappingYears(timeSpan);
        assertEquals(366, timeSpan.getStartDay());
        assertEquals(2004, timeSpan.getStartYear());
    }

    @Test
    public void testAdjustForOverlappingYears_endYear() {
        SeadasAuxdataImpl.TimeSpan timeSpan = new SeadasAuxdataImpl.TimeSpan();
        timeSpan.setStartDay(365);
        timeSpan.setEndDay(366);
        timeSpan.setEndYear(2007);

        timeSpan = SeadasAuxdataImpl.adjustForOverlappingYears(timeSpan);
        assertEquals(1, timeSpan.getEndDay());
        assertEquals(2008, timeSpan.getEndYear());
    }

    @Test
    public void testAdjustForOverlappingYears_endYear_leapYear() {
        SeadasAuxdataImpl.TimeSpan timeSpan = new SeadasAuxdataImpl.TimeSpan();
        timeSpan.setStartDay(365);
        timeSpan.setEndDay(367);
        timeSpan.setEndYear(2000);

        timeSpan = SeadasAuxdataImpl.adjustForOverlappingYears(timeSpan);
        assertEquals(1, timeSpan.getEndDay());
        assertEquals(2001, timeSpan.getEndYear());
    }

    @Test
    public void testAdjustForOverlappingYears_noAdjustments() {
        SeadasAuxdataImpl.TimeSpan timeSpan = new SeadasAuxdataImpl.TimeSpan();
        timeSpan.setStartDay(219);
        timeSpan.setStartYear(2012);
        timeSpan.setEndDay(220);
        timeSpan.setEndYear(2012);

        timeSpan = SeadasAuxdataImpl.adjustForOverlappingYears(timeSpan);
        assertEquals(219, timeSpan.getStartDay());
        assertEquals(2012, timeSpan.getStartYear());
        assertEquals(220, timeSpan.getEndDay());
        assertEquals(2012, timeSpan.getEndYear());
    }
}
