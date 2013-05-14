package org.esa.beam.waterradiance.realoptimizers;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AlphaTabTest {

    private AlphaTab alphaTab;

    @Before
    public void setUp() {
        alphaTab = new AlphaTab();
    }

    @Test
    public void testGetRecDeltaAlpha() {
        assertEquals(4999.95, alphaTab.getRecDelta(), 1e-8 );
    }

    @Test
    public void testGet(){
        double value  = alphaTab.get(-2.4651393278665097);
        assertEquals(0.07833230435305982, value, 1e-8);

        value  = alphaTab.get(3.2729894491103284);
        assertEquals(0.963491998600729, value, 1e-8);

        value  = alphaTab.get(8.857275854786685);
        assertEquals(0.9998576797706701, value, 1e-8);
    }
}
