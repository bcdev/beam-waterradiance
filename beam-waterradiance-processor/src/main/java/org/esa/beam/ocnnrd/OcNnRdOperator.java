package org.esa.beam.ocnnrd;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.GeoCoding;
import org.esa.beam.framework.datamodel.GeoPos;
import org.esa.beam.framework.datamodel.PixelPos;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.annotations.Parameter;
import org.esa.beam.framework.gpf.annotations.SourceProduct;
import org.esa.beam.framework.gpf.pointop.PixelOperator;
import org.esa.beam.framework.gpf.pointop.ProductConfigurer;
import org.esa.beam.framework.gpf.pointop.Sample;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;
import org.esa.beam.framework.gpf.pointop.WritableSample;
import org.esa.beam.util.ResourceInstaller;
import org.esa.beam.util.StringUtils;
import org.esa.beam.util.SystemUtils;
import org.esa.beam.waterradiance.AtmosphericAuxdata;
import org.esa.beam.waterradiance.AuxdataProviderFactory;
import org.esa.beam.waterradiance.NO2Auxdata;
import org.esa.beam.waterradiance.SalinityTemperatureAuxdata;
import org.esa.beam.waterradiance.realoptimizers.LevMarNN;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

/**
 * An operator computing water IOPs starting from radiances.
 *
 * @author Tom Block
 */
@OperatorMetadata(alias = "OCNNRD", version = "1.0",
                  authors = "Tom Block, Tonio Fincke, Roland Doerffer",
                  description = "An operator computing water IOPs starting from radiances.")
public class OcNnRdOperator extends PixelOperator {

    private static int NUM_OUTPUTS;
//    private static final int NUM_TARGET_BANDS = NUM_OUTPUTS + 4;

    private final ThreadLocal<double[]> input = new ThreadLocal<double[]>() {
        @Override
        protected double[] initialValue() {
            return new double[43];
        }
    };
    private final ThreadLocal<double[]> output = new ThreadLocal<double[]>() {
        @Override
        protected double[] initialValue() {
            return new double[NUM_OUTPUTS];
        }
    };

    private double[] solarFluxes;

    private SalinityTemperatureAuxdata salinityTemperatureAuxdata = null;
    private AtmosphericAuxdata atmosphericAuxdata = null;
    private Date date = null;
    private ThreadLocal<LevMarNN> levMarNN;

    // solar_flux from bands
    // copy lat, lon, row_index to target
    private boolean csvMode = false;

    // -----------------------------------
    // ----- Configurable Parameters -----
    // -----------------------------------
    @SourceProduct
    private Product sourceProduct;

    @Parameter(defaultValue = "!l1_flags.INVALID && !l1_flags.BRIGHT && !l1_flags.LAND_OCEAN")
    private String maskExpression;

    @Parameter(defaultValue = "true", description = "Enables/disables the usage of the climatology")
    private boolean useClimatology;

    @Parameter(defaultValue = "15.0", description = "Use this value, if the climatology is disabled")
    private double temperature;

    @Parameter(defaultValue = "35.0", description = "Use this value, if the climatology is disabled")
    private double salinity;

    @Parameter(description = "Path to the atmospheric auxiliary data directory.Use either this or tomsomiStartProduct, " +
                             "tomsomiEndProduct, ncepStartProduct, and ncepEndProduct to use ozone auxiliary data.")
    private String atmosphericAuxDataPath;

    @SourceProduct(description = "A product which is used for derivation of ozone values. Use either this and tomsomiEndProduct," +
                                 "ncepStartProduct, and ncepEndProduct or atmosphericAuxdataPath to use ozone auxiliary data.",
                   optional = true)
    private Product tomsomiStartProduct;

    @SourceProduct(description = "A product which is used for derivation of ozone values. Use either this and " +
                                 "tomsomiStartProduct, ncepStartProduct, and ncepEndProduct or atmosphericAuxdataPath to use ozone auxiliary data.",
                   optional = true)
    private Product tomsomiEndProduct;

