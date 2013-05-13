package org.esa.beam.waterradiance.realoptimizers;

public interface CostFunction {

    void setWeights(double[] weights);

    double getCost(double[] signal);

}
