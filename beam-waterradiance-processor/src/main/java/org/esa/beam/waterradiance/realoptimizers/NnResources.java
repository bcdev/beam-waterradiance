package org.esa.beam.waterradiance.realoptimizers;


import org.esa.beam.util.SystemUtils;

public class NnResources {

    private static final String AUXDATA_PATH = "auxdata/";
    private static final String SMILE_CORR_PATH = "auxdata/smile/";
    private static final String AC_FWD_PATH = "auxdata/oc_cci_20131206/ac_forward_all/";

    public String getNormNetPath() {
        return getResourcePath(AUXDATA_PATH + "27x41x27_23.3.net");
    }

    public String getNetWaterPath() {
        return getResourcePath(AUXDATA_PATH + "for_water_logrw29_20121206/17x97x47_292.6.net");
    }

    public String getCentralWavelengthFrPath() {
        return getResourcePath(SMILE_CORR_PATH + "central_wavelen_fr.txt");
    }

    public String getCentralWavelengthRrPath() {
        return getResourcePath(SMILE_CORR_PATH + "central_wavelen_rr.txt");
    }

    public String getSunSpectralFluxFrPath() {
        return getResourcePath(SMILE_CORR_PATH + "sun_spectral_flux_fr.txt");
    }

    public String getSunSpectralFluxRrPath() {
        return getResourcePath(SMILE_CORR_PATH + "sun_spectral_flux_rr.txt");
    }

    public String getNominalLamSunPath() {
        return getResourcePath(SMILE_CORR_PATH + "nominal_lam_sun.txt");
    }

    public String getAcForwardNetPath(String relativePathToNet) {
        return getResourcePath(AC_FWD_PATH + relativePathToNet);
    }

    private String getResourcePath(String name) {
        String auxpath = "/.beam/beam-waterradiance-processor/";
        StringBuilder builder = new StringBuilder();
        builder.append(SystemUtils.getUserHomeDir());
        builder.append(auxpath);
        builder.append(name);
        return builder.toString();
    }
}
