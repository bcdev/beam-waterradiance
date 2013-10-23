package org.esa.beam.waterradiance.no2;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

public class NO2AuxdataImplTest {

    private URL auxDirectoryURL;

    @Before
    public void setUp() {
        auxDirectoryURL = NO2AuxdataImplTest.class.getResource("../../../../../auxiliary/seadas/anc/no2");
    }

    @Test
    @Ignore
    public void testGetNO2Frac() {
        NO2AuxdataImpl no2Auxdata = null;
        try {
            no2Auxdata = NO2AuxdataImpl.create(auxDirectoryURL.getPath());
        } catch (Exception e) {
            if (!e.getMessage().equals("Could not find no2 climatology product")) {
                fail("Unexpected exception: " + e.getMessage());
            }
        }
        assertNotNull(no2Auxdata);
        double no2Frac = -1;
        try {
            no2Frac = no2Auxdata.getNO2Frac(0, 0);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
        assertEquals(0.9834201, no2Frac, 1e-8);
    }
} 