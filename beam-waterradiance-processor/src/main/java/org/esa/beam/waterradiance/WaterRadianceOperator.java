package org.esa.beam.waterradiance;


import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.runtime.internal.Platform;
import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.esa.beam.framework.datamodel.*;
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
import java.util.Date;

/**
 * An operator computing water IOPs starting from radiances.
 *
 * @author Marco Peters
 */
@OperatorMetadata(alias = "Meris.WaterRadiance", version = "1.0",
                  authors = "Olaf Danne, Roland Doerffer, Norman Fomferra, Marco Zühlke",
                  description = "An operator computing water IOPs starting from radiances.")
public class WaterRadianceOperator extends PixelOperator {

    private static final int[] SPECTRAL_INDEXES = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13};
    private static final int[] SPECTRAL_WAVELENGTHS = new int[]{
            412, 442, 449, 510, 560, 620, 665, 681, 708, 753, 778, 865
    };


    private static final File AUXDATA_DIR = new File(SystemUtils.getApplicationDataDir(), "beam-waterradiance-processor/auxdata");

    static {
        URL sourceUrl = ResourceInstaller.getSourceUrl(WaterRadianceOperator.class);
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

    @Parameter(defaultValue = "!l1_flags.INVALID && !l1_flags.BRIGHT && !l1_flags.LAND_OCEAN")
    private String maskExpression;

    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private double[] solarFluxes;
    private AuxdataProvider auxdataProvider;
    private Date date;

    private final LevMarNnLib lib = LevMarNnLib.INSTANCE;

    private final double[] input = new double[40];
    private final double[] output = new double[69];
    private final double[] debug_dat = new double[1000];
    private int maskIndex;


    @Override
    protected synchronized void computePixel(int x, int y, Sample[] sourceSamples, WritableSample[] targetSamples) {

        if (sourceSamples[maskIndex].getBoolean()) {
            GeoCoding geoCoding = sourceProduct.getGeoCoding();
            GeoPos geoPos = geoCoding.getGeoPos(new PixelPos(x + 0.5f, y + 0.5f), null);
            for (int i = 0; i < 8; i++) {
                input[i] = sourceSamples[i].getDouble();
            }
            try {
                input[8] = 20.0; // auxdataProvider.getTemperature(date, geoPos.getLat(), geoPos.getLon());
                input[9] = 12.0; // auxdataProvider.getSalinity(date, geoPos.getLat(), geoPos.getLon());
            } catch (Exception e) {
                throw new OperatorException(e);
            }
            for (int i = 8; i < sourceSamples.length; i++) {
                input[2 + i] = sourceSamples[i].getDouble();
            }
            System.arraycopy(solarFluxes, 0, input, 25, 15);
            int detectorIndex = sourceSamples[sourceSamples.length - 1].getInt();
            lib.levmar_nn(detectorIndex, input, input.length, output, output.length, debug_dat);
            for (int i = 0; i < output.length; i++) {
                targetSamples[i].set(output[i]);
            }
        } else {
            for (int i = 0; i < output.length; i++) {
                targetSamples[i].set(Double.NaN);
            }
        }
    }


    @Override
    protected void prepareInputs() throws OperatorException {
        super.prepareInputs();

        // todo validation of input product

        sourceProduct.addBand("_mask_", maskExpression);

        solarFluxes = getSolarFluxes(EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES);
        date = sourceProduct.getStartTime().getAsDate();
        auxdataProvider = createAuxdataDataProvider();
    }

    @Override
    protected void configureSourceSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        int index = -1;
        sampleConfigurer.defineSample(++index, EnvisatConstants.MERIS_SUN_ZENITH_DS_NAME);
        sampleConfigurer.defineSample(++index, EnvisatConstants.MERIS_SUN_AZIMUTH_DS_NAME);
        sampleConfigurer.defineSample(++index, EnvisatConstants.MERIS_VIEW_ZENITH_DS_NAME);
        sampleConfigurer.defineSample(++index, EnvisatConstants.MERIS_VIEW_AZIMUTH_DS_NAME);
        sampleConfigurer.defineSample(++index, "atm_press");
        sampleConfigurer.defineSample(++index, "ozone");
        sampleConfigurer.defineSample(++index, "merid_wind");
        sampleConfigurer.defineSample(++index, "zonal_wind");

        for (String radBandName : EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES) {
            sampleConfigurer.defineSample(++index, radBandName);
        }
        sampleConfigurer.defineSample(++index, EnvisatConstants.MERIS_DETECTOR_INDEX_DS_NAME);

        maskIndex = ++index;
        sampleConfigurer.defineSample(maskIndex, "_mask_");
    }

    @Override
    protected void configureTargetSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        Product targetProduct = getTargetProduct();
        String[] bandNames = targetProduct.getBandNames();
        for (int i = 0; i < output.length; i++) {
            final String bandName = bandNames[i];
            sampleConfigurer.defineSample(i, bandName);
        }
    }

    @Override
    protected void configureTargetProduct(ProductConfigurer productConfigurer) {
        super.configureTargetProduct(productConfigurer);

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

        productConfigurer.copyBands(EnvisatConstants.MERIS_DETECTOR_INDEX_DS_NAME);
        productConfigurer.copyBands(EnvisatConstants.MERIS_L1B_FLAGS_DS_NAME);

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
            super(WaterRadianceOperator.class);
        }
    }

}
