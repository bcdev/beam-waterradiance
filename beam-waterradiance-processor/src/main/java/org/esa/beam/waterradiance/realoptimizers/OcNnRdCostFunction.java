package org.esa.beam.waterradiance.realoptimizers;


import com.bc.ceres.binding.PropertySet;
import com.bc.siocs.core.CostFunction;
import com.bc.siocs.core.support.AbstractCostFunction;

import java.util.Arrays;

public class OcNnRdCostFunction extends AbstractCostFunction {

    protected OcNnRdCostFunction(double[] weights) {
        super(AbstractCostFunction.createConfig(weights));
    }

    @Override
    public double getCost(double[] signal) {
        calculateErrors(signal);
        int n = signal.length;
        double cost = 0;
        for (int i = 0; i < n; ++i) {
            cost += getWeights()[i] * (getErrors()[i] * getErrors()[i]);
        }
        return cost;
    }

    @Override
    public CostFunction clone() {
        OcNnRdCostFunction function = new OcNnRdCostFunction(getWeights());
        function.init(productHasIrradianceReflectances(), forwardModelReturnsIrradianceReflectances());
        return function;
    }

    @Override
    public String getName() {
        return "OcNnRd-Costs";
    }
}
