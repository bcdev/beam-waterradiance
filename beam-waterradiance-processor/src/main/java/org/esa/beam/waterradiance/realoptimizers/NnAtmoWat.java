package org.esa.beam.waterradiance.realoptimizers;

import java.io.IOException;

class NnAtmoWat {

    //char *atm_net_name_for={"27x57x47_47.7.net"};
    //char *atm_net_name_for={"./for_21bands_20110918/27x57x67_59.2.net"};
    //char *atm_net_name_for={"./for_21bands_20110918/17_3978.7.net"};

    //char *rhopath_net_name={"./for_21bands_20120112/rhopath/27x17_18.2.net"};
    //char *tdown_net_name  ={"./for_21bands_20120112/tdown/27x17_202.6.net"};
    //char *tup_net_name    ={"./for_21bands_20120112/tup/27x17_181.6.net"};

    //char *rhopath_net_name={"./oc_cci_20120222/ac_forward_all/ac_rhopath_b29/27x27_32.7.net"};
    //char *tdown_net_name  ={"./oc_cci_20120222/ac_forward_all/t_down_b29/27x27_73.7.net"};
    //char *tup_net_name    ={"./oc_cci_20120222/ac_forward_all/ac_tup_b29/27x27_75.4.net"};

    // new nets, RD 20130308:
    private static final String rhopath_net_name = "ac_rhopath_b29/17x37x31_121.8.net";
    private static final String tdown_net_name = "t_down_b29/17x37x31_89.4.net";
    private static final String tup_net_name = "ac_tup_b29/17x37x31_83.8.net";

    private static final double DEG_2_RAD = (3.1415927 / 180.0);
    private static final int[] lam29_meris11_ix = {1, 2, 4, 6, 11, 12, 15, 20, 22, 24, 25};

    private final a_nn rhopath_net;
    private final a_nn tdown_net;
    private final a_nn tup_net;
    private final a_nn wat_net_for;
    private final AlphaTab alphaTab;
    private final double[] innet;
    private final double[] tdown_nn;
    private final double[] rpath_nn;
    private final double[] tup_nn;
    private final double[] rw_nn;

    private double[] outnet1;
    private double[] outnet2;
    private double[] outnet3;
    private double[] rlw_nn;
    private final NnWater nnWater;

    NnAtmoWat(AlphaTab alphaTab) throws IOException {
        this.alphaTab = alphaTab;
        final NnResources nnResources = new NnResources();
        rhopath_net = LevMarNN.prepare_a_nn(nnResources.getAcForwardNetPath(rhopath_net_name));
        tdown_net = LevMarNN.prepare_a_nn(nnResources.getAcForwardNetPath(tdown_net_name));
        tup_net = LevMarNN.prepare_a_nn(nnResources.getAcForwardNetPath(tup_net_name));
        wat_net_for = LevMarNN.prepare_a_nn(nnResources.getNetWaterPath());

        nnWater = new NnWater();

        innet = new double[10];
        tdown_nn = new double[29];
        tup_nn = new double[29];
        outnet1 = new double[29];
        outnet2 = new double[29];
        outnet3 = new double[29];
        rlw_nn = new double[29];
        rpath_nn = new double[29];
        rw_nn = new double[29];
    }

