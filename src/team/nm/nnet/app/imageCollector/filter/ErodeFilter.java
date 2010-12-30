package team.nm.nnet.app.imageCollector.filter;

import java.awt.Point;
import java.util.Iterator;
import java.util.Vector;

import team.nm.nnet.util.Matrix;


public class ErodeFilter extends Morphilogy {

    /**
     * Applies a single iteration of the erode algorithm to the image.
     * 
     * @param binary
     *            The BinaryImage representation of the input image.
     * @param kernel
     *            The matrix representing the kernel.
     * @return The BinaryImage representation of the new eroded image.
     */
    public static BinaryImage erodeSingleIteration(BinaryImage binary, Matrix<Integer> kernel) {
        if((binary == null) || (kernel == null)) {
            return null;
        }
        
        Vector<Point> result = new Vector<Point>();
        Iterator<Point> it;
        Point p;

        it = binary.foregroundEdgePixels.iterator();
        if (!kernelAll1s(kernel)) {
            while (it.hasNext()) {
                p = new Point((Point) it.next());
                if (kernelMatch(p, binary.pixels, binary.width, binary.height, kernel, BinaryImage.BACKGROUND)) {
                    binary.backgroundEdgePixels.add(p);
                    result.add(p);
                }
            }
        } else {

            while (it.hasNext()) {
                p = new Point((Point) it.next());
                binary.backgroundEdgePixels.add(p);
                result.add(p);
            }
        }
        it = result.iterator();
        while (it.hasNext()) {
            binary.removePixel((Point) it.next());
        }
        binary.generateForegroundEdgeFromBackgroundEdge();
        return binary;
    }

    /**
     * Applies several iterations of the erode algorithm to an image.
     * 
     * @param binary
     *            The BinaryImage representation of the input image.
     * @param kernel
     *            The matrix representing the kernel.
     * @param iterations
     *            The number of iterations to be applied.
     * @return The BinaryImage representation of the new eroded image.
     */
    public static BinaryImage filter(BinaryImage binary, Matrix<Integer> kernel,
            int iterations) {
        kernel.set(kernel.getWidth() / 2, kernel.getHeight() / 2, 1);// Ignore centre pixel value in kernel (stops whiteout)
        int i = 0;
        while (i < iterations) {
            binary = erodeSingleIteration(binary, kernel);
            ++i;
        }
        binary.generateBackgroundEdgeFromForegroundEdge();
        return binary;
    }
}
