package org.esa.beam.waterradiance.util;

import junit.framework.*;
import org.esa.beam.framework.datamodel.PixelPos;
import org.esa.beam.waterradiance.seadas.SeadasAuxdataImpl;
import org.junit.*;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class LatLonToPixelPosConverterTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGetAuxPixelPos() {
        PixelPos auxPixelPos = LatLonToPixelPosConverter.getAuxPixelPos(88, -177);
        junit.framework.Assert.assertEquals(2.0, auxPixelPos.getY());
        junit.framework.Assert.assertEquals(3.0, auxPixelPos.getX());

        auxPixelPos = LatLonToPixelPosConverter.getAuxPixelPos(87, 178);
        junit.framework.Assert.assertEquals(3.0, auxPixelPos.getY());
        junit.framework.Assert.assertEquals(358.0, auxPixelPos.getX());

        auxPixelPos = LatLonToPixelPosConverter.getAuxPixelPos(-88, -178);
        junit.framework.Assert.assertEquals(178.0, auxPixelPos.getY());
        junit.framework.Assert.assertEquals(2.0, auxPixelPos.getX());

        auxPixelPos = LatLonToPixelPosConverter.getAuxPixelPos(-87, 177);
        junit.framework.Assert.assertEquals(177.0, auxPixelPos.getY());
        junit.framework.Assert.assertEquals(357.0, auxPixelPos.getX());
    }
} 