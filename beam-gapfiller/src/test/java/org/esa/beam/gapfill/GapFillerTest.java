package org.esa.beam.gapfill;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Norman Fomferra
 */
public class GapFillerTest {

    @Test
    public void testFill() throws Exception {
        int w = 3;
        int h = 3;
        int d = 2;
        final float[][] input = new float[d][w * h];

        for (int i = 0; i < w * h; i++) {
            input[0][i] = 1.0F;
        }
        for (int i = 0; i < w * h; i++) {
            input[1][i] = Float.NaN;
        }

        final int maxIter = 100;

        final float[][] output = GapFiller.fillGaps(3, 3, input, maxIter, true, null);

        for (int i = 0; i < w * h; i++) {
            assertEquals(1.0F, output[1][i], 1e-6F);
        }

    }
}
