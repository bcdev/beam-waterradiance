package org.esa.beam.waterradiance.util;

import org.esa.beam.framework.datamodel.PixelPos;

public class LatLonToPixelPosConverter {

    public static PixelPos getAuxPixelPos(int lat, int lon) {
        PixelPos pixelPos = new PixelPos();
        float pixelY = 180 - (lat + 90);
        float pixelX = lon + 180;
        pixelPos.setLocation(pixelX, pixelY);
        return pixelPos;
    }

    public static PixelPos getAuxPixelPos(double lat, double lon, double factor) {
        PixelPos pixelPos = new PixelPos();
        float pixelY = 180 - ((float)lat + 90);
        float pixelX = (float)lon + 180;
        pixelPos.setLocation(pixelX * factor, pixelY * factor);
        return pixelPos;
    }


}
