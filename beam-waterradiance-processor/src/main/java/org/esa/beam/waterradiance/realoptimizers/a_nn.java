package org.esa.beam.waterradiance.realoptimizers;

class a_nn {

    private long nnin;		    /* # of inputs to the NN */
    private long nnout;		    /* # of outputs from the NN */
    private double[] inmin;		/* minima of inputs */
    private double[] inmax;		/* maxima of inputs */
    private double[] outmin;	/* minima of outputs */
    private double[] outmax;	/* maxima of outputs */
    private feedforward nn;		/* the NN */

   public long getNnin() {
        return nnin;
    }

    public void setNnin(long nnin) {
        this.nnin = nnin;
    }

    public long getNnout() {
        return nnout;
    }

    public void setNnout(long nnout) {
        this.nnout = nnout;
    }

    public double[] getInmin() {
        return inmin;
    }

    public void setInmin(double[] inmin) {
        this.inmin = inmin;
    }

    public double[] getInmax() {
        return inmax;
    }

    public void setInmax(double[] inmax) {
        this.inmax = inmax;
    }

    public double[] getOutmin() {
        return outmin;
    }

    public void setOutmin(double[] outmin) {
        this.outmin = outmin;
    }

    public double[] getOutmax() {
        return outmax;
    }

    public void setOutmax(double[] outmax) {
        this.outmax = outmax;
    }

    public feedforward getNn() {
        return nn;
    }

    public void setNn(feedforward nn) {
        this.nn = nn;
    }
}
