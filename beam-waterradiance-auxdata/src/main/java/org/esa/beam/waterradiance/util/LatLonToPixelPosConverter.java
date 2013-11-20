package org.esa.beam.waterradiance.util;

import org.esa.beam.framework.datamodel.PixelPos;

public class LatLonToPixelPosConverter {

    public static PixelPos getAuxPixelPos(double lat, double lon, boolean flipVertical) {
        return getAuxPixelPos(lat, lon, 1, flipVertical);
    }

    public static PixelPos getAuxPixelPos(double lat, double lon, double factor) {
        return getAuxPixelPos(lat, lon, factor, false);
    }

    public static PixelPos getAuxPixelPos(double lat, double lon, double factor, boolean flipVertical) {
        PixelPos pixelPos = new PixelPos();
        float pixelY;
        if(flipVertical) {
            pixelY = (float)lat + 90;
        } else {
            pixelY = 180 - ((float)lat + 90);
        }
        float pixelX = (float)lon + 180;
        pixelPos.setLocation(pixelX * factor, pixelY * factor);
        return pixelPos;
    }


}
