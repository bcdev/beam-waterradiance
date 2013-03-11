////////////////////////////////////////////////////////////////////////////////////
//  Example program that shows how to use levmar in order to fit the three-
//  parameter exponential model x_i = p[0]*exp(-p[1]*i) + p[2] to a set of
//  data measurements; example is based on a similar one from GSL.
//
//  Copyright (C) 2008  Manolis Lourakis (lourakis at ics forth gr)
//  Institute of Computer Science, Foundation for Research & Technology - Hellas
//  Heraklion, Crete, Greece.
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
////////////////////////////////////////////////////////////////////////////////////

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define MINMAX 0
#define NORMALIZE 1
#define M_PI 3.1416
#define NLAM 40
#define NLAMX3 36
#define MONPRINT 0
#define RR_TAB 925
#define FR_TAB 3700

#include "levmar.h"
#include "a_ffbpnn.h"
#include "levmar_nn_dll.h"

#ifndef LM_DBL_PREC
#error Example program assumes that levmar has been compiled with double precision, see LM_DBL_PREC!
#endif


/* the following macros concern the initialization of a random number generator for adding noise */
#undef REPEATABLE_RANDOM
#define DBL_RAND_MAX (double)(RAND_MAX)

#ifdef _MSC_VER // MSVC
#include <process.h>
#define GETPID  _getpid
#elif defined(__GNUC__) // GCC
#include <sys/types.h>
#include <unistd.h>
#define GETPID  getpid
#else
//#warning Do not know the name of the function returning the process id for your OS/compiler combination
#define GETPID  0
#endif /* _MSC_VER */

#ifdef REPEATABLE_RANDOM
#define INIT_RANDOM(seed) srand(seed)
#else
#define INIT_RANDOM(seed) srand((int)GETPID()) // seed unused
#endif

//#define LM_DIFF_DELTA    1E-08

#define PI 3.14159
#define DEG2RAD (PI / 180.0)

#define NUM_RW 33
#define NUM_ATMO 29

typedef struct _nn_at_data {

	int prepare; //    =nn_at_data[0];    

	double sun_thet; //   =nn_at_data[1];
	double view_zeni; //  =nn_at_data[2];
	double azi_diff_hl; //=nn_at_data[3];
	double temperature; //=nn_at_data[4];
	double salinity; //   =nn_at_data[5];

	double tdown_nn[NUM_ATMO]; // =tdown_nn[ilam];
	double tup_nn[NUM_ATMO]; // =tup_nn[ilam];
	double rpath_nn[NUM_ATMO]; // =rpath_nn[ilam];
	double rw_nn[NUM_RW]; // =rw_nn[ilam]; 

} s_nn_at_data;


//                             0   1   2   3   4   5   6   7   8   9   0   1   2   3   4   5   6   7   8   9   0   1   2   3   4   5   6   7   8   9   0   1   2 
const static double lam33[]={320,340,360,380,400,412,443,465,489,500,510,520,531,551,555,560,620,632,659,665,670,674,678,681,709,748,754,765,779,865,869,885,1020};
const static double lam29[]={                400,412,443,465,489,500,510,520,531,551,555,560,620,632,659,665,670,674,678,681,709,748,754,765,779,865,869,885,1020};
const static double lam21[]={                    412,443,    489,    510,520,531,551,555,560,620,        665,670,    678,681,709,748,754,765,779,865,869         };

const static int lam33_meris11_ix[]={5,6,8,10,15,16,19,   24,26,28,29};
const static int lam33_meris12_ix[]={5,6,8,10,15,16,19,23,24,26,28,29};
const static int lam33_meris29_ix[]={4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32};
const static int lam33_meris21_ix[]={5,6,8,10,11,12,13,14,15,16,19,20,22,23,24,25,26,27,28,29,30};

const static int lam29_meris11_ix[]={1,2,4,6,11,12,15,   20,22,24,25};
const static int lam29_meris12_ix[]={1,2,4,6,11,12,15,19,20,22,24,25};


const static double merband10[] = {413,443,490,510,560,620,665,681,708,753};
const static double merband12[] = {412.3, 442.3, 489.7, 509.6, 559.5, 619.4, 664.3, 680.6, 708.1, 753.1, 778.2, 864.6};

