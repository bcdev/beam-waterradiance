package org.esa.beam.levitus;

/**
 * @author Marco Peters
 */
public interface LevitusDataProvider {

    /**
     * Gets the annual average salinity value at the specified geo-location.
     * @param lat the latitude value
     * @param lon the longitude value
     * @return the salinity value
     */
    double getSalinity(double lat, double lon);

    /**
     * Gets the annual average temperature value at the specified geo-location.
     * @param lat the latitude value
     * @param lon the longitude value
     * @return the temperature value
     */
    double getTemperature(double lat, double lon);

}
