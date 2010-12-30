package team.nm.nnet.app.imageCollector.filter;

import java.awt.Point;
import java.util.Iterator;
import java.util.Vector;

import team.nm.nnet.util.Matrix;

public class DilateFilter extends Morphilogy {
    /**
     * Takes a BinaryFast representation of an image and a kernel and applies a
     * single iteration of the dilate algorithm.
     * 
     * @param binary
     *            The BinaryImage representation of the input image.
     * @param kernel
     *            The matrix representing the kernel.
     * @return The BinaryImage representation of the new image after dilation.
     */
    public static BinaryImage dilateSingleIteration(BinaryImage binary, Matrix<Integer> kernel) {
        if((binary == null) || (kernel == null)) {
            return null;
        }
        
        Vector<Point> result = new Vector<Point>();
        Iterator<Point> it;
        Point p;

        it = binary.backgroundEdgePixels.iterator();
        if (!kernelAll1s(kernel)) {
            while (it.hasNext()) {
                p = new Point((Point) it.next());
                if (kernelMatch(p, binary.pixels, binary.width, binary.height, kernel, BinaryImage.FOREGROUND)) {

                    binary.foregroundEdgePixels.add(p);
                    result.add(p);
                }
            }
        } else {
            while (it.hasNext()) {
                p = new Point((Point) it.next());
                binary.foregroundEdgePixels.add(p);
                result.add(p);
            }
        }
        it = result.iterator();
        while (it.hasNext()) {
            binary.addPixel((Point) it.next());
        }
        binary.generateBackgroundEdgeFromForegroundEdge();
        return binary;
    }

    /**
     * Takes a BinaryFast image, a kernel and the number of iterations and
     * performs the necessary number of dilations on the image.
     * 
     * @param binary
     *            The BinaryImage representation of the input image.
     * @param kernel
     *            The matrix representing the kernel.
     * @param iterations
     *            The requested number of iterations.
     * @return The BinaryImage representation of the new image after dilation.
     */
    public static BinaryImage filter(BinaryImage binary, Matrix<Integer> kernel,
            int iterations) {
        int i = 0;
        kernel.set(kernel.getWidth() / 2, kernel.getHeight() / 2, 1);// Ignore centre pixel value in kernel (stops whiteout)
        while (i < iterations) {
            binary = dilateSingleIteration(binary, kernel);
            ++i;
        }
        binary.generateForegroundEdgeFromBackgroundEdge();
        return binary;
    }
}
