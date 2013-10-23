package org.esa.beam.waterradiance.no2;

import org.esa.beam.framework.dataio.AbstractProductReader;
import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.datamodel.PixelPos;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.util.math.MathUtils;
import org.esa.beam.waterradiance.NO2Auxdata;
import org.esa.beam.waterradiance.util.LatLonToPixelPosConverter;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class NO2AuxdataImpl implements NO2Auxdata {

    private final File auxDataDirectory;
    private final static String fracProductName = "trop_f_no2_200m.hdf";
    private final static String fracBandName = "Geophysical_Data/f_no2_200m";
    private Product fracProduct;
    private final static String climatologyProductName = "no2_climatology.hdf";
    private final static String tropoBandNameStem = "Geophysical_Data/trop_no2_";
    private final static String totBandNameStem = "Geophysical_Data/tot_no2_";
    private Product climatologyProduct;

    public static NO2AuxdataImpl create(String auxPath) throws IOException {
        final File auxDataDirectory = new File(auxPath);
        if (!auxDataDirectory.isDirectory()) {
            throw new IOException();
        }
        return new NO2AuxdataImpl(auxDataDirectory);
    }

    private NO2AuxdataImpl(File auxDataDirectory) throws IOException {
        this.auxDataDirectory = auxDataDirectory;
        initFracProduct();
        initClimatologyProduct();
    }

    @Override
    public double getNO2Tropo(Date date, double lat, double lon) throws Exception {
        final PixelPos pixelPos = getAuxPixelPosForClimatologyProduct(lat, lon);
        String month = getMonthFromDate(date);
        String tropoBandName = tropoBandNameStem + month;
        return Double.parseDouble(climatologyProduct.getBand(tropoBandName).getPixelString((int) pixelPos.getX(), (int) pixelPos.getY()));
    }

    private String getMonthFromDate(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH) + 1;
        if(month < 10) {
            return "0" + month;
        } else {
            return "" + month;
        }
    }

    @Override
    public double getNO2Strato(Date date, double lat, double lon) throws Exception {
        final PixelPos pixelPos = getAuxPixelPosForClimatologyProduct(lat, lon);
        String month = getMonthFromDate(date);
        String tropoBandName = tropoBandNameStem + month;
        String totBandName = totBandNameStem + month;
        final double tropo =
                Double.parseDouble(climatologyProduct.getBand(tropoBandName).getPixelString((int) pixelPos.getX(), (int) pixelPos.getY()));
        final double tot =
                Double.parseDouble(climatologyProduct.getBand(totBandName).getPixelString((int) pixelPos.getX(), (int) pixelPos.getY()));
        return tot - tropo;
    }

    @Override
    public double getNO2Frac(double lat, double lon) throws Exception {
        final int xPos = MathUtils.floorInt(lat);
        final int yPos = MathUtils.floorInt(lon);
        final PixelPos pixelPos = LatLonToPixelPosConverter.getAuxPixelPos(xPos, yPos);
        return Double.parseDouble(fracProduct.getBand(fracBandName).getPixelString((int) pixelPos.getX() / 2, (int) pixelPos.getY() / 2));
    }

    private void initFracProduct() throws IOException {
        String fracProductPath = auxDataDirectory.getPath() + "//" + fracProductName;
        try {
            final ProductReader productReader = ProductIO.getProductReader("NETCDF-CF");
            fracProduct = productReader.readProductNodes(new File(fracProductPath), null);
//            fracProduct = ProductIO.readProduct(new File(fracProductPath));
        } catch (IOException e) {
            throw new IOException("Could not find no2 frac product");
        }
    }

    private void initClimatologyProduct() throws IOException {
        String climatologyProductPath = auxDataDirectory.getPath() + "//" + climatologyProductName;
        try {
            final ProductReader productReader = ProductIO.getProductReader("NETCDF-CF");
            climatologyProduct = productReader.readProductNodes(new File(climatologyProductPath), null);
//            climatologyProduct = ProductIO.readProduct(new File(climatologyProductPath));
        } catch (IOException e) {
            throw new IOException("Could not find no2 climatology product");
        }
    }

    public static PixelPos getAuxPixelPosForClimatologyProduct(double lat, double lon) {
        PixelPos pixelPos = new PixelPos();
        float pixelY = 180 - ((float)lat + 90);
        float pixelX = (float)lon + 180;
        pixelPos.setLocation(pixelX * 4, pixelY * 4);
        return pixelPos;
    }

    @Override
    public void dispose() {
        fracProduct.dispose();
        climatologyProduct.dispose();
    }

}
