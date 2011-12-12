package org.esa.beam.waterradiance;

import java.util.Calendar;

/**
 * @author Marco Peters
 */
public interface AuxdataDataProvider {

    /**
     * Gets the annual average salinity value at the specified geo-location.
     *
     *
     * @param date the date to retrieve the data for
     * @param lat the latitude value
     * @param lon the longitude value
     * @return the salinity value
     */
    double getSalinity(Calendar date, double lat, double lon);

    /**
     * Gets the annual average temperature value at the specified geo-location.
     *
     * @param date the date to retrieve the data for
     * @param lat the latitude value
     * @param lon the longitude value
     * @return the temperature value
     */
    double getTemperature(Calendar date, double lat, double lon);

}
