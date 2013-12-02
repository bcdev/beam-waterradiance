package org.esa.beam.ocnnrd;

import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductNodeFilter;
import org.esa.beam.framework.gpf.pointop.ProductConfigurer;

import java.util.HashMap;

class TestProductConfigurer implements ProductConfigurer{

    private final HashMap<String, Band> bands;

    public Product getSourceProduct() {
        return null;
    }

    public void setSourceProduct(Product sourceProduct) {
    }

    public Product getTargetProduct() {
        return null;
    }

    public void copyMetadata() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void copyTimeCoding() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void copyGeoCoding() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void copyMasks() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void copyTiePointGrids(String... gridName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void copyBands(String... bandName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void copyBands(ProductNodeFilter<Band> filter) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void copyVectorData() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Band addBand(String name, int dataType) {
        final Band band = new Band(name, dataType, 2, 2);
        bands.put(name, band);
        return band;
    }

    public Band addBand(String name, int dataType, double noDataValue) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Band addBand(String name, String expression) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Band addBand(String name, String expression, double noDataValue) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    Band getBand(String name) {
        return bands.get(name);
    }

    TestProductConfigurer() {
        this.bands = new HashMap<String, Band>();
    }
}
