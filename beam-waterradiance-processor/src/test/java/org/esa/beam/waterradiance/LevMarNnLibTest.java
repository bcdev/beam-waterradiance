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


    private static double[] expected_Norman = new double[]{0.05106093416816374,0.04016991778889496,0.029099207692222077,0.02572070343095661,0.020117654587093057,0.012768282032716155,0.010003103221994807,0.009281329685685624,0.00797110510189507,0.006325542223074678,0.005709368943107607,0.004271587237326763,0.27587539530105826,0.2427175261946608,0.2037863549386491,0.1911050898944058,0.16717231303297816,0.14744256157763636,0.13745927178199094,0.13488745178748818,0.13148699536022943,0.12840499227209626,0.12757094886889025,0.12761385871306444,-1.3896755065411472,-1.3745882056193999,-1.1336524110499377,-1.2308747193300222,-1.444235839152686,-2.510153764441477,-3.0235538161313764,-3.124244694860061,-3.7723190285343637,-5.08306669366523,-4.987301364596675,-5.872622572338704,0.7100186652142568,0.7407404122706567,0.7776390375143074,0.7895438204697822,0.8135833254031137,0.8331326567852336,0.8444926988921531,0.8472084027878914,0.8498439842009425,0.8528227700244111,0.85431893945236,0.853658933923698,0.7333809846473465,0.7639541056420194,0.8003692728004209,0.8118643666731201,0.8351331829832501,0.8545673289208272,0.8651466471004011,0.8676825886186402,0.8695197126584968,0.8708872972064581,0.8711731091447428,0.8688059985750172,1.0,0.0223707718561656,0.011356962548479116,0.004357544449229813,0.007611578882388823,13.851959115211685,99.38488105187314,61.231942482241635,150.0,0.0,0.0,0.0,0.0,0.0,0.0};
    private static double[] expected_20060116_ocean = new double[]{0.06674266859987917, 0.05270575492147841, 0.03769036645776095, 0.031849165195698755, 0.021960431891169186, 0.014177790616298265, 0.011078825564823107, 0.010191746071164211, 0.008881982756463491, 0.007173714204852539, 0.006386963829719161, 0.004658747017442469, 0.32467855566183945, 0.2884512272491393, 0.2456295171745833, 0.23164986468158405, 0.20525354908374344, 0.18319015278395315, 0.1720752996055535, 0.16915508269254328, 0.1653422058637851, 0.16202860056127777, 0.16118994534721376, 0.16199792625024437, -1.389900326206968, -1.3744722930516078, -1.1335582111452212, -1.231281451966078, -1.444680819181566, -2.5106991257274416, -3.024104703619604, -3.124877229933551, -3.772815968462364, -5.0832145577759915, -4.988199614357974, -5.8732493933601635, 0.6904226351948937, 0.7214784385127693, 0.7591106261114349, 0.7713302106031092, 0.7960940157601746, 0.8163549633867099, 0.8281367106305706, 0.8309968524362188, 0.8337801314415668, 0.8368638183060986, 0.838377226472904, 0.8378468556585238, 0.7052117533049348, 0.7361298016334451, 0.7737900022949076, 0.7858391233867273, 0.8105477285172643, 0.8316843202138003, 0.8428533777281287, 0.8454069707325789, 0.8475076177697702, 0.8495425689620075, 0.8500378842955408, 0.8483740487060345, 1.0, 0.0223707718561656, 0.006422416130678855, 0.004504157274280943, 0.006937181800187133, 8.050750542122676, 99.38488105187314, 54.671576695436706, 16.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private static double[] expected_20060116_cloud = new double[]{0.3026792354396823, 0.30184442063205946, 0.30204615620313796, 0.30077584863968293, 0.30103565556431366, 0.30053645302368576, 0.3007750118065773, 0.30027829659637095, 0.2988340137234909, 0.2988576437013362, 0.2653722559834979, 0.29456224574584383, 0.2908901116370063, 0.25674877530036766, 0.21649717094374216, 0.2033100317784956, 0.1784462500900985, 0.15777779466776848, 0.1472967892542698, 0.14456367528400174, 0.14101491032649519, 0.13781361552262159, 0.13693901946695108, 0.1371137683947289, -1.386507790105366, -1.3733702299204973, -1.128863707612271, -1.2282017747862017, -1.4436776221352048, -2.510294681815604, -3.0235023362535296, -3.123690989132453, -3.7726394118777753, -5.087047361160208, -4.987253701754459, -5.874893287148792, 0.6961074469031436, 0.7271424502596022, 0.7646126991706121, 0.7767477672056768, 0.8013620274515967, 0.8214356177657367, 0.8331250303724951, 0.8359523845229954, 0.8387182155459025, 0.8418370445317347, 0.8433527601037547, 0.8428462537952117, 0.7327177353042682, 0.7632992567810709, 0.7997448105799411, 0.8112508765095435, 0.8345571390211798, 0.8540327835092385, 0.8646234613889289, 0.8671578965690744, 0.8690166991314628, 0.8703718663937507, 0.8706609353311111, 0.8682973342639707, 1.0, 0.0223707718561656, 0.0014241824604709667, 0.0010000697724927685, 0.0017540387901189463, 63.58334107362182, 99.38488105187314, 104.62546428750217, 150.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private static double[] expected_20060116_land = new double[]{0.05940486599705885, 0.04834449806498461, 0.03641968459789666, 0.03394154426453425, 0.035856072733429424, 0.025940122404248308, 0.020817244287082558, 0.01996426189636208, 0.04440945866001555, 0.09507140109908684, 0.09938762634020121, 0.1088207950157593, 0.28648486873857437, 0.2514213331905337, 0.2093197583403113, 0.1955631343937434, 0.1691076237347668, 0.1472992481099547, 0.13603852768483837, 0.13298077984915868, 0.12931478452842485, 0.12567787054115356, 0.12437958767400378, 0.12362624878405744, -1.3881630009804127, -1.3738577057712051, -1.1310339391101554, -1.2299149684667965, -1.444307006305423, -2.510554742934745, -3.023897622746162, -3.1244205229594133, -3.7727546838019865, -5.08424330239808, -4.987916232998709, -5.873782659851999, 0.6564663125266599, 0.6877857371123901, 0.7264291338115312, 0.7391401650635325, 0.7647945525004156, 0.7862746294901817, 0.7986386966716597, 0.8017153295974249, 0.8049353424147832, 0.8082170847252746, 0.8097204128525639, 0.8095513612737929, 0.7355406138046319, 0.7660323291802648, 0.8023329376503239, 0.8137839856183664, 0.8369282178247136, 0.8562257567627498, 0.8667597937144694, 0.8692809010592252, 0.8710995042619107, 0.8724199759679322, 0.872702315145844, 0.8702901616372646, 1.0, 0.0223707718561656, 0.003813501868874135, 0.0031004424806989842, 0.004533286386653638, 12.357912184228814, 99.38488105187314, 62.87696475815681, 150.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private static double[] expected_20060116_subset_ocean = new double[]{0.057672590497990096, 0.04572532386883093, 0.03171267837750812, 0.02564602280962399, 0.01766455364844092, 0.011981326776316076, 0.009699751775363646, 0.009049154275418927, 0.00817508445561847, 0.006887354852911231, 0.006331618722387518, 0.005084252164038501, 0.2502424863996915, 0.2186348874224709, 0.1816629300082498, 0.16969065867994182, 0.14716381003561502, 0.12878625321929274, 0.11947029047284169, 0.11706000521650206, 0.11396306886401333, 0.11103564849122105, 0.11014019485376694, 0.10956574715237488, -1.386585729890295, -1.3734483233245625, -1.1291426831912954, -1.228295268574949, -1.4436396974755858, -2.5101810638388455, -3.0233873526082204, -3.1236172403498506, -3.7725467673162747, -5.087352960310123, -4.987165610533628, -5.874860091083954, 0.707951878478396, 0.7387299746768496, 0.7757149109190375, 0.7876361021856404, 0.8117717305925434, 0.8314012842796887, 0.8428215260354266, 0.8455478966801165, 0.8482119291368175, 0.851212049087988, 0.8526926233477163, 0.8520793439558707, 0.7503912240735137, 0.780214514837042, 0.8156372747420382, 0.8268474095466484, 0.849079799318006, 0.867564123807829, 0.87769458276413, 0.8801117854464984, 0.8817507654220595, 0.8828476629583373, 0.88304926987355, 0.8803523377297023, 1.0, 0.0223707718561656, 0.0019125708380260526, 0.0014215617235870382, 0.00243880311416531, 88.43805433194422, 99.38488105187314, 63.69633761560796, 28.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

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
