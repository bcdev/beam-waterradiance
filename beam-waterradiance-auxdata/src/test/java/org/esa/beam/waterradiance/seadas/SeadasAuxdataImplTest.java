package org.esa.beam.waterradiance.seadas;


import org.junit.Test;

import java.net.URL;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

public class SeadasAuxdataImplTest {

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
        final URL auxDirectoryURL = SeadasAuxdataImplTest.class.getResource("../../../../../auxiliary/seadas/anc");
        assertNotNull(auxDirectoryURL);
        final String auxDirectoryPath = auxDirectoryURL.getPath();
        try {
            SeadasAuxdataImpl seadasAuxdata = SeadasAuxdataImpl.create(auxDirectoryPath);
            assertNotNull(seadasAuxdata);
        } catch (Exception unexpected) {
            fail("Auxdata Impl was not created although auxdata path was valid!" + unexpected.getMessage());
        }
    }

}
