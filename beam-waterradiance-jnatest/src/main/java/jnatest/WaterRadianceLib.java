package jnatest;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

/**
* @author Norman Fomferra
*/
public interface WaterRadianceLib extends Library {
    WaterRadianceLib INSTANCE = (WaterRadianceLib) Native.loadLibrary("water_radiance", WaterRadianceLib.class);

    int compute_pixel(double[] reflec, int n_reflec, double[] iop, int n_iop);

}
