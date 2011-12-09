package org.esa.beam.waterradiance;


import org.esa.beam.dataio.envisat.EnvisatConstants;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.gpf.OperatorException;
import org.esa.beam.framework.gpf.OperatorSpi;
import org.esa.beam.framework.gpf.annotations.OperatorMetadata;
import org.esa.beam.framework.gpf.annotations.SourceProduct;
import org.esa.beam.framework.gpf.pointop.PixelOperator;
import org.esa.beam.framework.gpf.pointop.Sample;
import org.esa.beam.framework.gpf.pointop.SampleConfigurer;
import org.esa.beam.framework.gpf.pointop.WritableSample;
import org.esa.beam.levitus.LevitusDataProvider;

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
    private LevitusDataProvider levitusDataProvider;

    @Override
    protected void configureSourceSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
        int index = 0;
        sampleConfigurer.defineSample(index++, EnvisatConstants.MERIS_VIEW_ZENITH_DS_NAME);
        sampleConfigurer.defineSample(index++, EnvisatConstants.MERIS_VIEW_AZIMUTH_DS_NAME);
        sampleConfigurer.defineSample(index++, EnvisatConstants.MERIS_SUN_ZENITH_DS_NAME);
        sampleConfigurer.defineSample(index++, EnvisatConstants.MERIS_SUN_AZIMUTH_DS_NAME);
        sampleConfigurer.defineSample(index++, "atm_press");
        sampleConfigurer.defineSample(index++, "merid_wind");
        sampleConfigurer.defineSample(index++, "zonal_wind");
        sampleConfigurer.defineSample(index++, "ozone");
        String[] radBandNames = EnvisatConstants.MERIS_L1B_SPECTRAL_BAND_NAMES;
        for (String radBandName : radBandNames) {
            sampleConfigurer.defineSample(index++, radBandName);
        }

        solarFluxes = getSolarFluxes(radBandNames);

        ProductData.UTC date = source.getStartTime();
        levitusDataProvider = createLevitusDataProvider(date);
    }

    private LevitusDataProvider createLevitusDataProvider(ProductData.UTC date) {
        Calendar asCalendar = date.getAsCalendar();
        int day = asCalendar.get(Calendar.DAY_OF_MONTH);
        int month = asCalendar.get(Calendar.MONTH);
        return  new MyLevitusDataProvider(day, month);
    }

    private double[] getSolarFluxes(String[] radBandNames) {
        double[] solarFluxes = new double[radBandNames.length];
        for (int i = 0; i < radBandNames.length; i++) {
            solarFluxes[i] = source.getBand(radBandNames[i]).getSolarFlux();
        }
        return solarFluxes;
    }

    @Override
    protected void configureTargetSamples(SampleConfigurer sampleConfigurer) throws OperatorException {
    }

    @Override
    protected void computePixel(int x, int y, Sample[] sourceSamples, WritableSample[] targetSamples) {
    }


    @SuppressWarnings({"UnusedDeclaration"})
    public static class Spi extends OperatorSpi {

        protected Spi() {
            super(WaterRadianceOperator.class);
        }
    }

    private class MyLevitusDataProvider implements LevitusDataProvider {

        public MyLevitusDataProvider(int day, int month) {

        }

        public double getSalinity(double lat, double lon) {
            return 0;
        }

        public double getTemperature(double lat, double lon) {
            return 0;
        }
    }
}
