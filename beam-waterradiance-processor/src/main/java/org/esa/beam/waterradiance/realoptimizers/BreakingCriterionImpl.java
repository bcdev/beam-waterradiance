package org.esa.beam.waterradiance.realoptimizers;

/**
 * Created with IntelliJ IDEA.
 * User: tonio
 * Date: 10.05.13
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
public class BreakingCriterionImpl implements BreakingCriterion {

    private final int itmax;
    private final double threshold;

    public BreakingCriterionImpl(int itmax, double threshold) {
        this.itmax = itmax;
        this.threshold = threshold;
    }

    @Override
    public boolean isMet(double cost, int numberOfIterations) {
        return cost < threshold || numberOfIterations > itmax;
    }
}
