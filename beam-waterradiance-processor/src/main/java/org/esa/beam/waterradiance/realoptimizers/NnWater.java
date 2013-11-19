package org.esa.beam.waterradiance.realoptimizers;


class NnWater {

    // todo 3 tb/tb duplicated all over the code :-(
    private static final int[] lam29_meris11_ix = {1, 2, 4, 6, 11, 12, 15, 20, 22, 24, 25};
    private static final int[] lam29_modis9_ix = {1, 2, 4, 8, 9, 15, 18, 21, 26};
    private static final int[] lam29_seawifs8_ix = {1, 2, 4, 6, 10, 16, 23, 25};

    private final double[] innet;
    private double[] outnet;

    NnWater() {
        innet = new double[10];
        outnet = new double[35];
    }

    /**
     * * water nn **
     */
    NNReturnData nn_water(double[] conc_all, double[] rlw_nn, int n, s_nn_atdata nn_data, a_nn wat_net_for, AlphaTab alphaTab, NNReturnData nnReturnData) {
        //char *wat_net_name_for={"./neural_nets/23x7x28_77.3.net"};
        //char *wat_net_name_for={"./neural_nets/27x17x41_43.8.net"};
        //char *wat_net_name_for={"./neural_nets/water_for_b33_20111220/17_1070.2.net"};
        //char *wat_net_name_for={"./neural_nets/water_for_b33_20111220/27_697.2.net"};
        //char *wat_net_name_for={"./neural_nets/water_for_b33_rlw_20120118/17x27x17_120.2.net"};
        //char *wat_net_name_for={"./neural_nets/for_b33_20120114_nokd_27x17/27x17_153.7.net"};
        //String wat_net_name_for = "./neural_nets/for_water_rw29_20120318/37x17_754.1.net";

        innet[0] = nn_data.getSun_thet();
        innet[1] = nn_data.getView_zeni();
        innet[2] = nn_data.getAzi_diff_hl();
        innet[3] = nn_data.getTemperature();
        innet[4] = nn_data.getSalinity();

        innet[5] = conc_all[3];     // log_conc_chl
        innet[6] = conc_all[4];     // log_conc_det
        innet[7] = conc_all[5];     // log_conc_gelb
        innet[8] = conc_all[6];     // log_conc_min
        innet[9] = conc_all[7];     // log_bwit

        final int prepare = nn_data.getPrepare();
        if (prepare < 0) {
            nn_data.setPrepare(prepare + 2);
        }
        outnet = LevMarNN.use_the_nn(wat_net_for, innet, outnet, alphaTab);

        if (n == 11) {
            for (int ilam = 0; ilam < n; ilam++) {
                final int ix = lam29_meris11_ix[ilam];
                rlw_nn[ilam] = outnet[ix];
            }
        } else if (n == 9) {
            for (int ilam = 0; ilam < n; ilam++) {
                final int ix = lam29_modis9_ix[ilam];
                rlw_nn[ilam] = outnet[ix];
            }
        } else if (n == 8) {
            for (int ilam = 0; ilam < n; ilam++) {
                final int ix = lam29_seawifs8_ix[ilam];
                rlw_nn[ilam] = outnet[ix];
            }
        } else {
            System.arraycopy(outnet, 0, rlw_nn, 0, 29);
        }

        nnReturnData.setOutputValues(rlw_nn);
        nnReturnData.setNn_atdata(nn_data);
        return nnReturnData;
    }
}
