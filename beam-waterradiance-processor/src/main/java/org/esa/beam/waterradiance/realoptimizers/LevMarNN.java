package org.esa.beam.waterradiance.realoptimizers;

import Jama.Matrix;
import org.esa.beam.util.SystemUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: tonio
 * Date: 08.05.13
 * Time: 11:31
 * To change this template use File | Settings | File Templates.
 */
public class LevMarNN {

    int FR_TAB = 3700;
    int RR_TAB = 925;

    private double[][] frlam = new double[FR_TAB][15];
    private double[][] fredtoa = new double[FR_TAB][15];
    private double[][] rrlam = new double[FR_TAB][15];
    private double[][] rredtoa = new double[FR_TAB][15];

    int N_ALPHA = 100000;
    double ALPHA_ANF = -10.0;
    double[] alpha_tab = new double[N_ALPHA];
    double rec_delta_alpha;

    private static int NLAM = 40;
    private static int LM_OPTS_SZ = 5; /* max(4, 5) */
    private static int LM_INFO_SZ = 10;
    private static int LM_ERROR = -1;
    private static double LM_INIT_MU = 1E-03;
    private static double LM_STOP_THRESH = 1E-7; // was 1E-17
    private static double LM_DIFF_DELTA = 1E-02; // was 1E-06
    private static double deg2rad = (Math.PI / 180.0);
    double M_PI = 3.1416;