// lam 400. 412.    443.     465.   489.    500.  510.  520.   531.    551.   555.    560.   620.   632.    659.   665.    670.   674.    678.    681.    709.    748.    754.    765.    779.    865. 869. 885. 1020.
const double a_ozon_lam21[]={     0.0008, 0.00287,        0.0202,       0.04, 0.048, 0.0644, 0.087, 0.095,  0.103, 0.105,                0.0525, 0.048,         0.0384, 0.0352, 0.0185, 0.0092, 0.0082, 0.0055, 0.0004, 0.0, 0.0          };
const double a_ozon_lam29[]={0.0, 0.0008, 0.00287, 0.008, 0.0202, 0.03, 0.04, 0.048, 0.0644, 0.087, 0.095,  0.103, 0.105, 0.0878, 0.058, 0.0525, 0.048, 0.0432, 0.0384, 0.0352, 0.0185, 0.0092, 0.0082, 0.0055, 0.0004, 0.0, 0.0, 0.0, 0.0};      

const double ozon_meris12[12]={0.0002179, 0.002814,       0.02006,      0.04081,                            0.104, 0.109,                0.0505,                        0.03526,0.01881,        0.008897,       0.007693,0.002192}; // L.Bourg 2010

const double deg2rad = 3.1415927/180.0;


double frlam[FR_TAB][15], fredtoa[FR_TAB][15];
double rrlam[RR_TAB][15], rredtoa[RR_TAB][15];

a_nn* prepare_a_nn(char *filename);
void use_the_nn(a_nn *aa_net, double *innet, double *outnet);
void make_alphatab(void);
void nn_water(double *conc, double *rlw_nn, int m, int n, void *nn_data);
void nn_atmo_wat(double *conc_all, double *rtosa_nn, int m, int n, void *nn_data);


/* Gaussian noise with mean m and variance s, uses the Box-Muller transformation */
double gNoise(double m, double s)
{
	double r1, r2, val;

	r1=((double)rand())/DBL_RAND_MAX;
	r2=((double)rand())/DBL_RAND_MAX;

	val=sqrt(-2.0*log(r1))*cos(2.0*M_PI*r2);

	val=s*val+m;

	return val;
}


/**** water nn ***/

void nn_water(double *conc_all, double *rlw_nn, int m, int n, void *nn_data){
	int ilam, nlam, ix;

	double sun_thet, view_zeni, azi_diff_hl, temperature, salinity;
	double log_apart,log_agelb, log_apig, log_bpart, log_bwit;
	double log_conc_chl, log_conc_det, log_conc_gelb, log_conc_min, log_conc_wit;
	double aot, ang, wind;
	static int prepare;
	double innet[10], outnet[35];
	//char *wat_net_name_for={"23x7x28_77.3.net"};
	//char *wat_net_name_for={"27x17x41_43.8.net"};
	//char *wat_net_name_for={"./water_for_b33_20111220/17_1070.2.net"};
	//char *wat_net_name_for={"./water_for_b33_20111220/27_697.2.net"};
	//char *wat_net_name_for={"./water_for_b33_rlw_20120118/17x27x17_120.2.net"};
	//char *wat_net_name_for={"./for_b33_20120114_nokd_27x17/27x17_153.7.net"};
	char *wat_net_name_for={"./for_water_rw29_20120318/37x17_754.1.net"};
	static a_nn *wat_net_for;

	s_nn_at_data *nn_at_data = (s_nn_at_data *) nn_data;

	nlam=n;
	prepare    =nn_at_data->prepare;    
	sun_thet   =nn_at_data->sun_thet;
	view_zeni  =nn_at_data->view_zeni;
	azi_diff_hl=nn_at_data->azi_diff_hl;
	temperature=nn_at_data->temperature;
	salinity   =nn_at_data->salinity;

	log_conc_chl  =conc_all[3];
	log_conc_det  =conc_all[4];
	log_conc_gelb =conc_all[5];
	log_conc_min  =conc_all[6];
	log_bwit      =conc_all[7];

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

	if(prepare< 0.0){
		make_alphatab();
		wat_net_for=prepare_a_nn(wat_net_name_for);
		prepare=prepare+2;
		nn_at_data->prepare=prepare;
		//nn_data[0]=(void) prepare;
	}

	use_the_nn(wat_net_for, innet, outnet);

	if(nlam==11){
		for(ilam=0;ilam<nlam;ilam++){
			ix=lam29_meris11_ix[ilam];
			rlw_nn[ilam]=outnet[ix];
		}
	}
	else{
		nlam=29;
		for(ilam=0;ilam<nlam;ilam++){ 
			//ix=lam33_meris29_ix[ilam];
			ix=ilam;
			rlw_nn[ilam]=outnet[ix];
		}
	}
} 




