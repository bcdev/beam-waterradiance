package org.esa.beam.ocnnrd;

import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.framework.gpf.Operator;
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

        OcNnRdOperator.setToInvalid(samples);

        assertEquals(Double.NaN, samples[0].getDouble(), 1e-8);
        assertEquals(Double.NaN, samples[1].getDouble(), 1e-8);
        assertEquals(Double.NaN, samples[2].getDouble(), 1e-8);
    }

    @Test
    public void testCopyRadiances_MERIS() {
        final TestSample[] samples = createTestSamples(23);
        final double[] inputs = new double[25];

        OcNnRdOperator.copyRadiances(inputs, samples, new MerisSensorConfig());

        for (int i = 0; i < 10; i++) {
            assertEquals(0.0, inputs[i], 1e-8);
        }

        for (int i = 10; i < inputs.length; i++) {
            assertEquals(i - 2, inputs[i], 1e-8);
        }
    }

    @Test
    public void testCopyRadiances_MODIS() {
        final TestSample[] samples = createTestSamples(19);
        final double[] inputs = new double[19];

        OcNnRdOperator.copyRadiances(inputs, samples, new ModisSensorConfig());

        for (int i = 0; i < 10; i++) {
            assertEquals(0.0, inputs[i], 1e-8);
        }

        for (int i = 10; i < inputs.length; i++) {
            assertEquals(i - 2, inputs[i], 1e-8);
        }
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
    public void testSpi() {
        final OcNnRdOperator.Spi spi = new OcNnRdOperator.Spi();
        final Class<? extends Operator> operatorClass = spi.getOperatorClass();
        assertTrue(operatorClass.isAssignableFrom(OcNnRdOperator.class));
    }

    @Test
    public void testGetProductType_fromParameter() {
        final OcNnRdOperator ocNnRdOperator = new OcNnRdOperator();
        ocNnRdOperator.sensorTypeString = "MODIS_CSV";

        final String sensorTypeString = ocNnRdOperator.getSensorTypeString();
        assertEquals("MODIS_CSV", sensorTypeString);
    }

    @Test
    public void testGetProductType_fromProduct() {
        final OcNnRdOperator ocNnRdOperator = new OcNnRdOperator();
        ocNnRdOperator.sourceProduct = new Product("Charly", "MOD021KM", 2, 2);

        final String sensorTypeString = ocNnRdOperator.getSensorTypeString();
        assertEquals("MOD021KM", sensorTypeString);
    }

    private TestSample[] createTestSamples(int count) {
        final TestSample[] samples = new TestSample[count];
        for (int i = 0; i < samples.length; i++) {
            samples[i] = new TestSample();
            samples[i].set((double) i);
        }
        return samples;
    }
}
