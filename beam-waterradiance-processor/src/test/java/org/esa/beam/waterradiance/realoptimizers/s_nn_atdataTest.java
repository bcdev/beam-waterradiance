package org.esa.beam.waterradiance.realoptimizers;


import org.junit.Test;

import static org.junit.Assert.*;

public class s_nn_atdataTest {

    @Test
    public void testSetRPath() {
        final double[] input = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9};

        final s_nn_atdata nnData = new s_nn_atdata();
        nnData.setRpath_nn(input);

        final double[] result = nnData.getRpath_nn();
        assertNotNull(result);
        assertNotSame(input, result);
        for (int i = 0; i < input.length; i++) {
            assertEquals(input[i], result[i], 1e-8);
        }
        for (int i = input.length; i < result.length; i++) {
            assertEquals(0.0, result[i], 1e-8);
        }
    }
}
