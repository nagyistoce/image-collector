package team.nm.nnet.app.imageCollector.filter;

import team.nm.nnet.util.Matrix;

public class CloseFilter {
	public static BinaryImage filter(BinaryImage b, Matrix<Integer> kernel, int iterations) {
        b = DilateFilter.filter(b, kernel, iterations);
        b = ErodeFilter.filter(b, kernel, iterations);
        return b;
    }
}
