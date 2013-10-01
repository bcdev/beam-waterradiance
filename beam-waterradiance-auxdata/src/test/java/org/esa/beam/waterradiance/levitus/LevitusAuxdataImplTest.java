package org.esa.beam.waterradiance.levitus;

import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.waterradiance.SalinityTemperatureAuxdata;
import org.esa.beam.waterradiance.AuxdataProviderFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.*;

/**
 * @author Marco Peters
 */
public class LevitusAuxdataImplTest {

    @Test
    public void testCreate() throws IOException {
        final LevitusAuxdataImpl provider = LevitusAuxdataImpl.create();
        assertNotNull(provider);
    }

    @Test
    public void testGetSalinity() throws Exception {
        final SalinityTemperatureAuxdata dataProvider = AuxdataProviderFactory.createSalinityTemperatureDataProvider();
        final Calendar calendar = createUTCCalendar();
        final double lat = 53.5;
        final double lon = 8.5;

        calendar.set(2011, Calendar.JANUARY, 16);
        double salinity0 = dataProvider.getSalinity(calendar.getTime(), lat, lon);
        assertEquals(32.5769, salinity0, 1.0E-4);

        calendar.set(2011, Calendar.FEBRUARY, 16);
        double salinity1 = dataProvider.getSalinity(calendar.getTime(), lat, lon);
        assertEquals(32.3815, salinity1, 1.0E-4);

        calendar.set(2011, Calendar.JANUARY, 31);
        double fract = LevitusAuxdataImpl.calculateLinearFraction(
                ProductData.UTC.create(calendar.getTime(), 0).getAsCalendar());
        double expected = LevitusAuxdataImpl.interpolate(salinity0, salinity1, fract);
        double actual = dataProvider.getSalinity(calendar.getTime(), lat, lon);
        assertEquals(expected, actual, 1.0E-6);
    }

    @Test
    public void testGetSalinity_outsideProduct() throws Exception {
        final SalinityTemperatureAuxdata dataProvider = AuxdataProviderFactory.createSalinityTemperatureDataProvider();
        final Calendar calendar = createUTCCalendar();
        final double lat = 18.5;
        final double lon = -181.5;

        calendar.set(2011, Calendar.JANUARY, 16);
        double salinity = dataProvider.getSalinity(calendar.getTime(), lat, lon);
        assertEquals(Double.NaN, salinity, 1.0E-8);
    }

    @Test
    public void testGetTemperature() throws Exception {
        SalinityTemperatureAuxdata dataProvider = AuxdataProviderFactory.createSalinityTemperatureDataProvider();
        Calendar calendar = createUTCCalendar();
        double lat = 53.5;
        double lon = 8.5;

        calendar.set(2011, Calendar.JANUARY, 16);
        double temperature0 = dataProvider.getTemperature(calendar.getTime(), lat, lon);
        assertEquals(5.5981, temperature0, 1.0E-4);

        calendar.set(2011, Calendar.FEBRUARY, 16);
        double temperature1 = dataProvider.getTemperature(calendar.getTime(), lat, lon);
        assertEquals(4.2726, temperature1, 1.0E-4);

        calendar.set(2011, Calendar.JANUARY, 31);
        double fract = LevitusAuxdataImpl.calculateLinearFraction(
                ProductData.UTC.create(calendar.getTime(), 0).getAsCalendar());
        double expected = LevitusAuxdataImpl.interpolate(temperature0, temperature1, fract);
        double actual = dataProvider.getTemperature(calendar.getTime(), lat, lon);
        assertEquals(expected, actual, 1.0E-6);
    }

    @Test
    public void testGetTemperature_outsideProduct() throws Exception {
        SalinityTemperatureAuxdata dataProvider = AuxdataProviderFactory.createSalinityTemperatureDataProvider();
        Calendar calendar = createUTCCalendar();
        double lat = 92.8;
        double lon = 8.5;

        calendar.set(2011, Calendar.JANUARY, 16);
        double temperature0 = dataProvider.getTemperature(calendar.getTime(), lat, lon);
        assertEquals(Double.NaN, temperature0, 1.0E-8);
    }

