#include "a_ffbpnn.h"
#include <math.h>

#define N_ALPHA 100000
#define ALPHA_ANF -10.0

double          alpha_tab[N_ALPHA], rec_delta_alpha;

double
calpha(double x)
{
	return (1. / (1. + exp(-x)));
}

double
alpha(double x)
{
	long            ind = (long) ((x - ALPHA_ANF) * rec_delta_alpha);
	if (ind < 0)
		ind = 0;
	if (ind >= N_ALPHA)
		ind = N_ALPHA - 1;
	return alpha_tab[ind];
}

void
make_alphatab()
{
	double          sum, delta;
	long            i;

	delta = -2. * ALPHA_ANF / (N_ALPHA - 1);
	sum = ALPHA_ANF+delta/2.;
	for (i = 0; i < N_ALPHA; i++) {
		alpha_tab[i] = calpha(sum);
		sum += delta;
	}
	rec_delta_alpha = 1. / delta;
}

double 
scp(double *x, double *y, long n)
{
	long            i;
	double          sum = 0.;

	for (i = 0; i < n; i++)
		sum += x[i] * y[i];
	return sum;
}

void 
ff_proc(feedforward ff)
{
	long            i, pl;

	for (pl = 0; pl < ff.nplanes - 1; pl++) {
		for (i = 0; i < ff.size[pl + 1]; i++) {
			ff.act[pl + 1][i] = ACTIVATION(ff.bias[pl][i]
			  + scp(ff.wgt[pl][i], ff.act[pl], ff.size[pl]));
		}
	}
}


void
use_the_nn(a_nn *a_net, double *nn_in, double *nn_out)
{
	int             i;

	for (i = 0; i < a_net->nnin; i++) {
		a_net->nn.input[i] = (nn_in[i] - a_net->inmin[i]) / 
					(a_net->inmax[i] - a_net->inmin[i]);
		/*printf("%ld %lf %lf %lf %lf\n",
			i,nn_in[i],a_net->nn.input[i],
			a_net->inmin[i],a_net->inmax[i]);*/
	}
	ff_proc(a_net->nn);

	for (i = 0; i < a_net->nnout; i++) {
		nn_out[i] = a_net->nn.output[i] * 
				(a_net->outmax[i] - a_net->outmin[i]) + 
							a_net->outmin[i];
		/*printf("%ld %lf %lf %lf %lf\n",
			i,nn_out[i],a_net->nn.output[i],
			a_net->inmin[i],a_net->inmax[i]);*/
	}

}
