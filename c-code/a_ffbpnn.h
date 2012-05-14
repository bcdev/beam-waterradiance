#ifndef A_FEEDFORWARD_NN
#define A_FEEDFORWARD_NN


#define ACTIVATION alpha

typedef struct {
	long            nplanes;	/* #of planes in net	*/
	long           *size;		/* their sizes	*/
	double       ***wgt;		/* weight[plane][to_neuron][from_neuron] */
	double        **bias;		/* [plane-1][neuron]	*/
	double        **act;		/* neuron output[plane][neuron]	*/
	double         *input;		/* input[neuron]=act[0][neuron]	*/
	double         *output;		/* output[neuron]=act[nplanes-1][neuron] */

}               feedforward;

typedef struct {
	char           *filename;	/* where the NN is */
	long            nnin;		/* # of inputs to the NN */
	long            nnout;		/* # of outputs from the NN */
	double         *inmin;		/* minima of inputs */
	double         *inmax;		/* maxima of inputs */
	double         *outmin;		/* minima of outputs */
	double         *outmax;		/* maxima of outputs */
	feedforward     nn;		/* the NN */
} 
              a_nn;
#endif
