package org.esa.beam.ocnnrd;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.esa.beam.framework.datamodel.*;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.annotations.Parameter;
import org.esa.beam.framework.gpf.annotations.SourceProduct;
import org.esa.beam.framework.gpf.pointop.*;
import org.esa.beam.util.ResourceInstaller;
import org.esa.beam.util.SystemUtils;
import org.esa.beam.waterradiance.AuxdataProvider;
import org.esa.beam.waterradiance.AuxdataProviderFactory;
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
@OperatorMetadata(alias = "Meris.OCNNRD", version = "1.0",
        authors = "Tom Block, Tonio Finke, Roland Doerffer",
        description = "An operator computing water IOPs starting from radiances.")
public class OcNnRdOperator extends PixelOperator {

    private static final int SRC_SZA = 0;
    private static final int SRC_SAA = 1;
    private static final int SRC_VZA = 2;
    private static final int SRC_VAA = 3;
    private static final int SRC_PRESS = 4;
    private static final int SRC_OZ = 5;
    private static final int SRC_MWIND = 6;
    private static final int SRC_ZWIND = 7;
    private static final int SRC_RAD_OFFSET = 8;
    private static final int SRC_DETECTOR = 23;
    private static final int SRC_MASK = 24;
    private static final int SRC_SOL_FLUX_OFFSET = 25;
    private static final int SRC_LAT = 40;
    private static final int SRC_LON = 41;

    private static final int NUM_OUTPUTS = 69;
    private static final int NUM_TARGET_BANDS = NUM_OUTPUTS + 2;

