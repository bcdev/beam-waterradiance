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

    private static final LevMarNnLib lib;

    private static final double[] SUN_SPECTRAL_FLUXES_NORMAN = {
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

    private static final double[] SUN_SPECTRAL_FLUXES_20060116 = {
            1773.7241,
            1943.4926,
            1994.6368,
            1995.2565,
            1865.0692,
            1707.5303,
            1584.2865,
            1522.7838,
            1456.3502,
            1309.5717,
            1297.716,
            1217.7358,
            991.3362,
            961.8075,
            926.2471
    };

    static {
        WaterRadianceOperator.installAuxdataAndLibrary();
        lib = LevMarNnLib.INSTANCE;

        final double[] input = assembleInput(38.532475,
                142.5679,
                23.14311,
                103.322136,
                7.8687496,
                -2.525,
                1017.61487,
                317.83008,
                20.0,
                12.0,
                new double[]{70.43595,
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
                        2.1505015},
                SUN_SPECTRAL_FLUXES_NORMAN);
        final double[] output = new double[75];
        final double[] debug_dat = new double[100];

        lib.levmar_nn(181, input, input.length, output, output.length, debug_dat);
    }


    private static double[] expected_Norman = new double[]{0.05106093416816374, 0.04016991778889496, 0.02909920769222207, 0.02572070343095661, 0.02011765458709306, 0.012768282032716158, 0.010003103221994807, 0.009281329685685624, 0.00797110510189507, 0.006325542223074678, 0.005709368943107607, 0.004271587237326763, 0.1623827113241803, 0.12554737638456048, 0.08621805880739447, 0.07452156083095067, 0.053223770919233, 0.03723204663801888, 0.02931542507041457, 0.0269939240634205, 0.02364909150084375, 0.019340176293081195, 0.0173652556195893, 0.012237306263804882, 0.001568657695522971, 0.0026339151877950748, 0.005623105138329359, 0.006934197866612184, 0.0080241343916769, 0.002245050307034188, 0.0012923721425673563, 0.0011410707944109603, 6.251041377930135E-4, 1.774599184817651E-4, 1.9176410793070385E-4, 7.410230710238745E-5, 0.8328306200668191, 0.868014701083974, 0.9065376181016092, 0.9184731259930503, 0.9405589013945194, 0.9575450507186729, 0.9660605803709226, 0.9685717844945578, 0.9722630801640497, 0.9771436322771312, 0.979331240684574, 0.9850985830732055, 0.8471858470889997, 0.8798765138449544, 0.9156650169507484, 0.9264758696866597, 0.9466022938868986, 0.9618970848122543, 0.9695529395795662, 0.9716631733307265, 0.9749707873633913, 0.979304144137769, 0.9812015550718829, 0.9869415760803479, 0.05079101236501857, 2.198994037259884, 0.03903679968148976, 0.026913059058050755, 0.4172957639537051, 0.35425272898748916, 0.12594365274665248, 2.3401578743149414E-5, 11.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private static double[] expected_20060116_ocean = new double[]{0.06674266859987914, 0.05270575492147841, 0.037690366457760964, 0.03184916519569875, 0.021960431891169183, 0.014177790616298265, 0.011078825564823104, 0.010191746071164211, 0.008881982756463491, 0.007173714204852539, 0.0063869638297191595, 0.004658747017442468, 0.19574706012342358, 0.15157018386536142, 0.10349393283013467, 0.08907175398902958, 0.06289482290160611, 0.04345901328482248, 0.03397234509281777, 0.03128066525240934, 0.02740034109927214, 0.02244432463303016, 0.0201471252506775, 0.014570731074423453, 0.02056395076082349, 0.01891260421427962, 0.018481981327013706, 0.012957774757059157, 0.007104752698667056, 0.0013654260129047871, 7.405070743609559E-4, 6.444426993673771E-4, 3.4440415078152933E-4, 9.308153919739267E-5, 9.889564357097544E-5, 3.579907731267747E-5, 0.8180041091526109, 0.8555100376608279, 0.8969983662984885, 0.9099336595067813, 0.9340149104228945, 0.9526773428641873, 0.9621514179206576, 0.9649425980113184, 0.969040510322309, 0.9743669805817792, 0.9767607053003194, 0.9826340208622213, 0.8281769357845747, 0.8644482752003646, 0.9042854655006227, 0.9165896915733827, 0.9394850211533595, 0.9571103680802469, 0.9659726475658675, 0.9685262611282467, 0.9723244906444477, 0.977221333549853, 0.9793969426347601, 0.9846996690438122, 0.06106450086164535, 1.8810524644322026, 0.034145380605745214, 0.0017470877139239942, 0.00751193219288796, 0.20640987932403929, 0.01649352341393581, 1.690457460535281E-7, 15.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private static double[] expected_20060116_cloud = new double[]{0.3026792354396823, 0.30184442063205946, 0.3020461562031379, 0.3007758486396829, 0.3010356555643136, 0.3005364530236857, 0.3007750118065772, 0.30027829659637095, 0.29883401372349083, 0.2988576437013362, 0.2653722559834979, 0.2945622457458439, 0.2912502054080488, 0.25706770268295703, 0.21683661414205652, 0.20367267231228936, 0.17894100597053506, 0.15836715305311627, 0.14798322483142307, 0.14528870155985246, 0.14177909080877787, 0.13864372167312708, 0.13779412906506006, 0.13798818481995623, 0.0506826703464467, 0.03778681086561182, 0.017660729157205502, 0.007474887477112944, 0.0029462811185233056, 4.8234962809398543E-4, 2.5226881124523445E-4, 2.1627441229813899E-4, 1.0847765951352204E-4, 2.7848669677397132E-5, 3.005386887593431E-5, 1.023467406690061E-5, 0.6963394296078214, 0.7273540761356064, 0.7648011439779634, 0.7769554825929026, 0.801566217927652, 0.8216954973491959, 0.8333790646636416, 0.8362241543303175, 0.8390265502217791, 0.842197438583068, 0.8437266912014763, 0.8433197471187758, 0.7329422240760464, 0.7634730262157314, 0.7999230491275399, 0.8115038485939088, 0.8347229789680487, 0.8540525658582826, 0.8645460215061157, 0.8671960262934348, 0.8692452082173961, 0.8710349122974814, 0.8714562778522269, 0.8697878618269281, 1.0, 0.03402864869708678, 0.0014020594318015465, 0.0012077282620016707, 0.0010266086004479587, 0.0209149083521504, 0.01380389927449996, 6.1300417851735185, 146.0, 0.0, 0.0, 0.0, 0.0,  0.0, 0.0};
    private static double[] expected_20060116_land = new double[]{0.05940486599705885, 0.04834449806498461, 0.03641968459789665, 0.033941544264534246, 0.035856072733429424, 0.025940122404248298, 0.020817244287082558, 0.01996426189636208, 0.04440945866001555, 0.09507140109908682, 0.09938762634020118, 0.1088207950157593, 0.2861516362277008, 0.25110823233267165, 0.2091002340678031, 0.19534451903870187, 0.1690007440322344, 0.14727878537231792, 0.1360584435237715, 0.13302024839968596, 0.12937359136718574, 0.12575618239900943, 0.12445800977584931, 0.12362624878405744, 1.9275933271288941E-4, 2.77419681982213E-4, 3.666989438929095E-4, 4.2620013340718765E-4, 4.936023586054581E-4, 2.27096291856562E-4, 1.2189942565112603E-4, 1.0690337335218247E-4, 6.06510936271031E-5, 1.7006299717784814E-5, 1.8337580603727284E-5, 5.898583680792376E-6, 0.6569523723522561, 0.6882092540451998, 0.7267631265404317, 0.7394281447615894, 0.7650146788352299, 0.7864268587575082, 0.7987883249449765, 0.8018427260130847, 0.8049983461235388, 0.8081965773164199, 0.8097003165245044, 0.8094302201531822, 0.7355406138046319, 0.7660323291802648, 0.8023329376503239, 0.8137839856183664, 0.8369282178247136, 0.8562257567627498, 0.8667597937144694, 0.8692809010592252, 0.8710995042619107, 0.8724199759679322, 0.872702315145844, 0.8702901616372646, 1.0, 0.0223707718561656, 1.6820898094440193, 2.4675568544226825, 4.953032424395115, 0.002704579812535653, 0.008388920394506888, 0.16216890527452435, 150.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private static double[] expected_20060116_subset_ocean = new double[]{0.057672590497990096, 0.04572532386883093, 0.03171267837750812, 0.02564602280962399, 0.017664553648440915, 0.011981326776316074,0.009699751775363648, 0.00904915427541893, 0.00817508445561847, 0.006887354852911231, 0.006331618722387516, 0.005084252164038501, 0.15384285653497967, 0.11979466343991138, 0.08313151038726542, 0.0721791290959648, 0.0521848502202637, 0.03725414572053879, 0.029959995521211556, 0.02788787734226448, 0.02498048460715011, 0.021366375425044982, 0.01976231899359089, 0.01635983159748708, 0.03935105652987402, 0.03186890420043511, 0.02005031297132654, 0.009994471772779978, 0.004181156959754461, 7.422810706980077E-4, 3.9261899149557836E-4, 3.3651359260786527E-4, 1.752318840397397E-4, 4.6180343576388235E-5, 4.8565717393852575E-5, 1.715343565079704E-5, 0.8198348895672114, 0.8552101261454912, 0.8942424086575024, 0.9064455315639166, 0.9292459328883619, 0.947036945141907, 0.9559350305505383, 0.9585655150357886, 0.9623774798787784, 0.967281874151591, 0.969299286805056, 0.9742365768784724, 0.8463023675133424, 0.878157719256748, 0.9128497831110457, 0.9234515666534178, 0.9431209528607113, 0.9581681961325944, 0.9656824659474197, 0.9678504583290315, 0.9709385268948585, 0.9748657625049898, 0.9765483889307479, 0.980458595636168, 0.128549086507779, 0.6979731863512365, 0.008230842510306934, 2.3216961463846646E-4, 0.002246813358801138, 0.0861648397431571, 0.009546428441837642, 7.67078634584641E-7, 8.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    @Test
    public void testL1b_testDataNorman() throws Exception {
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

        final double[] toa_radiances = {
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

        final double[] input = assembleInput(solar_zenith,
                solar_azimuth,
                view_zenith,
                view_azimuth,
                wind_x,
                wind_y,
                surf_pressure,
                ozone,
                temperature,
                salinity,
                toa_radiances,
                SUN_SPECTRAL_FLUXES_NORMAN);
        final double[] output = new double[75];
        final double[] debug_dat = new double[100];

        int result = lib.levmar_nn(181, input, input.length, output, output.length, debug_dat);
        assertEquals(0, result);
        assertArrayEquals(expected_Norman, output, 1e-8);
    }

    @Test
    public void testLib_MER_RR__1PRACR20060116_201233_000026092044_00200_20294_0000_ocean() throws Exception {
        final double solar_zenith = 43.913773;
        final double solar_azimuth = 76.12128;
        final double view_zenith = 34.740032;
        final double view_azimuth = 108.32797;
        final double wind_x = 5.7124996;
        final double wind_y = 2.25625;
        final double surf_pressure = 1021.8688;
        final double ozone = 277.4056;
        final double temperature = 18.54;
        final double salinity = 35.34;

        final double[] toa_radiances = {
                84.99878,
                73.27003,
                53.322674,
                44.631977,
                27.512224,
                16.187922,
                12.244357,
                10.961995,
                9.097538,
                6.74975,
                3.1055324,
                5.605122,
                3.3304179,
                3.0823767,
                2.4926267
        };

        final double[] input = assembleInput(solar_zenith,
                solar_azimuth,
                view_zenith,
                view_azimuth,
                wind_x,
                wind_y,
                surf_pressure,
                ozone,
                temperature,
                salinity,
                toa_radiances,
                SUN_SPECTRAL_FLUXES_20060116);

        final double[] output = new double[75];
        final double[] debug_dat = new double[100];

        int result = lib.levmar_nn(873, input, input.length, output, output.length, debug_dat);
        assertEquals(0, result);
        assertArrayEquals(expected_20060116_ocean, output, 1e-8);
    }

    @Test
    public void testLib_MER_RR__1PRACR20060116_201233_000026092044_00200_20294_0000_cloud() throws Exception {
        final double solar_zenith = 42.372684;
        final double solar_azimuth = 128.2194;
        final double view_zenith = 23.510494;
        final double view_azimuth = 102.27474;
        final double wind_x = -9.75;
        final double wind_y = 0.6750001;
        final double surf_pressure = 1009.8656;
        final double ozone = 237.87592;
        final double temperature = 27.4855;
        final double salinity = 34.962;

        final double[] toa_radiances = {
                398.09485,
                434.67896,
                440.62158,
                433.41943,
                390.89935,
                356.09332,
                342.04077,
                331.19794,
                312.51495,
                287.70917,
                135.00192,
                237.70004,
                215.58147,
                205.36565,
                164.44276
        };

        final double[] input = assembleInput(solar_zenith,
                solar_azimuth,
                view_zenith,
                view_azimuth,
                wind_x,
                wind_y,
                surf_pressure,
                ozone,
                temperature,
                salinity,
                toa_radiances,
                SUN_SPECTRAL_FLUXES_20060116);

        final double[] output = new double[75];
        final double[] debug_dat = new double[100];

        int result = lib.levmar_nn(748, input, input.length, output, output.length, debug_dat);
        assertEquals(0, result);
        assertArrayEquals(expected_20060116_cloud, output, 1e-8);
    }

    @Test
    public void testLib_MER_RR__1PRACR20060116_201233_000026092044_00200_20294_0000_land() throws Exception {
        final double solar_zenith = 50.82132;
        final double solar_azimuth = 140.0397;
        final double view_zenith = 21.953356;
        final double view_azimuth = 101.89219;
        final double wind_x = -4.1289067;
        final double wind_y = -1.8664063;
        final double surf_pressure = 1018.4313;
        final double ozone = 231.5093;
        final double temperature = 24.5196;
        final double salinity = 34.808;

        final double[] toa_radiances = {
                66.59864,
                59.22042,
                45.29473,
                41.82709,
                39.72433,
                26.201996,
                20.222364,
                18.84353,
                38.00525,
                78.18966,
                23.406841,
                76.28124,
                67.89858,
                66.15208,
                40.435944
        };

        final double[] input = assembleInput(solar_zenith,
                solar_azimuth,
                view_zenith,
                view_azimuth,
                wind_x,
                wind_y,
                surf_pressure,
                ozone,
                temperature,
                salinity,
                toa_radiances,
                SUN_SPECTRAL_FLUXES_20060116);

        final double[] output = new double[75];
        final double[] debug_dat = new double[100];

        int result = lib.levmar_nn(723, input, input.length, output, output.length, debug_dat);
        assertEquals(0, result);
        assertArrayEquals(expected_20060116_land, output, 1e-8);
    }

    @Test
    public void testLib_MER_RR__1PRACR20060116_201233_000026092044_00200_20294_0000_subset_ocean() throws Exception {
        final double solar_zenith = 39.14653;
        final double solar_azimuth = 75.055664;
        final double view_zenith = 9.815097;
        final double view_azimuth = 104.866035;
        final double wind_x = -2.06875;
        final double wind_y = 5.0;
        final double surf_pressure = 1021.28125;
        final double ozone = 258.8853;
        final double temperature = 18.283518;
        final double salinity = 35.010246;

        final double[] toa_radiances = {
                79.37073,
                68.774155,
                48.480423,
                38.893734,
                24.135004,
                14.9269495,
                11.607487,
                10.517964,
                8.971358,
                6.9750304,
                3.0966594,
                5.982914,
                3.9026668,
                3.6500223,
                2.8130295
        };


        final double[] input = assembleInput(solar_zenith,
                solar_azimuth,
                view_zenith,
                view_azimuth,
                wind_x,
                wind_y,
                surf_pressure,
                ozone,
                temperature,
                salinity,
                toa_radiances,
                SUN_SPECTRAL_FLUXES_20060116);

        final double[] output = new double[75];
        final double[] debug_dat = new double[100];

        int result = lib.levmar_nn(583, input, input.length, output, output.length, debug_dat);
        assertEquals(0, result);
        assertArrayEquals(expected_20060116_subset_ocean, output, 1e-8);
    }



    private static double[] assembleInput(double solar_zenith,
                                          double solar_azimuth,
                                          double view_zenith,
                                          double view_azimuth,
                                          double wind_x,
                                          double wind_y,
                                          double surf_pressure,
                                          double ozone,
                                          double temperature,
                                          double salinity,
                                          double[] toa_radiances,
                                          double[] sun_spectral_fluxes) {
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
            input[i + 25] = sun_spectral_fluxes[i];
        }
        return input;
    }
}
