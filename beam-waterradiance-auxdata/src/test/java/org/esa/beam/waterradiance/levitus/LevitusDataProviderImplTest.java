package org.esa.beam.waterradiance.levitus;

import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.waterradiance.AuxdataProvider;
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
public class LevitusDataProviderImplTest {

    @Test
    public void testCreate() throws IOException {
        final LevitusDataProviderImpl provider = LevitusDataProviderImpl.create();
        assertNotNull(provider);
    }

    @Test
    public void testGetSalinity() throws Exception {
        final AuxdataProvider dataProvider = AuxdataProviderFactory.createDataProvider();
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
        double fract = LevitusDataProviderImpl.calculateLinearFraction(
                ProductData.UTC.create(calendar.getTime(), 0).getAsCalendar());
        double expected = LevitusDataProviderImpl.interpolate(salinity0, salinity1, fract);
        double actual = dataProvider.getSalinity(calendar.getTime(), lat, lon);
        assertEquals(expected, actual, 1.0E-6);
    }

    @Test
    public void testGetTemperature() throws Exception {
        AuxdataProvider dataProvider = AuxdataProviderFactory.createDataProvider();
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
        double fract = LevitusDataProviderImpl.calculateLinearFraction(
                ProductData.UTC.create(calendar.getTime(), 0).getAsCalendar());
        double expected = LevitusDataProviderImpl.interpolate(temperature0, temperature1, fract);
        double actual = dataProvider.getTemperature(calendar.getTime(), lat, lon);
        assertEquals(expected, actual, 1.0E-6);
    }

    @Test
    public void testInterpolate() throws Exception {
        assertEquals(0.0, LevitusDataProviderImpl.interpolate(0.0, 10.0, 0.0), 1.0E-6);
        assertEquals(10.0, LevitusDataProviderImpl.interpolate(0.0, 10.0, 1.0), 1.0E-6);
        assertEquals(2.1, LevitusDataProviderImpl.interpolate(0.0, 10.0, 0.21), 1.0E-6);
        assertEquals(6.7, LevitusDataProviderImpl.interpolate(0.0, 10.0, 0.67), 1.0E-6);
    }

    @Test
    public void testCalculateLinearFraction() throws Exception {
        Calendar calendar = createUTCCalendar();
        calendar.set(2011, 2, 16);
        assertEquals(0.00, LevitusDataProviderImpl.calculateLinearFraction(
                ProductData.UTC.create(calendar.getTime(), 0).getAsCalendar()), 1.E-2);
        calendar.set(2011, 2, 31);
        assertEquals(0.48, LevitusDataProviderImpl.calculateLinearFraction(
                ProductData.UTC.create(calendar.getTime(), 0).getAsCalendar()), 1.E-2);
        calendar.set(2011, 3, 1);
        assertEquals(0.53, LevitusDataProviderImpl.calculateLinearFraction(
                ProductData.UTC.create(calendar.getTime(), 0).getAsCalendar()), 1.E-2);
        calendar.set(2011, 3, 15);
        assertEquals(1.00, LevitusDataProviderImpl.calculateLinearFraction(
                ProductData.UTC.create(calendar.getTime(), 0).getAsCalendar()), 1.E-2);
    }

    @Test
    public void testCalculateLowerMonth() throws Exception {
        assertEquals(3, LevitusDataProviderImpl.calculateLowerMonth(3, 4));
        assertEquals(11, LevitusDataProviderImpl.calculateLowerMonth(6, 0));
        assertEquals(5, LevitusDataProviderImpl.calculateLowerMonth(15, 6));
        assertEquals(6, LevitusDataProviderImpl.calculateLowerMonth(16, 6));
        assertEquals(11, LevitusDataProviderImpl.calculateLowerMonth(30, 11));
        assertEquals(0, LevitusDataProviderImpl.calculateLowerMonth(20, 0));
    }

    @Test
    public void testCalculateUpperMonth() throws Exception {
        assertEquals(4, LevitusDataProviderImpl.calculateUpperMonth(3, 4));
        assertEquals(0, LevitusDataProviderImpl.calculateUpperMonth(6, 0));
        assertEquals(6, LevitusDataProviderImpl.calculateUpperMonth(15, 6));
        assertEquals(7, LevitusDataProviderImpl.calculateUpperMonth(16, 6));
        assertEquals(0, LevitusDataProviderImpl.calculateUpperMonth(30, 11));
        assertEquals(1, LevitusDataProviderImpl.calculateUpperMonth(20, 0));
    }

    @Test
    public void testProductContainsPixel() {
        final Product product = new Product("testing", "test", 10, 8);

        assertFalse(LevitusDataProviderImpl.productContainsPixel(product, -1, 6));
        assertFalse(LevitusDataProviderImpl.productContainsPixel(product, 10, 4));

        assertFalse(LevitusDataProviderImpl.productContainsPixel(product, 5, -1));
        assertFalse(LevitusDataProviderImpl.productContainsPixel(product, 8, 8));

        assertTrue(LevitusDataProviderImpl.productContainsPixel(product, 8, 5));
        assertTrue(LevitusDataProviderImpl.productContainsPixel(product, 0, 0));
    }

    private static Calendar createUTCCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
    }
}