    @SourceProduct(description = "A product which is used for derivation of ozone values. Use either this and tomsomiStartProduct" +
                                 "tomsomiEndProduct, and ncepEndProduct or atmosphericAuxdataPath to use ozone auxiliary data.",
                   optional = true)
    private Product ncepStartProduct;

    @SourceProduct(description = "A product which is used for derivation of ozone values. Use either this and tomsomiStartProduct" +
                                 "tomsomiEndProduct, and ncepStartProduct or atmosphericAuxdataPath to use ozone auxiliary data.",
                   optional = true)
    private Product ncepEndProduct;

    private SensorConfig sensorConfig;
    private NO2Auxdata no2Auxdata;

    @Override
    protected void computePixel(int x, int y, Sample[] sourceSamples, WritableSample[] targetSamples) {
        final Sensor sensorType = sensorConfig.getSensor();
        if (sensorType == Sensor.MODIS || sensorType == Sensor.SEAWIFS || isValid(sourceSamples)) {
            final double[] input_local = input.get();
            sensorConfig.copyTiePointData(input_local, sourceSamples);
            copyAuxData(input_local, x, y);
            copyRadiances(input_local, sourceSamples, sensorConfig);
            copySolarFluxes(sourceSamples);

            final int detectorIndex = sensorConfig.getDetectorIndex(sourceSamples);
            final double[] output_local = output.get();
            final LevMarNN levMarNN_local = levMarNN.get();
            levMarNN_local.levmar_nn(detectorIndex, input_local, output_local);

            // @todo 2 tb/tb extract method and test tb 2013-05-13
//            for (int i = 0; i < output_local.length; i++) {
            for (int i = sensorConfig.getTargetSampleOffset(); i < targetSamples.length; i++) {
                targetSamples[i].set(output_local[i - sensorConfig.getTargetSampleOffset()]);
            }
            targetSamples[targetSamples.length - 4].set(input_local[8]);
            targetSamples[targetSamples.length - 3].set(input_local[9]);
            targetSamples[targetSamples.length - 2].set(input_local[4]);
            targetSamples[targetSamples.length - 1].set(input_local[5]);
        } else {
            setToInvalid(targetSamples);
        }
    }

