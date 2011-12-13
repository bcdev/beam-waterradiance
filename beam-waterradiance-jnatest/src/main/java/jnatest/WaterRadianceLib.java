package jnatest;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
* @author Norman Fomferra
*/
public interface WaterRadianceLib extends Library {
    WaterRadianceLib INSTANCE = (WaterRadianceLib) Native.loadLibrary("water_radiance", WaterRadianceLib.class);

    int levmar_nn(double[] reflec, int n_reflec, double[] iop, int n_iop);

}
