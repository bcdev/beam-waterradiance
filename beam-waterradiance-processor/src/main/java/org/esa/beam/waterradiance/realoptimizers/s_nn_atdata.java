package org.esa.beam.waterradiance.realoptimizers;


class s_nn_atdata {

    private static int NUM_RW = 33;
    private static int NUM_ATMO = 29;

    int prepare; //    =nn_at_data[0];

    private double sun_thet;    //   =nn_at_data[1];
    double view_zeni; //  =nn_at_data[2];
    double azi_diff_hl; //=nn_at_data[3];
    double temperature; //=nn_at_data[4];
    double salinity; //   =nn_at_data[5];

    double[] tdown_nn = new double[NUM_ATMO]; // =tdown_nn[ilam];
    double[] tup_nn = new double[NUM_ATMO]; // =tup_nn[ilam];
    double[] rpath_nn = new double[NUM_ATMO]; // =rpath_nn[ilam];
    double[] rw_nn = new double[NUM_RW]; // =rw_nn[ilam];

    public int getPrepare() {
        return prepare;
    }

    public void setPrepare(int prepare) {
        this.prepare = prepare;
    }

    public double getSun_thet() {
        return sun_thet;
    }

    public void setSun_thet(double sun_thet) {
        this.sun_thet = sun_thet;
    }

    public double getView_zeni() {
        return view_zeni;
    }

    public void setView_zeni(double view_zeni) {
        this.view_zeni = view_zeni;
    }

    public double getAzi_diff_hl() {
        return azi_diff_hl;
    }

//    public void setAzi_diff_hl(double azi_diff_hl) {
//        this.azi_diff_hl = azi_diff_hl;
//    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getSalinity() {
        return salinity;
    }

    public void setSalinity(double salinity) {
        this.salinity = salinity;
    }

    public double[] getTdown_nn() {
        return tdown_nn;
    }

    public void setTdown_nn(double[] tdown_nn) {
        this.tdown_nn = tdown_nn;
    }

    public void setTdown_nn(int index, double value) {
        this.tdown_nn[index] = value;
    }

    public double[] getTup_nn() {
        return tup_nn;
    }

    public void setTup_nn(double[] tup_nn) {
        this.tup_nn = tup_nn;
    }

    public void setTup_nn(int index, double value) {
        this.tup_nn[index] = value;
    }

    public double[] getRpath_nn() {
        return rpath_nn;
    }

    public void setRpath_nn(double[] rpath_nn) {
        System.arraycopy(rpath_nn, 0, this.rpath_nn, 0, rpath_nn.length);
    }

    public void setRpath_nn(int index, double value) {
        this.rpath_nn[index] = value;
    }

    public double[] getRw_nn() {
        return rw_nn;
    }

    public void setRw_nn(double[] rw_nn) {
        this.rw_nn = rw_nn;
    }

    public void setRw_nn(int index, double value) {
        this.rw_nn[index] = value;
    }

}