    private static final int[] SPECTRAL_INDEXES = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13};
    private static final int[] SPECTRAL_WAVELENGTHS = new int[]{412, 442, 449, 510, 560, 620, 665, 681, 708, 753, 778, 865};

    private final double[] input = new double[40];
    private final double[] output = new double[NUM_OUTPUTS];
    private final double[] debug_dat = new double[1000];

    private double[] solarFluxes;

    private AuxdataProvider auxdataProvider = null;
    private Date date = null;
    private LevMarNN levMarNN;

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

    // -----------------------------------
    // ----- Configurable Parameters -----
    // -----------------------------------

    static {
        installAuxdata();
    }


    @Override
    protected void computePixel(int x, int y, Sample[] sourceSamples, WritableSample[] targetSamples) {
        if (isValid(sourceSamples)) {
            copyTiePointData(input, sourceSamples);
            copyAuxData(x, y);
            copyRadiances(input, sourceSamples);
            copySolarFluxes(sourceSamples);

            final int detectorIndex = getDetectorIndex(sourceSamples);
            final int result = levMarNN.levmar_nn(detectorIndex, input, input.length, output, output.length, debug_dat);

            // @todo 2 tb/tb extract method and test tb 2013-05-13
            for (int i = 0; i < output.length; i++) {
                targetSamples[i].set(output[i]);
            }
            targetSamples[output.length].set(input[8]);
            targetSamples[output.length + 1].set(input[9]);

        } else {
            setToInvalid(targetSamples, NUM_TARGET_BANDS);
        }
    }


    @Override
    protected void configureSourceSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        sampleConfigurer.defineSample(SRC_SZA, EnvisatConstants.MERIS_SUN_ZENITH_DS_NAME);
        sampleConfigurer.defineSample(SRC_SAA, EnvisatConstants.MERIS_SUN_AZIMUTH_DS_NAME);
        sampleConfigurer.defineSample(SRC_VZA, EnvisatConstants.MERIS_VIEW_ZENITH_DS_NAME);
        sampleConfigurer.defineSample(SRC_VAA, EnvisatConstants.MERIS_VIEW_AZIMUTH_DS_NAME);
        sampleConfigurer.defineSample(SRC_PRESS, "atm_press");
        sampleConfigurer.defineSample(SRC_OZ, "ozone");
        sampleConfigurer.defineSample(SRC_MWIND, "merid_wind");
        sampleConfigurer.defineSample(SRC_ZWIND, "zonal_wind");

        for (int i = 0; i < EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES.length; i++) {
            sampleConfigurer.defineSample(SRC_RAD_OFFSET + i, EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES[i]);
        }
        sampleConfigurer.defineSample(SRC_DETECTOR, EnvisatConstants.MERIS_DETECTOR_INDEX_DS_NAME);
        sampleConfigurer.defineSample(SRC_MASK, "_mask_");
        if (csvMode) {
            for (int i = 0; i < EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES.length; i++) {
                sampleConfigurer.defineSample(SRC_SOL_FLUX_OFFSET + i, "solar_flux_" + (i + 1));
            }
            sampleConfigurer.defineSample(SRC_LAT, EnvisatConstants.MERIS_LAT_DS_NAME);
            sampleConfigurer.defineSample(SRC_LON, EnvisatConstants.MERIS_LON_DS_NAME);
        }
    }

    @Override
    protected void configureTargetSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        Product targetProduct = getTargetProduct();
        String[] bandNames = targetProduct.getBandNames();
        for (int i = 0; i < output.length + 2; i++) {
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

        productConfigurer.copyBands(EnvisatConstants.MERIS_DETECTOR_INDEX_DS_NAME);
        productConfigurer.copyBands(EnvisatConstants.MERIS_L1B_FLAGS_DS_NAME);

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

    private void addBand(ProductConfigurer productConfigurer, String name, int type, String unit, String description) {
        Band band = productConfigurer.addBand(name, type);
        band.setDescription(description);
        band.setUnit(unit);
        band.setNoDataValue(Float.NaN);
    }

    private void addSpectralBands(ProductConfigurer productConfigurer,
                                  String bandNameFormat, String unit, String descriptionFormat) {
        for (int i = 0; i < SPECTRAL_INDEXES.length; i++) {
            int bandIndex = SPECTRAL_INDEXES[i];
            Band band = productConfigurer.addBand(String.format(bandNameFormat, bandIndex), ProductData.TYPE_FLOAT32);
            int wavelength = SPECTRAL_WAVELENGTHS[i];
            band.setSpectralBandIndex(i);
            band.setSpectralWavelength(wavelength);
            band.setDescription(String.format(descriptionFormat, wavelength));
            band.setUnit(unit);
            band.setNoDataValue(Float.NaN);
        }
    }

    @Override
    protected void prepareInputs() throws OperatorException {
        super.prepareInputs();

        if (isCsvMode(sourceProduct)) {
            csvMode = true;
            maskExpression = "true";
        } else {
            solarFluxes = getSolarFluxes(EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES, sourceProduct);
        }
        sourceProduct.addBand("_mask_", maskExpression);

        final ProductData.UTC startTime = sourceProduct.getStartTime();
        if (startTime != null && useClimatology) {
            date = startTime.getAsDate();
            auxdataProvider = createAuxdataDataProvider();
        }

        levMarNN = new LevMarNN();
    }

    // package access for testing only tb 2013-05-13
    static boolean isValid(Sample[] sourceSamples) {
        return sourceSamples[SRC_MASK].getBoolean();
    }

    // package access for testing only tb 2013-05-13
    static void setToInvalid(WritableSample[] targetSamples, int numTargetBands) {
        for (int i = 0; i < numTargetBands; i++) {
            targetSamples[i].set(Double.NaN);
        }
    }

    // package access for testing only tb 2013-05-13
    static void copyTiePointData(double[] inputs, Sample[] sourceSamples) {
        inputs[0] = sourceSamples[SRC_SZA].getDouble();
        inputs[1] = sourceSamples[SRC_SAA].getDouble();
        inputs[2] = sourceSamples[SRC_VZA].getDouble();
        inputs[3] = sourceSamples[SRC_VAA].getDouble();
        inputs[4] = sourceSamples[SRC_PRESS].getDouble();
        inputs[5] = sourceSamples[SRC_OZ].getDouble();
        inputs[6] = sourceSamples[SRC_MWIND].getDouble();
        inputs[7] = sourceSamples[SRC_ZWIND].getDouble();
    }

    // package access for testing only tb 2013-05-13
    static void copyRadiances(double[] inputs, Sample[] sourceSamples) {
        for (int i = 0; i < EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES.length; i++) {
            inputs[10 + i] = sourceSamples[SRC_RAD_OFFSET + i].getDouble();
        }
    }

    // package access for testing only tb 2013-05-13
    static double[] getSolarFluxes(String[] radBandNames, Product sourceProduct) {
        double[] solarFluxes = new double[radBandNames.length];
        for (int i = 0; i < radBandNames.length; i++) {
            solarFluxes[i] = sourceProduct.getBand(radBandNames[i]).getSolarFlux();
        }
        return solarFluxes;
    }

    // package access for testing only tb 2013-05-13
    static boolean isCsvMode(Product sourceProduct) {
        return sourceProduct.containsBand("solar_flux_1");
    }

    // package access for testing only tb 2013-05-13
    static void copySolarFluxes(double[] inputs, Sample[] sourceSamples) {
        for (int i = 0; i < EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES.length; i++) {
            inputs[25 + i] = sourceSamples[SRC_SOL_FLUX_OFFSET + i].getDouble();
        }
    }

    // package access for testing only tb 2013-05-13
    static int getDetectorIndex(Sample[] sourceSamples) {
        return sourceSamples[SRC_DETECTOR].getInt();
    }

    private static void installAuxdata() {
        // @ todo 3 tb/** move auxdata access classes to separate package? tb 2013-05-13
        final File AUXDATA_DIR = new File(SystemUtils.getApplicationDataDir(), "beam-waterradiance-processor/auxdata");
        final URL sourceUrl = ResourceInstaller.getSourceUrl(OcNnRdOperator.class);
        final ResourceInstaller installer = new ResourceInstaller(sourceUrl, "auxdata/", AUXDATA_DIR);
        try {
            installer.install(".*", ProgressMonitor.NULL);
        } catch (IOException e) {
            throw new RuntimeException("Unable to install auxdata of the Meris.OCNNRD module");
        }
    }

    private static AuxdataProvider createAuxdataDataProvider() {
        try {
            return AuxdataProviderFactory.createDataProvider();
        } catch (IOException e) {
            throw new OperatorException("Unable to create provider for auxiliary data.", e);
        }
    }

    private void copyAuxData(int x, int y) {
        if (auxdataProvider != null) {
            try {
                GeoCoding geoCoding = sourceProduct.getGeoCoding();
                GeoPos geoPos = geoCoding.getGeoPos(new PixelPos(x + 0.5f, y + 0.5f), null);
                input[8] = auxdataProvider.getTemperature(date, geoPos.getLat(), geoPos.getLon());
                input[9] = auxdataProvider.getSalinity(date, geoPos.getLat(), geoPos.getLon());
            } catch (Exception e) {
                throw new OperatorException(e);
            }
        } else {
            input[8] = temperature;
            input[9] = salinity;
        }
    }

    private void copySolarFluxes(Sample[] sourceSamples) {
        if (csvMode) {
            copySolarFluxes(input, sourceSamples);
        } else {
            System.arraycopy(solarFluxes, 0, input, 25, 15);
        }
    }
}
