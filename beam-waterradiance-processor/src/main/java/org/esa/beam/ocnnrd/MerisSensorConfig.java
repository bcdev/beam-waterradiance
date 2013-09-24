package org.esa.beam.ocnnrd;

import org.esa.beam.dataio.envisat.EnvisatConstants;

class MerisSensorConfig implements SensorConfig {

    @Override
    public int getNumSpectralBands() {
        return EnvisatConstants.MERIS_L1B_NUM_SPECTRAL_BANDS;
    }
}
