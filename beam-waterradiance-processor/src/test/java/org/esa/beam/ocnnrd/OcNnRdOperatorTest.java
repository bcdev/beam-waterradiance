package org.esa.beam.ocnnrd;

import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.datamodel.RasterDataNode;
import org.esa.beam.framework.gpf.Operator;
import org.esa.beam.framework.gpf.pointop.WritableSample;
import org.junit.Test;

import static org.junit.Assert.*;

public class OcNnRdOperatorTest {

    @Test
    public void testIsValid() {
        final TestSample[] samples = new TestSample[25];
        samples[24] = new TestSample();

        samples[24].set(true);
        assertTrue(OcNnRdOperator.isValid(samples));

        samples[24].set(false);
        assertFalse(OcNnRdOperator.isValid(samples));
    }

    @Test
    public void testSetToInvalid() {
        final TestSample[] samples = new TestSample[3];
        samples[0] = new TestSample();
        samples[1] = new TestSample();
        samples[2] = new TestSample();

        OcNnRdOperator.setToInvalid(samples, 2);

        assertEquals(Double.NaN, samples[0].getDouble(), 1e-8);
        assertEquals(Double.NaN, samples[1].getDouble(), 1e-8);
        assertEquals(0.0, samples[2].getDouble(), 1e-8);

        OcNnRdOperator.setToInvalid(samples, 3);

        assertEquals(Double.NaN, samples[0].getDouble(), 1e-8);
        assertEquals(Double.NaN, samples[1].getDouble(), 1e-8);
        assertEquals(Double.NaN, samples[2].getDouble(), 1e-8);
    }

    @Test
    public void testMerisTiePointData() {
        final TestSample[] samples = createTestSamples(8);
        final double[] inputs = new double[8];

        OcNnRdOperator.copyTiePointData(inputs, samples);

        for (int i = 0; i < inputs.length; i++) {
            assertEquals(i, inputs[i], 1e-8);
        }
    }

    @Test
    public void testCopyRadiances() {
        final TestSample[] samples = createTestSamples(23);
        final double[] inputs = new double[25];

        OcNnRdOperator.copyRadiances(inputs, samples);

        for (int i = 0; i < 10; i++) {
            assertEquals(0.0, inputs[i], 1e-8);
        }

        for (int i = 10; i < inputs.length; i++) {
            assertEquals(i - 2, inputs[i], 1e-8);
        }
    }

    @Test
    public void testGetSolarFluxes() {
        final Product product = new Product("test", "type", 5, 5);
        final Band schnick = createbandWithSolaFlux("schnick", 23.6f);
        final Band schnack = createbandWithSolaFlux("schnack", 34.7f);
        product.addBand(schnick);
        product.addBand(schnack);

        final double[] solarFluxes = OcNnRdOperator.getSolarFluxes(new String[]{"schnick", "schnack"}, product);
        assertEquals(2, solarFluxes.length);
        assertEquals(23.6, solarFluxes[0], 1e-6);
        assertEquals(34.7, solarFluxes[1], 1e-6);
    }

    @Test
    public void testIsCsvMode() {
        final Product product = new Product("csv_test", "type", 5, 5);

        assertFalse(OcNnRdOperator.isCsvMode(product));

        product.addBand(new Band("solar_flux_1", ProductData.TYPE_INT8, 5, 5));
        assertTrue(OcNnRdOperator.isCsvMode(product));
    }

    @Test
    public void testCopySolarFluxes() {
        final TestSample[] samples = createTestSamples(40);
        final double[] inputs = new double[40];

        OcNnRdOperator.copySolarFluxes(inputs, samples);

        for (int i = 0; i < 25; i++) {
            assertEquals(0.0, inputs[i], 1e-8);
        }

        for (int i = 25; i < inputs.length; i++) {
            assertEquals(i, inputs[i], 1e-8);
        }
    }

    @Test
    public void testGetDetectorIndex() {
        final TestSample[] samples = new TestSample[24];
        samples[23] = new TestSample();
        samples[23].set(776);

        final int detectorIndex = OcNnRdOperator.getDetectorIndex(samples);
        assertEquals(776, detectorIndex);
    }

    private Band createbandWithSolaFlux(String schnick1, float solarFlux) {
        final Band schnick = new Band(schnick1, ProductData.TYPE_INT16, 5, 5);
        schnick.setSolarFlux(solarFlux);
        return schnick;
    }

    private TestSample[] createTestSamples(int count) {
        final TestSample[] samples = new TestSample[count];
        for (int i = 0; i < samples.length; i++) {
            samples[i] = new TestSample();
            samples[i].set((double) i);
        }
        return samples;
    }

    @Test
    public void testSpi() {
        final OcNnRdOperator.Spi spi = new OcNnRdOperator.Spi();
        final Class<? extends Operator> operatorClass = spi.getOperatorClass();
        assertTrue(operatorClass.isAssignableFrom(OcNnRdOperator.class));
    }

    private static class TestSample implements WritableSample {

        private boolean boolValue;
        private double doubleValue;
        private int intValue;

        @Override
        public RasterDataNode getNode() {
            return null;
        }

        @Override
        public int getIndex() {
            return 0;
        }

        @Override
        public int getDataType() {
            return 0;
        }

        @Override
        public boolean getBit(int i) {
            return false;
        }

        @Override
        public boolean getBoolean() {
            return boolValue;
        }

        public void set(boolean boolValue) {
            this.boolValue = boolValue;
        }

        @Override
        public int getInt() {
            return intValue;
        }

        @Override
        public float getFloat() {
            return 0;
        }

        @Override
        public double getDouble() {
            return doubleValue;
        }

        @Override
        public void set(int i, boolean b) {
        }

        @Override
        public void set(int i) {
            intValue = i;
        }

        @Override
        public void set(float v) {
        }

        @Override
        public void set(double v) {
            doubleValue = v;
        }
    }
}