    @Override
    protected void configureSourceSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        sensorConfig.configureSourceSamples(sampleConfigurer, csvMode);
    }

    @Override
    protected void configureTargetSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        Product targetProduct = getTargetProduct();
        String[] bandNames = targetProduct.getBandNames();
        for (int i = sensorConfig.getTargetSampleOffset(); i < bandNames.length; i++) {
            final String bandName = bandNames[i];
            sampleConfigurer.defineSample(i, bandName);
        }
    }

    @Override
    protected void configureTargetProduct(ProductConfigurer productConfigurer) {
        super.configureTargetProduct(productConfigurer);

        /* 0-11*/
        addSpectralBands(productConfigurer, "rl_tosa_%d", "sr^-1", "TOSA Reflectance at %d nm");
        /*12-23*/
        addSpectralBands(productConfigurer, "rl_path_%d", "dxd", "Water leaving radiance reflectance path at %d nm");
        /*24-35*/
        addSpectralBands(productConfigurer, "reflec_%d", "sr^-1", "Water leaving radiance reflectance at %d nm");
        /*36-47*/
        addSpectralBands(productConfigurer, "trans_down_%d", "dl", "Downwelling radiance transmittance at %d nm");
        /*48-59*/
        addSpectralBands(productConfigurer, "trans_up_%d", "dl", "Upwelling radiance transmittance at %d nm");
        /*   60*/
        addBand(productConfigurer, "aot_550", ProductData.TYPE_FLOAT32, "dl", "Aerosol Optical Thickness at 550 nm");
        /*   61*/
        addBand(productConfigurer, "ang_864_443", ProductData.TYPE_FLOAT32, "dl", "Aerosol Angstrom coefficient between 864 nm and 443 nm");
        /*   62*/
        addBand(productConfigurer, "a_pig", ProductData.TYPE_FLOAT32, "m^-1", "Pigment absorption coefficient at 443 nm");
        /*   63*/
        addBand(productConfigurer, "a_ys", ProductData.TYPE_FLOAT32, "m^-1", "Yellow substance absorption coefficient at 443 nm");
        /*   64*/
        addBand(productConfigurer, "a_part", ProductData.TYPE_FLOAT32, "m^-1", "todo - add description");
        /*   65*/
        addBand(productConfigurer, "b_part", ProductData.TYPE_FLOAT32, "m^-1", "todo - add description");
        /*   66*/
        addBand(productConfigurer, "b_wit", ProductData.TYPE_FLOAT32, "m^-1", "todo - add description");
        /*   67*/
        addBand(productConfigurer, "sum_sq", ProductData.TYPE_FLOAT32, "", "Square sums");
        /*   68*/
        addBand(productConfigurer, "num_iter", ProductData.TYPE_INT32, "", "Number of iterations in LM");

        addBand(productConfigurer, "temperature", ProductData.TYPE_INT32, "", "Temperature");
        addBand(productConfigurer, "salinity", ProductData.TYPE_INT32, "", "Salinity");

        addBand(productConfigurer, "atm_press_2", ProductData.TYPE_FLOAT32, "", "Surface Pressure");
        addBand(productConfigurer, "ozone_2", ProductData.TYPE_FLOAT32, "", "Ozone");

        // @todo 1 tb/** what to do in the general case? This is ENVSAT specific ... tb 2013-09-25
        // productConfigurer.copyBands(EnvisatConstants.MERIS_DETECTOR_INDEX_DS_NAME);
        // productConfigurer.copyBands(EnvisatConstants.MERIS_L1B_FLAGS_DS_NAME);

        if (csvMode) {
            //copy row_index and lat/lon from input
            productConfigurer.copyBands("row_index");
            productConfigurer.copyBands(EnvisatConstants.MERIS_LAT_DS_NAME);
            productConfigurer.copyBands(EnvisatConstants.MERIS_LON_DS_NAME);
        }

        String autoGrouping = String.format("%s:%s:%s:%s:%s", "rl_tosa", "rl_path", "reflec", "trans_down", "trans_up");
        final Product targetProduct = productConfigurer.getTargetProduct();
        targetProduct.setAutoGrouping(autoGrouping);
        if (csvMode) {
            targetProduct.setPreferredTileSize(targetProduct.getSceneRasterWidth(),
                                               targetProduct.getSceneRasterHeight());
        }
    }

    //@todo 4 tb/** make static and add test
    private void addBand(ProductConfigurer productConfigurer, String name, int type, String unit, String description) {
        Band band = productConfigurer.addBand(name, type);
        band.setDescription(description);
        band.setUnit(unit);
        band.setNoDataValue(Float.NaN);
    }

    //@todo 4 tb/** make static and add test
    private void addSpectralBands(ProductConfigurer productConfigurer, String bandNameFormat, String unit, String descriptionFormat) {
        final int[] indices = sensorConfig.getSpectralOutputBandIndices();
        final float[] wavelengths = sensorConfig.getSpectralOutputWavelengths();
        for (int i = 0; i < sensorConfig.getNumSpectralOutputBands(); i++) {
            final Band band = productConfigurer.addBand(String.format(bandNameFormat, indices[i]), ProductData.TYPE_FLOAT32);
            band.setSpectralBandIndex(i);
            band.setSpectralWavelength(wavelengths[i]);
            band.setDescription(String.format(descriptionFormat, (int) wavelengths[i]));
            band.setUnit(unit);
            band.setNoDataValue(Float.NaN);
        }
    }

    @Override
    protected void prepareInputs() throws OperatorException {
        super.prepareInputs();

        final File AUXDATA_DIR = new File(SystemUtils.getApplicationDataDir(), "beam-waterradiance-processor/auxdata");
        URL sourceUrl = ResourceInstaller.getSourceUrl(OcNnRdOperator.class);
        ResourceInstaller installer = new ResourceInstaller(sourceUrl, "auxdata/", AUXDATA_DIR);
        try {
            installer.install(".*", ProgressMonitor.NULL);
        } catch (IOException e) {
            throw new RuntimeException("Unable to install auxdata of the beam-waterradiance-processor module");
        }

        sensorConfig = SensorConfigFactory.fromTypeString(sourceProduct.getProductType());
        sensorConfig.init(sourceProduct);
        NUM_OUTPUTS = 13 + 5 * sensorConfig.getNumSpectralInputBands();
        if (sensorConfig.getSensor() == Sensor.MERIS) {
            if (isCsvMode(sourceProduct)) {
                csvMode = true;
                maskExpression = "true";
            } else {
                solarFluxes = sensorConfig.getSolarFluxes(sourceProduct);
            }
            sourceProduct.addBand("_mask_", maskExpression);
        } else if (sensorConfig.getSensor() == Sensor.MODIS || sensorConfig.getSensor() == Sensor.SEAWIFS) {
            solarFluxes = sensorConfig.getSolarFluxes(sourceProduct);
        }

        final ProductData.UTC startTime = sourceProduct.getStartTime();
        if (startTime != null && useClimatology) {
            date = startTime.getAsDate();
            initAuxdataDataProviders();
        }

        levMarNN = new ThreadLocal<LevMarNN>() {
            @Override
            protected LevMarNN initialValue() {
                try {
                    return new LevMarNN(sensorConfig);
                } catch (IOException e) {
                    // @todo 3 tb/tb improve error handling here ... tb 2013-05-20
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    @Override
    public void dispose() {
        if (salinityTemperatureAuxdata != null) {
            salinityTemperatureAuxdata.dispose();
        }
        if (atmosphericAuxdata != null) {
            atmosphericAuxdata.dispose();
        }
        super.dispose();
    }

    // package access for testing only tb 2013-05-13
    static boolean isValid(Sample[] sourceSamples) {
        return sourceSamples[Constants.SRC_MASK].getBoolean();
    }

    // package access for testing only tb 2013-05-13
    static void setToInvalid(WritableSample[] targetSamples) {
        for (WritableSample targetSample : targetSamples) {
            targetSample.set(Double.NaN);
        }
    }

    // package access for testing only tb 2013-05-13
    static void copyRadiances(double[] inputs, Sample[] sourceSamples, SensorConfig sensorConfig) {
        for (int i = 0; i < sensorConfig.getNumSpectralInputBands(); i++) {
            inputs[10 + i] = sourceSamples[Constants.SRC_RAD_OFFSET + i].getDouble();
        }
    }

    // package access for testing only tb 2013-05-13
    static boolean isCsvMode(Product sourceProduct) {
        return sourceProduct.containsBand("solar_flux_1");
    }

    // package access for testing only tb 2013-05-13
    static void copySolarFluxes(double[] inputs, Sample[] sourceSamples) {
        for (int i = 0; i < EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES.length; i++) {
            inputs[25 + i] = sourceSamples[Constants.SRC_SOL_FLUX_OFFSET + i].getDouble();
        }
    }

    private void initAuxdataDataProviders() {
        try {
            salinityTemperatureAuxdata = AuxdataProviderFactory.createSalinityTemperatureDataProvider();
        } catch (IOException e) {
            getLogger().severe("Unable to create provider for salinity and temperature auxiliary data.");
            getLogger().severe(e.getMessage());
            salinityTemperatureAuxdata = null;
        }
        if (StringUtils.isNotNullAndNotEmpty(atmosphericAuxDataPath)) {
            try {
                atmosphericAuxdata = AuxdataProviderFactory.createAtmosphericDataProvider(atmosphericAuxDataPath);
            } catch (IOException e) {
                getLogger().severe("Unable to create provider for atmospheric auxiliary data.");
                getLogger().severe(e.getMessage());
                atmosphericAuxdata = null;
            }
            try {
                no2Auxdata = AuxdataProviderFactory.createNO2AuxdataProvider(atmosphericAuxDataPath + "//no2");
            } catch (IOException e) {
                getLogger().severe("Unable to create provider for no2 auxiliary data.");
                getLogger().severe(e.getMessage());
                no2Auxdata = null;
            }
        } else {
            try {
                atmosphericAuxdata = AuxdataProviderFactory.createAtmosphericDataProvider(tomsomiStartProduct,
                                                                                          tomsomiEndProduct,
                                                                                          ncepStartProduct,
                                                                                          ncepEndProduct);
            } catch (IOException e) {
                getLogger().severe("Unable to create provider for atmospheric auxiliary data.");
                getLogger().severe(e.getMessage());
                atmosphericAuxdata = null;
            }
        }

    }

    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private void copyAuxData(double[] input_local, int x, int y) {
        if (salinityTemperatureAuxdata != null) {
            try {
                final GeoCoding geoCoding = sourceProduct.getGeoCoding();
                final GeoPos geoPos = geoCoding.getGeoPos(new PixelPos(x + 0.5f, y + 0.5f), null);
                synchronized (this) {
                    input_local[8] = salinityTemperatureAuxdata.getTemperature(date, geoPos.getLat(), geoPos.getLon());
                    input_local[9] = salinityTemperatureAuxdata.getSalinity(date, geoPos.getLat(), geoPos.getLon());
                }
            } catch (Exception e) {
                throw new OperatorException(e);
            }
        } else {
            input_local[8] = temperature;
            input_local[9] = salinity;
        }
        if (atmosphericAuxdata != null) {
            try {
                final GeoCoding geoCoding = sourceProduct.getGeoCoding();
                GeoPos geoPos = geoCoding.getGeoPos(new PixelPos(x + 0.5f, y + 0.5f), null);
                synchronized (this) {
                    input_local[4] = atmosphericAuxdata.getSurfacePressure(date, geoPos.getLat(), geoPos.getLon());
                    input_local[5] = atmosphericAuxdata.getOzone(date, geoPos.getLat(), geoPos.getLon());
                }
            } catch (Exception e) {
                throw new OperatorException(e);
            }
        } else {
            input_local[4] = sensorConfig.getSurfacePressure();
            input_local[5] = sensorConfig.getOzone();
        }
        if (no2Auxdata != null) {
            try {
                final GeoCoding geoCoding = sourceProduct.getGeoCoding();
                GeoPos geoPos = geoCoding.getGeoPos(new PixelPos(x + 0.5f, y + 0.5f), null);
                synchronized (this) {
                    input_local[40] = no2Auxdata.getNO2Tropo(date, geoPos.getLat(), geoPos.getLon());
                    input_local[41] = no2Auxdata.getNO2Strato(date, geoPos.getLat(), geoPos.getLon());
                    input_local[42] = no2Auxdata.getNO2Frac(geoPos.getLat(), geoPos.getLon());
                }
            } catch (Exception e) {
                throw new OperatorException(e);
            }
        } else {
            input_local[40] = 0.0;
            input_local[41] = 0.0;
            input_local[42] = 0.0;
        }
    }

    private void copySolarFluxes(Sample[] sourceSamples) {
        final double[] input_local = input.get();
        if (csvMode) {
            copySolarFluxes(input_local, sourceSamples);
        } else {
            sensorConfig.copySolarFluxes(input_local, solarFluxes);
        }
    }

    public static class Spi extends OperatorSpi {
        public Spi() {
            super(OcNnRdOperator.class);
        }
    }
}
