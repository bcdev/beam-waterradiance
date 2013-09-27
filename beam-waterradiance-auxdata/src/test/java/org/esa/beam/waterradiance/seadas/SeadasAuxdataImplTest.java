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

//            @todo what to do when date is out of validity range?
//    @Test
//    public void testGetOzoneWithInvalidValue() {
//        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
//        calendar.set(2005, Calendar.JUNE, 15, 6, 0, 0);
//        try {
//            final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());
//            final double ozone = seadasAuxdata.getOzone(calendar.getTime(), 83, 52);
//        } catch (Exception unexpected) {
//            fail("Auxdata Impl was not created although auxdata path was valid!" + unexpected.getMessage());
//        }
//    }

    @Test
    public void testGetOzoneForProductAfterNoon() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(2005, Calendar.JUNE, 15, 18, 0, 0);
        try {
            final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());
            final double ozone = seadasAuxdata.getOzone(calendar.getTime(), 83, 65);
            assertEquals(297f, ozone, 1e-3);
        } catch (Exception unexpected) {
            fail("Auxdata Impl was not created although auxdata path was valid!" + unexpected.getMessage());
        }
    }

    @Test
    public void testGetOzoneForProductBeforeNoon() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(2005, Calendar.JUNE, 16, 6, 0, 0);
        try {
            final SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryURL.getPath());
            final double ozone = seadasAuxdata.getOzone(calendar.getTime(), 83, 65);
            assertEquals(309f, ozone, 1e-4);
        } catch (Exception unexpected) {
            fail("Auxdata Impl was not created although auxdata path was valid!" + unexpected.getMessage());
        }
    }

}
