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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author Norman Fomferra
 */
public class LevMarNnLibTest {

    static {
        WaterRadianceOperator.installAuxdataAndLibrary();
    }

    private static final double[] MERIS_SUN_SPECTRAL_FLUXES = {
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

    private static double[] expected = new double[]{0.05106093416816374, 0.04016991778889496, 0.02909920769222207, 0.02572070343095661, 0.02011765458709306, 0.012768282032716158, 0.010003103221994807, 0.009281329685685624, 0.00797110510189507, 0.006325542223074678, 0.005709368943107607, 0.004271587237326763, 0.1624973099755441, 0.12595583100329133, 0.08650862387487807, 0.07475306287696568, 0.05331361808572474, 0.0372790477983821, 0.029419369370368254, 0.027168281071752634, 0.023989072817804387, 0.02003337547448819, 0.01833730308478722, 0.014420431517833246, 0.0012778766948638813, 0.0015750932296141002, 0.0021290065625050314, 0.0018271732725580782, 0.0012669662524066433, 2.585147245134416E-4, 1.532371210091965E-4, 1.3862355065286021E-4, 1.0628718620169615E-4, 3.901543902158368E-5, 3.768225163471599E-5, 1.6834009702789792E-5, 0.8309826770032178, 0.8662276540566973, 0.9053840448295283, 0.9174105406933322, 0.9398856985727462, 0.9570527535617652, 0.9656945300353932, 0.9680997059610024, 0.9716275644656794, 0.9761296536617454, 0.9781070201344053, 0.9827647076987795, 0.8449310287964144, 0.8780478030781262, 0.9144468473439091, 0.9255657593076188, 0.9461675997246516, 0.9618695152376522, 0.9696751966654038, 0.971882807458226, 0.9750929774273647, 0.9791703563115154, 0.9809125316626273, 0.9851024686164649, 0.05973076286291826, 0.29766715775438746, 7.759718155157483E-9, 0.0015022583098090291, 1.8841773357640426, 1.650875536051587E-5, 3.3138010791243407E-7, 1.2909261739892807E-4, 7.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    @Test
    public void testLib() throws Exception {
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

        int result = lib.levmar_nn(181, input, input.length, output, output.length, debug_dat);
        assertEquals(0, result);
        assertArrayEquals(expected, output, 1e-8);
    }
}
