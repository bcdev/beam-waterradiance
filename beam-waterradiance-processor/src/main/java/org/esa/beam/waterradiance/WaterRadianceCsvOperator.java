package org.esa.beam.waterradiance;


import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.runtime.internal.Platform;
import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.annotations.Parameter;
import org.esa.beam.framework.gpf.annotations.SourceProduct;
import org.esa.beam.framework.gpf.pointop.*;
import org.esa.beam.util.ResourceInstaller;
import org.esa.beam.util.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * An operator computing water IOPs starting from radiances.
 * Modified in order to use CSV input provided by H.Krasemann
 * // todo: lots of duplication from WaterRadianceOperator. discuss later how to handle this special case
 *
 * @author Olaf Danne
 */
@OperatorMetadata(alias = "Meris.WaterRadianceCsv", version = "1.0",
        authors = "Olaf Danne, Roland Doerffer, Norman Fomferra, Marco Zühlke",
        description = "An operator computing water IOPs starting from radiances.")
public class WaterRadianceCsvOperator extends PixelOperator {

    private static final int[] SPECTRAL_INDEXES = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13};
    private static final int[] SPECTRAL_WAVELENGTHS = new int[]{
            412, 442, 449, 510, 560, 620, 665, 681, 708, 753, 778, 865
    };


    private static final File AUXDATA_DIR = new File(SystemUtils.getApplicationDataDir(), "beam-waterradiance-processor/auxdata");

    static {
        URL sourceUrl = ResourceInstaller.getSourceUrl(WaterRadianceCsvOperator.class);
        ResourceInstaller installer = new ResourceInstaller(sourceUrl, "auxdata/", AUXDATA_DIR);
        try {
            installer.install(".*", ProgressMonitor.NULL);
        } catch (IOException e) {
            throw new RuntimeException("Unable to install auxdata of the beam-waterradiance-processor module");
        }
        Platform currentPlatform = Platform.getCurrentPlatform();
        Platform.ID id = currentPlatform.getId();
        int numBits = currentPlatform.getBitCount();
        String libDir = String.format("lib/%s%d/", id, numBits);
        String absolutePath = new File(AUXDATA_DIR, libDir).getAbsolutePath();
        System.out.println("absolutePath = " + absolutePath);
        System.setProperty("jna.library.path", absolutePath);
    }


    @SourceProduct
    private Product sourceProduct;

    //    @Parameter(defaultValue = "!l1_flags.INVALID && !l1_flags.BRIGHT && !l1_flags.LAND_OCEAN")
    @Parameter(defaultValue = "true")
    private String maskExpression;

    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private double[] solarFluxes;
    private AuxdataProvider auxdataProvider;

    private final LevMarNnLib lib = LevMarNnLib.INSTANCE;

    private final double[] input = new double[40];
    private final double[] output = new double[69];
    private final double[] debug_dat = new double[1000];

    @Override
    protected synchronized void computePixel(int x, int y, Sample[] sourceSamples, WritableSample[] targetSamples) {

        int detectorIndex = sourceSamples[1].getInt();

        input[0] = sourceSamples[4].getDouble(); // SZA
        input[1] = sourceSamples[7].getDouble(); // SAA
        input[2] = sourceSamples[5].getDouble(); // VZA
        input[3] = sourceSamples[6].getDouble(); // VAA
        input[4] = sourceSamples[11].getDouble(); // atm_press
        input[5] = sourceSamples[12].getDouble(); // ozone
        input[6] = sourceSamples[10].getDouble(); // ozone
        input[7] = sourceSamples[9].getDouble(); // ozone

        try {
            input[8] = 20.0; // auxdataProvider.getTemperature(date, geoPos.getLat(), geoPos.getLon());
            input[9] = 12.0; // auxdataProvider.getSalinity(date, geoPos.getLat(), geoPos.getLon());
        } catch (Exception e) {
            throw new OperatorException(e);
        }
        for (int i = 14; i < 29; i++) {
            input[i-4] = sourceSamples[i].getDouble();   // radiances
        }
        for (int i = 30; i < 45; i++) {
            input[i-5] = sourceSamples[i].getDouble();   // solar fluxes
        }

        lib.levmar_nn(detectorIndex, input, input.length, output, output.length, debug_dat);

        // copy row index to target product
        double rowIndex = sourceSamples[0].getDouble();
        targetSamples[0].set(rowIndex);

        // copy lat, lon to target product
        double lat = sourceSamples[2].getDouble();
        targetSamples[1].set(lat);
        double lon = sourceSamples[3].getDouble();
        targetSamples[2].set(lon);

        for (int i = 0; i < output.length; i++) {
            targetSamples[i+3].set(output[i]);
        }
    }


    @Override
    protected void prepareInputs() throws OperatorException {
        super.prepareInputs();

        // todo validation of input product

        sourceProduct.addBand("_mask_", maskExpression);

        solarFluxes = getSolarFluxes(EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES);
        auxdataProvider = createAuxdataDataProvider();
    }

    @Override
    protected void configureSourceSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        int index = -1;
        sampleConfigurer.defineSample(++index, "row_index");
        sampleConfigurer.defineSample(++index, EnvisatConstants.MERIS_DETECTOR_INDEX_DS_NAME);
        sampleConfigurer.defineSample(++index, EnvisatConstants.MERIS_LAT_DS_NAME);
        sampleConfigurer.defineSample(++index, EnvisatConstants.MERIS_LON_DS_NAME);
        sampleConfigurer.defineSample(++index, EnvisatConstants.MERIS_SUN_ZENITH_DS_NAME);
        sampleConfigurer.defineSample(++index, EnvisatConstants.MERIS_VIEW_ZENITH_DS_NAME);
        sampleConfigurer.defineSample(++index, EnvisatConstants.MERIS_VIEW_AZIMUTH_DS_NAME);
        sampleConfigurer.defineSample(++index, EnvisatConstants.MERIS_SUN_AZIMUTH_DS_NAME);
        sampleConfigurer.defineSample(++index, "SCATT_ANGLE");
        sampleConfigurer.defineSample(++index, "zonal_wind");
        sampleConfigurer.defineSample(++index, "merid_wind");
        sampleConfigurer.defineSample(++index, "atm_press");
        sampleConfigurer.defineSample(++index, "ozone");
        sampleConfigurer.defineSample(++index, "rel_hum");

        for (String radBandName : EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES) {
            sampleConfigurer.defineSample(++index, radBandName);
        }
        sampleConfigurer.defineSample(++index, "l1_flags");
        for (int i = 1; i <= 15; i++) {
            sampleConfigurer.defineSample(++index, "solar_flux_" + i);
        }
    }


    @Override
    protected void configureTargetSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        Product targetProduct = getTargetProduct();
        String[] bandNames = targetProduct.getBandNames();
        for (int i = 0; i < bandNames.length; i++) {
            sampleConfigurer.defineSample(i, bandNames[i]);
        }
    }


    @Override
    protected void configureTargetProduct(ProductConfigurer productConfigurer) {
        super.configureTargetProduct(productConfigurer);

        /////
        // take row_index and lat/lon from input
        addBand(productConfigurer, "row_index", ProductData.TYPE_FLOAT32, "", "Latitude");
        addBand(productConfigurer, "lat", ProductData.TYPE_FLOAT32, "", "Latitude");
        addBand(productConfigurer, "lon", ProductData.TYPE_FLOAT32, "", "Latitude");
        /////

        /* 0-11*/
        addSpectralBands(productConfigurer, "rl_tosa" + "_%d", "sr^-1", "TOSA Reflectance at %d nm");
        /*12-23*/
        addSpectralBands(productConfigurer, "rl_path" + "_%d", "dxd", "Water leaving radiance reflectance path at %d nm");
        /*24-35*/
        addSpectralBands(productConfigurer, "reflec" + "_%d", "sr^-1", "Water leaving radiance reflectance at %d nm");
        /*36-47*/
        addSpectralBands(productConfigurer, "trans_down" + "_%d", "dl", "Downwelling radiance transmittance at %d nm");
        /*48-59*/
        addSpectralBands(productConfigurer, "trans_up" + "_%d", "dl", "Upwelling radiance transmittance at %d nm");
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

        String autoGrouping = String.format("%s:%s:%s:%s:%s", "rl_tosa", "rl_path", "reflec", "trans_down", "trans_up");
        final Product targetProduct = productConfigurer.getTargetProduct();
        targetProduct.setAutoGrouping(autoGrouping);
        targetProduct.setPreferredTileSize(targetProduct.getSceneRasterWidth(), targetProduct.getSceneRasterHeight());
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

    private AuxdataProvider createAuxdataDataProvider() {
        try {
            return AuxdataProviderFactory.createDataProvider();
        } catch (IOException ioe) {
            throw new OperatorException("Not able to create provider for auxiliary data.", ioe);
        }
    }

    private double[] getSolarFluxes(String[] radBandNames) {
        double[] solarFluxes = new double[radBandNames.length];
        for (int i = 0; i < radBandNames.length; i++) {
            solarFluxes[i] = getSourceProduct().getBand(radBandNames[i]).getSolarFlux();
        }
        return solarFluxes;
    }


    @SuppressWarnings({"UnusedDeclaration"})
    public static class Spi extends OperatorSpi {

        public Spi() {
            super(WaterRadianceCsvOperator.class);
        }
    }

}
