package org.esa.beam.waterradiance.realoptimizers;

class feedforward {

    private int nplanes;	    /* #of planes in net	*/
    private int size[];		/* their sizes	*/
    private double[][][] wgt;	/* weight[plane][to_neuron][from_neuron] */
    private double[][] bias;	/* [plane-1][neuron]	*/
    private double[][] act;		/* neuron output[plane][neuron]	*/

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

    public void setInput(int index, double value) {
        this.act[0][index] = value;
    }

    public double[] getOutput() {
        return act[nplanes - 1];
    }
}
