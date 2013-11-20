package org.esa.beam.waterradiance.util;

import junit.framework.*;
import org.esa.beam.framework.datamodel.PixelPos;
import org.esa.beam.waterradiance.seadas.SeadasAuxdataImpl;
import org.junit.*;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class LatLonToPixelPosConverterTest {

    @Test
    public void testGetAuxPixelPos() {
        PixelPos auxPixelPos = LatLonToPixelPosConverter.getAuxPixelPos(88, -177, false);
        assertEquals(2.0, auxPixelPos.getY(), 1e-8);
        assertEquals(3.0, auxPixelPos.getX(), 1e-8);

        auxPixelPos = LatLonToPixelPosConverter.getAuxPixelPos(87, 178, false);
        assertEquals(3.0, auxPixelPos.getY(), 1e-8);
        assertEquals(358.0, auxPixelPos.getX(), 1e-8);

        auxPixelPos = LatLonToPixelPosConverter.getAuxPixelPos(-88, -178, false);
        assertEquals(178.0, auxPixelPos.getY(), 1e-8);
        assertEquals(2.0, auxPixelPos.getX(), 1e-8);

        auxPixelPos = LatLonToPixelPosConverter.getAuxPixelPos(-87, 177, false);
        assertEquals(177.0, auxPixelPos.getY(), 1e-8);
        assertEquals(357.0, auxPixelPos.getX(), 1e-8);
    }

    @Test
    public void testGetAuxPixelPosWithVerticalFlip() {
        PixelPos auxPixelPos = LatLonToPixelPosConverter.getAuxPixelPos(-88, -177, true);
        assertEquals(2.0, auxPixelPos.getY(), 1e-8);
        assertEquals(3.0, auxPixelPos.getX(), 1e-8);

        auxPixelPos = LatLonToPixelPosConverter.getAuxPixelPos(-87, 178, true);
        assertEquals(3.0, auxPixelPos.getY(), 1e-8);
        assertEquals(358.0, auxPixelPos.getX(), 1e-8);

        auxPixelPos = LatLonToPixelPosConverter.getAuxPixelPos(88, -178, true);
        assertEquals(178.0, auxPixelPos.getY(), 1e-8);
        assertEquals(2.0, auxPixelPos.getX(), 1e-8);

        auxPixelPos = LatLonToPixelPosConverter.getAuxPixelPos(87, 177, true);
        assertEquals(177.0, auxPixelPos.getY(), 1e-8);
        assertEquals(357.0, auxPixelPos.getX(), 1e-8);
    }

    @Test
    public void testGetAuxPixelPosWithFactor() {
        PixelPos auxPixelPos = LatLonToPixelPosConverter.getAuxPixelPos(88, -177, 1);
        assertEquals(2.0, auxPixelPos.getY(), 1e-8);
        assertEquals(3.0, auxPixelPos.getX(), 1e-8);

        auxPixelPos = LatLonToPixelPosConverter.getAuxPixelPos(88, -177, 2);
        assertEquals(4.0, auxPixelPos.getY(), 1e-8);
        assertEquals(6.0, auxPixelPos.getX(), 1e-8);

        auxPixelPos = LatLonToPixelPosConverter.getAuxPixelPos(88, -177, 4);
        assertEquals(8.0, auxPixelPos.getY(), 1e-8);
        assertEquals(12.0, auxPixelPos.getX(), 1e-8);

        auxPixelPos = LatLonToPixelPosConverter.getAuxPixelPos(88, -177, 0.5);
        assertEquals(1.0, auxPixelPos.getY(), 1e-8);
        assertEquals(1.5, auxPixelPos.getX(), 1e-8);
    }
} 