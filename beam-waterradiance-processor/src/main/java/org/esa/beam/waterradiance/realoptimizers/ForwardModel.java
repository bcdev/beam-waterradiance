package org.esa.beam.waterradiance.realoptimizers;

public interface ForwardModel {

    void init(double[] knownParameters);

    double[] getModeledSignal(double[] variables);

    double getPartialDerivative(double[] signal, double[] variables, int parameterIndex);

}