/*** atmosphere ***/
void nn_atmo_wat(double *conc_all, double *rtosa_nn, int m, int n, void *nn_data){
	int ilam, nlam, ix;

	double sun_thet, view_zeni, azi_diff_hl, temperature, salinity;
	double aot, ang, wind, log_aot, log_ang, log_wind, log_agelb, log_apart, log_apig, log_bpart;
	double log_conc_chl, log_conc_det, log_conc_gelb, log_conc_min, log_conc_wit;

	double innet[10], outnet[63], tdown_nn[29], tup_nn[29], outnet1[29], outnet2[29],outnet3[29];
	double rlpath_nn[29], rw_2flow[29], conc_2flow[5], rlw_nn[29];
	double rpath_nn[29], rw_nn[29];
	static int prepare;
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
	char *rhopath_net_name={"./oc_cci_20121127/ac_forward_all/ac_rhopath_b29/17x37x31_121.8.net"};
	char *tdown_net_name  ={"./oc_cci_20121127/ac_forward_all/t_down_b29/17x37x31_89.4.net"};
	char *tup_net_name    ={"./oc_cci_20121127/ac_forward_all/ac_tup_b29/17x37x31_83.8.net"};


	static a_nn *atm_net_for, *rhopath_net, *tdown_net, *tup_net;

	s_nn_at_data *nn_at_data = (s_nn_at_data *) nn_data;

	nlam=n;

	prepare    =nn_at_data->prepare;    
	sun_thet   =nn_at_data->sun_thet;
	view_zeni  =nn_at_data->view_zeni;
	azi_diff_hl=nn_at_data->azi_diff_hl;
	//azi_diff_hl=180.0-azi_diff_hl;
	temperature=nn_at_data->temperature;
	salinity   =nn_at_data->salinity;

	azimuth   = deg2rad * azi_diff_hl;
	elevation = deg2rad * view_zeni;
	x = sin(elevation) * cos(azimuth); 
	y = sin(elevation) * sin(azimuth); 
	z = cos(elevation); 


	log_aot   =conc_all[0];
	log_ang   =conc_all[1];
	log_wind  =conc_all[2];
	log_conc_chl  =conc_all[3];
	log_conc_det  =conc_all[4];
	log_conc_gelb =conc_all[5];
	log_conc_min = conc_all[6];
	log_conc_wit = conc_all[7];

	// innet[0] = sun_thet;
	// CHANGED for new nets, RD 20130308:
	innet[0] = cos(deg2rad * sun_thet);

	innet[1] = x;
	innet[2] = y;
	innet[3] = z; 

	//innet[4] = log_aot;
	//innet[5] = log_ang;
	//innet[6] = log_wind;

	// CHANGED for new nets, RD 20130308:
	innet[4] = exp(log_aot);
	innet[5] = exp(log_ang);
	innet[6] = exp(log_wind);

	innet[7] = temperature;
	innet[8] = salinity;

	if(prepare< 0.0){
		make_alphatab();
		rhopath_net=prepare_a_nn(rhopath_net_name);
		tdown_net  =prepare_a_nn(tdown_net_name);
		tup_net    =prepare_a_nn(tup_net_name);
	}

	use_the_nn(rhopath_net,innet, outnet1);
	use_the_nn(tdown_net,innet, outnet2);
	use_the_nn(tup_net,innet, outnet3);

	nlam=n; // if n == 11, then iteration for LM fit, if > 11, then computation for full spectrum
	if(nlam==11){
		for(ilam=0;ilam<nlam;ilam++){
			ix=lam29_meris11_ix[ilam];
			rpath_nn[ilam] = outnet1[ix];
			tdown_nn[ilam] = outnet2[ix];
			tup_nn[ilam]   = outnet3[ix];
		}
		nn_water(conc_all, rlw_nn, m, n, nn_at_data);
		for(ilam=0;ilam<11;ilam++){
			rw_nn[ilam]=rlw_nn[ilam];//M_PI;
			rtosa_nn[ilam]=rpath_nn[ilam]+rw_nn[ilam]*tdown_nn[ilam]*tup_nn[ilam];
		}
	}
	else{
		nlam=29; // all bands for other calculations
		for(ilam=0;ilam<nlam;ilam++){
			rpath_nn[ilam] =outnet1[ilam];
			tdown_nn[ilam] =outnet2[ilam];
			tup_nn[ilam]   =outnet3[ilam];
		}
		n=nlam;
		nn_water(conc_all, rlw_nn, m, n, nn_data);
		for(ilam=0;ilam<nlam;ilam++){
			rw_nn[ilam]=rlw_nn[ilam];//*M_PI;// ! with pi included, 21 bands
			rtosa_nn[ilam]=rpath_nn[ilam]+rw_nn[ilam]*tdown_nn[ilam]*tup_nn[ilam];
			nn_at_data->tdown_nn[ilam] = tdown_nn[ilam];
			nn_at_data->tup_nn[ilam]   = tup_nn[ilam];
			nn_at_data->rpath_nn[ilam] = rpath_nn[ilam];
			nn_at_data->rw_nn[ilam]    = rw_nn[ilam]; 
		}
	}  
}

