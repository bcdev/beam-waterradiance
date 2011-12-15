package org.esa.beam.waterradiance;


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
import org.esa.beam.framework.gpf.annotations.SourceProduct;
import org.esa.beam.framework.gpf.pointop.PixelOperator;
import org.esa.beam.framework.gpf.pointop.ProductConfigurer;
import org.esa.beam.framework.gpf.pointop.Sample;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;
import org.esa.beam.framework.gpf.pointop.WritableSample;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * An operator computing water IOPs starting from radiances.
 *
 * @author Marco Peters
 */
@OperatorMetadata(alias = "Meris.WaterRadiance", version = "1.0",
                  authors = "Roland Doerffer (HZG), Marco Peters (BC)",
                  description = "An operator computing water IOPs starting from radiances.")
public class WaterRadianceOperator extends PixelOperator {

    private static final int[] SPECTRAL_INDEXES = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13};
    private static final int[] SPECTRAL_WAVELENGTHS = new int[]{
            412, 442, 449, 510, 560, 620, 665, 681, 708, 753, 778, 865
    };

    @SourceProduct()
    private Product sourceProduct;

    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private double[] solarFluxes;
    private AuxdataProvider auxdataProvider;
    private Date date;


    @Override
    protected void prepareInputs() throws OperatorException {
        super.prepareInputs();
        // todo validation of input product
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
    }

    @Override
    protected void configureTargetSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        Product targetProduct = getTargetProduct();
        String[] bandNames = targetProduct.getBandNames();
        for (int i = 0; i < bandNames.length; i++) {
            String bandName = bandNames[i];
            sampleConfigurer.defineSample(i, bandName);
        }
    }


    @Override
    protected void configureTargetProduct(ProductConfigurer productConfigurer) {
        super.configureTargetProduct(productConfigurer);
        String rl_tosa = "rl_tosa";
        String rl_path = "rl_path";
        String reflec = "reflec";
        String trans_down = "trans_down";
        String trans_up = "trans_up";
        addTosaBands(productConfigurer, rl_tosa);
        addPathBands(productConfigurer, rl_path);
        addReflecBands(productConfigurer, reflec);
        addDownTransBands(productConfigurer, trans_down);
        addUpTransBands(productConfigurer, trans_up);
        addAot550(productConfigurer);
        addAng864_443(productConfigurer);
        addAPig(productConfigurer);
        addAGelb(productConfigurer);
        addAPart(productConfigurer);
        addBPart(productConfigurer);
        addBWit(productConfigurer);
        addSumSq(productConfigurer);
        addAPigStdDev(productConfigurer);
        addAGelbStdDev(productConfigurer);
        addAPartStdDev(productConfigurer);
        addBPartStdDev(productConfigurer);
        addBWitStdDev(productConfigurer);
        String autoGrouping = String.format("%s:%s:%s:%s:%s", rl_tosa, rl_path, reflec, trans_down, trans_up);
        productConfigurer.getTargetProduct().setAutoGrouping(autoGrouping);
    }

    private void addBWitStdDev(ProductConfigurer productConfigurer) {
        Band stdDev = productConfigurer.addBand("b_wit_stddev", ProductData.TYPE_FLOAT32);
        stdDev.setDescription("Standard deviation of b_wit");
    }

    private void addBPartStdDev(ProductConfigurer productConfigurer) {
        Band stdDev = productConfigurer.addBand("b_part_stddev", ProductData.TYPE_FLOAT32);
        stdDev.setDescription("Standard deviation of b_part");
    }

    private void addAPartStdDev(ProductConfigurer productConfigurer) {
        Band stdDev = productConfigurer.addBand("a_part_stddev", ProductData.TYPE_FLOAT32);
        stdDev.setDescription("Standard deviation of a_part");
    }

    private void addAGelbStdDev(ProductConfigurer productConfigurer) {
        Band stdDev = productConfigurer.addBand("a_ys_stddev", ProductData.TYPE_FLOAT32);
        stdDev.setDescription("Standard deviation of a_ys");
    }

    private void addAPigStdDev(ProductConfigurer productConfigurer) {
        Band stdDev = productConfigurer.addBand("a_pig_stddev", ProductData.TYPE_FLOAT32);
        stdDev.setDescription("Standard deviation of a_pig");
    }

    private void addSumSq(ProductConfigurer productConfigurer) {
        Band band = productConfigurer.addBand("sum_sq", ProductData.TYPE_FLOAT32);
        band.setDescription("todo - add description"); // todo - add description
        band.setUnit("dl");
    }

    private void addBWit(ProductConfigurer productConfigurer) {
        Band bWit = productConfigurer.addBand("b_wit", ProductData.TYPE_FLOAT32);
        bWit.setDescription("todo - add description"); // todo - add description
        bWit.setUnit("m^-1"); // todo - check unit
    }

    private void addBPart(ProductConfigurer productConfigurer) {
        Band bPart = productConfigurer.addBand("b_part", ProductData.TYPE_FLOAT32);
        bPart.setDescription("todo - add description"); // todo - add description
        bPart.setUnit("m^-1"); // todo - check unit
    }

    private void addAPart(ProductConfigurer productConfigurer) {
        Band aPart = productConfigurer.addBand("a_part", ProductData.TYPE_FLOAT32);
        aPart.setDescription("todo - add description"); // todo - add description
        aPart.setUnit("m^-1"); // todo - check unit
    }

    private void addAGelb(ProductConfigurer productConfigurer) {
        Band band = productConfigurer.addBand("a_ys", ProductData.TYPE_FLOAT32);
        band.setDescription("Yellow substance absorption coefficient at 443 nm");
        band.setUnit("m^-1");
    }

    private void addAPig(ProductConfigurer productConfigurer) {
        Band apig = productConfigurer.addBand("a_pig", ProductData.TYPE_FLOAT32);
        apig.setDescription("Pigment absorption coefficient at 443 nm");
        apig.setUnit("m^-1");
    }

    private void addAot550(ProductConfigurer productConfigurer) {
        Band band = productConfigurer.addBand("aot_550", ProductData.TYPE_FLOAT32);
        band.setDescription("Aerosol Optical Thickness at 550 nm");
        band.setUnit("dl");
    }

    private void addAng864_443(ProductConfigurer productConfigurer) {
        Band band = productConfigurer.addBand("ang_864_443", ProductData.TYPE_FLOAT32);
        band.setDescription("Aerosol Angstrom coefficient between 864 nm and 443 nm");
        band.setUnit("dl");
    }

    private void addUpTransBands(ProductConfigurer productConfigurer, String namePrefix) {
        addSpectralBands(productConfigurer, namePrefix + "_%d", "Upwelling radiance transmittance at %d nm", "dl");
    }

    private void addDownTransBands(ProductConfigurer productConfigurer, final String namePrefix) {
        addSpectralBands(productConfigurer, namePrefix + "_%d", "Downwelling radiance transmittance at %d nm", "dl");
    }

    private void addReflecBands(ProductConfigurer productConfigurer, final String namePrefix) {
        addSpectralBands(productConfigurer, namePrefix + "_%d", "Water leaving radiance reflectance at %d nm", "sr^-1");
    }

    private void addPathBands(ProductConfigurer productConfigurer, final String namePrefix) {
        addSpectralBands(productConfigurer, namePrefix + "_%d", "Water leaving radiance reflectance path at %d nm",
                         "dxd");
    }

    private void addTosaBands(ProductConfigurer productConfigurer, final String namePrefix) {
        addSpectralBands(productConfigurer, namePrefix + "_%d", "TOSA Reflectance at %d nm", "sr^-1");
    }

    private void addSpectralBands(ProductConfigurer productConfigurer,
                                  String bandNameFormat, String descriptionFormat, String unit) {
        for (int i = 0; i < SPECTRAL_INDEXES.length; i++) {
            int bandIndex = SPECTRAL_INDEXES[i];
            Band band = productConfigurer.addBand(String.format(bandNameFormat, bandIndex), ProductData.TYPE_FLOAT32);
            int wavelength = SPECTRAL_WAVELENGTHS[i];
            band.setSpectralBandIndex(i);
            band.setSpectralWavelength(wavelength);
            band.setDescription(String.format(descriptionFormat, wavelength));
            band.setUnit(unit);
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


    @Override
    protected void computePixel(int x, int y, Sample[] sourceSamples, WritableSample[] targetSamples) {
        try {
            double[] input = createInputArray(x, y, sourceSamples);
            int detectorIndex = sourceSamples[sourceSamples.length - 1].getInt();

            double[] output = new double[73];
            // call c-lib
            // int levmar_nn(int detectorIndex, double input[], int input_length, double output[], int output_length);
            fillTargetSamples(targetSamples, output);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new OperatorException(t);
        }
    }

    private double[] createInputArray(int x, int y, Sample[] sourceSamples) {
        double[] input = new double[40];
        Sample[] inputSamples = Arrays.copyOfRange(sourceSamples, 0, sourceSamples.length - 1);
        GeoCoding geoCoding = sourceProduct.getGeoCoding();
        GeoPos geoPos = geoCoding.getGeoPos(new PixelPos(x + 0.5f, y + 0.5f), null);
        for (int i = 0; i < 8; i++) {
            input[i] = inputSamples[i].getDouble();
        }
        try {
            input[8] = auxdataProvider.getTemperature(date, geoPos.getLat(), geoPos.getLon());
            input[9] = auxdataProvider.getSalinity(date, geoPos.getLat(), geoPos.getLon());
        } catch (Exception e) {
            throw new OperatorException(e);
        }
        for (int i = 8; i < inputSamples.length; i++) {
            input[i + 2] = inputSamples[i].getDouble();
        }
        System.arraycopy(solarFluxes, 0, input, inputSamples.length + 2, solarFluxes.length);

        return input;
    }

    private void fillTargetSamples(WritableSample[] targetSamples, double[] output) {
        for (int i = 0; i < output.length; i++) {
            targetSamples[i].set(output[i]);
        }
    }


    @SuppressWarnings({"UnusedDeclaration"})
    public static class Spi extends OperatorSpi {

        public Spi() {
            super(WaterRadianceOperator.class);
        }
    }

}