    /**
     * atmosphere **
     * <p/>
     * conc_all: Input parameters
     * rtose_nn: Output values
     * m:        conc_all size
     * n:        size of ...
     * nn_data:  additional data
     */
    NNReturnData nn_atmo_wat(final double[] conc_all, final double[] rtosa_nn, s_nn_atdata nn_data, NNReturnData nnReturnData) {
        final double sun_thet = nn_data.getSun_thet();
        final double view_zeni = nn_data.getView_zeni();
        final double azi_diff_hl = nn_data.getAzi_diff_hl();
        //azi_diff_hl=180.0-azi_diff_hl;
        final double temperature = nn_data.getTemperature();
        final double salinity = nn_data.getSalinity();

        final double azimuth = DEG_2_RAD * azi_diff_hl;
        final double elevation = DEG_2_RAD * view_zeni;

        final double sin_elevation = Math.sin(elevation);
        final double x = sin_elevation * Math.cos(azimuth);
        final double y = sin_elevation * Math.sin(azimuth);
        final double z = Math.cos(elevation);


        final double log_aot = conc_all[0];
        final double log_ang = conc_all[1];
        final double log_wind = conc_all[2];
//            log_conc_chl = conc_all[3];
//            log_conc_det = conc_all[4];
//            log_conc_gelb = conc_all[5];
//            log_conc_min = conc_all[6];
//            log_conc_wit = conc_all[7];

        // innet[0] = sun_thet;
        // CHANGED for new nets, RD 20130308:
        innet[0] = Math.cos(DEG_2_RAD * sun_thet);

        innet[1] = x;
        innet[2] = y;
        innet[3] = z;

        //innet[4] = log_aot;
        //innet[5] = log_ang;
        //innet[6] = log_wind;

        // CHANGED for new nets, RD 20130308:
        innet[4] = Math.exp(log_aot);
        innet[5] = Math.exp(log_ang);
        innet[6] = Math.exp(log_wind);

        innet[7] = temperature;
        innet[8] = salinity;

        outnet1 = LevMarNN.use_the_nn(rhopath_net, innet, outnet1, alphaTab);
        outnet2 = LevMarNN.use_the_nn(tdown_net, innet, outnet2, alphaTab);
        outnet3 = LevMarNN.use_the_nn(tup_net, innet, outnet3, alphaTab);

        int nlam = rtosa_nn.length; // if n == 11, then iteration for LM fit, if > 11, then computation for full spectrum
        if (nlam == 11) {
            for (int ilam = 0; ilam < nlam; ilam++) {
                final int ix = lam29_meris11_ix[ilam];
                rpath_nn[ilam] = outnet1[ix];
                tdown_nn[ilam] = outnet2[ix];
                tup_nn[ilam] = outnet3[ix];
            }
            nnReturnData = nnWater.nn_water(conc_all, rlw_nn, rtosa_nn.length, nn_data, wat_net_for, alphaTab, nnReturnData);
            rlw_nn = nnReturnData.getOutputValues();
            for (int ilam = 0; ilam < 11; ilam++) {
//                final double rlwnn = rlw_nn[ilam];
                final double rlwnn = Math.exp(rlw_nn[ilam]);  // new net 27x17_754.1 --> 37x77x97_86.7.net, 20130517
                rw_nn[ilam] = rlwnn;//M_PI;
                rtosa_nn[ilam] = rpath_nn[ilam] + rw_nn[ilam] * tdown_nn[ilam] * tup_nn[ilam];
            }
        } else {
            nlam = 29; // all bands for other calculations
            for (int ilam = 0; ilam < nlam; ilam++) {
                rpath_nn[ilam] = outnet1[ilam];
                tdown_nn[ilam] = outnet2[ilam];
                tup_nn[ilam] = outnet3[ilam];
            }
            nnReturnData = nnWater.nn_water(conc_all, rlw_nn, nlam, nn_data, wat_net_for, alphaTab, nnReturnData);
            rlw_nn = nnReturnData.getOutputValues();
            nn_data = nnReturnData.getNn_atdata();
            for (int ilam = 0; ilam < nlam; ilam++) {
//                final double rlwnn = rlw_nn[ilam];
                final double rlwnn = Math.exp(rlw_nn[ilam]);  // new net 27x17_754.1 --> 37x77x97_86.7.net, 20130517
                rw_nn[ilam] = rlwnn;//*M_PI;// ! with pi included, 21 bands
                rtosa_nn[ilam] = rpath_nn[ilam] + rw_nn[ilam] * tdown_nn[ilam] * tup_nn[ilam];
                nn_data.setTdown_nn(ilam, tdown_nn[ilam]);
                nn_data.setTup_nn(ilam, tup_nn[ilam]);
                nn_data.setRw_nn(ilam, rw_nn[ilam]);
            }

            nn_data.setRpath_nn(rpath_nn);
        }

        nnReturnData.setOutputValues(rtosa_nn);
        nnReturnData.setNn_atdata(nn_data);
        return nnReturnData;
    }
}
