package team.nm.nnet.app.imageCollector.filter;

import team.nm.nnet.util.Matrix;


public class OpenFilter {
    /**
     * Method to open a binary image by eroding and then dilating the image
     * using the specified kernel.
     */
    public static BinaryImage filter(BinaryImage b, Matrix<Integer> kernel, int iterations) {
        b = ErodeFilter.filter(b, kernel, iterations);
        b = DilateFilter.filter(b, kernel, iterations);
        return b;
    }
}
