package org.esa.beam.levitus;

import org.esa.beam.framework.datamodel.ProductData;
import org.junit.Test;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.*;

/**
 * @author Marco Peters
 */
public class LevitusDataProviderImplTest {

    @Test
    public void testGetSalinity() throws Exception {
        LevitusDataProvider dataProvider = LevitusFactory.createDataProvider(16, 0);
        double salinity = dataProvider.getSalinity(8.5, 53.5);
        // todo - not yet correct
//        assertEquals(32.5769, salinity, 1.0E-6);
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
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        calendar.set(2011, 2, 16);
        assertEquals(0.00, LevitusDataProviderImpl.calculateLinearFraction(calendar), 1.E-2);
        calendar.set(2011, 2, 31);
        assertEquals(0.48, LevitusDataProviderImpl.calculateLinearFraction(calendar), 1.E-2);
        calendar.set(2011, 3, 1);
        assertEquals(0.53, LevitusDataProviderImpl.calculateLinearFraction(calendar), 1.E-2);
        calendar.set(2011, 3, 15);
        assertEquals(1.00, LevitusDataProviderImpl.calculateLinearFraction(calendar), 1.E-2);
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
    public void testGetMaximumFieldValueOfCalendar() throws Exception {
        Calendar calendar = ProductData.UTC.createCalendar();
        calendar.set(Calendar.YEAR, 2012);

        calendar.set(Calendar.MONTH, 1); // February
        assertEquals(29, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        assertEquals(28, calendar.getLeastMaximum(Calendar.DAY_OF_MONTH));
        assertEquals(31, calendar.getMaximum(Calendar.DAY_OF_MONTH));

        calendar.set(Calendar.MONTH, 2); // March
        assertEquals(31, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        assertEquals(28, calendar.getLeastMaximum(Calendar.DAY_OF_MONTH));
        assertEquals(31, calendar.getMaximum(Calendar.DAY_OF_MONTH));
    }
}
