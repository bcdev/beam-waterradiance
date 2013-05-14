package org.esa.beam.waterradiance.realoptimizers;


class AlphaTab {

    private static final double ALPHA_ANF = -10.0;
    private static final int N_ALPHA = 100000;

    private final double rec_delta_alpha;
    private final double[] alpha_tab;

    AlphaTab() {
        alpha_tab =  new double[N_ALPHA];
        double sum, delta;
        int i;

        delta = -2. * ALPHA_ANF / (N_ALPHA - 1);
        sum = ALPHA_ANF + delta / 2.;
        for (i = 0; i < N_ALPHA; i++) {
            alpha_tab[i] = calpha(sum);
            sum += delta;
        }
        rec_delta_alpha = 1. / delta;
    }

    double getRecDelta() {
        return rec_delta_alpha;
    }

    double get(double x) {
        int ind = (int) ((x - ALPHA_ANF) * rec_delta_alpha);
        if (ind < 0)
            ind = 0;
        if (ind >= N_ALPHA)
            ind = N_ALPHA - 1;
        return alpha_tab[ind];
    }

    private double calpha(double x) {
        return (1. / (1. + Math.exp(-x)));
    }
}
