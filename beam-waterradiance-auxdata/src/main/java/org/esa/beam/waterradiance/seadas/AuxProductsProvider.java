package org.esa.beam.waterradiance.seadas;

import org.esa.beam.framework.datamodel.Product;

import java.io.IOException;
import java.util.Date;

interface AuxProductsProvider {

    SeadasAuxDataProducts getTOMSOMIProducts(Date date) throws IOException;

    SeadasAuxDataProducts getNCEPProducts(Date date) throws IOException;

    void dispose();

}
