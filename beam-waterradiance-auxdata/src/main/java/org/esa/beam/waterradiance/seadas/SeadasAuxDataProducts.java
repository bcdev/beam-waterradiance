package org.esa.beam.waterradiance.seadas;

import org.esa.beam.framework.datamodel.Product;

class SeadasAuxDataProducts {

    private final Product startProduct;
    private final Product endProduct;

    SeadasAuxDataProducts(Product startProduct, Product endProduct) {
        this.startProduct = startProduct;
        this.endProduct = endProduct;
    }

    Product getStartProduct() {
        return startProduct;
    }

    Product getEndProduct() {
        return endProduct;
    }
}
