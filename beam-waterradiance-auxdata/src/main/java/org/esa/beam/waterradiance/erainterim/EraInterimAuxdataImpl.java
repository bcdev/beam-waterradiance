/*
 * Copyright (C) 2014 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.beam.waterradiance.erainterim;

import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.GeoCoding;
import org.esa.beam.framework.datamodel.GeoPos;
import org.esa.beam.framework.datamodel.PixelPos;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.waterradiance.AtmosphericAuxdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EraInterimAuxdataImpl implements AtmosphericAuxdata {

    private final GeoCoding geoCoding;
    private final Band[] press;
    private final Band[] ozone;
    private final double fraction;

    public EraInterimAuxdataImpl(Date date, Product eraInterimStart, Product eraInterimEnd) {
        this.geoCoding = eraInterimStart.getGeoCoding();

        Calendar cal = ProductData.UTC.createCalendar();
        cal.setTime(date);
        int hoursBy6 = (cal.get(Calendar.HOUR_OF_DAY) / 6); // 0,1,2,3 -> time_band

        press = getBandPair(eraInterimStart, eraInterimEnd, hoursBy6, "Mean_sea_level_pressure_surface");
        ozone = getBandPair(eraInterimStart, eraInterimEnd, hoursBy6, "Total_column_ozone_surface");

        // interpolation factor
        cal.set(Calendar.HOUR_OF_DAY, hoursBy6 * 6);
        long d0 = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, 6);
        long d1 = cal.getTimeInMillis();
        long time = date.getTime();
        fraction = (time - d0) / (double)(d1 - d0);
    }

    private Band[] getBandPair(Product startProduct, Product endProduct, int hoursBy6, String name) {
        Band[] bandPair = new Band[2];
        bandPair[0] = startProduct.getBand(String.format("%s_time%d", name, hoursBy6 + 1));
        if (hoursBy6 == 3) {
            bandPair[1] = endProduct.getBand(String.format("%s_time%d", name, 1));
        } else {
            bandPair[1] = startProduct.getBand(String.format("%s_time%d", name, hoursBy6 + 2));
        }
        return bandPair;
    }

    @Override
    public double getOzone(Date date, double lat, double lon) throws IOException {
        double ozone = getInterpolatedValue(lat, lon, this.ozone);
        // # convert from kg/m2 to DU
        return ozone / 2.144e-5;
    }

    @Override
    public double getSurfacePressure(Date date, double lat, double lon) throws Exception {
        double pressure = getInterpolatedValue(lat, lon, press);
        // convert from Pa to HPa
        return pressure / 100.0;
    }

    @Override
    public void dispose() {
    }

    private double getInterpolatedValue(double lat, double lon, Band[] bandPair) {
        PixelPos pixelPos = geoCoding.getPixelPos(new GeoPos((float) lat, (float) lon), null);

        float v0 = interpolateSpatial(bandPair[0], pixelPos.getX(), pixelPos.getY());
        float v1 = interpolateSpatial(bandPair[1], pixelPos.getX(), pixelPos.getY());
        return interpolateTemporal(v0, v1);
    }

    double interpolateTemporal(double startValue, double endValue) {
        return (1.0 - fraction) * startValue + fraction * endValue;
    }

    static float interpolateSpatial(Band band, double pixelX, double pixelY) {
        List<Float> pixelValues = new ArrayList<>();
        List<Double> weights = new ArrayList<>();
        final double xFloor = Math.floor(pixelX);
        final double yFloor = Math.floor(pixelY);
        int xStart = -1;
        int xEnd = 1;
        int yStart = -1;
        int yEnd = 1;
        double totalSumOfWeights = 0;
        for (int i = xStart; i <= xEnd; i++) {
            int origX = (int) xFloor + i;
            int x = origX % band.getSceneRasterWidth();
            if (x < 0) {
                x = band.getSceneRasterWidth() - 1;
            }
            for (int j = yStart; j <= yEnd; j++) {
                int y = (int) yFloor + j;
                if (y >= 0 && y < band.getSceneRasterHeight()) {
                    pixelValues.add(band.getSampleFloat(x, y));
                    final double distanceToPixelCenter = Math.pow(pixelX - (origX + 0.5), 2) +
                                                         Math.pow(pixelY - (y + 0.5), 2);
                    final double weight = Math.exp(-Math.pow(distanceToPixelCenter / 0.5, 2));
                    weights.add(weight);
                    totalSumOfWeights += weight;
                }
            }
        }
        float interpolatedValue = 0;
        for (int i = 0; i < weights.size(); i++) {
            interpolatedValue += pixelValues.get(i) * (weights.get(i) / totalSumOfWeights);
        }
        return interpolatedValue;
    }


    public static void main(String[] args) throws Exception {
        EraInterimProductReaderPlugin plugin = new EraInterimProductReaderPlugin();
        Product p0 = plugin.createReaderInstance().readProductNodes(args[0], null);
        Product p1 = plugin.createReaderInstance().readProductNodes(args[1], null);

        Date d1 = ProductData.UTC.parse("2011-01-01 01:00:00", "yyyy-MM-dd HH:mm:ss").getAsDate();
        Date d2 = ProductData.UTC.parse("2011-01-01 02:00:00", "yyyy-MM-dd HH:mm:ss").getAsDate();
        Date d3 = ProductData.UTC.parse("2011-01-01 03:00:00", "yyyy-MM-dd HH:mm:ss").getAsDate();

        EraInterimAuxdataImpl aux1 = new EraInterimAuxdataImpl(d1, p0, p1);
        EraInterimAuxdataImpl aux2 = new EraInterimAuxdataImpl(d2, p0, p1);
        EraInterimAuxdataImpl aux3 = new EraInterimAuxdataImpl(d3, p0, p1);

        System.out.println("press = " + aux1.getSurfacePressure(null, 45, -90));
        System.out.println("press = " + aux2.getSurfacePressure(null, 45, -90));
        System.out.println("press = " + aux3.getSurfacePressure(null, 45, -90));

        System.out.println("ozone = " + aux1.getOzone(null, 45, -90));
        System.out.println("ozone = " + aux2.getOzone(null, 45, -90));
        System.out.println("ozone = " + aux3.getOzone(null, 45, -90));
    }
}
