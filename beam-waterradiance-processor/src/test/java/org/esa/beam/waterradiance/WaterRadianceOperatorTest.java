package org.esa.beam.waterradiance;

import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.gpf.GPF;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.ParseException;

import static org.junit.Assert.*;

/**
 * @author Marco Peters
 */
public class WaterRadianceOperatorTest {

    @BeforeClass
    public static void beforeClass() throws ParseException {
        GPF.getDefaultInstance().getOperatorSpiRegistry().addOperatorSpi(new WaterRadianceOperator.Spi());
    }


    @Test
    public void testCreatedTargetProduct() throws Exception {
        Product l1bProduct = MerisL1bProduct.create();
        Product target = GPF.createProduct("Meris.WaterRadiance", GPF.NO_PARAMS, l1bProduct);
        assertNotNull(target);
        assertNotNull(target.getGeoCoding());
        assertNotNull(target.getMetadataRoot());
        assertNotNull(target.getStartTime());
        assertNotNull(target.getEndTime());
        assertEquals(73, target.getNumBands());


    }
}
