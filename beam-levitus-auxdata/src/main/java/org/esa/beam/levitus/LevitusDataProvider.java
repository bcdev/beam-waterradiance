package org.esa.beam.levitus;

/**
 * @author Marco Peters
 */
public interface LevitusDataProvider {

    double getSalinity(double lat, double lon);

    double getTemperature(double lat, double lon);

}
