/*==================================================================
**  PROJECT        :  MERISL2
**  FILENAME       :  smile_table1.c
**  VERSION        :  0.1
**  LANGUAJE       :  ANSI C
**  AUTHOR         :  R. Doerffer
**  COMPANY        :  GKSS
**  COMPILER       :  WATCOM 11
**  CREATED        :  17/05/2004
**  LAST MODIFIED  :  17/05/2004
** 
**..................................................................
**  DESCRIPTION
**
**  Atmospheric correction and retrieval of water consituents using
**  neural networks and correction layers
**  subroutine to read and process smile correction table for MERIS
**.................................................................
**  HISTORY
**
**             DATE    VERSION         AUTHOR  REASONS
**
**             15/05/2004      0.1       RD      Created
**             
=================================================================*/

#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>
//#include "atm_wat_merisatm_wat_meris_20040629.h.h"

#define FR_TAB 3700
#define RR_TAB 925 /* ??*/
#define COMMENT '%'

extern double frlam[FR_TAB][15], fredtoa[FR_TAB][15];
extern double rrlam[RR_TAB][15], rredtoa[RR_TAB][15];

//extern double *frlam, *fredtoa;
//extern double *rrlam, *rredtoa;

//double *get_FR_lam_table(), *get_RR_lam_table(),*get_FR_edtoa_table(),*get_RR_edtoa_table();
void smile_tab_ini();
FILE* open_auxfile(const char* fileName);


void smile_tab_ini()
{
        FILE *fp_ini, *fp_tab;
        char name[500], buf[500];
        int i, k, iband, ipix, num;
        double  nomi_lam[15], nomi_sun[15];
        char smile_name[300];
        char *fr_lam_tab_name={"./smile/central_wavelen_fr.txt"};
        char *rr_lam_tab_name={"./smile/central_wavelen_rr.txt"};
        char *fr_edtoa_tab_name={"./smile/sun_spectral_flux_fr.txt"};
        char *rr_edtoa_tab_name={"./smile/sun_spectral_flux_rr.txt"};
        char *nominal_tab_name={"./smile/nominal_lam_sun.txt"};
        
        /* read the tables */
        fp_tab=open_auxfile(fr_lam_tab_name);
        do fgets(name,250,fp_tab) ;while(name[0]==COMMENT);// header
        for(i=0;i<FR_TAB;i++){
                do fgets(name,250,fp_tab) ;while(name[0]==COMMENT);
                sscanf(name,"%d%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf",&num,&frlam[i][0],&frlam[i][1],&frlam[i][2],&frlam[i][3],&frlam[i][4],
                &frlam[i][5],&frlam[i][6],&frlam[i][7],&frlam[i][8],&frlam[i][9],&frlam[i][10],&frlam[i][11],&frlam[i][12],&frlam[i][13],&frlam[i][14]);
        }
        fclose(fp_tab);

        fp_tab=open_auxfile(fr_edtoa_tab_name);
        do fgets(name,400,fp_tab) ;while(name[0]==COMMENT);// header
        for(i=0;i<FR_TAB;i++)
        {
                do fgets(name,400,fp_tab) ;while(name[0]==COMMENT);
                sscanf(name,"%d%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf",&num,&fredtoa[i][0],&fredtoa[i][1],&fredtoa[i][2],&fredtoa[i][3],&fredtoa[i][4],
                &fredtoa[i][5],&fredtoa[i][6],&fredtoa[i][7],&fredtoa[i][8],&fredtoa[i][9],&fredtoa[i][10],&fredtoa[i][11],&fredtoa[i][12],&fredtoa[i][13],&fredtoa[i][14]);
        }
        fclose(fp_tab);


        fp_tab=open_auxfile(rr_lam_tab_name);
        do fgets(name,250,fp_tab) ;while(name[0]==COMMENT);// header
        for(i=0;i<RR_TAB;i++){
                do fgets(name,250,fp_tab) ;while(name[0]==COMMENT);
                sscanf(name,"%d%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf",&num,&rrlam[i][0],&rrlam[i][1],&rrlam[i][2],&rrlam[i][3],&rrlam[i][4],
                &rrlam[i][5],&rrlam[i][6],&rrlam[i][7],&rrlam[i][8],&rrlam[i][9],&rrlam[i][10],&rrlam[i][11],&rrlam[i][12],&rrlam[i][13],&rrlam[i][14]);
        }
        fclose(fp_tab);
        
        fp_tab=open_auxfile(rr_edtoa_tab_name);
        do fgets(name,400,fp_tab) ;while(name[0]==COMMENT);// header
        for(i=0;i<RR_TAB;i++)
        {
                do fgets(name,400,fp_tab) ;while(name[0]==COMMENT);
                sscanf(name,"%d%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf%lf",&num,&rredtoa[i][0],&rredtoa[i][1],&rredtoa[i][2],&rredtoa[i][3],&rredtoa[i][4],
                &rredtoa[i][5],&rredtoa[i][6],&rredtoa[i][7],&rredtoa[i][8],&rredtoa[i][9],&rredtoa[i][10],&rredtoa[i][11],&rredtoa[i][12],&rredtoa[i][13],&rredtoa[i][14]);
        }
        fclose(fp_tab);

        fp_tab=open_auxfile(nominal_tab_name);
        for(i=0;i<15;i++)
            fscanf(fp_tab,"%lf%lf",&nomi_lam[i], &nomi_sun[i]);
        fclose(fp_tab);


        /* make ed ratio tab, i.e. compute the ratio between the ed_toa for each pixel relative to ed-toa at the mean pixel for each camera */

        
        /* compute ratio */
        for(ipix=0;ipix<RR_TAB;ipix++){
                for(iband=0;iband<15;iband++){
                        rredtoa[ipix][iband]/=nomi_sun[iband];
                }
        }
        
        /* compute ratio */
        for(ipix=0;ipix<FR_TAB;ipix++){
                for(iband=0;iband<15;iband++){
                        fredtoa[ipix][iband]/=nomi_sun[iband];
                }
        }
}

/*
double *get_FR_lam_table()
{
        return(&frlam[0][0]);
}

double *get_RR_lam_table()
{
        return(&rrlam[0][0]);
}

double *get_FR_edtoa_rat_table()
{
        return(&fredtoa[0][0]);
}

double *get_RR_edtoa_rat_table()
{
        return(&rredtoa[0][0]);
}
*/
