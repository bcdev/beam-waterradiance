package org.esa.beam.waterradiance.levitus.filler;

import org.esa.beam.framework.dataio.ProductIO;
import org.esa.beam.framework.datamodel.Band;
import org.esa.beam.framework.datamodel.CrsGeoCoding;
import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.ProductData;
import org.esa.beam.util.io.FileUtils;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import java.io.*;
import java.util.Properties;

/**
 * Tool used to fill gaps in "Levitus" climatologies (retrieved from http://data.nodc.noaa.gov/las/getUI.do).
 *
 * @author Norman Fomferra
 */
public class GapFiller {
    public static void main(String[] args) throws IOException, TransformException, FactoryException {
        if (args.length == 0) {
            System.out.println("Usage: GapFiller <configFile>");
            System.exit(1);
            return;
        }
        try {
            run(args);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static void run(String[] args) throws Exception {
        final String configFile = args[0];
        final Properties config = loadConfig(new File(configFile));
        final String bandName = config.getProperty("bandName");
        final String outputFile = config.getProperty("outputFile", bandName);
        final String outputFormat = getFormat(outputFile);
        final String inputDir = config.getProperty("inputDir");
        final String[] inputFiles = config.getProperty("inputFiles").split("\\s");
        final boolean is360 = Boolean.parseBoolean(config.getProperty("is360", "false"));
        final boolean interpolZ = Boolean.parseBoolean(config.getProperty("interpolZ", "true"));
        final int maxIter = Integer.parseInt(config.getProperty("maxIter", "1000"));
        final String maskFile = config.getProperty("maskFile");

        final float[][] buffers = new float[inputFiles.length][];
        int w = -1;
        int h = -1;
        for (int z = 0; z < inputFiles.length; z++) {
            final File file = new File(inputDir, inputFiles[z]);
            System.out.println("Reading " + file);
            final Product product = ProductIO.readProduct(file);
            final Band band = product.getBand(bandName);
            if (w == -1) {
                w = band.getSceneRasterWidth();
                h = band.getSceneRasterHeight();
            }
            buffers[z] = new float[w * h];
            band.readPixels(0, 0, w, h, buffers[z]);

            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (!band.isPixelValid(x, y)) {
                        buffers[z][index(x, y, w)] = Float.NaN;
                    }
                }

                if (is360) {
                    moveZeroMeridianToCenter(buffers[z], y, w);
                }
            }

            product.dispose();
        }

        final boolean[] mask;
        if (maskFile != null) {
            System.out.println("Reading mask " + maskFile);
            mask = readMask(maskFile, w, h);
        } else {
            mask = null;
        }

        System.out.println("Filling gaps");

        final float[][] results = fillGaps(w, h, buffers, maxIter, interpolZ, mask);

        final Product product = new Product(FileUtils.getFilenameWithoutExtension(outputFile),
                                            GapFiller.class.getSimpleName() + "." + bandName, w, h);
        product.setGeoCoding(new CrsGeoCoding(DefaultGeographicCRS.WGS84, w, h, -180, 90, 1, 1, 0, 0));
        for (int z = 0; z < inputFiles.length; z++) {
            final Band band = product.addBand(bandName + "_" + (z + 1), ProductData.TYPE_FLOAT32);
            band.setNoDataValueUsed(true);
            band.setNoDataValue(Float.NaN);
            band.setRasterData(ProductData.createInstance(results[z]));
        }

        System.out.println("Writing " + outputFile);
        ProductIO.writeProduct(product, outputFile, outputFormat);
    }

    private static String getFormat(String outputName) {
        final String extension = FileUtils.getExtension(outputName);
        if (".dim".equalsIgnoreCase(extension)) {
            return "BEAM-DIMAP";
        } else if (".nc".equalsIgnoreCase(extension)) {
            return "NetCDF-BEAM";
        } else {
            throw new IllegalArgumentException("Can't derive format from output file name " + outputName);
        }
    }

    private static void moveZeroMeridianToCenter(float[] buffer, int y, int w) {
        final int w2 = w / 2;
        for (int x = 0; x < w2; x++) {
            final int i1 = index(x, y, w);
            final int i2 = index(w2 + x, y, w);
            final float v1 = buffer[i1];
            final float v2 = buffer[i2];
            buffer[i1] = v2;
            buffer[i2] = v1;
        }
    }

    private static boolean[] readMask(String maskFile, int nx, int ny) throws IOException {

        final Reader reader = new FileReader(maskFile);
        try {
            final StreamTokenizer streamTokenizer = new StreamTokenizer(reader);
            streamTokenizer.resetSyntax();
            streamTokenizer.commentChar('#');
            streamTokenizer.parseNumbers();
            streamTokenizer.whitespaceChars(0, 32);
            streamTokenizer.eolIsSignificant(false);
            final boolean[] mask = new boolean[nx * ny];
            int tokenType;
            for (int y = 0; y < ny; y++) {
                for (int x = 0; x < nx; x++) {
                    tokenType = streamTokenizer.nextToken();
                    if (tokenType != StreamTokenizer.TT_NUMBER) {
                        throw new IOException("Only numbers expected in mask file " + maskFile);
                    }
                    mask[y * nx + x] = streamTokenizer.nval > 0.0;
                }
            }
            tokenType = streamTokenizer.nextToken();
            if (tokenType != StreamTokenizer.TT_EOF) {
                throw new IOException("Too many values in mask file " + maskFile);
            }
            return mask;
        } finally {
            reader.close();
        }
    }

    private static Properties loadConfig(File configFile) throws IOException {
        final Properties config = new Properties();
        final FileReader reader = new FileReader(configFile);
        config.load(reader);
        reader.close();
        return config;
    }

