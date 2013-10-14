package org.esa.beam.ocnnrd;

import org.esa.beam.framework.datamodel.MetadataAttribute;
import org.esa.beam.framework.datamodel.MetadataElement;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.gpf.pointop.Sample;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;

class ModisSensorConfig implements SensorConfig {

    private final static String MODIS_L1B_RADIANCE_1_BAND_NAME = "EV_1KM_RefSB.8";
    private final static String MODIS_L1B_RADIANCE_2_BAND_NAME = "EV_1KM_RefSB.9";
    private final static String MODIS_L1B_RADIANCE_3_BAND_NAME = "EV_1KM_RefSB.10";
    private final static String MODIS_L1B_RADIANCE_4_BAND_NAME = "EV_1KM_RefSB.11";
    private final static String MODIS_L1B_RADIANCE_5_BAND_NAME = "EV_1KM_RefSB.12";
    private final static String MODIS_L1B_RADIANCE_6_BAND_NAME = "EV_1KM_RefSB.13lo";
    private final static String MODIS_L1B_RADIANCE_7_BAND_NAME = "EV_1KM_RefSB.14lo";
    private final static String MODIS_L1B_RADIANCE_8_BAND_NAME = "EV_1KM_RefSB.15";
    private final static String MODIS_L1B_RADIANCE_9_BAND_NAME = "EV_1KM_RefSB.16";

    private final static String[] MODIS_L1B_SPECTRAL_BAND_NAMES = {
            MODIS_L1B_RADIANCE_1_BAND_NAME,
            MODIS_L1B_RADIANCE_2_BAND_NAME,
            MODIS_L1B_RADIANCE_3_BAND_NAME,
            MODIS_L1B_RADIANCE_4_BAND_NAME,
            MODIS_L1B_RADIANCE_5_BAND_NAME,
            MODIS_L1B_RADIANCE_6_BAND_NAME,
            MODIS_L1B_RADIANCE_7_BAND_NAME,
            MODIS_L1B_RADIANCE_8_BAND_NAME,
            MODIS_L1B_RADIANCE_9_BAND_NAME,
    };
    private final static int MODIS_L1B_NUM_SPECTRAL_BANDS = MODIS_L1B_SPECTRAL_BAND_NAMES.length;
    private final static double surfacePressureDefaultValue = 1019.0;
    private final static double ozoneDefaultValue = 330.0;
    private final static double[] defaultSolarFluxes = {556.3234802246094, 606.7602844238281, 629.3416137695312,
            600.0957214355469, 602.5105712890625, 492.57408142089844, 479.41172790527344, 412.11121520996096,
            309.7836883544922};
    private double[] solarFluxes;
    private double earthSunDistance;
    private final String globalMetadataName = "GLOBAL_METADATA";
    private final String alternativeGlobalMetadataName = "Global_Attributes";
    private final String solarFluxesName = "Solar_Irradiance_on_RSB_Detectors_over_pi";
    private final static String[] earthSunDistanceNames = {"Earth-Sun_Distance", "Earth-Sun Distance"};
    private MetadataAttribute solarFluxesAttribute;

    @Override
    public int getNumSpectralBands() {
        return MODIS_L1B_NUM_SPECTRAL_BANDS;
    }

    @Override
    public String[] getSpectralBandNames() {
        return MODIS_L1B_SPECTRAL_BAND_NAMES;
    }

    @Override
    public Sensor getSensor() {
        return Sensor.MODIS;
    }

    @Override
    public void configureSourceSamples(SampleConfigurer sampleConfigurer, boolean csvMode) {
        sampleConfigurer.defineSample(Constants.SRC_SZA, "SolarZenith");
        sampleConfigurer.defineSample(Constants.SRC_SAA, "SolarAzimuth");
        sampleConfigurer.defineSample(Constants.SRC_VZA, "SensorZenith");
        sampleConfigurer.defineSample(Constants.SRC_VAA, "SensorAzimuth");
        for (int i = 0; i < MODIS_L1B_NUM_SPECTRAL_BANDS; i++) {
            sampleConfigurer.defineSample(Constants.SRC_RAD_OFFSET + i, MODIS_L1B_SPECTRAL_BAND_NAMES[i]);
        }
    }

    @Override
    public void copyTiePointData(double[] inputs, Sample[] sourceSamples) {
        inputs[0] = sourceSamples[Constants.SRC_SZA].getDouble();
        inputs[1] = sourceSamples[Constants.SRC_SAA].getDouble();
        inputs[2] = sourceSamples[Constants.SRC_VZA].getDouble();
        inputs[3] = sourceSamples[Constants.SRC_VAA].getDouble();
        inputs[4] = surfacePressureDefaultValue;
        inputs[5] = ozoneDefaultValue;
    }

    @Override
    public double[] getSolarFluxes(Product sourceProduct) {
        return solarFluxes;
    }

    @Override
    public double[] copySolarFluxes(double[] input, double[] solarFluxes) {
        System.arraycopy(solarFluxes, 0, input, Constants.SRC_SOL_FLUX_OFFSET, MODIS_L1B_NUM_SPECTRAL_BANDS);
        return input;
    }

    @Override
    public double getSurfacePressure() {
        return surfacePressureDefaultValue;
    }

    @Override
    public double getOzone() {
        return ozoneDefaultValue;
    }

    @Override
    public double getEarthSunDistance() {
        return earthSunDistance;
    }

    @Override
    public void init(Product sourceProduct) {
        //todo look up real solar irradiance values as soon as governmental shutdown is over
        earthSunDistance = 1;
        solarFluxes = defaultSolarFluxes;
        MetadataElement globalMetadataElement = sourceProduct.getMetadataRoot().getElement(globalMetadataName);
        if(globalMetadataElement != null) {
            solarFluxesAttribute = globalMetadataElement.getAttribute(solarFluxesName);
            if(solarFluxesAttribute != null) {
                final ProductData productData =
                        solarFluxesAttribute.getData();
                solarFluxes = new double[MODIS_L1B_NUM_SPECTRAL_BANDS];
                int[] startPositionInProductData = new int[]{180, 190, 200, 210, 220, 230, 250, 270, 280};
                for (int i = 0; i < MODIS_L1B_NUM_SPECTRAL_BANDS; i++) {
                    for (int j = 0; j < 10; j++) {
                        solarFluxes[i] += productData.getElemDoubleAt(startPositionInProductData[i] + j);
                    }
                    solarFluxes[i] /= 10;
//            solarFluxes[i] *= Math.PI;
                }
            }
        } else {
            globalMetadataElement = sourceProduct.getMetadataRoot().getElement(alternativeGlobalMetadataName);
        }
        if(globalMetadataElement != null) {
            for(int i = 0; i < earthSunDistanceNames.length; i++) {
                final MetadataAttribute earthSunDistanceAttribute =
                        globalMetadataElement.getAttribute(earthSunDistanceNames[i]);
                if(earthSunDistanceAttribute != null) {
                    earthSunDistance = earthSunDistanceAttribute.getData().getElemDouble();
                    break;
                }
            }
        }
    }
}
