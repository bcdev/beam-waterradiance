package org.esa.beam.waterradiance;

import java.util.Date;

/**
 * @author Marco Peters
 */
public interface SalinityTemperatureAuxdata {

    /**
     * Gets the annual average salinity value at the specified geo-location.
     *
     * @param date the date (UTC) to retrieve the data for
     * @param lat  the latitude value
     * @param lon  the longitude value
     * @return the salinity value or NaN if value could not be retrieved
     *
     * @throws Exception in case of disk access failures
     */
    double getSalinity(Date date, double lat, double lon) throws Exception;

    /**
     * Gets the annual average temperature value at the specified geo-location.
     *
     * @param date the date (UTC) to retrieve the data for
     * @param lat  the latitude value
     * @param lon  the longitude value
     * @return the temperature value or NaN if value could not be retrieved
     *
     * @throws Exception in case of disk access failures
     */
    double getTemperature(Date date, double lat, double lon) throws Exception;

    /**
     * Releases all resources.
     */
    void dispose();
}