    @Test
    public void testInterpolate() throws Exception {
        assertEquals(0.0, LevitusAuxdataImpl.interpolate(0.0, 10.0, 0.0), 1.0E-6);
        assertEquals(10.0, LevitusAuxdataImpl.interpolate(0.0, 10.0, 1.0), 1.0E-6);
        assertEquals(2.1, LevitusAuxdataImpl.interpolate(0.0, 10.0, 0.21), 1.0E-6);
        assertEquals(6.7, LevitusAuxdataImpl.interpolate(0.0, 10.0, 0.67), 1.0E-6);
    }

    @Test
    public void testCalculateLinearFraction() throws Exception {
        Calendar calendar = createUTCCalendar();
        calendar.set(2011, Calendar.MARCH, 16);
        assertEquals(0.00, LevitusAuxdataImpl.calculateLinearFraction(
                ProductData.UTC.create(calendar.getTime(), 0).getAsCalendar()), 1.E-2);
        calendar.set(2011, Calendar.MARCH, 31);
        assertEquals(0.48, LevitusAuxdataImpl.calculateLinearFraction(
                ProductData.UTC.create(calendar.getTime(), 0).getAsCalendar()), 1.E-2);
        calendar.set(2011, Calendar.APRIL, 1);
        assertEquals(0.53, LevitusAuxdataImpl.calculateLinearFraction(
                ProductData.UTC.create(calendar.getTime(), 0).getAsCalendar()), 1.E-2);
        calendar.set(2011, Calendar.APRIL, 15);
        assertEquals(1.00, LevitusAuxdataImpl.calculateLinearFraction(
                ProductData.UTC.create(calendar.getTime(), 0).getAsCalendar()), 1.E-2);
    }

    @Test
    public void testCalculateLowerMonth() throws Exception {
        assertEquals(3, LevitusAuxdataImpl.calculateLowerMonth(3, 4));
        assertEquals(11, LevitusAuxdataImpl.calculateLowerMonth(6, 0));
        assertEquals(5, LevitusAuxdataImpl.calculateLowerMonth(15, 6));
        assertEquals(6, LevitusAuxdataImpl.calculateLowerMonth(16, 6));
        assertEquals(11, LevitusAuxdataImpl.calculateLowerMonth(30, 11));
        assertEquals(0, LevitusAuxdataImpl.calculateLowerMonth(20, 0));
    }

    @Test
    public void testCalculateUpperMonth() throws Exception {
        assertEquals(4, LevitusAuxdataImpl.calculateUpperMonth(3, 4));
        assertEquals(0, LevitusAuxdataImpl.calculateUpperMonth(6, 0));
        assertEquals(6, LevitusAuxdataImpl.calculateUpperMonth(15, 6));
        assertEquals(7, LevitusAuxdataImpl.calculateUpperMonth(16, 6));
        assertEquals(0, LevitusAuxdataImpl.calculateUpperMonth(30, 11));
        assertEquals(1, LevitusAuxdataImpl.calculateUpperMonth(20, 0));
    }

    @Test
    public void testProductContainsPixel() {
        final Product product = new Product("testing", "test", 10, 8);

        assertFalse(LevitusAuxdataImpl.productContainsPixel(product, -1, 6));
        assertFalse(LevitusAuxdataImpl.productContainsPixel(product, 10, 4));

        assertFalse(LevitusAuxdataImpl.productContainsPixel(product, 5, -1));
        assertFalse(LevitusAuxdataImpl.productContainsPixel(product, 8, 8));

        assertTrue(LevitusAuxdataImpl.productContainsPixel(product, 8, 5));
        assertTrue(LevitusAuxdataImpl.productContainsPixel(product, 0, 0));
    }

    private static Calendar createUTCCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
    }
}
