package org.esa.beam.waterradiance;

import java.util.Date;

public interface NO2Auxdata {

    /**
     * Gets the daily interpolated ozone content value at the specified geo-location in dobson units.
     *
     * @param date the date (UTC) to retrieve the data for
     * @param lat  the latitude value
     * @param lon  the longitude value
     *
     * @return the ozone value or NaN if value could not be retrieved
     *
     * @throws Exception in case of disk access failures
     *
     */
    // @todo 1 tb/tf this comment lies - please correct tb 2013-11-14
    double getNO2Tropo(Date date, double lat, double lon) throws Exception;


    /**
     * Gets the daily interpolated ozone content value at the specified geo-location in dobson units.
     *
     * @param date the date (UTC) to retrieve the data for
     * @param lat  the latitude value
     * @param lon  the longitude value
     *
     * @return the ozone value or NaN if value could not be retrieved
     *
     * @throws Exception in case of disk access failures
     *
     */
    // @todo 1 tb/tf this comment lies - please correct tb 2013-11-14
    double getNO2Strato(Date date, double lat, double lon) throws Exception;

    /**
     * Gets the daily interpolated nitrogen dioxide fraction at the specified geo-location.
     *
     * @param lat  the latitude value
     * @param lon  the longitude value
     *
     * @return the no2 fraction value or NaN if value could not be retrieved
     *
     * @throws Exception in case of disk access failures
     *
     */
    // @todo 1 tb/tf this comment is not correct - please correct tb 2013-11-14
    double getNO2Frac(double lat, double lon) throws Exception;

    /**
     * Releases all resources.
     */
    void dispose();

}
