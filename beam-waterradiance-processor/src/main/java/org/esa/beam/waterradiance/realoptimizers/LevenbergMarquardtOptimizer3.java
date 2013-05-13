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

    private static final double convergenceThreshold = 1e-17;
    private final static double initialDampingFactor = 1e-3;
    private final static int blockSize = 32;
    private final static int squaredBlockSize = blockSize * blockSize;

    public double[] solveConstrainedLevenbergMarquardt(ForwardModel model, CostFunction function,
                                                       double[] variables, double[] measuredSignal,
                                                       BreakingCriterion criterion,
                                                       double[] lowerBounds,
                                                       double[] upperBounds) {
        final double[] modeledSignal = model.getModeledSignal(variables);
        final int numberOfVariables = variables.length;
        final int numberOfSignalValues = measuredSignal.length;
        final double cost = function.getCost(modeledSignal);
        double squaredTotalError = 0;   //  This should be the cost from the cost function
        double[] errorPerSignalValue = new double[numberOfSignalValues];
        for (int i = 0; i < numberOfSignalValues; ++i) {
            errorPerSignalValue[i] = measuredSignal[i] - modeledSignal[i];
            squaredTotalError += Math.pow(errorPerSignalValue[i], 2);
        }
        final double initialSquaredTotalError = squaredTotalError;
        Matrix jacobianTransposedJacobianMatrix = new Matrix(numberOfVariables, numberOfVariables);
        double[] jacobianTransposedE = new double[numberOfVariables];
        int numberOfIterations = 0;
        double dampingConstant = 0;
        double nu = 2;
        double[] updatedSignal = modeledSignal;
        double updatedTotalSquaredError = Double.MAX_VALUE;
        double alpha = 0;
        int previousGradientTaken = 0;
        boolean breaknested = false;
        double[] diagonalOfJacobianTransposedJacobianMatrix = new double[numberOfVariables];
        while (!criterion.isMet(squaredTotalError, numberOfIterations) && !breaknested) {
//            if (numberOfIterations % 1 == 0) {
            System.out.println("Iteration " + numberOfIterations + ":");
            System.out.println("Current total error: " + squaredTotalError);
//            }
            if (squaredTotalError <= convergenceThreshold) {
                break;
            }
            // It is also possible to derive a jacobian matrix from center differences.
            // I am indifferent to which one to use.
//            final Matrix jacobianMatrix = getJacobianMatrixFromForwardDifferences(model, variables, updatedSignal);
            final Matrix jacobianMatrix = getJacobianMatrixFromCenterDifferences(model, variables, updatedSignal);
            if (numberOfVariables * numberOfSignalValues < squaredBlockSize) {
                for (int i = 0; i < numberOfVariables; i++) {
                    for (int j = 0; j < numberOfVariables; j++) {
                        jacobianTransposedJacobianMatrix.set(i, j, 0);
                    }
                    jacobianTransposedE[i] = 0;
                }
                for (int i = numberOfSignalValues - 1; i >= 0; i--) {
                    for (int j = numberOfVariables - 1; j >= 0; j--) {
                        alpha = jacobianMatrix.get(i, j);
                        for (int k = j; k >= 0; k--) {
                            final double value2 = jacobianMatrix.get(i, k);
                            final double currentValue = jacobianTransposedJacobianMatrix.get(j, k);
                            jacobianTransposedJacobianMatrix.set(j, k, currentValue + (alpha * value2));
                            jacobianTransposedJacobianMatrix.set(k, j, currentValue + (alpha * value2));
                        }
                        jacobianTransposedE[j] += alpha * errorPerSignalValue[i];
                    }
                }
            } else {
                jacobianTransposedJacobianMatrix =
                        blockedMultiplication(jacobianMatrix, jacobianTransposedJacobianMatrix,
                                              numberOfSignalValues, numberOfVariables);
                for (int i = 0; i < numberOfVariables; i++) {
                    jacobianTransposedE[numberOfVariables] = 0;
                }
                for (int i = 0; i < numberOfSignalValues; i++) {
                    for (int j = 0; j < numberOfVariables; j++) {
                        jacobianTransposedE[j] = jacobianMatrix.get(i, j) * measuredSignal[j];
                    }
                }
            }
            double jacobianTransposedEInf = 0;
            double squaredParameters = 0;
            int numActive = 0;
            int jReplacement = 0;
            for (int i = 0; i < numberOfVariables; ++i) {
                if (upperBounds != null && variables[i] == upperBounds[i]) {
                    numActive++;
                    if (jacobianTransposedE[i] > 0) {
                        jReplacement++;
                    }
                } else if (lowerBounds != null && variables[i] == lowerBounds[i]) {
                    numActive++;
                    if (jacobianTransposedE[i] < 0) {
                        jReplacement++;
                    }
                } else if (jacobianTransposedEInf < Math.abs(jacobianTransposedE[i])) {
                    jacobianTransposedEInf = Math.abs(jacobianTransposedE[i]);
                }
                diagonalOfJacobianTransposedJacobianMatrix[i] = jacobianTransposedJacobianMatrix.get(i, i);
                squaredParameters += variables[i] * variables[i];
            }
            if (numActive == jReplacement && jacobianTransposedEInf < convergenceThreshold) {
                updatedTotalSquaredError = 0;
                break;  // gradient too small
            }
            if (numberOfIterations == 0) {
                if (lowerBounds == null && upperBounds == null) {
                    double largestElementOfDiagonalOfJacobianTransposedJacobianMatrix = Double.MIN_VALUE;
                    for (int i = 0; i < numberOfVariables; i++) {
                        if (diagonalOfJacobianTransposedJacobianMatrix[i] >
                                largestElementOfDiagonalOfJacobianTransposedJacobianMatrix) {
                            largestElementOfDiagonalOfJacobianTransposedJacobianMatrix =
                                    diagonalOfJacobianTransposedJacobianMatrix[i];
                        }
                    }
                    dampingConstant = initialDampingFactor * largestElementOfDiagonalOfJacobianTransposedJacobianMatrix;
                } else {
                    dampingConstant = 0.5 * initialDampingFactor * squaredTotalError;
                }
            }
            while (!breaknested) {
                for (int i = 0; i < numberOfVariables; ++i) {
                    jacobianTransposedJacobianMatrix.set(i, i, jacobianTransposedJacobianMatrix.get(i, i) + dampingConstant);
                }
                double[] solutionForLinearEquation = getSolutionForLinearEquation(jacobianTransposedJacobianMatrix, jacobianTransposedE);
                double[] updatedVariables = new double[numberOfVariables];
                double updatedTotalSignalError = 0;
                if (solutionForLinearEquation != null) {
                    for (int i = 0; i < numberOfVariables; ++i) {
                        updatedVariables[i] = variables[i] + solutionForLinearEquation[i];
                    }
                    updatedVariables = fitToBounds(updatedVariables, lowerBounds, upperBounds);
                    updatedTotalSquaredError = 0;
                    for (int i = 0; i < numberOfVariables; ++i) {
                        solutionForLinearEquation[i] = updatedVariables[i] - variables[i];
                        updatedTotalSquaredError += Math.pow(solutionForLinearEquation[i], 2);
                    }
                    if (updatedTotalSquaredError <= convergenceThreshold * convergenceThreshold * squaredParameters) {
                        break;  // stopped by small Dp
                    }
                    double epsilon = calculateMachineEpsilonDouble();
                    if (updatedTotalSquaredError >= (squaredParameters + (convergenceThreshold * convergenceThreshold)) / (epsilon * epsilon)) {
                        break;  //singular (or almost singular) matrix
                    }
                    updatedSignal = model.getModeledSignal(updatedVariables);
                    updatedTotalSignalError = 0;
                    for (int i = 0; i < numberOfSignalValues; ++i) {
                        updatedSignal[i] = measuredSignal[i] - updatedSignal[i];
                        updatedTotalSignalError += Math.pow(updatedSignal[i], 2);
                    }
                    if (!(updatedTotalSignalError > Double.NEGATIVE_INFINITY) || !(updatedTotalSignalError < Double.POSITIVE_INFINITY)) {
                        break;
                    }
                    double gamma = 0.99995;
                    double dL = 0;
                    if (updatedTotalSignalError <= gamma * squaredTotalError) {
                        for (int i = 0; i < numberOfVariables; ++i) {
                            dL += solutionForLinearEquation[i] *
                                    (dampingConstant * solutionForLinearEquation[i] + jacobianTransposedE[i]);
                        }
                        double dF = 0;
                        if (dL > 0) {
                            dF = squaredTotalError - updatedTotalSignalError;
                            double temp = 2d * dF / dL - 1d;
                            temp = 1 - temp * temp * temp;
                            dampingConstant = dampingConstant * Math.max(temp, (1d / 3d));
                        } else {
                            double temp = 0.1 * updatedTotalSignalError;
                            dampingConstant = Math.min(dampingConstant, temp);
                        }
                        nu = 2;
                        for (int i = 0; i < numberOfVariables; ++i) {
                            variables[i] = updatedVariables[i];
                        }
                        for (int i = 0; i < numberOfSignalValues; ++i) {
                            errorPerSignalValue[i] = updatedSignal[i];
                        }
                        squaredTotalError = updatedTotalSignalError;
                        previousGradientTaken = 0;
                        break;
                    }
                } else {    // is NOT solved
                    dampingConstant *= nu;
                    if (2 * nu < nu) {
                        break;
                    }
                    nu *= 2;
                    for (int i = 0; i < numberOfVariables; ++i) {
                        jacobianTransposedJacobianMatrix.set(i, i, diagonalOfJacobianTransposedJacobianMatrix[i]);
                    }
                    continue;
                }
                double jacTeDp = 0;
                for (int i = 0; i < numberOfVariables; ++i) {
                    jacobianTransposedE[i] = -jacobianTransposedE[i];
                    jacTeDp += jacobianTransposedE[i] * solutionForLinearEquation[i];
                }
                double rho = 1e8;
                boolean gradproj = jacTeDp <= rho * Math.pow(updatedTotalSquaredError, (2.1d / 2d));
                double t = 1;
                if (gradproj) {
                    while (gradproj && t > 1e-12) {
                        for (int i = 0; i < numberOfVariables; ++i) {
                            updatedVariables[i] = variables[i] + t * solutionForLinearEquation[i];
                        }
                        updatedVariables = fitToBounds(updatedVariables, lowerBounds, upperBounds);
                        updatedSignal = model.getModeledSignal(updatedVariables);
                        updatedTotalSignalError = 0;
                        for (int i = 0; i < numberOfSignalValues; ++i) {
                            updatedSignal[i] = measuredSignal[i] - updatedSignal[i];
                            updatedTotalSignalError += Math.pow(updatedSignal[i], 2);
                        }
                        if (!(updatedTotalSignalError > Double.NEGATIVE_INFINITY) ||
                                !(updatedTotalSignalError < Double.POSITIVE_INFINITY)) {
                            // todo goto wtf! -> Line 855
                            gradproj = false;
                            break;
                        } else {
                            if (updatedTotalSignalError <= squaredTotalError + 2 * t * alpha * jacTeDp) {
                                break;
                            }
                        }
                        t *= 0.9;
                    }
                    previousGradientTaken = 0;
                }
                if (!gradproj) {
                    double temp = 0;
                    for (int i = 0; i < numberOfVariables; ++i) {
                        temp += jacobianTransposedE[i] * jacobianTransposedE[i];
                    }
                    temp = Math.sqrt(temp);
                    temp = 100 / (1 + temp);
                    double t0 = Math.min(temp, 1);
                    if (previousGradientTaken == 0) {
                        t = t0;
                    }
                    boolean terminatePGLS = false;
                    while (t > 1e-18 && !breaknested && !terminatePGLS) {
                        for (int i = 0; i < numberOfVariables; ++i) {
                            updatedVariables[i] = variables[i] - t * jacobianTransposedE[i];
                        }
                        updatedVariables = fitToBounds(updatedVariables, lowerBounds, upperBounds);
                        updatedTotalSquaredError = 0;
                        for (int i = 0; i < numberOfVariables; ++i) {
                            solutionForLinearEquation[i] = updatedVariables[i] - variables[i];
                            updatedTotalSquaredError += solutionForLinearEquation[i] * solutionForLinearEquation[i];
                        }
                        updatedSignal = model.getModeledSignal(updatedVariables);
                        updatedTotalSignalError = 0;
                        for (int i = 0; i < numberOfSignalValues; ++i) {
                            updatedSignal[i] = measuredSignal[i] - updatedSignal[i];
                            updatedTotalSignalError += Math.pow(updatedSignal[i], 2);
                        }
                        if (!(updatedTotalSignalError > Double.NEGATIVE_INFINITY) ||
                                !(updatedTotalSignalError < Double.POSITIVE_INFINITY)) {
                            breaknested = true;
                            break;
//                            goto breaknested line 917
                        }
                        if (!breaknested) {
                            jacTeDp = 0;
                            for (int i = 0; i < numberOfVariables; ++i) {
                                jacTeDp += jacobianTransposedE[i] * solutionForLinearEquation[i];
                            }
                            if (previousGradientTaken == 1 && updatedTotalSignalError <= squaredTotalError + 2 * 0.99999 * jacTeDp) {
                                t = t0;
                                previousGradientTaken = 0;
                            }
                            if (updatedTotalSignalError <= squaredTotalError + 2 * alpha * jacTeDp) {
                                terminatePGLS = true;
                            }
                        }
                        t *= 0.9;
                    }
                    if (!breaknested && !terminatePGLS) {
                        previousGradientTaken = 0;
                    }
                    if (!breaknested) {
                        previousGradientTaken = 1;
                    }
                }
                if (!breaknested) {
                    updatedTotalSquaredError = 0;
                    for (int i = 0; i < numberOfVariables; ++i) {
                        double temp = updatedVariables[i] + variables[i];
                        updatedTotalSquaredError += temp * temp;
                    }
                    if (updatedTotalSquaredError <= convergenceThreshold * convergenceThreshold * squaredParameters) {
//                        stop = 2
                        break;
                    }
                    for (int i = 0; i < numberOfVariables; ++i) {
                        variables[i] = updatedVariables[i];
                    }
                    for (int i = 0; i < numberOfSignalValues; ++i) {
                        errorPerSignalValue[i] = updatedSignal[i];
                    }
                    squaredTotalError = updatedTotalSignalError;
                    break;
                }
            }
            numberOfIterations++;
        }

        for (int i = 0; i < numberOfVariables; ++i) {
            jacobianTransposedJacobianMatrix.set(i, i, diagonalOfJacobianTransposedJacobianMatrix[i]);
        }
        return variables;
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
