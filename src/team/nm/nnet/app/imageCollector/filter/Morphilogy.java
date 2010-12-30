package team.nm.nnet.app.imageCollector.filter;

import java.awt.Point;

import team.nm.nnet.util.Matrix;

public class Morphilogy {

    /**
     * Returns true if the kernel matches the area of image centered on the
     * given point.
     * 
     * @param p
     *            The center point identifying the pixel neighborhood.
     * @param pixels
     *            The 2D array representing the image.
     * @param w
     *            The width of the image.
     * @param h
     *            The height of the image.
     * @param kernel
     *            The matrix representing the kernel.
     * @return True or false (true - the kernel and image match).
     */
    public static boolean kernelMatch(Point p, int[][] pixels, int w, int h,
            Matrix<Integer> kernel, int matchColor) {
        
        int rows = kernel.getWidth() / 2;
        int cols = kernel.getHeight() / 2;
        for (int j = -cols; j <= cols; ++j) {
            for (int i = -rows; i <= rows; ++i) {
                if (kernel.get(i + rows, j + cols) == 1) {
                    if ((p.x + i >= 0) && (p.x + i < w) && (p.y + j >= 0)
                            && (p.y + j < h)) {
                        if (pixels[p.x + i][p.y + j] == matchColor) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns true if the kernel consists of 9 1s.
     * 
     * @param kernel
     *            The array representing the kernel.
     * @return True or false (true - kernel is all 1s).
     */

    public static boolean kernelAll1s(Matrix<Integer> kernel) {
        for (int i = 0, rows = kernel.getWidth(); i < rows; ++i) {
            for (int j = 0, cols = kernel.getHeight(); j <= cols; ++j) {
                if (kernel.get(i, j) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

}
