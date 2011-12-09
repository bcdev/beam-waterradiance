package jnatest;


/**
 * @author Norman Fomferra
 */
public class JnaTest {

    public static void main(String[] args) {
        CLib.INSTANCE.printf("Hello, World\n");
        for (int i = 0; i < args.length; i++) {
            CLib.INSTANCE.printf("Argument %d: %s\n", i, args[i]);
        }
        double y = CLib.INSTANCE.sqrt(16.0);
        System.out.println("y = " + y);

        double[] reflec = new double[13];
        double[] iop = new double[3];

        int n = 1121 * 1121;
        System.out.println("n = " + n);

        long t1 = System.currentTimeMillis();
        double sum = 0;
        for (int i = 0; i < n; i++) {
            reflec[0] = 0.1 * i;
            reflec[2] = 0.2 * i;
            reflec[3] = 0.3 * i;
            reflec[7] = 0.2 * i;
            sum += WaterRadianceLib.INSTANCE.compute_pixel(reflec, reflec.length, iop, iop.length);
        }
        long t2 = System.currentTimeMillis();

        double delta = t2 - t1;

        System.out.printf("JNA: total %s s / %d pixels --> %s ms / pixel\n", delta / 1000.0, n, delta / n);

        t1 = System.currentTimeMillis();
        sum = 0;
        for (int i = 0; i < n; i++) {
            reflec[0] = 0.1 * i;
            reflec[2] = 0.2 * i;
            reflec[3] = 0.3 * i;
            reflec[7] = 0.2 * i;
            sum += compute_pixel(reflec, reflec.length, iop, iop.length);
        }
        t2 = System.currentTimeMillis();

        delta = t2 - t1;

        System.out.printf("Java: total %s s / %d pixels --> %s ms / pixel\n", delta / 1000.0, n, delta / n);
    }

    static int compute_pixel(double[] reflec, int n_reflec, double[] iop, int n_iop) {
        double sum;
        int i;

        sum = 0;
        for (i = 0; i < n_reflec; i++) {
            sum += reflec[i];
        }
        for (i = 0; i < n_iop; i++) {
            iop[i] = (i + 1.0) * sum;
        }

        return n_iop;
    }

}
