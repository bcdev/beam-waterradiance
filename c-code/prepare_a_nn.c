#include <stdio.h>
#include <string.h>
#include <stdlib.h>
//#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>

#include "a_ffbpnn.h"

static char errmsg[1024];

FILE* open_auxfile(const char* fileName);

void
myexit(int i) {
	system("echo I would like to exit now, but I can't.");
	//system("rm msg/working");
	//exit(i);
}

char           *
mcalloc(long n)
{
	char           *res;

	if ((res = (char *) malloc(n * sizeof(char))) == NULL) {
		sprintf(errmsg, "echo no space left for calloc %ld\n", n);
		system(errmsg); myexit(1);
	}
	return res;
}

char          **
mcpalloc(long n)
{
	char          **res;

	if ((res = (char **) malloc(n * sizeof(char *))) == NULL) {
		sprintf(errmsg, "echo no space left for cpalloc %ld\n", n);
		system(errmsg); myexit(1);
	}
	return res;
}

long           *
lalloc(long n)
{
	long           *res;

	if ((res = (long *) malloc(n * sizeof(long))) == NULL) {
		sprintf(errmsg, "echo no space left for lalloc %ld\n", n);
		system(errmsg); myexit(1);
	}
	return res;
}

long           **
lpalloc(long n)
{
	long           **res;

	if ((res = (long **) malloc(n * sizeof(long *))) == NULL) {
		sprintf(errmsg, "echo no space left for lpalloc %ld\n", n);
		system(errmsg); myexit(1);
	}
	return res;
}


double         *
dalloc(long n)
{
	double         *res;

	if ((res = (double *) malloc(n * sizeof(double))) == NULL) {
		sprintf(errmsg, "echo no space left for dalloc %ld\n", n);
		system(errmsg); myexit(1);
	}
	return res;
}

double        **
dpalloc(long n)
{
	double        **res;

	if ((res = (double **) malloc(n * sizeof(double *))) == NULL) {
		sprintf(errmsg, "echo no space left for dpalloc %ld\n", n);
		system(errmsg); myexit(1);
	}
	return res;
}

double       ***
dppalloc(long n)
{
	double       ***res;

	if ((res = (double ***) malloc(n * sizeof(double **))) == NULL) {
		sprintf(errmsg, "echo no space left for dppalloc %ld\n", n);
		system(errmsg); myexit(1);
	}
	return res;
}

double        **
make_vecv(long n, long *s)
{
	long            i;
	double        **wgt;

	wgt = dpalloc(n);
	for (i = 0; i < n; i++) {
		wgt[i] = dalloc(s[i]);
#ifdef DEBUG
		sprintf(fpo, "  Vektor %ld mit %ld Komp\n", i, s[i]);
#endif
	}
	return wgt;
}

double       ***
make_mtxv(long n, long *s)
{
	long            i, j;
	double       ***wgt;

	wgt = dppalloc(n - 1);
	for (i = 0; i < n - 1; i++) {
#ifdef DEBUG
		sprintf(fpo, "  Matrix %ld hat %ld Vektoren der Laenge %ld\n",
			i, s[i + 1], s[i]);
#endif
		wgt[i] = dpalloc(s[i + 1]);
		for (j = 0; j < s[i + 1]; j++)
			wgt[i][j] = dalloc(s[i]);
	}
	return wgt;
}




feedforward
make_ff_from_file(char *filename)
{
	long            i, j, pl, n, *s, id1, id2, id3;
	feedforward    *ff;
	FILE           *fp;
	char            ch;

        fp=open_auxfile(filename);
	do
		ch = getc(fp);
	while (ch != '$');
	fscanf(fp, " #planes=%ld", &n);
	s = lalloc(n);
	for (pl = 0; pl < n; pl++)
		fscanf(fp, "%ld", &s[pl]);

	if ((ff = (feedforward *) malloc(sizeof(feedforward))) == NULL) {
		sprintf(errmsg, "echo no memory for make_ff_from_file\n");
		system(errmsg); myexit(1);
	}
	ff->nplanes = n;
	ff->size = lalloc(n);
	for (pl = 0; pl < n; pl++)
		ff->size[pl] = s[pl];
#ifdef DEBUG
	sprintf(fpo, "wgt;\n");
#endif
	ff->wgt = make_mtxv(n, s);
#ifdef DEBUG
	sprintf(fpo, "bias;\n");
#endif
	ff->bias = make_vecv(n - 1, &s[1]);
#ifdef DEBUG
	sprintf(fpo, "act;\n");
#endif
	ff->act = make_vecv(n, s);
	ff->input = ff->act[0];
	ff->output = ff->act[n - 1];
	for (pl = 0; pl < ff->nplanes - 1; pl++) {
		fscanf(fp, " bias %ld %ld", &id1, &id2);
		if ((id1 != (pl + 1)) || (id2 != ff->size[pl + 1])) {
			sprintf(errmsg, "echo inconsistent bias %ld %ld from %s\n",
				id1, id2, filename);
			system(errmsg); myexit(1);
		}
		for (i = 0; i < ff->size[pl + 1]; i++)
			fscanf(fp, "%lf", &(ff->bias[pl][i]));
	}
	for (pl = 0; pl < ff->nplanes - 1; pl++) {
		fscanf(fp, " wgt %ld %ld %ld", &id1, &id2, &id3);
		if ((id1 != pl) || (id2 != ff->size[pl]) ||
		    (id3 != ff->size[pl + 1])) {
			sprintf(errmsg,"inconsistent wgt %ld %ld %ld from %s\n",
				id1, id2, id3, filename);
			system(errmsg); myexit(1);
		}
		for (i = 0; i < ff->size[pl + 1]; i++) {
			for (j = 0; j < ff->size[pl]; j++) {
				fscanf(fp, "%lf",
				       &(ff->wgt[pl][i][j]));
			}
		}
	}
	fclose(fp);
	return *ff;
}




a_nn *
prepare_a_nn(char *filename)
{
	FILE           *fp;
	char            ch;
	int		i;
	a_nn	       *res;

    sprintf(errmsg, "echo about to read %s\n", filename);
	system(errmsg);

    res=(a_nn*)malloc(sizeof(a_nn));
	res->filename=strdup(filename);
        
        fp = open_auxfile(res->filename);
	do {
		ch = getc(fp);
		/*printf("%c", ch);*/
	}
	while (ch != '#');
	fscanf(fp, "%ld", &(res->nnin));
	res->inmin = dalloc(res->nnin);
	res->inmax = dalloc(res->nnin);
	for (i = 0; i < res->nnin; i++) 
		fscanf(fp, "%lf %lf", &(res->inmin[i]), &(res->inmax[i]));
	fscanf(fp, "%ld", &(res->nnout));
	res->outmin = dalloc(res->nnout);
	res->outmax = dalloc(res->nnout);
	for (i = 0; i < res->nnout; i++) 
		fscanf(fp, "%lf %lf", &(res->outmin[i]), &(res->outmax[i]));
	fclose(fp);
	res->nn = make_ff_from_file(filename);
	if ((res->nnin  != res->nn.size[0]) || 
	    (res->nnout != res->nn.size[res->nn.nplanes - 1])) {
		sprintf(errmsg, "echo inconsistence %ld %ld %ld %ld\n",
			res->nnin, res->nn.size[0], res->nnout,
			res->nn.size[res->nn.nplanes - 1]);
		exit(1);
	}
	
	return res;
}
