package org.esa.beam.waterradiance.realoptimizers;

import org.esa.beam.siocs.abstractprocessor.support.AbstractCostFunction;

public class OcNnRdCostFunction extends AbstractCostFunction {

    @Override
    public double getCost(double[] signal) {
        setErrors(signal);
        int n = signal.length;
        double cost = 0;
        for (int i = 0; i < n; ++i) {
            cost += getWeights()[i] * (getErrors()[i] * getErrors()[i]);
        }
        return cost;
    }

}
