/*
 * Copyright (C) 2012 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.beam.waterradiance;

import org.junit.Test;

/**
 * @author Norman Fomferra
 */
public class LevMarNnLibTest {
    final double[] MERIS_SUN_SPECTRAL_FLUXES = {
            1773.03,
            1942.73,
            1993.86,
            1994.48,
            1864.34,
            1706.86,
            1583.67,
            1522.19,
            1455.78,
            1309.06,
            1297.21,
            1217.26,
            990.95,
            961.43,
            925.89
    };

    @Test
    public void testLib() throws Exception {

        // final double latitude = 55.106583;
        // final double longitude = 18.03734;
        final double solar_zenith = 38.532475;
        final double solar_azimuth = 142.5679;
        final double view_zenith = 23.14311;
        final double view_azimuth = 103.322136;
        final double wind_x = 7.8687496;
        final double wind_y = -2.525;
        final double surf_pressure = 1017.61487;
        final double ozone = 317.83008;
        final double temperature = 20.0;
        final double salinity = 12.0;

        double[] toa_radiances = {
                70.43595,
                60.354992,
                44.56492,
                39.043613,
                27.241674,
                15.729385,
                11.943042,
                10.802422,
                8.662219,
                6.4378233,
                2.8837085,
                5.4307566,
                3.2948744,
                3.0640657,
                2.1505015
        };

        final double[] input = new double[40];
        input[0] = solar_zenith;
        input[1] = solar_azimuth;
        input[2] = view_zenith;
        input[3] = view_azimuth;
        input[4] = surf_pressure;
        input[5] = ozone;
        input[6] = wind_x;
        input[7] = wind_y;
        input[8] = temperature;
        input[9] = salinity;
        for (int i = 0; i < 15; i++) {
            input[i + 10] = toa_radiances[i];
            input[i + 25] = MERIS_SUN_SPECTRAL_FLUXES[i];
        }
        final double[] output = new double[75];
        double[] debug_dat = new double[100];

        final LevMarNnLib lib = LevMarNnLib.INSTANCE;


        int result;
        final int N = 100;
        final long t0 = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            input[10] += 0.0001 * Math.random();
            result = lib.levmar_nn(181, input, input.length, output, output.length, debug_dat);
        }
        final long t1 = System.currentTimeMillis();
        System.out.println("time " + (t1 - t0) / 1000.0 + " s");


        //assertEquals(0, i);
    }
}
