package org.esa.beam.waterradiance;


import org.esa.beam.dataio.envisat.EnvisatConstants;
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
import java.util.Calendar;

/**
 * An operator computing water IOPs starting from radiances.
 *
 * @author Marco Peters
 */
@OperatorMetadata(alias = "Meris.WaterRadiance", version = "1.0",
                  authors = "Roland Doerffer (HZG), Marco Peters (BC)",
                  description = "An operator computing water IOPs starting from radiances.")
public class WaterRadianceOperator extends PixelOperator {

    @SourceProduct
    private Product source;

    private double[] solarFluxes;
    private AuxdataProvider auxdataProvider;
    private Calendar date;

    @Override
    protected void configureSourceSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        int index = 0;
        sampleConfigurer.defineSample(index++, EnvisatConstants.MERIS_SUN_ZENITH_DS_NAME);
        sampleConfigurer.defineSample(index++, EnvisatConstants.MERIS_SUN_AZIMUTH_DS_NAME);
        sampleConfigurer.defineSample(index++, EnvisatConstants.MERIS_VIEW_ZENITH_DS_NAME);
        sampleConfigurer.defineSample(index++, EnvisatConstants.MERIS_VIEW_AZIMUTH_DS_NAME);
        sampleConfigurer.defineSample(index++, "atm_press");
        sampleConfigurer.defineSample(index++, "ozone");
        sampleConfigurer.defineSample(index++, "merid_wind");
        sampleConfigurer.defineSample(index++, "zonal_wind");
        
        String[] radBandNames = EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES;
        for (String radBandName : radBandNames) {
            sampleConfigurer.defineSample(index++, radBandName);
        }
        solarFluxes = getSolarFluxes(radBandNames);
        ProductData.UTC date = source.getStartTime();
        this.date = date.getAsCalendar();

        auxdataProvider = createAuxdataDataProvider();
    }

    @Override
    protected void configureTargetSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        sampleConfigurer.defineSample(0, "dummy");
    }


    @Override
    protected void configureTargetProduct(ProductConfigurer productConfigurer) {
        productConfigurer.addBand("dummy", ProductData.TYPE_FLOAT32);
    }

    private AuxdataProvider createAuxdataDataProvider() {
        try {
            return  AuxdataProviderFactory.createDataProvider();
        } catch (IOException ioe) {
            throw new OperatorException("Not able to create provider for auxiliary data.", ioe);
        }
    }

    private double[] getSolarFluxes(String[] radBandNames) {
        double[] solarFluxes = new double[radBandNames.length];
        for (int i = 0; i < radBandNames.length; i++) {
            solarFluxes[i] = source.getBand(radBandNames[i]).getSolarFlux();
        }
        return solarFluxes;
    }


    @Override
    protected void computePixel(int x, int y, Sample[] sourceSamples, WritableSample[] targetSamples) {
        try {
            double[] input = createInputArray(x,y, sourceSamples);

            double[] output = new double[72];
            // call c-lib
            // int levmar_nn(double input[], int input_length, double output[], int output_length);
            fillTargetSamples(output);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new OperatorException(t);
        }
    }

    private double[] createInputArray(int x, int y, Sample[] sourceSamples) {
        double[] input = new double[40];
        GeoCoding geoCoding = source.getGeoCoding();
        GeoPos geoPos = geoCoding.getGeoPos(new PixelPos(x + 0.5f, y + 0.5f), null);
        for (int i = 0; i < 8; i++) {
            input[i] = sourceSamples[i].getDouble();
        }
        input[8] = auxdataProvider.getTemperature(date, geoPos.getLat(), geoPos.getLon());
        input[9] = auxdataProvider.getSalinity(date, geoPos.getLat(), geoPos.getLon());
        for (int i = 8; i < sourceSamples.length; i++) {
            input[i + 2] = sourceSamples[i].getDouble();
        }
        System.arraycopy(solarFluxes, 0, input, sourceSamples.length + 2, solarFluxes.length);

        return input;
    }

    private void fillTargetSamples(double[] output) {
    }


    @SuppressWarnings({"UnusedDeclaration"})
    public static class Spi extends OperatorSpi {

        public Spi() {
            super(WaterRadianceOperator.class);
        }
    }

}