    static float[][] fillGaps(int w, int h, float[][] input, int maxIter, boolean interpolZ, boolean[] mask) {
        final float[][] output = clone(input);

        int i;
        for (i = 0; i < maxIter; i++) {
            final int fillCount = fillGaps(w, h, input, output, interpolZ, mask);
            System.out.println("Iteration " + (i + 1) + ": " + fillCount + " fill(s)");
            if (fillCount == 0) {
                break;
            }
            input = clone(output);
        }

        if (i < maxIter) {
            System.out.println("Success!");
        } else {
            System.out.println("Problem!");
        }

        return output;
    }

    private static float[][] clone(float[][] input) {
        final int nz = input.length;
        float[][] output = new float[nz][];
        for (int i = 0; i < nz; i++) {
            output[i] = input[i].clone();
        }
        return output;
    }

    private static int fillGaps(final int nx, final int ny, final float[][] input, final float[][] output, boolean interpolZ, boolean[] mask) {
        final int nz = input.length;
        int fillCount = 0;
        for (int z1 = 0; z1 < nz; z1++) {
            final int z0 = z1 - 1;
            final int z2 = z1 + 1;
            for (int y1 = 0; y1 < ny; y1++) {
                final int y0 = y1 - 1;
                final int y2 = y1 + 1;
                for (int x1 = 0; x1 < nx; x1++) {
                    final int x0 = x1 - 1;
                    final int x2 = x1 + 1;
                    final int i1 = index(x1, y1, nx);
                    if (Float.isNaN(input[z1][i1]) && (mask == null || mask[i1])) {
                        final float[] nb;
                        if (interpolZ) {
                            nb = new float[]{
                                    getValue(input, mask, x0, y0, z0, nx, ny, nz),
                                    getValue(input, mask, x1, y0, z0, nx, ny, nz),
                                    getValue(input, mask, x2, y0, z0, nx, ny, nz),
                                    getValue(input, mask, x0, y1, z0, nx, ny, nz),
                                    getValue(input, mask, x1, y1, z0, nx, ny, nz),
                                    getValue(input, mask, x2, y1, z0, nx, ny, nz),
                                    getValue(input, mask, x0, y2, z0, nx, ny, nz),
                                    getValue(input, mask, x1, y2, z0, nx, ny, nz),
                                    getValue(input, mask, x2, y2, z0, nx, ny, nz),

                                    getValue(input, mask, x0, y0, z1, nx, ny, nz),
                                    getValue(input, mask, x1, y0, z1, nx, ny, nz),
                                    getValue(input, mask, x2, y0, z1, nx, ny, nz),
                                    getValue(input, mask, x0, y1, z1, nx, ny, nz),
                                    // getValue(input, x1, y1, z1, nx, ny, nz),
                                    getValue(input, mask, x2, y1, z1, nx, ny, nz),
                                    getValue(input, mask, x0, y2, z1, nx, ny, nz),
                                    getValue(input, mask, x1, y2, z1, nx, ny, nz),
                                    getValue(input, mask, x2, y2, z1, nx, ny, nz),

                                    getValue(input, mask, x0, y0, z2, nx, ny, nz),
                                    getValue(input, mask, x1, y0, z2, nx, ny, nz),
                                    getValue(input, mask, x2, y0, z2, nx, ny, nz),
                                    getValue(input, mask, x0, y1, z2, nx, ny, nz),
                                    getValue(input, mask, x1, y1, z2, nx, ny, nz),
                                    getValue(input, mask, x2, y1, z2, nx, ny, nz),
                                    getValue(input, mask, x0, y2, z2, nx, ny, nz),
                                    getValue(input, mask, x1, y2, z2, nx, ny, nz),
                                    getValue(input, mask, x2, y2, z2, nx, ny, nz),
                            };
                        } else {
                            nb = new float[]{
                                    getValue(input, mask, x0, y0, z1, nx, ny, nz),
                                    getValue(input, mask, x1, y0, z1, nx, ny, nz),
                                    getValue(input, mask, x2, y0, z1, nx, ny, nz),
                                    getValue(input, mask, x0, y1, z1, nx, ny, nz),
                                    // getValue(input, x1, y1, z1, nx, ny, nz),
                                    getValue(input, mask, x2, y1, z1, nx, ny, nz),
                                    getValue(input, mask, x0, y2, z1, nx, ny, nz),
                                    getValue(input, mask, x1, y2, z1, nx, ny, nz),
                                    getValue(input, mask, x2, y2, z1, nx, ny, nz),
                            };

                        }

                        float sum = 0.0F;
                        int n = 0;
                        for (float v : nb) {
                            if (!Float.isNaN(v)) {
                                sum += v;
                                n++;
                            }
                        }

                        if (n > 0) {
                            output[z1][i1] = sum / n;
                            fillCount++;
                        } else {
                            output[z1][i1] = input[z1][i1];
                        }
                    }
                }
            }
        }

        return fillCount;
    }

    private static float getValue(float[][] input, boolean[] mask, int x0, int y0, int z0, int nx, int ny, int nz) {
        if (x0 < 0) {
            x0 = nx + x0;
        } else if (x0 >= nx) {
            x0 = nx - x0;
        }
        if (y0 < 0 || y0 >= ny) {
            return Float.NaN;
        }
        if (z0 < 0) {
            z0 = nz + z0;
        } else if (z0 >= nz) {
            z0 = nz - z0;
        }
        final int index = index(x0, y0, nx);
        if (mask == null || mask[index]) {
            return input[z0][index];
        } else {
            return Float.NaN;
        }
    }

    private static int index(int x, int y, int w) {
        return y * w + x;
    }

}
