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

import org.esa.beam.framework.dataio.DecodeQualification;
import org.esa.beam.framework.dataio.ProductReader;
import org.esa.beam.framework.dataio.ProductReaderPlugIn;
import org.esa.beam.util.Debug;
import org.esa.beam.util.io.BeamFileFilter;
import ucar.nc2.NetcdfFile;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class EraInterimProductReaderPlugin implements ProductReaderPlugIn {

    static final String GRIB_IOSP_CLASS_NAME = "ucar.nc2.grib.grib1.Grib1Iosp";
    static final int DEFAULT_BUFFERSIZE = 8092;

    @Override
    public DecodeQualification getDecodeQualification(Object input) {
        NetcdfFile grib = openGrib(input);
        if (grib != null) {
            try {
                grib.close();
            } catch (IOException ignore) {
                Debug.trace(ignore);
            }
            return DecodeQualification.INTENDED;
        }
        return DecodeQualification.UNABLE;
    }

    @Override
    public Class[] getInputTypes() {
        return new Class[]{String.class, File.class};
    }

    @Override
    public ProductReader createReaderInstance() {
        return new EraInterimProductReader(this);
    }

    @Override
    public String[] getFormatNames() {
        return new String[]{"ERA-INTERIM"};
    }

    @Override
    public String[] getDefaultFileExtensions() {
        return new String[]{".grib"};
    }

    @Override
    public String getDescription(Locale locale) {
        return "era-interim aux data product";
    }

    @Override
    public BeamFileFilter getProductFileFilter() {
        return new BeamFileFilter(getFormatNames()[0], getDefaultFileExtensions(), getDescription(null));
    }

    static NetcdfFile openGrib(Object input) {
        File inputFile = null;
        if (input instanceof String) {
            inputFile = new File((String) input);
        } else if (input instanceof File) {
            inputFile = (File) input;
        }
        if (inputFile != null) {
            if (!inputFile.getName().startsWith("era_interim_")) {
                return null;
            }
            try {
                NetcdfFile netcdfFile = NetcdfFile.open(inputFile.getAbsolutePath(), GRIB_IOSP_CLASS_NAME, DEFAULT_BUFFERSIZE, null, null);
                if (netcdfFile != null) {
                    if (netcdfFile.findVariable("Mean_sea_level_pressure_surface") != null &&
                        netcdfFile.findVariable("10_metre_U_wind_component_surface") != null &&
                        netcdfFile.findVariable("10_metre_V_wind_component_surface") != null &&
                        netcdfFile.findVariable("Total_column_ozone_surface") != null) {
                        return netcdfFile;
                    }
                }
            } catch (Exception ignore) {
                Debug.trace(ignore);
            }
        }
        return null;
    }
}
