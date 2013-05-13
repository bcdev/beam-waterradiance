package org.esa.beam.waterradiance.realoptimizers;

/**
 * Created with IntelliJ IDEA.
 * User: tonio
 * Date: 26.04.13
 * Time: 14:31
 * To change this template use File | Settings | File Templates.
 */
class EverFalseBreakingCriterionImpl implements BreakingCriterion {

    @Override
    public boolean isMet(double cost, int numberOfIterations) {
        return false;
    }
}
