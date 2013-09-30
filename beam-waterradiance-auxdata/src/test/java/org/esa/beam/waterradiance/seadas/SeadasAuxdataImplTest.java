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

    @Test
    public void testGetOzoneWithInvalidValue() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(2005, Calendar.JUNE, 15, 6, 0, 0);
        try {
            final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());
            final double ozone = seadasAuxdata.getOzone(calendar.getTime(), 83, 52);
            fail("Exception expected");
        } catch (Exception expected) {
            assertEquals("Could not retrieve ozone for given day", expected.getMessage());
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
    public void testGetSurfacePressureWithInvalidValue() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(2005, Calendar.JUNE, 15, 1, 30, 0);
        try {
            final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());
            final double surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), 83, 52);
            fail("Exception expected");
        } catch (Exception expected) {
            assertEquals("Could not retrieve surface pressure for given day", expected.getMessage());
        }
    }

    @Test
    public void testGetSurfacePressure() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(Calendar.MILLISECOND, 0);
        try {
            final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());

            calendar.set(2005, Calendar.JUNE, 16, 6, 0, 0);
            double surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), 182, 51);
            assertEquals(1021.36, surfacePressure, 1e-8);

            calendar.set(2005, Calendar.JUNE, 16, 1, 30, 0);
            surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), 146, 98);
            assertEquals(1015.49, surfacePressure, 1e-8);

            calendar.set(2005, Calendar.JUNE, 15, 22, 30, 0);
            surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), 146, 98);
            assertEquals(1014.49, surfacePressure, 1e-8);

            calendar.set(2005, Calendar.JUNE, 16, 10, 30, 0);
            surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), 159, 92);
            assertEquals(1014.2, surfacePressure, 1e-8);

            calendar.set(2005, Calendar.JUNE, 16, 19, 30, 0);
            surfacePressure = seadasAuxdata.getSurfacePressure(calendar.getTime(), 178, 86);
            assertEquals(1013.28, surfacePressure, 1e-8);

        } catch (Exception unexpected) {
            fail("Auxdata Impl was not created although auxdata path was valid!" + unexpected.getMessage());
        }
    }

    @Test
    public void testGetSurfacePressureDateFraction_varyHour() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);

        calendar.set(2012, Calendar.JULY, 16, 12, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(0.5, SeadasAuxdataImpl.getDateFractionForSurfacePressure(calendar), 1e-8);

        calendar.set(2012, Calendar.JULY, 16, 4, 30, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(0.25, SeadasAuxdataImpl.getDateFractionForSurfacePressure(calendar), 1e-8);

        calendar.set(2012, Calendar.JULY, 16, 19, 30, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(0.75, SeadasAuxdataImpl.getDateFractionForSurfacePressure(calendar), 1e-8);

        calendar.set(2012, Calendar.JULY, 16, 9, 36, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertEquals(0.1, SeadasAuxdataImpl.getDateFractionForSurfacePressure(calendar), 1e-8);

        calendar.set(2012, Calendar.JULY, 16, 14, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        assertEquals(0.9999999537037038, SeadasAuxdataImpl.getDateFractionForSurfacePressure(calendar), 1e-8);

        calendar.set(2012, Calendar.JULY, 16, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        assertEquals(0.4999999537037037, SeadasAuxdataImpl.getDateFractionForSurfacePressure(calendar), 1e-8);
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
}
