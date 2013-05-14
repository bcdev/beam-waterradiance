package org.esa.beam.waterradiance.realoptimizers;

import Jama.Matrix;

/**
 * Created with IntelliJ IDEA.
 * User: tonio
 * Date: 23.04.13
 * Time: 15:16
 * To change this template use File | Settings | File Templates.
 */
public class LevenbergMarquardtOptimizer3 {

    private static final double eps1 = 1e-17;
    private final static double tau = 1e-3;
    private final static int blockSize = 32;
    private final static int squaredBlockSize = blockSize * blockSize;

    public double[] solveConstrainedLevenbergMarquardt(ForwardModel model, CostFunction function,
                                                       double[] p, double[] x,
                                                       BreakingCriterion criterion,
                                                       double[] lb,
                                                       double[] ub) {
        final int m = p.length;
        final int n = x.length;
        double[] hx = model.getModeledSignal(p);
        final double cost = function.getCost(hx);
        double p_eL2 = 0;   //  This should be the cost from the cost function
        double[] e = new double[n];
        for (int i = 0; i < n; ++i) {
            e[i] = x[i] - hx[i];
            p_eL2 += Math.pow(e[i], 2);
        }
//        final double initialSquaredTotalError = p_eL2;
        Matrix jacTjac = new Matrix(m, m);
        double[] jacTe = new double[m];
        int numberOfIterations = 0;
        double mu = 0;
        double nu = 2;
        double Dp_L2 = Double.MAX_VALUE;
        double alpha = 0;
        int gprevtaken = 0;
        boolean breaknested = false;
        double[] diag_jacTjac = new double[m];
        double eps3 = 1e-10;
        while (!criterion.isMet(p_eL2, numberOfIterations) && !breaknested) {
//            if (numberOfIterations % 1 == 0) {
            System.out.println("Iteration " + numberOfIterations + ":");
            System.out.println("Current total error: " + p_eL2);
//            }
            if (p_eL2 <= eps3) {
                break;
            }
            // It is also possible to derive a jacobian matrix from center differences.
            // I am indifferent to which one to use.
//            final Matrix jac = getJacobianMatrixFromForwardDifferences(model, variables, hx);
            final Matrix jac = getJacobianMatrixFromCenterDifferences(model, p, hx);
            if (m * n < squaredBlockSize) {
                for (int i = 0; i < m; i++) {
                    for (int j = 0; j < m; j++) {
                        jacTjac.set(i, j, 0);
                    }
                    jacTe[i] = 0;
                }
                for (int i = n - 1; i >= 0; i--) {
                    for (int j = m - 1; j >= 0; j--) {
                        alpha = jac.get(i, j);
                        for (int k = j; k >= 0; k--) {
                            final double value2 = jac.get(i, k);
                            final double currentValue = jacTjac.get(j, k);
                            jacTjac.set(j, k, currentValue + (alpha * value2));
                            jacTjac.set(k, j, currentValue + (alpha * value2));
                        }
                        jacTe[j] += alpha * e[i];
                    }
                }
            } else {
                jacTjac = blockedMultiplication(jac, jacTjac, n, m);
                for (int i = 0; i < m; i++) {
                    jacTe[m] = 0;
                }
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < m; j++) {
                        jacTe[j] = jac.get(i, j) * x[j];
                    }
                }
            }
            double jacTe_inf = 0;
            double p_L2 = 0;
            int numActive = 0;
            int jReplacement = 0;
            for (int i = 0; i < m; ++i) {
                if (ub != null && p[i] == ub[i]) {
                    numActive++;
                    if (jacTe[i] > 0) {
                        jReplacement++;
                    }
                } else if (lb != null && p[i] == lb[i]) {
                    numActive++;
                    if (jacTe[i] < 0) {
                        jReplacement++;
                    }
                } else if (jacTe_inf < Math.abs(jacTe[i])) {
                    jacTe_inf = Math.abs(jacTe[i]);
                }
                diag_jacTjac[i] = jacTjac.get(i, i);
                p_L2 += p[i] * p[i];
            }
            if (numActive == jReplacement && jacTe_inf < eps1) {
                Dp_L2 = 0;
                break;  // gradient too small
            }
            if (numberOfIterations == 0) {
                if (lb == null && ub == null) {
                    double tmp = Double.MIN_VALUE;
                    for (int i = 0; i < m; i++) {
                        if (diag_jacTjac[i] > tmp) {
                            tmp = diag_jacTjac[i];
                        }
                    }
                    mu = tau * tmp;
                } else {
                    mu = 0.5 * tau * p_eL2;
                }
            }
            while (!breaknested) {
                for (int i = 0; i < m; ++i) {
                    jacTjac.set(i, i, jacTjac.get(i, i) + mu);
                }
                double[] Dp = getSolutionForLinearEquation(jacTjac, jacTe);
                double[] pDp = new double[m];
                double pDp_eL2 = 0;
                if (Dp != null) {
                    for (int i = 0; i < m; ++i) {
                        pDp[i] = p[i] + Dp[i];
                    }
                    pDp = fitToBounds(pDp, lb, ub);
                    Dp_L2 = 0;
                    for (int i = 0; i < m; ++i) {
                        Dp[i] = pDp[i] - p[i];
                        Dp_L2 += Math.pow(Dp[i], 2);
                    }
                    if (Dp_L2 <= eps1 * eps1 * p_L2) {
                        break;  // stopped by small Dp
                    }
                    double epsilon = calculateMachineEpsilonDouble();
                    if (Dp_L2 >= (p_L2 + (eps1 * eps1)) / (epsilon * epsilon)) {
                        break;  //singular (or almost singular) matrix
                    }
                    hx = model.getModeledSignal(pDp);
                    pDp_eL2 = 0;
                    for (int i = 0; i < n; ++i) {
                        hx[i] = x[i] - hx[i];
                        pDp_eL2 += Math.pow(hx[i], 2);
                    }
                    if (!(pDp_eL2 > Double.NEGATIVE_INFINITY) || !(pDp_eL2 < Double.POSITIVE_INFINITY)) {
                        break;
                    }
                    double gamma_sq = Math.pow(0.99995, 2);
                    double dL = 0;
                    if (pDp_eL2 <= gamma_sq * p_eL2) {
                        for (int i = 0; i < m; ++i) {
                            dL += Dp[i] * (mu * Dp[i] + jacTe[i]);
                        }
                        double dF = 0;
                        if (dL > 0) {
                            dF = p_eL2 - pDp_eL2;
                            double temp = 2d * dF / dL - 1d;
                            temp = 1 - temp * temp * temp;
                            mu = mu * Math.max(temp, (1d / 3d));
                        } else {
                            double temp = 0.1 * pDp_eL2;
                            mu = Math.min(mu, temp);
                        }
                        nu = 2;
                        for (int i = 0; i < m; ++i) {
                            p[i] = pDp[i];
                        }
                        for (int i = 0; i < n; ++i) {
                            e[i] = hx[i];
                        }
                        p_eL2 = pDp_eL2;
                        gprevtaken = 0;
                        break;
                    }
                } else {    // is NOT solved
                    mu *= nu;
                    if (2 * nu < nu) {
                        break;
                    }
                    nu *= 2;
                    for (int i = 0; i < m; ++i) {
                        jacTjac.set(i, i, diag_jacTjac[i]);
                    }
                    continue;
                }
                double jacTeDp = 0;
                for (int i = 0; i < m; ++i) {
                    jacTe[i] = -jacTe[i];
                    jacTeDp += jacTe[i] * Dp[i];
                }
                double rho = 1e-8;
                boolean gradproj = jacTeDp <= -rho * Math.pow(Dp_L2, (2.1d / 2d));
                double t = 1;
                if (gradproj) {
                    while (gradproj && t > 1e-12) {
                        for (int i = 0; i < m; ++i) {
                            pDp[i] = p[i] + t * Dp[i];
                        }
                        pDp = fitToBounds(pDp, lb, ub);
                        hx = model.getModeledSignal(pDp);
                        pDp_eL2 = 0;
                        for (int i = 0; i < n; ++i) {
                            hx[i] = x[i] - hx[i];
                            pDp_eL2 += Math.pow(hx[i], 2);
                        }
                        if (!(pDp_eL2 > Double.NEGATIVE_INFINITY) || !(pDp_eL2 < Double.POSITIVE_INFINITY)) {
                            gradproj = false;
                            break;
                        } else {
                            if (pDp_eL2 <= p_eL2 + 2 * t * alpha * jacTeDp) {
                                break;
                            }
                        }
                        t *= 0.9;
                    }
                    gprevtaken = 0;
                }
                if (!gradproj) {
                    double temp = 0;
                    for (int i = 0; i < m; ++i) {
                        temp += jacTe[i] * jacTe[i];
                    }
                    temp = Math.sqrt(temp);
                    temp = 100 / (1 + temp);
                    double t0 = Math.min(temp, 1);
                    if (gprevtaken == 0) {
                        t = t0;
                    }
                    boolean terminatePGLS = false;
                    while (t > 1e-18 && !breaknested && !terminatePGLS) {
                        for (int i = 0; i < m; ++i) {
                            pDp[i] = p[i] - t * jacTe[i];
                        }
                        pDp = fitToBounds(pDp, lb, ub);
                        Dp_L2 = 0;
                        for (int i = 0; i < m; ++i) {
                            Dp[i] = pDp[i] - p[i];
                            Dp_L2 += Dp[i] * Dp[i];
                        }
                        hx = model.getModeledSignal(pDp);
                        pDp_eL2 = 0;
                        for (int i = 0; i < n; ++i) {
                            hx[i] = x[i] - hx[i];
                            pDp_eL2 += Math.pow(hx[i], 2);
                        }
                        if (!(pDp_eL2 > Double.NEGATIVE_INFINITY) ||
                                !(pDp_eL2 < Double.POSITIVE_INFINITY)) {
                            breaknested = true;
                            break;
                        }
                        if (!breaknested) {
                            jacTeDp = 0;
                            for (int i = 0; i < m; ++i) {
                                jacTeDp += jacTe[i] * Dp[i];
                            }
                            if (gprevtaken == 1 && pDp_eL2 <= p_eL2 + 2 * 0.99999 * jacTeDp) {
                                t = t0;
                                gprevtaken = 0;
                            }
                            if (pDp_eL2 <= p_eL2 + 2 * alpha * jacTeDp) {
                                terminatePGLS = true;
                            }
                        }
                        t *= 0.9;
                    }
                    if (!breaknested && !terminatePGLS) {
                        gprevtaken = 0;
                    }
                    if (!breaknested) {
                        gprevtaken = 1;
                    }
                }
                if (!breaknested) {
                    Dp_L2 = 0;
                    for (int i = 0; i < m; ++i) {
                        double temp = pDp[i] - p[i];
                        Dp_L2 += temp * temp;
                    }
                    if (Dp_L2 <= eps1 * eps1 * p_L2) {
//                        stop = 2
                        break;
                    }
                    for (int i = 0; i < m; ++i) {
                        p[i] = pDp[i];
                    }
                    for (int i = 0; i < n; ++i) {
                        e[i] = hx[i];
                    }
                    p_eL2 = pDp_eL2;
                    break;
                }
            }
            numberOfIterations++;
        }

        for (int i = 0; i < m; ++i) {
            jacTjac.set(i, i, diag_jacTjac[i]);
        }
        return p;
    }

    /**
     * Source: Wikipedia
     * http://en.wikipedia.org/wiki/Machine_epsilon#Approximation_using_Java, accessed 22.04.2013
     */
    private static double calculateMachineEpsilonDouble() {
        double machEps = 1.0f;

        do
            machEps /= 2.0f;
        while ((double) (1.0 + (machEps / 2.0)) != 1.0);

        return machEps;
    }

    private double[] fitToBounds(double[] updatedParameters, double[] lowerBounds, double[] upperBounds) {
        for (int i = updatedParameters.length - 1; i >= 0; --i) {
            if (lowerBounds != null) {
                updatedParameters[i] = Math.max(updatedParameters[i], lowerBounds[i]);
            }
            if (upperBounds != null) {
                updatedParameters[i] = Math.min(updatedParameters[i], upperBounds[i]);
            }
        }
        return updatedParameters;
    }

    private static double[] getSolutionForLinearEquation(Matrix jTJMatrix, double[] jTE) {
        double[][] jTEMatrixArray = new double[jTE.length][1];
        for (int i = 0; i < jTE.length; i++) {
            jTEMatrixArray[i][0] = jTE[i];
        }
        Matrix jTEMatrix = new Matrix(jTEMatrixArray);
        final double det = jTJMatrix.det();
        if (det != 0) {
            final Matrix solutionForLinearEquationMatrix = jTJMatrix.solve(jTEMatrix);
            return solutionForLinearEquationMatrix.getRowPackedCopy();
        }
        return null;
    }

    private Matrix blockedMultiplication(Matrix jacobianMatrix,
                                         Matrix jacobianTransposedJacobianMatrix,
                                         int numberOfSignalValues, int numberofVariables) {
        for (int i = 0; i < numberofVariables; i += blockSize) {
            for (int j = 0; j < numberofVariables; j++) {
//                final int max = Math.max(i, j);
                final int min = Math.min(i + blockSize, numberofVariables);
                for (int max = Math.max(i, j); max < min; max++) {
                    jacobianTransposedJacobianMatrix.set(j, max, 0);
                }
            }
            for (int j = 0; j < numberOfSignalValues; j++) {
                for (int k = 0; k < numberofVariables; k++) {
                    final int min = Math.min(i + blockSize, numberofVariables);
                    for (int max = Math.max(i, k); max < min; max++) {
                        int sum = 0;
                        for (int l = j; l < Math.min(j + blockSize, numberOfSignalValues); l++) {
                            sum += jacobianMatrix.get(l, k) * jacobianMatrix.get(l, max);
                        }
                        jacobianTransposedJacobianMatrix.set(k, max, sum);
                        jacobianTransposedJacobianMatrix.set(max, k, sum);
                    }
                }
            }
        }
        return jacobianTransposedJacobianMatrix;
    }

    private Matrix getJacobianMatrixFromForwardDifferences(ForwardModel model, double[] variables,
                                                           double[] modeledSignal) {
        double delta = 0.001;
        Matrix jacobianMatrix = new Matrix(modeledSignal.length, variables.length);
        final double[] matrixVariables = variables.clone();
        for (int i = 0; i < variables.length; i++) {
            double value = Math.max(Math.abs(variables[i] * 1e04), delta);
            matrixVariables[i] += value;
            final double[] tempModeledSignal = model.getModeledSignal(matrixVariables);
            value = (1 / value);
            for (int j = 0; j < modeledSignal.length; j++) {
                jacobianMatrix.set(j, i, (tempModeledSignal[j] - modeledSignal[j]) * value);
            }
        }
        return jacobianMatrix;
    }

    private Matrix getJacobianMatrixFromCenterDifferences(ForwardModel model, double[] variables, double[] modeledSignal) {
        Matrix jac = new Matrix(modeledSignal.length, variables.length);
        for (int j = 0; j < variables.length; ++j) {
            double d = Math.abs(1E-04 * variables[j]); // force evaluation
            final double delta = 1E-01;
            if (d < delta) {
                d = delta;
            }
            double tmp = variables[j];
            variables[j] -= d;
            final double[] modeledSignal1 = model.getModeledSignal(variables);
            variables[j] = tmp + d;
            final double[] modeledSignal2 = model.getModeledSignal(variables);
            variables[j] = tmp; /* restore */
            d = 0.5 / d; /* invert so that divisions can be carried out faster as multiplications */
            for (int i = 0; i < modeledSignal.length; ++i) {
                jac.set(i, j, (modeledSignal2[i] - modeledSignal1[i]) * d);
            }
        }
        return jac;
    }

    public static void main(String[] args) {
        double[] startVariables = {3.05, 2.05};
        double[] measuredSignal = {2, 4};
        double[] lowerBounds = {0, 1};
        double[] upperBounds = {4, 3};
        LevenbergMarquardtOptimizer3 optimizer = new LevenbergMarquardtOptimizer3();
        final double[] optimizedVariables = optimizer.solveConstrainedLevenbergMarquardt(new ForwardModelImpl(),
                                                                                         new CostFunctionImpl(), startVariables,
                                                                                         measuredSignal,
                                                                                         new BreakingCriterionImpl(),
                                                                                         lowerBounds, upperBounds);
        System.out.println("Optimized Variables. 1 = " + optimizedVariables[0] + ", 2 = " + optimizedVariables[1]);
    }

    private static class ForwardModelImpl implements ForwardModel {

        @Override
        public void init(double[] knownParameters) {
        }

        @Override
        public double[] getModeledSignal(double[] variables) {
            final double[] modeledSignal = {Math.pow(variables[0], 2) - 4 * variables[0] + 5, Math.pow(variables[1], 2)};
            return modeledSignal;
        }

        @Override
        public double getPartialDerivative(double[] signal, double[] variables, int parameterIndex) {
            return 0;
        }
    }

    private static class BreakingCriterionImpl implements BreakingCriterion {

        @Override
        public boolean isMet(double cost, int numberOfIterations) {
            return cost < 0.000001 || numberOfIterations > 100000;
        }
    }

    private static class CostFunctionImpl implements CostFunction {

        @Override
        public void setWeights(double[] weights) {
        }

        @Override
        public double getCost(double[] signal) {
            return 0;
        }
    }

    private static class EverFalseBreakingCriterionImpl implements BreakingCriterion {

        @Override
        public boolean isMet(double cost, int numberOfIterations) {
            return false;
        }
    }

}
