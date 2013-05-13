package org.esa.beam.waterradiance.realoptimizers;

/**
 * Created with IntelliJ IDEA.
 * User: tonio
 * Date: 08.05.13
 * Time: 13:42
 * To change this template use File | Settings | File Templates.
 */
public class feedforward {

    int nplanes;	    /* #of planes in net	*/
    int size[];		/* their sizes	*/
    double[][][] wgt;	/* weight[plane][to_neuron][from_neuron] */
    double[][] bias;	/* [plane-1][neuron]	*/
    double[][] act;		/* neuron output[plane][neuron]	*/
//    double[] input;		/* input[neuron]=act[0][neuron]	*/
//    double[] output;	/* output[neuron]=act[nplanes-1][neuron] */

//    public feedforward() {
//
//    }

//    public feedforward(long nplanes, long[] size, double[][][] wgt, double[][] bias, double[][] act, double[] input, double[] output) {
//        this.nplanes = nplanes;
//        this.size = size;
//        this.wgt = wgt;
//        this.bias = bias;
//        this.act = act;
//        this.input = input;
//        this.output = output;
//    }

    public long getNplanes() {
        return nplanes;
    }

    public void setNplanes(int nplanes) {
        this.nplanes = nplanes;
    }

    public int[] getSize() {
        return size;
    }

    public void setSize(int[] size) {
        this.size = size;
    }

    public double[][][] getWgt() {
        return wgt;
    }

    public void setWgt(double[][][] wgt) {
        this.wgt = wgt;
    }

    public double[][] getBias() {
        return bias;
    }

    public void setBias(double[][] bias) {
        this.bias = bias;
    }

    public double[][] getAct() {
        return act;
    }

    public void setAct(double[][] act) {
        this.act = act;
    }

    public double[] getInput() {
        return act[0];
    }

//    public void setInput(double[] input) {
//        this.input = input;
//    }

    public void setInput(int index, double value) {
        this.act[0][index] = value;
    }

    public double[] getOutput() {
        return act[nplanes - 1];
    }

//    public void setOutput(double[] output) {
//        this.output = output;
//    }

    public void setOutput(int index, double value) {
        this.act[nplanes - 1][index] = value;
    }

}
