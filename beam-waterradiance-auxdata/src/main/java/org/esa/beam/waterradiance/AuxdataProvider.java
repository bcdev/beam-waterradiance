package org.esa.beam.waterradiance;

import java.util.Date;

/**
 * @author Marco Peters
 */
public interface AuxdataProvider {

    /**
     * Gets the annual average salinity value at the specified geo-location.
     *
     * @param date the date (UTC) to retrieve the data for
     * @param lat  the latitude value
     * @param lon  the longitude value
     *
     * @return the salinity value
     *
     * @throws Exception in case the value could not be retrieved
     */
    double getSalinity(Date date, double lat, double lon) throws Exception;

    /**
     * Gets the annual average temperature value at the specified geo-location.
     *
     * @param date the date (UTC) to retrieve the data for
     * @param lat  the latitude value
     * @param lon  the longitude value
     *
     * @return the temperature value
     *
     * @throws Exception in case the value could not be retrieved
     */
    double getTemperature(Date date, double lat, double lon) throws Exception;

}