/***************************************/
int levmar_nn(int detector, double *input, int input_length, double *output, int output_length, double *debug_dat)
{

	//const int n=12, m=5; // 10 measurements == wavelength, 5 parameters
	// int n=12, m=5; // 10 measurements == wavelength, 5 parameters

	//double p[m], x[n], opts[LM_OPTS_SZ], info[LM_INFO_SZ];

	static int FIRST=1;
	const double h2o_cor_poly[]={0.3832989, 1.6527957, -1.5635101, 0.5311913}; // polynom coefficients for band708 correction
	const int merband12_index[]={0,1,2,3,4,5,6,7,8,9,11,12};
	const int meris11_outof_12_ix[]={0,1,2,3,4,5,6,8,9,10,11};
	const int lam21_meris12_ix[]={0,1,2,3,8,9,10,13,14,16,18,19};

	double p[8], x[NLAM], xb[NLAM], xr[NLAM], opts[LM_OPTS_SZ], info[LM_INFO_SZ], x11[11], x11_vor[11];
	static double p_alt[8];
	double rw1[NLAM], rw2[NLAM],rw_2flow[NLAM], rw_min[NLAM], rw_max[NLAM];
	double log_apart, log_agelb, log_apig, log_bpart, log_bwit,  sun_thet, view_zeni, azi_diff_hl, temperature, salinity, ozone;
	double log_conc_chl, log_conc_det, log_conc_gelb, log_conc_min, log_conc_wit, aot_550, ang_865_443, a_pig, a_gelb, a_part,b_part, b_wit;
	double a_pig_stdev, a_part_stdev, a_gelb_stdev, b_part_stdev, b_wit_stdev;

	double dif, difp, agelb, btot;
	static double nn_data[60], prepare, ang, aot,wind;
	static s_nn_at_data nn_at_data;
	register int i,j;
	int n, m, ret, SMILE;

	double pixel_x, pixel_y,sun_azi, view_azi,  azi_diff_deg;
	double sun_spec[15], sun_lam[15];

	char   procver[80], site[80], pi[80], time_is[80], pqc[80], mqc[80], time_is1[80], time_is_2[80], time[80], resolution[4];
	int land, cloud, ice_haze, white_scatter, high_glint, medium_glint, pcd_1_13, pde_14, pcd_15, pcd_16, pcd_17,pcd_18,pcd_19,oadb,absoa_dust,bpac_on,case2_s,case2_anom;
	int detectorIndex, na_flag, isite, pixel_valid;
	double lat_is, lon_is, thetas_is, chl_is,aot_870_is_1,aot_870_is_2, alpha_is_1, alpha_is_2, lat, lon, delta_azimuth, scatt_angle, windm, press_ecmwf, ozon_ecmwf, vapour_ecmwf;
	double altitude,chl1, chl2,spm,odoc,vapr,tau_aer_05, tau_aer_13, aer_1, aer_2, aer_mix;
	double rho_wn_is[15], rho_wn_isme[15], rn[15],rho_wn[15], toar[15],rho_toa[15],rho_gc[15],rho_ray[15],rho_aer[15],t_down[15],t_up[15], rho_w_c2[4];                   
	double sun_zenith,view_zenith, alpha, cos_teta_sun, sin_teta_sun, cos_teta_view, sin_teta_view, cos_azi_diff;
	double sun_azimuth, view_azimuth, surf_pressure, ozhone, wind_x, wind_y, cos_sun_zenith;
	double trans_ozon[NLAM],rho_toa_ocz[NLAM], solar_flux[NLAM],RL_toa[NLAM];
	char buffer[3600];
	long int icas, ncas, orbit;
	int nlam, ilam, ilami,ix;
	FILE *fppix, *fput, *fp_tab;

	double covar_out[8][8];

	//double conc[5]   ={0.0,0.0,0.0,0.0,0.0};
	double conc_at[8]={0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
	double conc_at_min[8], conc_at_max[8], stdev[8];

	// lower and upper boundary for variables aot, ang, wind, log_conc_chl, log_conc_det, log_conc_gelb, log_conc_min
	double lb[8]= {0.001, 0.001, 0.001, -13.96, -15.42, -16.38, -15.87, 0.0};
	double ub[8]= {1.0, 2.2, 10.0, 3.9, 2.294, 1.599, 4.594, 1.1}; 
	double lub;
	char *norm_net_name={"27x41x27_23.3.net"};
	char *nominal_lam_sun_name={"./smile/nominal_lam_sun.txt"};

	static char errmsg[1024];
	static a_nn *norm_net;
	a_nn* prepare_a_nn(char *filename);
	void use_the_nn(a_nn *norm_net, double *innet, double *outnet);
	void make_alphatab(void);
	double nn_in[40], nn_out[40];
	double rwn1[40], rwn2[40], rlw1[40], rlw2[40];
	double trans708, X2;
	int flag1,flag2, flag3, flag4, flag5;

	double nomi_lam[15],nomi_sun[15],nomi_lam12[15],nomi_sun12[15], smile_lam;
	double tau_rayl_smile_rat, cos_gamma_plus, rayleigh_reflect, teta_view_rad, teta_sun_rad, conc_ozon;
	double L_toa[15], Ed_toa[15], L_toa_ocz[15],surf_press, rayl_rel_mass_tosa, rayl_rel_mass_toa_tosa,rayl_mass_toa_tosa;
	double cos_scat_ang, phase_rayl_min,phase_rayl_plus;
	double tau_rayl_standard[15], tau_rayl_toa_tosa[15],tau_rayl_smile[15], L_rayl_toa_tosa[15],L_rayl_smile[15];
	double L_toa_corr[15], rho_tosa_corr[15], Ed_toa_smile_rat[15], Ed_toa_smile_corr[15], L_tosa[15], Ed_tosa[15];
	double trans_extra[15],trans_rayl_press[15],trans_rayl_smile[15],trans_rayl_pressd[15],trans_rayl_smiled[15],trans_rayl_pressu[15],trans_rayl_smileu[15], trans_ozond[15], trans_ozonu[15];
	void smile_tab_ini();

	/***********************************************/
	if(FIRST==1) {

		/** network for normalisation */

		make_alphatab();
		sprintf(errmsg, "echo norm_net_name: %s\n", norm_net_name);
		norm_net=prepare_a_nn(norm_net_name);
		/*********** tables for smile correction **************/
		smile_tab_ini();
		nn_at_data.prepare = -1; // prepare neural networks only once


		/* table with nominal wavelengths and solar flux */

		if((fp_tab=fopen(nominal_lam_sun_name,"r"))==0){
			printf("Can't find Parameter  file: %s\n",nominal_lam_sun_name);
			exit(0);
		}
		for(i=0;i<15;i++){
			fscanf(fp_tab,"%lf%lf",&nomi_lam[i],&nomi_sun[i]);
		}
		for (i=0;i<12;i++){
			ilami = merband12_index[i];
			nomi_lam12[i]=nomi_lam[ilami];
			nomi_sun12[i]=nomi_sun[ilami];
		}
		fclose(fp_tab);

		printf("");

		//FIRST=0; //do this later down
	} // end of FIRST
	/***********************************************/

	// input data
	sun_zenith=input[0];
	sun_azimuth=input[1];
	view_zenith=input[2];
	view_azimuth=input[3];
	surf_pressure=input[4];
	ozone=input[5];
	wind_x=input[6];
	wind_y=input[7];
	temperature=input[8];
	salinity=input[9];

	cos_sun_zenith=cos(sun_zenith*deg2rad);

	for(i=0;i<15;i++){
		L_toa[i]      =input[i+10];
		solar_flux[i] =input[i+25];

		Ed_toa[i]= solar_flux[i]*cos_sun_zenith;
		RL_toa[i]=L_toa[i]/Ed_toa[i];
	}
	// end of input data

	delta_azimuth=fabs(view_azimuth-sun_azimuth);
	if(delta_azimuth>180.0) delta_azimuth=180.0-delta_azimuth;

	// nn_at_data[0]= -1.0; // prepare
	nn_at_data.sun_thet=sun_thet=sun_zenith;
	nn_at_data.view_zeni=view_zeni=view_zenith;
	nn_at_data.azi_diff_hl=azi_diff_hl=delta_azimuth;
	nn_at_data.temperature=temperature;
	nn_at_data.salinity=salinity;


	//nn_at_data[0]= -1.0;
	if(nn_data[0]<1.0)
		nn_data[0]=0.0;  //if < 1.2345 then NN has to be created
	m=8;
	n=12;

	/* ++++ angles ++++ */

	cos_teta_sun=cos(sun_zenith*deg2rad);
	cos_teta_view=cos(view_zenith*deg2rad);
	sin_teta_sun=sin(sun_zenith*deg2rad);
	sin_teta_view=sin(view_zenith*deg2rad);
	cos_azi_diff=cos(delta_azimuth*deg2rad);
	teta_view_rad=view_zenith*deg2rad;
	teta_sun_rad = sun_zenith*deg2rad;
	conc_ozon=ozone;


	/*+++ ozone correction +++*/
	nlam=12;
	for(i=0;i<nlam;i++){
		//trans_ozon[i]= exp(-ozon_meris12[i]* ozone / 1000.0 *(1.0/cos_teta_sun+1.0/cos_teta_view));
		trans_ozond[i]=exp(-ozon_meris12[i]* ozone / 1000.0 *(1.0/cos_teta_sun));
		trans_ozonu[i]=exp(-ozon_meris12[i]* ozone / 1000.0 *(1.0/cos_teta_view));
		trans_ozon[i]=trans_ozond[i]*trans_ozonu[i];
	}

	for(i=0; i<12; ++i){
		ix=merband12_index[i];
		L_toa_ocz[i]=L_toa[ix]/trans_ozon[i]; // shall be both ways RD20120318
	}


	/* +++ water vapour correction for band 9 +++++ */
	//X2=rho_900/rho_885;
	X2=RL_toa[14]/RL_toa[13];
	trans708=h2o_cor_poly[0]+h2o_cor_poly[1]*X2+h2o_cor_poly[2]*X2*X2+h2o_cor_poly[3]*X2*X2*X2;

	L_toa[8] /= trans708;

	/*+++ smile and pressure correction +++*/

	/* calculate relative airmass rayleigh correction for correction layer*/
	surf_press=surf_pressure;
	rayl_rel_mass_tosa     = surf_press / 1013.2;
	rayl_rel_mass_toa_tosa =(surf_press - 1013.2) / 1013.2; //?? oder rayl_mass_toa_tosa =surf_press - 1013.2; // RD20120105
	rayl_mass_toa_tosa =surf_press - 1013.2; // RD20120105

	/* calculate phase function for rayleigh path radiance*/
	cos_scat_ang = -cos_teta_view * cos_teta_sun - sin_teta_view * sin_teta_sun * cos_azi_diff; // this is the scattering angle without fresnel reflection
	cos_gamma_plus = cos_teta_view * cos_teta_sun - sin_teta_view * sin_teta_sun * cos_azi_diff; // for fresnel reflection
	phase_rayl_plus = 0.75 * (1.0 + cos_gamma_plus * cos_gamma_plus);
	phase_rayl_min = 0.75 * (1.0 + cos_scat_ang * cos_scat_ang);

	/* calculate optical thickness of rayleigh for correction layer, lam in micrometer */

	for (ilam = 0; ilam < nlam; ilam++) {
		ix=merband12_index[ilam];
		tau_rayl_standard[ilam] = 0.008735 * pow(merband12[ilam] / 1000.0, -4.08);/* lam in µm */
		tau_rayl_toa_tosa[ilam] = tau_rayl_standard[ilam] * rayl_rel_mass_toa_tosa;
		//tau_rayl_toa_tosa[ilam] = tau_rayl_standard[ilam] * rayl_mass_toa_tosa; // RD20120105
		L_rayl_toa_tosa[ilam]   = Ed_toa[ix]* tau_rayl_toa_tosa[ilam]* phase_rayl_min / (4 * M_PI) * (1.0/cos_teta_view );
		trans_rayl_press[ilam]=exp(-tau_rayl_toa_tosa[ilam]*(1.0/cos_teta_view + 1.0/ cos_teta_sun));
		trans_rayl_pressd[ilam]=exp(-tau_rayl_toa_tosa[ilam]*(1.0/ cos_teta_sun));
		trans_rayl_pressu[ilam]=exp(-tau_rayl_toa_tosa[ilam]*(1.0/cos_teta_view));
	}

	/* calculate rayleigh for correction of smile, lam in micrometer */

	for (ilam = 0; ilam < nlam; ilam++) {
		ix = merband12_index[ilam];
		detectorIndex=detector;
		smile_lam=rrlam[detectorIndex][ix];
		tau_rayl_smile[ilam] = 0.008735 * pow(smile_lam / 1000.0,-4.08);
		L_rayl_smile[ilam] = Ed_toa[ix]* (tau_rayl_smile[ilam]-tau_rayl_standard[ilam])* phase_rayl_min / (4 * M_PI) * (1.0/cos_teta_view);
		trans_rayl_smile[ilam]=exp(-(tau_rayl_smile[ilam]-tau_rayl_standard[ilam])*(1.0/cos_teta_view + 1.0/ cos_teta_sun));
		trans_rayl_smiled[ilam]=exp(-(tau_rayl_smile[ilam]-tau_rayl_standard[ilam])*(1.0/ cos_teta_sun));
		trans_rayl_smileu[ilam]=exp(-(tau_rayl_smile[ilam]-tau_rayl_standard[ilam])*(1.0/cos_teta_view));
	}
	/* +++++ Esun smile correction ++++++ */
	for (ilam = 0; ilam < nlam; ilam++){
		ix = merband12_index[ilam];
		Ed_toa_smile_rat[ilam]=rredtoa[detectorIndex][ix];///nomi_sun[ix];
		Ed_toa_smile_corr[ilam]= Ed_toa[ix]*Ed_toa_smile_rat[ilam]; // RD20120105 geaendert von / in *, wieder zurueck 20120119
	}
	SMILE=1;
	if(SMILE){
		/* subtract all correcting radiances */
		for (ilam = 0; ilam < nlam; ilam++) {
			ix = merband12_index[ilam];
			// L_tosa[ilam] = ((L_toa[ix]-L_rayl_smile[ilam])-L_rayl_toa_tosa[ilam])/(trans_ozon[ilam]*trans_rayl_smile[ilam]);
			L_tosa[ilam] = L_toa[ix]/(trans_ozon[ilam]*trans_rayl_press[ilam]/**trans_rayl_smile[ilam]*/)-L_rayl_toa_tosa[ilam]+L_rayl_smile[ilam];//*trans_rayl_smile[ilam]);
			Ed_tosa[ilam]=Ed_toa_smile_corr[ilam];//*trans_rayl_smiled[ilam]*trans_rayl_pressd[ilam];
			rho_tosa_corr[ilam]=L_tosa[ilam]/Ed_tosa[ilam]*M_PI;
			x[ilam]=xb[ilam]=rho_tosa_corr[ilam];
		}
	}
	else{ /* subtract only correction for ozone */
		for (ilam = 0; ilam < nlam; ilam++) {
			ix = merband12_index[ilam];
			L_tosa[ilam] = L_toa[ix]/trans_ozon[ilam];//-L_rayl_toa_tosa[ilam]-L_rayl_smile[ilam];
			Ed_tosa[ilam]=Ed_toa[ix];
			rho_tosa_corr[ilam]=L_tosa[ilam]/Ed_tosa[ilam]*M_PI;
			x[ilam]=xb[ilam]=rho_tosa_corr[ilam];
		}
	}


	/* extra trans for rho_water: ozon, rayl_smile, rayl_press */
	for (ilam = 0; ilam < nlam; ilam++) {
		trans_extra[ilam]=trans_ozon[ilam]*trans_rayl_press[ilam]*trans_rayl_smile[ilam];
	}

	/* +++++ vicarious adjustment +++++*/
	if(0){
		x[10] /= 0.98;
		xb[10]=x[10];

		x[11] /= 0.93;
		xb[11]=x[11];
	}
	/* initial parameters estimate: */
	icas= -1L;
	/*
	input  5 is log_aot in [-4.605000,0.000000]
	input  6 is log_angstrom in [-3.817000,0.788500]
	input  7 is log_wind in [-2.303000,2.303000]
	input  8 is temperature in [0.000047,36.000000]
	input  9 is salinity in [0.000004,43.000000]
	*/
	//lower boundary
	lb[0]= -4.6; // aot
	lb[1]= -3.8; // ang
	lb[2]= -2.3; // wind
	lb[3]= -19.9; // apig
	lb[4]= -15.89; // apart
	lb[5]= -17.23; // agelb
	lb[6]= -15.8; // bpart
	lb[7]= -14.92; // bwit

	// upper boundary
	ub[0]=  0.0;   // aot
	ub[1]=  0.788; // ang
	ub[2]=  2.3;   // wind
	ub[3]=  0.685; // apig
	ub[4]=  2.297; // apart
	ub[5]=  1.6;   // agelb
	ub[6]=  4.598; // bpart
	ub[7]=  4.599; // bwit


	if(FIRST==1){
		for(i=0;i<m;i++){
			if(lb[i]<0.0)
				p[i]=lb[i]-lb[i]*0.2;
			else
				p[i]=lb[i]+lb[i]*0.2;
		}
		FIRST=0;
		/*
		} else{
		for(i=0;i<m;i++)
		p[i]=p_alt[i];
		}
		*/
	}else{
		for(i=0;i<m;i++){
			lub=fabs(ub[i]-lb[i]);
			/*
			if(lb[i]<0.0)
				p[i]=lb[i]-lb[i]*0.2;
			else
				p[i]=lb[i]+lb[i]*0.2;
				*/
			if(ub[i]<0.0)
				p[i]=ub[i]-lub*0.2;
			else
				p[i]=ub[i]-lub*0.2;
		}
		p[0]=log(0.1);   // tau550
		p[1]=log(1.0);   // ang
		p[2]=log(3.0);   // wind
		p[3]=log(0.005); // apig
		p[4]=log(0.005); // adet
		p[5]=log(0.005); // agelb
		p[6]=log(0.01);  // bspm
		p[7]=log(0.01);  // bwit
	}

	// select the 11 bands for iterations
	for(i=0;i<11;i++){
		ix=meris11_outof_12_ix[i];
		x11[i]=x11_vor[i]=x[ix];
	}
	m=8;
	n=11;
	/* optimization control parameters; passing to levmar NULL instead of opts reverts to defaults */
	//  opts[0]=LM_INIT_MU; opts[1]=1E-15; opts[2]=1E-15; opts[3]=1E-20;
	opts[0]=LM_INIT_MU; opts[1]=1E-10; opts[2]=1E-10; opts[3]=1E-10;
	//  opts[4]=LM_DIFF_DELTA; // relevant only if the finite difference Jacobian version is used 
	//  opts[4]= 0.2; // relevant only if the finite difference Jacobian version is used 
	opts[4]= -0.1; // relevant only if the finite difference Jacobian version is used 

	/* invoke the optimization function */
	ret=dlevmar_bc_dif(nn_atmo_wat, p, x11, m, n, lb, ub, 150, opts, info, NULL, &covar_out[0][0], &nn_at_data); // without Jacobian
	if(MONPRINT)
		printf("Levenberg-Marquardt returned in %g iter, reason %g, sumsq %g [%g]\n", info[5], info[6], info[1], info[0]);
	if(MONPRINT)
		printf("Best fit parameters: %.7g %.7g %.7g %.7g %.7g %.7g %.7g %.7g\n", p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7]);

	for(i=0;i<m;i++){
		conc_at[i]=p[i];
		p_alt[i]=p[i];
	}

	n=11;
	nn_atmo_wat(conc_at,x11,m,n,&nn_at_data);

	n=29;
	nn_atmo_wat(conc_at,x,m,n,&nn_at_data);


	/**********************************************************/
	if(NORMALIZE){
		/* normalize water leaving radiance reflectances */

		// requires first to make RLw again
		for(i=0;i<12;i++){
			rlw1[i]=rw1[i]/M_PI;
			rlw2[i]=rw2[i]/M_PI;
			if(rlw2[i]<0.0)
				rlw2[i]=1.0e-6;
		}

		nn_in[0]= sun_thet;
		nn_in[1]= view_zeni;
		nn_in[2]= azi_diff_hl;
		nn_in[3]= temperature;
		nn_in[4]=salinity;
		for(i=5;i<17;i++)
			nn_in[i]=rlw1[i-5];

		use_the_nn(norm_net, nn_in, nn_out);

		for(i=0;i<12;i++){
			rwn1[i]=nn_out[i]*M_PI;
		}

		for(i=5;i<17;i++)
			nn_in[i]=rlw2[i-5];

		use_the_nn(norm_net, nn_in, nn_out);

		for(i=0;i<12;i++){
			rwn2[i]=nn_out[i]*M_PI;
		}

	} // end normalize

	/*********************************************************/   

	// put all results into output
	nlam=12;
	for(i=0;i<nlam;i++){
		ix=lam29_meris12_ix[i];
		output[i]=rho_tosa_corr[i]/M_PI;
		output[i+nlam]=nn_at_data.rpath_nn[ix]; 
		output[i+nlam*2]=nn_at_data.rw_nn[ix];
		output[i+nlam*3]=nn_at_data.tdown_nn[ix];
		output[i+nlam*4]=nn_at_data.tup_nn[ix];
	}
	output[60]=aot_550=exp(p[0]);
	output[61]=ang_865_443=exp(p[1]);
	output[62]=a_pig =exp(p[3]);
	output[63]=a_part=exp(p[4]);
	output[64]=a_gelb=exp(p[5]);
	output[65]=b_part=exp(p[6]);
	output[66]=b_wit =exp(p[7]);
	output[67]=info[1]; // sum_sq
	output[68]=(double) info[5];

	return(0);
}
