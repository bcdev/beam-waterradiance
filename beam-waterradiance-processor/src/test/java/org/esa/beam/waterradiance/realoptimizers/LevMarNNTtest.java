package org.esa.beam.waterradiance.realoptimizers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LevMarNNTtest {

    @Test
    public void testScp() {
        final double[] x = {1.0, 2.0, 3.0, 4.0};
        final double[] y = {5.0, 6.0, 7.0, 8.0};

        double scp = LevMarNN.scp(x, y, 2);
        assertEquals(17.0, scp, 1e-8);

        scp = LevMarNN.scp(x, y, 3);
        assertEquals(38.0, scp, 1e-8);

        scp = LevMarNN.scp(x, y, 4);
        assertEquals(70.0, scp, 1e-8);
    }
}