    static int[] lam29_meris11_ix = {1, 2, 4, 6, 11, 12, 15, 20, 22, 24, 25};
    static int[] lam29_meris12_ix = {1, 2, 4, 6, 11, 12, 15, 19, 20, 22, 24, 25};
    double[] ozon_meris12 = {0.0002179, 0.002814, 0.02006, 0.04081, 0.104, 0.109, 0.0505, 0.03526, 0.01881, 0.008897, 0.007693, 0.002192}; // L.Bourg 2010
    static double[] merband12 = {412.3, 442.3, 489.7, 509.6, 559.5, 619.4, 664.3, 680.6, 708.1, 753.1, 778.2, 864.6};
    int[] merband12_index = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12};


    public int levmar_nn(int detector, double[] input, int input_length, double[] output, int output_length, double[] debug_dat) {
        int FIRST = 1;
        double h2o_cor_poly[] = {0.3832989, 1.6527957, -1.5635101, 0.5311913}; // polynom coefficients for band708 correction
        int merband12_index[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12};
        int meris11_outof_12_ix[] = {0, 1, 2, 3, 4, 5, 6, 8, 9, 10, 11};
        int lam21_meris12_ix[] = {0, 1, 2, 3, 8, 9, 10, 13, 14, 16, 18, 19};

        double[] p = new double[8];
        double[] x = new double[NLAM];
        double[] xb = new double[NLAM];
        double[] xr = new double[NLAM];
        double[] opts = new double[LM_OPTS_SZ];
        double[] info = new double[LM_INFO_SZ];
        double[] x11 = new double[11];
        double[] x11_vor = new double[11];
        double[] p_alt = new double[8];
        double[] rw1 = new double[NLAM];
        double[] rw2 = new double[NLAM];
        double[] rw_2flow = new double[NLAM];
        double[] rw_min = new double[NLAM];
        double[] rw_max = new double[NLAM];
        double log_apart, log_agelb, log_apig, log_bpart, log_bwit, sun_thet, view_zeni, azi_diff_hl, temperature, salinity, ozone;
        double log_conc_chl, log_conc_det, log_conc_gelb, log_conc_min, log_conc_wit, aot_550, ang_865_443, a_pig, a_gelb, a_part, b_part, b_wit;
        double a_pig_stdev, a_part_stdev, a_gelb_stdev, b_part_stdev, b_wit_stdev;

        double dif, difp, agelb, btot;
        double[] nn_data = new double[60];
        double prepare, ang, aot, wind;
        s_nn_atdata nn_at_data = new s_nn_atdata();
        int i, j;
        int n, m, ret, SMILE;

        double pixel_x, pixel_y, sun_azi, view_azi, azi_diff_deg;
        double[] sun_spec = new double[15];
        double[] sun_lam = new double[15];

        char[] procver = new char[80];
        char[] site = new char[80];
        char[] pi = new char[80];
        char[] time_is = new char[80];
        char[] pqc = new char[80];
        char[] mqc = new char[80];
        char[] time_is1 = new char[80];
        char[] time_is_2 = new char[80];
        char[] time = new char[80];
        char[] resolution = new char[4];
        int land, cloud, ice_haze, white_scatter, high_glint, medium_glint, pcd_1_13, pde_14, pcd_15, pcd_16, pcd_17, pcd_18, pcd_19, oadb, absoa_dust, bpac_on, case2_s, case2_anom;
        int detectorIndex = -1;
        int na_flag, isite, pixel_valid;
        double lat_is, lon_is, thetas_is, chl_is, aot_870_is_1, aot_870_is_2, alpha_is_1, alpha_is_2, lat, lon, delta_azimuth, scatt_angle, windm, press_ecmwf, ozon_ecmwf, vapour_ecmwf;
        double altitude, chl1, chl2, spm, odoc, vapr, tau_aer_05, tau_aer_13, aer_1, aer_2, aer_mix;
        double[] rho_wn_is = new double[15];
        double[] rho_wn_isme = new double[15];
        double[] rn = new double[15];
        double[] rho_wn = new double[15];
        double[] toar = new double[15];
        double[] rho_toa = new double[15];
        double[] rho_gc = new double[15];
        double[] rho_ray = new double[15];
        double[] rho_aer = new double[15];
        double[] t_down = new double[15];
        double[] t_up = new double[15];
        double[] rho_w_c2 = new double[4];
        double sun_zenith, view_zenith, alpha, cos_teta_sun, sin_teta_sun, cos_teta_view, sin_teta_view, cos_azi_diff;
        double sun_azimuth, view_azimuth, surf_pressure, ozhone, wind_x, wind_y, cos_sun_zenith;
        double[] trans_ozon = new double[NLAM];
        double[] rho_toa_ocz = new double[NLAM];
        double[] solar_flux = new double[NLAM];
        double[] RL_toa = new double[NLAM];
        char[] buffer = new char[3600];
        long icas, ncas, orbit;
        int nlam, ilam, ilami, ix;
        File fp_tab;

        Matrix covar_out = new Matrix(8, 8);

        double[] conc_at = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        double[] conc_at_min = new double[8];
        double[] conc_at_max = new double[8];
        double[] stdev = new double[8];

        // lower and upper boundary for variables aot, ang, wind, log_conc_chl, log_conc_det, log_conc_gelb, log_conc_min
        double[] lb = {0.001, 0.001, 0.001, -13.96, -15.42, -16.38, -15.87, 0.0};
        double[] ub = {1.0, 2.2, 10.0, 3.9, 2.294, 1.599, 4.594, 1.1};
        double lub;
        String norm_net_name = "27x41x27_23.3.net";
        String nominal_lam_sun_name = "./smile/nominal_lam_sun.txt";

        char[] errmsg = new char[1024];
        a_nn norm_net = null;
//        a_nn[] prepare_a_nn(char *filename); //todo instead of a_nn*: call prepare_a_nn
//        void use_the_nn(a_nn *norm_net, double *innet, double *outnet);
//        void make_alphatab(void);

        double[] nn_in = new double[40];
        double[] nn_out = new double[40];
        double[] rwn1 = new double[40];
        double[] rwn2 = new double[40];
        double[] rlw1 = new double[40];
        double[] rlw2 = new double[40];
        double trans708, X2;
        int flag1, flag2, flag3, flag4, flag5;

        double[] nomi_lam = new double[15];
        double[] nomi_sun = new double[15];
        double[] nomi_lam12 = new double[15];
        double[] nomi_sun12 = new double[15];
        double smile_lam;
        double tau_rayl_smile_rat, cos_gamma_plus, rayleigh_reflect, teta_view_rad, teta_sun_rad, conc_ozon;
        double[] L_toa = new double[15];
        double[] Ed_toa = new double[15];
        double[] L_toa_ocz = new double[15];
        double surf_press, rayl_rel_mass_tosa, rayl_rel_mass_toa_tosa, rayl_mass_toa_tosa;
        double cos_scat_ang, phase_rayl_min, phase_rayl_plus;
        double[] tau_rayl_standard = new double[15];
        double[] tau_rayl_toa_tosa = new double[15];
        double[] tau_rayl_smile = new double[15];
        double[] L_rayl_toa_tosa = new double[15];
        double[] L_rayl_smile = new double[15];
        double[] L_toa_corr = new double[15];
        double[] rho_tosa_corr = new double[15];
        double[] Ed_toa_smile_rat = new double[15];
        double[] Ed_toa_smile_corr = new double[15];
        double[] L_tosa = new double[15];
        double[] Ed_tosa = new double[15];
        double[] trans_extra = new double[15];
        double[] trans_rayl_press = new double[15];
        double[] trans_rayl_smile = new double[15];
        double[] trans_rayl_pressd = new double[15];
        double[] trans_rayl_smiled = new double[15];
        double[] trans_rayl_pressu = new double[15];
        double[] trans_rayl_smileu = new double[15];
        double[] trans_ozond = new double[15];
        double[] trans_ozonu = new double[15];
//        void smile_tab_ini();

        /***********************************************/
        if (FIRST == 1) {
            /** network for normalisation */
            make_alphatab();
            norm_net = prepare_a_nn(norm_net_name);
            /*********** tables for smile correction **************/
            smile_tab_ini();
            nn_at_data.prepare = -1; // prepare neural networks only once


		/* table with nominal wavelengths and solar flux */
            fp_tab = open_auxfile(nominal_lam_sun_name);
            try {
                BufferedReader reader = new BufferedReader(new FileReader(fp_tab));
                for (int count = 0; count < 15; count++) {
                    String line = reader.readLine();
                    final String[] tableLine = line.split("\t");
                    nomi_lam[count] = Double.parseDouble(tableLine[0]);
                    nomi_sun[count] = Double.parseDouble(tableLine[1]);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            for (i = 0; i < 12; i++) {
                ilami = merband12_index[i];
                nomi_lam12[i] = nomi_lam[ilami];
                nomi_sun12[i] = nomi_sun[ilami];
            }
            //FIRST=0; //do this later down
        } // end of FIRST
        /***********************************************/

        // input data
        sun_zenith = input[0];
        sun_azimuth = input[1];
        view_zenith = input[2];
        view_azimuth = input[3];
        surf_pressure = input[4];
        ozone = input[5];
        wind_x = input[6];
        wind_y = input[7];
        temperature = input[8];
        salinity = input[9];

        cos_sun_zenith = Math.cos(sun_zenith * deg2rad);

        for (i = 0; i < 15; i++) {
            L_toa[i] = input[i + 10];
            solar_flux[i] = input[i + 25];

            Ed_toa[i] = solar_flux[i] * cos_sun_zenith;
            RL_toa[i] = L_toa[i] / Ed_toa[i];
        }
        // end of input data

        delta_azimuth = Math.abs(view_azimuth - sun_azimuth);
        if (delta_azimuth > 180.0) delta_azimuth = 180.0 - delta_azimuth;

        // nn_at_data[0]= -1.0; // prepare
        nn_at_data.sun_thet = sun_thet = sun_zenith;
        nn_at_data.view_zeni = view_zeni = view_zenith;
        nn_at_data.azi_diff_hl = azi_diff_hl = delta_azimuth;
        nn_at_data.temperature = temperature;
        nn_at_data.salinity = salinity;


        //nn_at_data[0]= -1.0;
        if (nn_data[0] < 1.0)
            nn_data[0] = 0.0;  //if < 1.2345 then NN has to be created
        m = 8;
        n = 12;

        /* ++++ angles ++++ */

        cos_teta_sun = Math.cos(sun_zenith * deg2rad);
        cos_teta_view = Math.cos(view_zenith * deg2rad);
        sin_teta_sun = Math.sin(sun_zenith * deg2rad);
        sin_teta_view = Math.sin(view_zenith * deg2rad);
        cos_azi_diff = Math.cos(delta_azimuth * deg2rad);
        teta_view_rad = view_zenith * deg2rad;
        teta_sun_rad = sun_zenith * deg2rad;
        conc_ozon = ozone;

       /*+++ ozone correction +++*/

        nlam = 12;
        for (i = 0; i < nlam; i++) {
            //trans_ozon[i]= exp(-ozon_meris12[i]* ozone / 1000.0 *(1.0/cos_teta_sun+1.0/cos_teta_view));
            trans_ozond[i] = Math.exp(-ozon_meris12[i] * ozone / 1000.0 * (1.0 / cos_teta_sun));
            trans_ozonu[i] = Math.exp(-ozon_meris12[i] * ozone / 1000.0 * (1.0 / cos_teta_view));
            trans_ozon[i] = trans_ozond[i] * trans_ozonu[i];
        }

        for (i = 0; i < 12; ++i) {
            ix = merband12_index[i];
            L_toa_ocz[i] = L_toa[ix] / trans_ozon[i]; // shall be both ways RD20120318
        }

        /*+++ ozone correction +++*/
        nlam = 12;
        for (i = 0; i < nlam; i++) {
            //trans_ozon[i]= exp(-ozon_meris12[i]* ozone / 1000.0 *(1.0/cos_teta_sun+1.0/cos_teta_view));
            trans_ozond[i] = Math.exp(-ozon_meris12[i] * ozone / 1000.0 * (1.0 / cos_teta_sun));
            trans_ozonu[i] = Math.exp(-ozon_meris12[i] * ozone / 1000.0 * (1.0 / cos_teta_view));
            trans_ozon[i] = trans_ozond[i] * trans_ozonu[i];
        }

        for (i = 0; i < 12; ++i) {
            ix = merband12_index[i];
            L_toa_ocz[i] = L_toa[ix] / trans_ozon[i]; // shall be both ways RD20120318
        }

        /* +++ water vapour correction for band 9 +++++ */

        //X2=rho_900/rho_885;
        X2 = RL_toa[14] / RL_toa[13];
        trans708 = h2o_cor_poly[0] + h2o_cor_poly[1] * X2 + h2o_cor_poly[2] * X2 * X2 + h2o_cor_poly[3] * X2 * X2 * X2;

        L_toa[8] /= trans708;

        	/*+++ smile and pressure correction +++*/

	/* calculate relative airmass rayleigh correction for correction layer*/
        surf_press = surf_pressure;
        rayl_rel_mass_tosa = surf_press / 1013.2;
        rayl_rel_mass_toa_tosa = (surf_press - 1013.2) / 1013.2; //?? oder rayl_mass_toa_tosa =surf_press - 1013.2; // RD20120105
        rayl_mass_toa_tosa = surf_press - 1013.2; // RD20120105

	/* calculate phase function for rayleigh path radiance*/
        cos_scat_ang = -cos_teta_view * cos_teta_sun - sin_teta_view * sin_teta_sun * cos_azi_diff; // this is the scattering angle without fresnel reflection
        cos_gamma_plus = cos_teta_view * cos_teta_sun - sin_teta_view * sin_teta_sun * cos_azi_diff; // for fresnel reflection
        phase_rayl_plus = 0.75 * (1.0 + cos_gamma_plus * cos_gamma_plus);
        phase_rayl_min = 0.75 * (1.0 + cos_scat_ang * cos_scat_ang);

        	/* calculate optical thickness of rayleigh for correction layer, lam in micrometer */

        for (ilam = 0; ilam < nlam; ilam++) {
            ix = merband12_index[ilam];
            tau_rayl_standard[ilam] = 0.008735 * Math.pow(merband12[ilam] / 1000.0, -4.08);/* lam in �m */
            tau_rayl_toa_tosa[ilam] = tau_rayl_standard[ilam] * rayl_rel_mass_toa_tosa;
            //tau_rayl_toa_tosa[ilam] = tau_rayl_standard[ilam] * rayl_mass_toa_tosa; // RD20120105
            L_rayl_toa_tosa[ilam] = Ed_toa[ix] * tau_rayl_toa_tosa[ilam] * phase_rayl_min / (4 * M_PI) * (1.0 / cos_teta_view);
            trans_rayl_press[ilam] = Math.exp(-tau_rayl_toa_tosa[ilam] * (1.0 / cos_teta_view + 1.0 / cos_teta_sun));
            trans_rayl_pressd[ilam] = Math.exp(-tau_rayl_toa_tosa[ilam] * (1.0 / cos_teta_sun));
            trans_rayl_pressu[ilam] = Math.exp(-tau_rayl_toa_tosa[ilam] * (1.0 / cos_teta_view));
        }

        	/* calculate rayleigh for correction of smile, lam in micrometer */

        for (ilam = 0; ilam < nlam; ilam++) {
            ix = merband12_index[ilam];
            detectorIndex = detector;
            smile_lam = rrlam[detectorIndex][ix];
            tau_rayl_smile[ilam] = 0.008735 * Math.pow(smile_lam / 1000.0, -4.08);
            L_rayl_smile[ilam] = Ed_toa[ix] * (tau_rayl_smile[ilam] - tau_rayl_standard[ilam]) * phase_rayl_min / (4 * M_PI) * (1.0 / cos_teta_view);
            trans_rayl_smile[ilam] = Math.exp(-(tau_rayl_smile[ilam] - tau_rayl_standard[ilam]) * (1.0 / cos_teta_view + 1.0 / cos_teta_sun));
            trans_rayl_smiled[ilam] = Math.exp(-(tau_rayl_smile[ilam] - tau_rayl_standard[ilam]) * (1.0 / cos_teta_sun));
            trans_rayl_smileu[ilam] = Math.exp(-(tau_rayl_smile[ilam] - tau_rayl_standard[ilam]) * (1.0 / cos_teta_view));
        }

            /* +++++ Esun smile correction ++++++ */
        for (ilam = 0; ilam < nlam; ilam++) {
            ix = merband12_index[ilam];
            Ed_toa_smile_rat[ilam] = rredtoa[detectorIndex][ix];///nomi_sun[ix];
            Ed_toa_smile_corr[ilam] = Ed_toa[ix] * Ed_toa_smile_rat[ilam]; // RD20120105 geaendert von / in *, wieder zurueck 20120119
        }
        SMILE = 1;
        if (SMILE == 1) {
        /* subtract all correcting radiances */
            for (ilam = 0; ilam < nlam; ilam++) {
                ix = merband12_index[ilam];
                // L_tosa[ilam] = ((L_toa[ix]-L_rayl_smile[ilam])-L_rayl_toa_tosa[ilam])/(trans_ozon[ilam]*trans_rayl_smile[ilam]);
                L_tosa[ilam] = L_toa[ix] / (trans_ozon[ilam] * trans_rayl_press[ilam]/**trans_rayl_smile[ilam]*/) - L_rayl_toa_tosa[ilam] + L_rayl_smile[ilam];//*trans_rayl_smile[ilam]);
                Ed_tosa[ilam] = Ed_toa_smile_corr[ilam];//*trans_rayl_smiled[ilam]*trans_rayl_pressd[ilam];
                rho_tosa_corr[ilam] = L_tosa[ilam] / Ed_tosa[ilam] * M_PI;
                x[ilam] = xb[ilam] = rho_tosa_corr[ilam];
            }
        } else { /* subtract only correction for ozone */
            for (ilam = 0; ilam < nlam; ilam++) {
                ix = merband12_index[ilam];
                L_tosa[ilam] = L_toa[ix] / trans_ozon[ilam];//-L_rayl_toa_tosa[ilam]-L_rayl_smile[ilam];
                Ed_tosa[ilam] = Ed_toa[ix];
                rho_tosa_corr[ilam] = L_tosa[ilam] / Ed_tosa[ilam] * M_PI;
                x[ilam] = xb[ilam] = rho_tosa_corr[ilam];
            }
        }

        	/* extra trans for rho_water: ozon, rayl_smile, rayl_press */
        for (ilam = 0; ilam < nlam; ilam++) {
            trans_extra[ilam] = trans_ozon[ilam] * trans_rayl_press[ilam] * trans_rayl_smile[ilam];
        }

	/* +++++ vicarious adjustment +++++*/
//        if (0) {
//            x[10] /= 0.98;
//            xb[10] = x[10];
//
//            x[11] /= 0.93;
//            xb[11] = x[11];
//        }

        	/* initial parameters estimate: */
        icas = -1L;
    /*
    input  5 is log_aot in [-4.605000,0.000000]
	input  6 is log_angstrom in [-3.817000,0.788500]
	input  7 is log_wind in [-2.303000,2.303000]
	input  8 is temperature in [0.000047,36.000000]
	input  9 is salinity in [0.000004,43.000000]
	*/

        //lower boundary
        lb[0] = -4.6; // aot
        lb[1] = -3.8; // ang
        lb[2] = -2.3; // wind
        lb[3] = -19.9; // apig
        lb[4] = -15.89; // apart
        lb[5] = -17.23; // agelb
        lb[6] = -15.8; // bpart
        lb[7] = -14.92; // bwit

        // upper boundary
        ub[0] = 0.0;   // aot
        ub[1] = 0.788; // ang
        ub[2] = 2.3;   // wind
        ub[3] = 0.685; // apig
        ub[4] = 2.297; // apart
        ub[5] = 1.6;   // agelb
        ub[6] = 4.598; // bpart
        ub[7] = 4.599; // bwit

        if (FIRST == 1) {
            for (i = 0; i < m; i++) {
                if (lb[i] < 0.0)
                    p[i] = lb[i] - lb[i] * 0.2;
                else
                    p[i] = lb[i] + lb[i] * 0.2;
            }
            FIRST = 0;
                    /*
		} else{
		for(i=0;i<m;i++)
		p[i]=p_alt[i];
		}
		*/
        } else {
            for (i = 0; i < m; i++) {
                lub = Math.abs(ub[i] - lb[i]);
			/*
			if(lb[i]<0.0)
				p[i]=lb[i]-lb[i]*0.2;
			else
				p[i]=lb[i]+lb[i]*0.2;
				*/
                if (ub[i] < 0.0)
                    p[i] = ub[i] - lub * 0.2;
                else
                    p[i] = ub[i] - lub * 0.2;
            }
            p[0] = Math.log(0.1);   // tau550
            p[1] = Math.log(1.0);   // ang
            p[2] = Math.log(3.0);   // wind
            p[3] = Math.log(0.005); // apig
            p[4] = Math.log(0.005); // adet
            p[5] = Math.log(0.005); // agelb
            p[6] = Math.log(0.01);  // bspm
            p[7] = Math.log(0.01);  // bwit
        }

        // select the 11 bands for iterations
        for (i = 0; i < 11; i++) {
            ix = meris11_outof_12_ix[i];
            x11[i] = x11_vor[i] = x[ix];
        }
        m = 8;
        n = 11;

        	/* optimization control parameters; passing to levmar NULL instead of opts reverts to defaults */
        //  opts[0]=LM_INIT_MU; opts[1]=1E-15; opts[2]=1E-15; opts[3]=1E-20;
        opts[0] = LM_INIT_MU;
        opts[1] = 1E-10;
        opts[2] = 1E-10;
        opts[3] = 1E-10;
        //  opts[4]=LM_DIFF_DELTA; // relevant only if the finite difference Jacobian version is used
        //  opts[4]= 0.2; // relevant only if the finite difference Jacobian version is used
        opts[4] = -0.1; // relevant only if the finite difference Jacobian version is used

        	/* invoke the optimization function */
//        ret = dlevmar_bc_dif(nn_atmo_wat, p, x11, m, n, lb, ub, 150, opts, info, NULL, & covar_out[0][0],&nn_at_data)
//        ; // without Jacobian

        nn_atmo_watForwardModel model = new nn_atmo_watForwardModel();
        model.init(x11, nn_at_data);
        BreakingCriterion breakingCriterion = new BreakingCriterionImpl(150, 1e-10);
        LevenbergMarquardtOptimizer3 optimizer = new LevenbergMarquardtOptimizer3();
        p = optimizer.solveConstrainedLevenbergMarquardt(model, new CostFunctionImpl(), p, x11, breakingCriterion, lb, ub);

        for (i = 0; i < m; i++) {
            conc_at[i] = p[i];
            p_alt[i] = p[i];
        }

        n = 11;
        model.init(x11, nn_at_data);
        x11 = model.getModeledSignal(conc_at);
        nn_at_data = model.getNn_data();

        n = 29;
        model.init(x, nn_at_data);
        x = model.getModeledSignal(conc_at);
        nn_at_data = model.getNn_data();

        /**********************************************************/
//        if (NORMALIZE) {
		/* normalize water leaving radiance reflectances */

        // requires first to make RLw again
        for (i = 0; i < 12; i++) {
            rlw1[i] = rw1[i] / M_PI;
            rlw2[i] = rw2[i] / M_PI;
            if (rlw2[i] < 0.0)
                rlw2[i] = 1.0e-6;
        }

        nn_in[0] = sun_thet;
        nn_in[1] = view_zeni;
        nn_in[2] = azi_diff_hl;
        nn_in[3] = temperature;
        nn_in[4] = salinity;
        for (i = 5; i < 17; i++) {
            nn_in[i] = rlw1[i - 5];
        }

        nn_out = use_the_nn(norm_net, nn_in, nn_out);

        for (i = 0; i < 12; i++) {
            rwn1[i] = nn_out[i] * M_PI;
        }

        for (i = 5; i < 17; i++)
            nn_in[i] = rlw2[i - 5];

        nn_out = use_the_nn(norm_net, nn_in, nn_out);

        for (i = 0; i < 12; i++) {
            rwn2[i] = nn_out[i] * M_PI;
        }

//        } // end normalize

        /*********************************************************/

        // put all results into output
        nlam = 12;
        for (i = 0; i < nlam; i++) {
            ix = lam29_meris12_ix[i];
            output[i] = rho_tosa_corr[i] / M_PI;
            output[i + nlam] = nn_at_data.rpath_nn[ix];
            output[i + nlam * 2] = nn_at_data.rw_nn[ix];
            output[i + nlam * 3] = nn_at_data.tdown_nn[ix];
            output[i + nlam * 4] = nn_at_data.tup_nn[ix];
        }
        output[60] = aot_550 = Math.exp(p[0]);
        output[61] = ang_865_443 = Math.exp(p[1]);
        output[62] = a_pig = Math.exp(p[3]);
        output[63] = a_part = Math.exp(p[4]);
        output[64] = a_gelb = Math.exp(p[5]);
        output[65] = b_part = Math.exp(p[6]);
        output[66] = b_wit = Math.exp(p[7]);
        output[67] = info[1]; // sum_sq
        output[68] = (double) info[5];

        return (0);

    }

    private void make_alphatab() {
        double sum, delta;
        int i;

        delta = -2. * ALPHA_ANF / (N_ALPHA - 1);
        sum = ALPHA_ANF + delta / 2.;
        for (i = 0; i < N_ALPHA; i++) {
            alpha_tab[i] = calpha(sum);
            sum += delta;
        }
        rec_delta_alpha = 1. / delta;
    }

    private double calpha(double x) {
        return (1. / (1. + Math.exp(-x)));
    }

    private a_nn prepare_a_nn(String filename) {
        File fp;
        char ch;
        int i;
        a_nn res = new a_nn();
        res.setFilename(filename);

//        res = new a_nn[](a_nn*)malloc(sizeof(a_nn));
//        res->filename=strdup(filename);

        fp = open_auxfile(filename);

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fp));
            String line = reader.readLine();
            boolean startReadingAnn = false;
            while (line != null) {
                if (line.startsWith("#")) {
                    startReadingAnn = true;
//                    line = reader.readLine();
                    line = reader.readLine();
                }
                if (startReadingAnn) {
                    final int nnin = Integer.parseInt(line);
                    res.setNnin(nnin);
                    double[] inmin = new double[nnin];
                    double[] inmax = new double[nnin];
                    for (int j = 0; j < nnin; j++) {
                        line = reader.readLine().trim();
                        final String[] range = line.split(" ");
                        inmin[j] = Double.parseDouble(range[0]);
                        inmax[j] = Double.parseDouble(range[1]);
                    }
                    res.setInmin(inmin);
                    res.setInmax(inmax);
                    line = reader.readLine();
                    final int nnout = Integer.parseInt(line);
                    res.setNnout(nnout);
                    double[] outmin = new double[nnout];
                    double[] outmax = new double[nnout];
                    for (int j = 0; j < nnout; j++) {
                        line = reader.readLine().trim();
                        final String[] range = line.split(" ");
                        outmin[j] = Double.parseDouble(range[0]);
                        outmax[j] = Double.parseDouble(range[1]);
                    }
                    res.setOutmin(outmin);
                    res.setOutmax(outmax);
//                    startReadingAnn = false;
                    break;
                }
                line = reader.readLine();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        res.setNn(make_ff_from_file(filename));
        return res;
    }

    private File open_auxfile(String fileName) {
        String path;
        String home;
//        char[] home = System.getenv("HOME").toCharArray();
        char[] mapred_home = "/home/mapred".toCharArray();
        File fd;

        if (System.getProperty("os.name").contains("indows")) {
            home = System.getenv("HOMEPATH");
        } else {
            home = System.getenv("HOME");
        }

//        SystemUtils.getApplicationDataDir()

        StringBuilder builder = new StringBuilder(home);
        builder.append("/Projekte/beam-waterradiance/beam-waterradiance-processor/");
        builder.append(fileName);
//        path = concat_path(home, fileName);

        return new File(builder.toString());
    }

    private char[] concat_path(char[] homeDir, char[] fileName) {
        char[] auxPath = ".beam/beam-waterradiance-processor/auxdata".toCharArray();
        StringBuilder buf = new StringBuilder();
        buf.append(homeDir);
        buf.append("/");
        buf.append(auxPath);
        buf.append("/");
        buf.append(fileName);
        return buf.toString().toCharArray();
    }

    private feedforward make_ff_from_file(String filename) {
        long pl, n, id1, id2, id3;
        long[] s;
        feedforward ff = new feedforward();
        File fp;
        char ch;

        fp = open_auxfile(filename);

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fp));
            String line = reader.readLine();
            boolean startReadingFeedForward = false;
            while (line != null) {
                if (line.startsWith("$")) {
                    startReadingFeedForward = true;
                    line = reader.readLine();
                }
                if (startReadingFeedForward) {
                    final String[] initLine = line.split(" ");
                    int numberOfPlanes = Integer.parseInt(initLine[0].substring(initLine[0].indexOf("=") + 1).trim());
                    int[] size = new int[numberOfPlanes];
                    int[] subSize = new int[numberOfPlanes - 1];
                    for (int i = 0; i < numberOfPlanes; i++) {
                        size[i] = Integer.parseInt(initLine[i + 1]);
                        if (i > 0) {
                            subSize[i - 1] = size[i];
                        }
                    }
                    double[][] bias = make_vecv(numberOfPlanes - 1, subSize);
                    double[][][] wgt = make_mtxv(numberOfPlanes, size);
                    double[][] act = make_vecv(numberOfPlanes, size);
                    for (int i = 0; i < numberOfPlanes - 1; i++) {
                        line = reader.readLine();
                        for (int j = 0; j < subSize[i]; j++) {
                            line = reader.readLine();
                            bias[i][j] = Double.parseDouble(line);
                        }
                    }
                    for (int i = 0; i < numberOfPlanes - 1; i++) {
                        line = reader.readLine();
                        for (int j = 0; j < size[i + 1]; j++) {
                            for (int k = 0; k < size[i]; k++) {
                                line = reader.readLine();
                                wgt[i][j][k] = Double.parseDouble(line);
                            }
                        }
                    }
                    ff.setNplanes(numberOfPlanes);
                    ff.setSize(size);
                    ff.setBias(bias);
                    ff.setWgt(wgt);
                    ff.setAct(act);
                    break;
                }
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return ff;
    }

    private double[][][] make_mtxv(int n, int[] s) {
        int i, j;
        double[][][] wgt = new double[n - 1][][];
        for (i = 0; i < n - 1; i++) {
            wgt[i] = new double[s[i + 1]][];
            for (j = 0; j < s[i + 1]; j++) {
                wgt[i][j] = new double[s[i]];
            }
        }
        return wgt;
    }

    private double[][] make_vecv(int n, int[] s) {
        int i;
        double[][] bias;
        bias = new double[n][];
        for (i = 0; i < n; i++) {
            bias[i] = new double[s[i]];
        }
        return bias;
    }

    private void smile_tab_ini() {
        File fp_ini, fp_tab;
        char[] name = new char[500];
        char[] buf = new char[500];
        int i, k, iband, ipix, num;
        double[] nomi_lam = new double[15];
        double[] nomi_sun = new double[15];
        String smile_name = "";
        String fr_lam_tab_name = "./smile/central_wavelen_fr.txt";
        String rr_lam_tab_name = "./smile/central_wavelen_rr.txt";
        String fr_edtoa_tab_name = "./smile/sun_spectral_flux_fr.txt";
        String rr_edtoa_tab_name = "./smile/sun_spectral_flux_rr.txt";
        String nominal_tab_name = "./smile/nominal_lam_sun.txt";

        /* read the tables */
        fp_tab = open_auxfile(fr_lam_tab_name);
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fp_tab));
            String line = reader.readLine(); // header
            for (int count = 0; count < FR_TAB; count++) {
                line = reader.readLine();
                final String[] tableLine = line.split("\t");
                for (int count2 = 0; count2 < 15; count2++) {
                    frlam[count][count2] = Double.parseDouble(tableLine[count2 + 1]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        fp_tab = open_auxfile(fr_edtoa_tab_name);
        try {
            reader = new BufferedReader(new FileReader(fp_tab));
            String line = reader.readLine(); // header
            for (int count = 0; count < FR_TAB; count++) {
                line = reader.readLine();
                final String[] tableLine = line.split("\t");
                for (int count2 = 0; count2 < 15; count2++) {
                    fredtoa[count][count2] = Double.parseDouble(tableLine[count2 + 1]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        fp_tab = open_auxfile(rr_lam_tab_name);
        try {
            reader = new BufferedReader(new FileReader(fp_tab));
            String line = reader.readLine(); // header
            for (int count = 0; count < RR_TAB; count++) {
                line = reader.readLine();
                final String[] tableLine = line.split("\t");
                for (int count2 = 0; count2 < 15; count2++) {
                    rrlam[count][count2] = Double.parseDouble(tableLine[count2 + 1]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        fp_tab = open_auxfile(rr_edtoa_tab_name);
        try {
            reader = new BufferedReader(new FileReader(fp_tab));
            String line = reader.readLine(); // header
            for (int count = 0; count < RR_TAB; count++) {
                line = reader.readLine();
                final String[] tableLine = line.split("\t");
                for (int count2 = 0; count2 < 15; count2++) {
                    rredtoa[count][count2] = Double.parseDouble(tableLine[count2 + 1]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        fp_tab = open_auxfile(nominal_tab_name);
        try {
            reader = new BufferedReader(new FileReader(fp_tab));
            for (int count = 0; count < 15; count++) {
                String line = reader.readLine();
                final String[] tableLine = line.split("\t");
                nomi_lam[count] = Double.parseDouble(tableLine[0]);
                nomi_sun[count] = Double.parseDouble(tableLine[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        /* make ed ratio tab, i.e. compute the ratio between the ed_toa for each pixel relative to ed-toa at the mean pixel for each camera */

        /* compute ratio */
        for (ipix = 0; ipix < RR_TAB; ipix++) {
            for (iband = 0; iband < 15; iband++) {
                rredtoa[ipix][iband] /= nomi_sun[iband];
            }
        }

        /* compute ratio */
        for (ipix = 0; ipix < FR_TAB; ipix++) {
            for (iband = 0; iband < 15; iband++) {
                fredtoa[ipix][iband] /= nomi_sun[iband];
            }
        }
    }


    private double[] use_the_nn(a_nn a_net, double[] nn_in, double[] nn_out) {
        int i;

        final long anetNnin = a_net.getNnin();
        for (i = 0; i < anetNnin; i++) {
            double value = (nn_in[i] - a_net.getInmin()[i]) / (a_net.getInmax()[i] - a_net.getInmin()[i]);
            a_net.getNn().setInput(i, value);
		/*printf("%ld %lf %lf %lf %lf\n",
			i,nn_in[i],a_net->nn.input[i],
			a_net->inmin[i],a_net->inmax[i]);*/
        }
        ff_proc(a_net.getNn());

        for (i = 0; i < a_net.getNnout(); i++) {
//            double value = (nn_in[i] - a_net.getInmin()[i]) / (a_net.getInmax()[i] - a_net.getInmin()[i]);
//            a_net.getNn().setOutput(i, value);

            nn_out[i] = a_net.getNn().getOutput()[i] * (a_net.getOutmax()[i] - a_net.getOutmin()[i]) + a_net.getOutmin()[i];

		/*printf("%ld %lf %lf %lf %lf\n",
			i,nn_in[i],a_net->nn.input[i],
			a_net->inmin[i],a_net->inmax[i]);*/
        }
        return nn_out;
    }

    private void ff_proc(feedforward ff) {
        int i, pl;
        for (pl = 0; pl < ff.nplanes - 1; pl++) {
            for (i = 0; i < ff.size[pl + 1]; i++) {
                ff.act[pl + 1][i] = alpha(ff.bias[pl][i] + scp(ff.wgt[pl][i], ff.act[pl], ff.size[pl]));
            }
        }
    }

    private double scp(double[] x, double[] y, long n) {
        int i;
        double sum = 0.;
        for (i = 0; i < n; i++)
            sum += x[i] * y[i];
        return sum;
    }

    private double alpha(double x) {
        int ind = (int) ((x - ALPHA_ANF) * rec_delta_alpha);
        if (ind < 0)
            ind = 0;
        if (ind >= N_ALPHA)
            ind = N_ALPHA - 1;
        return alpha_tab[ind];
    }

    /**
     * * water nn **
     */
    private NNReturnData nn_water(double[] conc_all, double[] rlw_nn, int m, int n, s_nn_atdata nn_data) {
        int ilam, nlam, ix;

        double sun_thet, view_zeni, azi_diff_hl, temperature, salinity;
        double log_apart, log_agelb, log_apig, log_bpart, log_bwit;
        double log_conc_chl, log_conc_det, log_conc_gelb, log_conc_min, log_conc_wit;
        double aot, ang, wind;
        int prepare;
        double[] innet = new double[10];
        double[] outnet = new double[35];
        //char *wat_net_name_for={"23x7x28_77.3.net"};
        //char *wat_net_name_for={"27x17x41_43.8.net"};
        //char *wat_net_name_for={"./water_for_b33_20111220/17_1070.2.net"};
        //char *wat_net_name_for={"./water_for_b33_20111220/27_697.2.net"};
        //char *wat_net_name_for={"./water_for_b33_rlw_20120118/17x27x17_120.2.net"};
        //char *wat_net_name_for={"./for_b33_20120114_nokd_27x17/27x17_153.7.net"};
        String wat_net_name_for = "./for_water_rw29_20120318/37x17_754.1.net";
        a_nn wat_net_for;

        s_nn_atdata nn_at_data = nn_data;

        nlam = n;
        prepare = nn_at_data.getPrepare();
        sun_thet = nn_at_data.getSun_thet();
        view_zeni = nn_at_data.getView_zeni();
        azi_diff_hl = nn_at_data.getAzi_diff_hl();
        temperature = nn_at_data.getTemperature();
        salinity = nn_at_data.getSalinity();

        log_conc_chl = conc_all[3];
        log_conc_det = conc_all[4];
        log_conc_gelb = conc_all[5];
        log_conc_min = conc_all[6];
        log_bwit = conc_all[7];

        innet[0] = sun_thet;
        innet[1] = view_zeni;
        innet[2] = azi_diff_hl;
        innet[3] = temperature;
        innet[4] = salinity;

        innet[5] = log_conc_chl;
        innet[6] = log_conc_det;
        innet[7] = log_conc_gelb;
        innet[8] = log_conc_min;
        innet[9] = log_bwit;

        if (prepare < 0.0) {
            make_alphatab();
            prepare = prepare + 2;
            nn_at_data.setPrepare(prepare);
        }
        wat_net_for = prepare_a_nn(wat_net_name_for);
        outnet = use_the_nn(wat_net_for, innet, outnet);

        if (nlam == 11) {
            for (ilam = 0; ilam < nlam; ilam++) {
                ix = lam29_meris11_ix[ilam];
                rlw_nn[ilam] = outnet[ix];
            }
        } else {
            nlam = 29;
            for (ilam = 0; ilam < nlam; ilam++) {
                //ix=lam33_meris29_ix[ilam];
                ix = ilam;
                rlw_nn[ilam] = outnet[ix];
            }
        }
        NNReturnData res = new NNReturnData(rlw_nn, nn_at_data);
        return res;
    }

    private class nn_atmo_watForwardModel implements ForwardModel {

        private double[] rtosa_nn;
        private s_nn_atdata nn_data;

        public void init(double[] rtosa_nn, s_nn_atdata nn_data) {
            this.rtosa_nn = rtosa_nn.clone();
            this.nn_data = nn_data;
        }

        public s_nn_atdata getNn_data() {
            return nn_data;
        }

        @Override
        public void init(double[] knownParameters) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public double[] getModeledSignal(double[] variables) {
            final NNReturnData nnReturnData = nn_atmo_wat(variables, rtosa_nn.clone(), variables.length, rtosa_nn.length, nn_data);
            nn_data = nnReturnData.getNn_atdata();
            return nnReturnData.getOutputValues();  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public double getPartialDerivative(double[] signal, double[] variables, int parameterIndex) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
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
        private NNReturnData nn_atmo_wat(double[] conc_all, double[] rtosa_nn, int m, int n, s_nn_atdata nn_data) {
            int ilam, nlam, ix;

            double sun_thet, view_zeni, azi_diff_hl, temperature, salinity;
            double aot, ang, wind, log_aot, log_ang, log_wind, log_agelb, log_apart, log_apig, log_bpart;
            double log_conc_chl, log_conc_det, log_conc_gelb, log_conc_min, log_conc_wit;

            double[] innet = new double[10];
            double[] outnet = new double[63];
            double[] tdown_nn = new double[29];
            double[] tup_nn = new double[29];
            double[] outnet1 = new double[29];
            double[] outnet2 = new double[29];
            double[] outnet3 = new double[29];
            double[] rlpath_nn = new double[29];
            double[] rw_2flow = new double[29];
            double[] conc_2flow = new double[5];
            double[] rlw_nn = new double[29];
            double[] rpath_nn = new double[29];
            double[] rw_nn = new double[29];
            int prepare;
            double x, y, z, radius, azimuth, elevation;

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
            String rhopath_net_name = "./oc_cci_20121127/ac_forward_all/ac_rhopath_b29/17x37x31_121.8.net";
            String tdown_net_name = "./oc_cci_20121127/ac_forward_all/t_down_b29/17x37x31_89.4.net";
            String tup_net_name = "./oc_cci_20121127/ac_forward_all/ac_tup_b29/17x37x31_83.8.net";


            a_nn atm_net_for, rhopath_net, tdown_net, tup_net;

            s_nn_atdata nn_at_data = nn_data;

            nlam = n;

            prepare = nn_at_data.getPrepare();
            sun_thet = nn_at_data.getSun_thet();
            view_zeni = nn_at_data.getView_zeni();
            azi_diff_hl = nn_at_data.getAzi_diff_hl();
            //azi_diff_hl=180.0-azi_diff_hl;
            temperature = nn_at_data.getTemperature();
            salinity = nn_at_data.getSalinity();

            azimuth = deg2rad * azi_diff_hl;
            elevation = deg2rad * view_zeni;
            x = Math.sin(elevation) * Math.cos(azimuth);
            y = Math.sin(elevation) * Math.sin(azimuth);
            z = Math.cos(elevation);


            log_aot = conc_all[0];
            log_ang = conc_all[1];
            log_wind = conc_all[2];
            log_conc_chl = conc_all[3];
            log_conc_det = conc_all[4];
            log_conc_gelb = conc_all[5];
            log_conc_min = conc_all[6];
            log_conc_wit = conc_all[7];

            // innet[0] = sun_thet;
            // CHANGED for new nets, RD 20130308:
            innet[0] = Math.cos(deg2rad * sun_thet);

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

            if (prepare < 0.0) {
                make_alphatab();
            }

            rhopath_net = prepare_a_nn(rhopath_net_name);
            tdown_net = prepare_a_nn(tdown_net_name);
            tup_net = prepare_a_nn(tup_net_name);

            outnet1 = use_the_nn(rhopath_net, innet, outnet1);
            outnet2 = use_the_nn(tdown_net, innet, outnet2);
            outnet3 = use_the_nn(tup_net, innet, outnet3);

            nlam = n; // if n == 11, then iteration for LM fit, if > 11, then computation for full spectrum
            if (nlam == 11) {
                for (ilam = 0; ilam < nlam; ilam++) {
                    ix = lam29_meris11_ix[ilam];
                    rpath_nn[ilam] = outnet1[ix];
                    tdown_nn[ilam] = outnet2[ix];
                    tup_nn[ilam] = outnet3[ix];
                }
                final NNReturnData res = nn_water(conc_all, rlw_nn, m, n, nn_at_data);
                rlw_nn = res.getOutputValues();
                nn_at_data = res.getNn_atdata();
                for (ilam = 0; ilam < 11; ilam++) {
                    rw_nn[ilam] = rlw_nn[ilam];//M_PI;
                    rtosa_nn[ilam] = rpath_nn[ilam] + rw_nn[ilam] * tdown_nn[ilam] * tup_nn[ilam];
                }
            } else {
                nlam = 29; // all bands for other calculations
                for (ilam = 0; ilam < nlam; ilam++) {
                    rpath_nn[ilam] = outnet1[ilam];
                    tdown_nn[ilam] = outnet2[ilam];
                    tup_nn[ilam] = outnet3[ilam];
                }
                n = nlam;
                final NNReturnData res = nn_water(conc_all, rlw_nn, m, n, nn_data);
                rlw_nn = res.getOutputValues();
                nn_at_data = res.getNn_atdata();
                for (ilam = 0; ilam < nlam; ilam++) {
                    rw_nn[ilam] = rlw_nn[ilam];//*M_PI;// ! with pi included, 21 bands
                    rtosa_nn[ilam] = rpath_nn[ilam] + rw_nn[ilam] * tdown_nn[ilam] * tup_nn[ilam];
                    nn_at_data.setTdown_nn(ilam, tdown_nn[ilam]);
                    nn_at_data.setTup_nn(ilam, tup_nn[ilam]);
                    nn_at_data.setRpath_nn(ilam, rpath_nn[ilam]);
                    nn_at_data.setRw_nn(ilam, rw_nn[ilam]);
                }
            }
            NNReturnData res = new NNReturnData(rtosa_nn, nn_data);
            return res;
        }

    }

    private class NNReturnData {

        double[] outputValues;
        s_nn_atdata nn_atdata;

        NNReturnData(double[] output, s_nn_atdata data) {
            outputValues = output;
            nn_atdata = data;
        }

        private double[] getOutputValues() {
            return outputValues;
        }

//        private void setOutputValues(double[] outputValues) {
//            this.outputValues = outputValues;
//        }

        private s_nn_atdata getNn_atdata() {
            return nn_atdata;
        }

//        private void setNn_atdata(s_nn_atdata nn_atdata) {
//            this.nn_atdata = nn_atdata;
//        }

    }

}