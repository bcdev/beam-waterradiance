package org.esa.beam.waterradiance.realoptimizers;

/**
 * Created with IntelliJ IDEA.
 * User: tonio
 * Date: 08.05.13
 * Time: 13:41
 * To change this template use File | Settings | File Templates.
 */
public class a_nn {

    String filename;	/* where the NN is */
    long nnin;		    /* # of inputs to the NN */
    long nnout;		    /* # of outputs from the NN */
    double[] inmin;		/* minima of inputs */
    double[] inmax;		/* maxima of inputs */
    double[] outmin;	/* minima of outputs */
    double[] outmax;	/* maxima of outputs */
    feedforward nn;		/* the NN */

    public a_nn() {

    }

    public a_nn(String filename, long nnin, long nnout, double[] inmin, double[] inmax, double[] outmin, double[] outmax, feedforward nn) {
        this.filename = filename;
        this.nnin = nnin;
        this.nnout = nnout;
        this.inmin = inmin;
        this.inmax = inmax;
        this.outmin = outmin;
        this.outmax = outmax;
        this.nn = nn;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

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
