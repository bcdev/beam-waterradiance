package org.esa.beam.waterradiance.seadas;

import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.util.Guardian;

import java.io.IOException;
import java.util.Date;

class ProductsAuxProductsProvider implements AuxProductsProvider {

    private final SeadasAuxDataProducts tomsomiProducts;
    private final SeadasAuxDataProducts ncepProducts;

    ProductsAuxProductsProvider(Product tomsomiStartProduct, Product tomsomiEndProduct, Product ncepStartProduct,
                                   Product ncepEndProduct) throws IOException {
        if(isValidProduct(tomsomiStartProduct) && isValidProduct(tomsomiEndProduct) && isValidProduct(ncepStartProduct)
            && isValidProduct(ncepEndProduct)) {
            tomsomiProducts = new SeadasAuxDataProducts(tomsomiStartProduct, tomsomiEndProduct);
            ncepProducts = new SeadasAuxDataProducts(ncepStartProduct, ncepEndProduct);
        } else {
            throw new IOException("At least one aux product is not valid");
        }
    }

    @Override
    public SeadasAuxDataProducts getTOMSOMIProducts(Date date) throws IOException {
        return tomsomiProducts;
    }

    @Override
    public SeadasAuxDataProducts getNCEPProducts(Date date) throws IOException {
        return ncepProducts;
    }

    @Override
    public void dispose() {
        tomsomiProducts.getStartProduct().dispose();
        tomsomiProducts.getEndProduct().dispose();
        ncepProducts.getStartProduct().dispose();
        ncepProducts.getEndProduct().dispose();
    }

    private boolean isValidProduct(Product product) {
        return product != null;
    }
}
