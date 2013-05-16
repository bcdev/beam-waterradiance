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
    private static final double eps2_sq = 1e-20;
    private static final double eps2 = Math.sqrt(1e-20);
    private final static double tau = 1e-3;
    private final static int blockSize = 32;
    private final static int squaredBlockSize = blockSize * blockSize;
    private final double epsilon_sq;
    public static final double GAMMA_SQ = 0.99995 * 0.99995;
    public static final double DELTA = 1E-01;

    public LevenbergMarquardtOptimizer3() {
        final double epsilon = calculateMachineEpsilonDouble();
        epsilon_sq = epsilon * epsilon;
    }

    public double[] solveConstrainedLevenbergMarquardt(ForwardModel model, CostFunction function,
                                                       double[] p, double[] x,
                                                       BreakingCriterion criterion,
                                                       double[] lb,
                                                       double[] ub) {
        final int m = p.length;
        final int n = x.length;
        double[] hx = model.getModeledSignal(p);
       // final double cost = function.getCost(hx);
        double p_eL2 = 0;   //  This should be the cost from the cost function
        double[] e = new double[n];
        for (int i = 0; i < n; ++i) {
            e[i] = x[i] - hx[i];
            p_eL2 += e[i] * e[i];
        }
        Matrix jacTjac = new Matrix(m, m);
        double[] jacTe = new double[m];
        int numberOfIterations = 0;
        double mu = 0;
        double nu = 2;
        double Dp_L2;
        double alpha;
        int gprevtaken = 0;
        boolean breaknested = false;
        double[] diag_jacTjac = new double[m];
        double eps3 = 1e-10;
        int stop = 0;
        double jacTe_inf = 0;
        while (!criterion.isMet(p_eL2, numberOfIterations) && !breaknested && stop == 0) {
//            System.out.println("Iteration " + numberOfIterations + ":");
//            StringBuilder builder = new StringBuilder("Current parameter estimates:");
//            for (int i = 0; i < p.length; ++i) {
//                builder.append(" " + p[i]);
//
//            }
//            System.out.println(builder.toString());
//            System.out.println("Current jacTe_inf and p_eL2: " + jacTe_inf + ", " + p_eL2);
            if (p_eL2 <= eps3) {
                //stop = 6;
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
                for (int l = n - 1; l >= 0; --l) {
                    for (int i = m - 1; i >= 0; --i) {
                        alpha = jac.get(l, i);
                        for (int j = i; j >= 0; --j) {
                            final double jaclmj = jac.get(l, j);
                            final double currentValue = jacTjac.get(i, j);
                            jacTjac.set(i, j, currentValue + (alpha * jaclmj));
                            jacTjac.set(j, i, currentValue + (alpha * jaclmj));
                        }
                        jacTe[i] += alpha * e[l];
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
            jacTe_inf = 0;
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
                stop = 1;
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
//                double[] Dp = getSolutionForLinearEquation2(jacTjac, jacTe);
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
                        Dp_L2 += Dp[i] * Dp[i];
                    }
                    if (Dp_L2 <= eps2_sq * p_L2) {
                        stop = 2;
                        break;  // stopped by small Dp
                    }

                    if (Dp_L2 >= (p_L2 + eps2) / (epsilon_sq)) {
                        stop = 4;
                        break;  //singular (or almost singular) matrix
                    }
                    hx = model.getModeledSignal(pDp);
                    pDp_eL2 = 0;
                    for (int i = 0; i < n; ++i) {
                        hx[i] = x[i] - hx[i];
                        pDp_eL2 += hx[i] * hx[i];
                    }
                    if (!(pDp_eL2 > Double.NEGATIVE_INFINITY) || !(pDp_eL2 < Double.POSITIVE_INFINITY)) {
                        stop = 7;
                        break;
                    }
                    double dL = 0;
                    if (pDp_eL2 <= GAMMA_SQ * p_eL2) {
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
                        System.arraycopy(pDp, 0, p, 0, m);
                        System.arraycopy(hx, 0, e, 0, n);
                        p_eL2 = pDp_eL2;
                        gprevtaken = 0;
                        break;
                    }
                } else {    // is NOT solved
                    mu *= nu;
                    if (2 * nu < nu) {
                        stop = 5;
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
                    double tmp = Math.sqrt(p_L2);
                    double stepmx = 1e3 * ((tmp >= 1.0) ? tmp : 1.0);
                    alpha = 0.0001;
                    final double[][] doubles = lineSearch(p, model, p_eL2, stepmx, jacTe, Dp, pDp, hx.length, x, pDp_eL2, alpha);
                    pDp = doubles[0];
                    hx = doubles[1];
                    pDp_eL2 = doubles[2][0];
                    Dp = doubles[3];
                    if (doubles[4][0] != 0) {
                        while (gradproj && t > 1e-12) {
                            for (int i = 0; i < m; ++i) {
                                pDp[i] = p[i] + t * Dp[i];
                            }
                            hx = model.getModeledSignal(pDp);
                            pDp_eL2 = 0;
                            for (int i = 0; i < n; ++i) {
                                hx[i] = x[i] - hx[i];
                                pDp_eL2 += hx[i] * hx[i];
                            }
                            if (!(pDp_eL2 > Double.NEGATIVE_INFINITY) || !(pDp_eL2 < Double.POSITIVE_INFINITY)) {
                                stop = 7;
                                gradproj = false;
                            } else {
                                if (pDp_eL2 <= p_eL2 + 2 * t * alpha * jacTeDp) {
                                    break;
                                }
                            }
                            t *= 0.9;
                        }
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
                    alpha = 0.0001;
                    while (t > 1e-18 && !breaknested && !terminatePGLS) {
                        for (int i = 0; i < m; ++i) {
                            pDp[i] = p[i] - t * jacTe[i];
                        }
                        pDp = fitToBounds(pDp, lb, ub);
                        for (int i = 0; i < m; ++i) {
                            Dp[i] = pDp[i] - p[i];
                        }
                        hx = model.getModeledSignal(pDp);
                        pDp_eL2 = 0;
                        for (int i = 0; i < n; ++i) {
                            hx[i] = x[i] - hx[i];
                            pDp_eL2 += hx[i] * hx[i];
                        }
                        if (!(pDp_eL2 > Double.NEGATIVE_INFINITY) ||
                                !(pDp_eL2 < Double.POSITIVE_INFINITY)) {
                            breaknested = true;
                            break;
                        }
                        if (!breaknested) {
                            temp = 0;
                            for (int i = 0; i < m; ++i) {
                                temp += jacTe[i] * Dp[i];
                            }
                            if (gprevtaken == 1 && pDp_eL2 <= p_eL2 + 2 * 0.99999 * temp) {
                                t = t0;
                                gprevtaken = 0;
                                continue;
                            }
                            if (pDp_eL2 <= p_eL2 + 2 * alpha * temp) {
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
                    if (Dp_L2 <= eps2_sq * p_L2) {
                        stop = 2;
                        break;
                    }
                    System.arraycopy(pDp, 0, p, 0, m);
                    System.arraycopy(hx, 0, e, 0, n);
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

    private static double[] getSolutionForLinearEquation2(Matrix jTJMatrix, double[] jTE) {
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

    private double[] getSolutionForLinearEquation(Matrix A, double[] B) {
        int maxi = -1;
        double max, sum, tmp;
        int m = B.length;
        int[] idx = new int[m];
        double[] work = new double[m];
        double[][] a = A.getArrayCopy();
        double[] x = B.clone();
        double epsilon = calculateMachineEpsilonDouble();

          /* compute the LU decomposition of a row permutation of matrix a; the permutation itself is saved in idx[] */
        for (int i = 0; i < m; ++i) {
            max = 0.0;
            for (int j = 0; j < m; ++j)
                if ((tmp = Math.abs(a[i][j])) > max)
                    max = tmp;
            if (max == 0.0) {
                return null;
            }
            work[i] = 1.0 / max;
        }
        for (int j = 0; j < m; ++j) {
            for (int i = 0; i < j; ++i) {
                sum = a[i][j];
                for (int k = 0; k < i; ++k)
                    sum -= a[i][k] * a[k][j];
                a[i][j] = sum;
            }
            max = 0.0;
            for (int i = j; i < m; ++i) {
                sum = a[i][j];
                for (int k = 0; k < j; ++k)
                    sum -= a[i][k] * a[k][j];
                a[i][j] = sum;
                if ((tmp = work[i] * Math.abs(sum)) >= max) {
                    max = tmp;
                    maxi = i;
                }
            }
            if (j != maxi) {
                for (int k = 0; k < m; ++k) {
                    tmp = a[maxi][k];
                    a[maxi][k] = a[j][k];
                    a[j][k] = tmp;
                }
                work[maxi] = work[j];
            }
            idx[j] = maxi;
            if (a[j][j] == 0.0)
                a[j][j] = epsilon;
            if (j != m - 1) {
                tmp = 1.0 / (a[j][j]);
                for (int i = j + 1; i < m; ++i)
                    a[i][j] *= tmp;
            }
        }

          /* The decomposition has now replaced a. Solve the linear system using
   * forward and back substitution
   */
        int k = 0;
        for (int i = 0; i < m; ++i) {
            int j = idx[i];
            sum = x[j];
            x[j] = x[i];
            if (k != 0)
                for (j = k - 1; j < i; ++j)
                    sum -= a[i][j] * x[j];
            else if (sum != 0.0)
                k = i + 1;
            x[i] = sum;
        }

        for (int i = m - 1; i >= 0; --i) {
            sum = x[i];
            for (int j = i + 1; j < m; ++j)
                sum -= a[i][j] * x[j];
            x[i] = sum / a[i][i];
        }

        return x;

    }

    private double[][] lineSearch(double[] x, ForwardModel model, double p_eL2, double stepmx, double[] jacTe, double[] Dp,
                                  double[] pDp, int n, double[] stateX, double pDp_eL2, double alpha) {
        int i, j;
        int firstback = 1;
        double disc;
        double a3, b;
        double t1, t2, t3, lambda, tlmbda, rmnlmb;
        double scl, rln, sln, slp;
        double tmp1, tmp2;
        double fpls, pfpls = 0., plmbda = 0.; /* -Wall */
        double f = p_eL2;
        double steptl = 1e-3;
        double[] clonedDp = Dp.clone();

        double[] g = jacTe;
        double[] xpls = pDp.clone();
        double ffpls = pDp_eL2;
        double[] stateHX = new double[n];
        int m = x.length;

        f *= 0.5;
        int mxtake = 0;
        int iretcd = 2;
        tmp1 = 0.;
//        if(!sx) /* no scaling */
        for (i = 0; i < m; ++i)
            tmp1 += clonedDp[i] * clonedDp[i];
//        else
//            for (i = 0; i < m; ++i)
//                tmp1 += sx[i] * sx[i] * p[i] * p[i];
        sln = Math.sqrt(tmp1);
        if (sln > stepmx) {
          /*    newton step longer than maximum allowed */
            scl = stepmx / sln;
            for (i = 0; i < m; ++i) /* p * scl */
                clonedDp[i] *= scl;
            sln = stepmx;
        }
        for (i = 0, slp = 0.; i < m; ++i) /* g^T * p */
            slp += g[i] * clonedDp[i];
        rln = 0.;
//        if(!sx) /* no scaling */
        for (i = 0; i < m; ++i) {
            tmp1 = (Math.abs(x[i]) >= 1.) ? Math.abs(x[i]) : 1.;
            tmp2 = Math.abs(clonedDp[i]) / tmp1;
            if (rln < tmp2) rln = tmp2;
        }
//        else
//            for (i = 0; i < m; ++i) {
//                tmp1 = (FABS(x[i])>=LM_CNST(1.)/sx[i])? FABS(x[i]) : LM_CNST(1.)/sx[i];
//                tmp2 = FABS(p[i])/tmp1;
//                if(rln < tmp2) rln = tmp2;
//            }
        rmnlmb = steptl / rln;
        lambda = 1.0;
            /*  check if new iterate satisfactory.  generate new lambda if necessary. */
        int __LSITMAX = 150;
        for (j = __LSITMAX; j >= 0; --j) {
            for (i = 0; i < m; ++i)
                xpls[i] = x[i] + lambda * clonedDp[i];

      /* evaluate function at new point */
            stateHX = model.getModeledSignal(xpls);
//            (*func)(xpls, state.hx, m, state.n, state.adata); ++(*(state.nfev));
      /* ### state.hx=state.x-state.hx, tmp1=||state.hx|| */
//            #if 1
//            tmp1=LEVMAR_L2NRMXMY(state.hx, state.x, state.hx, state.n);
//            #else
            for (i = 0, tmp1 = 0.0; i < n; ++i) {
                stateHX[i] = tmp2 = stateX[i] - stateHX[i];
                tmp1 += tmp2 * tmp2;
            }
//            #endif
            fpls = 0.5 * tmp1;
            ffpls = tmp1;

            if (fpls <= f + slp * alpha * lambda) { /* solution found */
                iretcd = 0;
                if (lambda == 1. && sln > stepmx * .99)
                    mxtake = 1;
//                double[] hx = model.getModeledSignal(Dp);
                double[][] res = new double[5][];
                res[0] = xpls;
                res[1] = stateHX;
                double[] pDp_eL2A = {ffpls};
                res[2] = pDp_eL2A;
                res[3] = clonedDp;
                double[] iretcdA = {iretcd};
                res[4] = iretcdA;
                return res;
            }
                        /* else : solution not (yet) found */

      /* First find a point with a finite value */

            if (lambda < rmnlmb) {
              /* no satisfactory xpls found sufficiently distinct from x */

                iretcd = 1;
//                double[] hx = model.getModeledSignal(Dp);
                double[][] res = new double[5][];
                res[0] = xpls;
                res[1] = stateHX;
                double[] pDp_eL2A = {ffpls};
                res[2] = pDp_eL2A;
                res[3] = clonedDp;
                double[] iretcdA = {iretcd};
                res[4] = iretcdA;
                return res;
            } else { /*   calculate new lambda */

              /* modifications to cover non-finite values */
                if (!(fpls > Double.NEGATIVE_INFINITY) || !(fpls < Double.POSITIVE_INFINITY)) {
                    lambda *= 0.1;
                    firstback = 1;
                } else {
                    if (firstback == 1) { /*       first backtrack: quadratic fit */
                        tlmbda = -lambda * slp / ((fpls - f - slp) * 2.);
                        firstback = 0;
                    } else { /* all subsequent backtracks: cubic fit */
                        t1 = fpls - f - lambda * slp;
                        t2 = pfpls - f - plmbda * slp;
                        t3 = 1. / (lambda - plmbda);
                        a3 = 3. * t3 * (t1 / (lambda * lambda)
                                - t2 / (plmbda * plmbda));
                        b = t3 * (t2 * lambda / (plmbda * plmbda)
                                - t1 * plmbda / (lambda * lambda));
                        disc = b * b - a3 * slp;
                        if (disc > b * b)
                              /* only one positive critical point, must be minimum */
                            tlmbda = (-b + ((a3 < 0) ? -Math.sqrt(disc) : Math.sqrt(disc))) / a3;
                        else
                              /* both critical points positive, first is minimum */
                            tlmbda = (-b + ((a3 < 0) ? Math.sqrt(disc) : -Math.sqrt(disc))) / a3;

                        if (tlmbda > lambda * .5)
                            tlmbda = lambda * .5;
                    }
                    plmbda = lambda;
                    pfpls = fpls;
                    if (tlmbda < lambda * .1)
                        lambda *= .1;
                    else
                        lambda = tlmbda;
                }
            }
        }
    /* this point is reached when the iterations limit is exceeded */
        iretcd = 1; /* failed */


//        double[] p = new double[8];
//        double[] hx = model.getModeledSignal(Dp);
        double[][] res = new double[5][];
        res[0] = xpls;
        res[1] = stateHX;
        double[] pDp_eL2A = {ffpls};
        res[2] = pDp_eL2A;
        res[3] = clonedDp;
        double[] iretcdA = {iretcd};
        res[4] = iretcdA;
        return res;
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
            if (d < DELTA) {
                d = DELTA;
            }
            final double tmp = variables[j];
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
            final double v0_sq = variables[0] * variables[0];
            final double v1_sq = variables[1] * variables[1];
            return new double[]{v0_sq - 4 * variables[0] + 5, v1_sq};
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
