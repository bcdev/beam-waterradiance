package org.esa.beam.waterradiance.realoptimizers;

/**
 * Created with IntelliJ IDEA.
 * User: tonio
 * Date: 26.04.13
 * Time: 14:30
 * To change this template use File | Settings | File Templates.
 */
class ForwardModelImpl implements ForwardModel {

    @Override
    public void init(double[] knownParameters) {
    }

    @Override
    public double[] getModeledSignal(double[] variables) {
        final double[] modeledSignal = {Math.pow(variables[0], 2), Math.pow(variables[1], 3)};
        return modeledSignal;
    }

    @Override
    public double getPartialDerivative(double[] signal, double[] variables, int parameterIndex) {
        return 0;
    }
}
