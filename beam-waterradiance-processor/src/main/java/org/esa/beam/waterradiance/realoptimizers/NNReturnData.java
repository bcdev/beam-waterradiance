package org.esa.beam.waterradiance.realoptimizers;

class NNReturnData {

    private double[] outputValues;
    private s_nn_atdata nn_atdata;

    void setOutputValues(double[] outputValues) {
        this.outputValues = outputValues;
    }

    double[] getOutputValues() {
        return outputValues;
    }

    void setNn_atdata(s_nn_atdata nn_atdata) {
        this.nn_atdata = nn_atdata;
    }

    s_nn_atdata getNn_atdata() {
        return nn_atdata;
    }
}
