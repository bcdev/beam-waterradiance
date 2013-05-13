package org.esa.beam.waterradiance.realoptimizers;

public interface BreakingCriterion {

    boolean isMet(double cost, int numberOfIterations);

}
