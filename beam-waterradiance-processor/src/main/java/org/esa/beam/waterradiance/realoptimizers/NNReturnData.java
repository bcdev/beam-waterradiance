package org.esa.beam.waterradiance.realoptimizers;

class NNReturnData {

    double[] outputValues;
    s_nn_atdata nn_atdata;

    NNReturnData(double[] output, s_nn_atdata data) {
        outputValues = output;
        nn_atdata = data;
    }

    double[] getOutputValues() {
        return outputValues;
    }

    s_nn_atdata getNn_atdata() {
        return nn_atdata;
    }
}
