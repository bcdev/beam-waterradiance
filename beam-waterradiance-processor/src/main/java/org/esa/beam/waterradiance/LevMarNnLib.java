/*
 * Copyright (C) 2012 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.beam.waterradiance;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * @author Norman Fomferra
 */
public interface LevMarNnLib extends Library {
    LevMarNnLib INSTANCE = (LevMarNnLib) Native.loadLibrary("levmar4beam_dll", LevMarNnLib.class);

    /*
     * int levmar_nn(int detector, double *input, int input_length, double *output, int output_length);
     *
     input[0]=solar_zenith
     input[1]=solar_azimuth
     input[2]=view_zenith
     input[3]=view_azimuth
     input[4]=surf_pressure
     input[5]=ozone
     input[6]=wind_x
     input[7]=wind_y
     input[8]=temperature
     input[9]=salinity
     input[10-24]=toa_radiance[0-14]
     input[25-39]=solar_flux[0-14]

     output[0-11] = RL_tosa_meris[0-11]
     output[12-23]= RL_path_meris[0-11]
     output[24-35]= RL_w[0-11]
     output[36-47]= t_down_meris[0-11]
     output[48-59]= t_up_meris[0-11]
     output[60]=aot_550
     output[61]=ang_865_443
     output[62]=a_pig
     output[63]=a_gelb
     output[64]=a_part
     output[65]=b_part
     output[66]=b_wit
     output[67]=sum_sq
     output[68]=num_iter
     */

    //int levmar_nn(int detector, double *input, int input_length, double *output, int output_length, double *debug_dat)
    int levmar_nn( int detector, double[] input, int input_length, double[] output, int output_length, double[] debug_dat);
}
