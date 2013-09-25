package org.esa.beam.ocnnrd;

import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;

class MerisSensorConfig implements SensorConfig {

    @Override
    public int getNumSpectralBands() {
        return EnvisatConstants.MERIS_L1B_NUM_SPECTRAL_BANDS;
    }

    @Override
    public Sensor getSensor() {
        return Sensor.MERIS;
    }

    @Override
    public void configureSourceSamples(SampleConfigurer sampleConfigurer, boolean csvMode) {
        sampleConfigurer.defineSample(Constants.SRC_SZA, EnvisatConstants.MERIS_SUN_ZENITH_DS_NAME);
        sampleConfigurer.defineSample(Constants.SRC_SAA, EnvisatConstants.MERIS_SUN_AZIMUTH_DS_NAME);
        sampleConfigurer.defineSample(Constants.SRC_VZA, EnvisatConstants.MERIS_VIEW_ZENITH_DS_NAME);
        sampleConfigurer.defineSample(Constants.SRC_VAA, EnvisatConstants.MERIS_VIEW_AZIMUTH_DS_NAME);
        sampleConfigurer.defineSample(Constants.SRC_PRESS, "atm_press");
        sampleConfigurer.defineSample(Constants.SRC_OZ, "ozone");
        sampleConfigurer.defineSample(Constants.SRC_MWIND, "merid_wind");
        sampleConfigurer.defineSample(Constants.SRC_ZWIND, "zonal_wind");

        for (int i = 0; i < EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES.length; i++) {
            sampleConfigurer.defineSample(Constants.SRC_RAD_OFFSET + i, EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES[i]);
        }

        sampleConfigurer.defineSample(Constants.SRC_DETECTOR, EnvisatConstants.MERIS_DETECTOR_INDEX_DS_NAME);
        sampleConfigurer.defineSample(Constants.SRC_MASK, "_mask_");

        if (csvMode) {
            for (int i = 0; i < EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES.length; i++) {
                sampleConfigurer.defineSample(Constants.SRC_SOL_FLUX_OFFSET + i, "solar_flux_" + (i + 1));
            }
            sampleConfigurer.defineSample(Constants.SRC_LAT, EnvisatConstants.MERIS_LAT_DS_NAME);
            sampleConfigurer.defineSample(Constants.SRC_LON, EnvisatConstants.MERIS_LON_DS_NAME);
        }
    }
}
