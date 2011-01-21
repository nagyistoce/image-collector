package team.nm.nnet.app.imageCollector.filter;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Iterator;

import team.nm.nnet.app.imageCollector.utils.ColorSpace;
import team.nm.nnet.util.ImageUtils;

public class BinaryImage {
    
    /**
     * Background is black.
     */
    public static final int BACKGROUND = 0xff000000;
    /**
     * Foreground is white.
     */
    public static final int FOREGROUND = 0xffffffff;
    
    /**
     * Buffer of the binary image
     */
    protected BufferedImage binaryBuffer;
    /**
     * Width of the image.
     */
    protected int width;
    /**
     * Height of the image.
     */
    protected int height;
    /**
     * Size of the image (w*h), number of pixels.
     */
    protected int size;
    /**
     * The 2D array of all pixels.
     */
    protected int[][] pixels;
    /**
     * The hash set storing positions of foreground edge pixels as Points.
     */
    protected HashSet<Point> foregroundEdgePixels = new HashSet<Point>();
    /**
     * The hash set storing positions of background edge pixels as Points.
     */
    protected HashSet<Point> backgroundEdgePixels = new HashSet<Point>();
    
    /** Constructor taking an image and converting it to a BinaryImage image **/
    
    protected void initialize(BufferedImage bufferedImage, boolean isRGB) {
        if (bufferedImage != null) {
            width = bufferedImage.getWidth(null);
            height = bufferedImage.getHeight(null);
            size = width * height;
            pixels = new int[width][height];
            int[] array1D = new int[size];
            if(isRGB) {
            	binaryBuffer = ColorSpace.toYCbCr(bufferedImage);
            	array1D = binaryBuffer.getRGB(0, 0, width, height, null, 0, width).clone();
            } else {
                array1D = bufferedImage.getRGB(0, 0, width, height, null, 0, width).clone();
            }
            
            int[][] array2D = new int[width][height];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    array2D[i][j] = array1D[(j * width) + i];
                }
            }
            pixels = array2D;
        }
        generateForegroundEdge();
        generateBackgroundEdgeFromForegroundEdge();
    }
    
    /**
     * Covert image in RGB model to binary image
     */
    public BinaryImage(Image image) {
        if(image != null) {
            BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
            initialize(bufferedImage, true);
        }
    }

    /**
     * Covert image in RGB model to binary image
     */
    public BinaryImage(BufferedImage bufferedImage, boolean isRGB) {
        initialize(bufferedImage, isRGB);
    }
    
    /**
     * Removes a foreground pixel from the 2D array by setting its value to
     * background.
     * 
     * @param p
     *            The point to be removed.
     */
    public void removePixel(Point p) {
        pixels[p.x][p.y] = BACKGROUND;
    }

    /**
     * Adds a foreground pixel to the 2D array by setting its value to
     * foreground.
     * 
     * @param p
     *            The point to be added.
     */
    public void addPixel(Point p) {
        pixels[p.x][p.y] = FOREGROUND;
    }

    /**
     * Converts the 2D array into a 1D array of pixel values.
     * 
     * @return The 1D array of pixel values.
     */
    public int[] convertToArray() {
        int[] p = new int[size];
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                p[(j * width) + i] = pixels[i][j];
            }
        }
        return p;
    }

    /**
     * Generates a new 2D array of pixels from a hash set of foreground pixels.
     * 
     * @param pix
     *            The hash set of foreground pixels.
     */
    public void generatePixels(HashSet<Point> pix) {
        // Reset all pixels to background
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                pixels[i][j] = BACKGROUND;
            }
        }
        convertToPixels(pix);
    }

    /**
     * Adds the pixels from a hash set to the 2D array of pixels.
     * 
     * @param pix
     *            The hash set of foreground pixels to be added.
     */
    public void convertToPixels(HashSet<Point> pix) {
        Point p = new Point();
        Iterator<Point> it = pix.iterator();
        while (it.hasNext()) {
            p = (Point) it.next();
            pixels[p.x][p.y] = FOREGROUND;
        }
    }

    /**
     * Generates the foreground edge hash set from the 2D array of pixels.
     */
    public void generateForegroundEdge() {
        foregroundEdgePixels.clear();
        Point p;
        for (int n = 0; n < height; ++n) {
            for (int m = 0; m < width; ++m) {
                if (pixels[m][n] == FOREGROUND) {
                    p = new Point(m, n);
                    for (int j = -1; j < 2; ++j) {
                        for (int i = -1; i < 2; ++i) {
                            if ((p.x + i >= 0) && (p.x + i < width)
                                    && (p.y + j >= 0) && (p.y + j < height)) {
                                if ((pixels[p.x + i][p.y + j] == BACKGROUND)
                                        && (!foregroundEdgePixels.contains(p))) {
                                    foregroundEdgePixels.add(p);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Generates the background edge hash set from the foreground edge hash set
     * and the 2D array of pixels.
     */
    public void generateBackgroundEdgeFromForegroundEdge() {
        backgroundEdgePixels.clear();
        Point p, p2;
        Iterator<Point> it = foregroundEdgePixels.iterator();
        while (it.hasNext()) {
            p = new Point((Point) it.next());
            for (int j = -1; j < 2; ++j) {
                for (int i = -1; i < 2; ++i) {
                    if ((p.x + i >= 0) && (p.x + i < width) && (p.y + j >= 0)
                            && (p.y + j < height)) {
                        p2 = new Point(p.x + i, p.y + j);
                        if (pixels[p2.x][p2.y] == BACKGROUND) {
                            backgroundEdgePixels.add(p2);
                        }
                    }
                }
            }
        }
    }

    /**
     * Generates the foreground edge hash set from the background edge hash set
     * and the 2D array of pixels.
     */
    public void generateForegroundEdgeFromBackgroundEdge() {
        foregroundEdgePixels.clear();
        Point p, p2;
        Iterator<Point> it = backgroundEdgePixels.iterator();
        while (it.hasNext()) {
            p = new Point((Point) it.next());
            for (int j = -1; j < 2; ++j) {
                for (int i = -1; i < 2; ++i) {
                    if ((p.x + i >= 0) && (p.x + i < width) && (p.y + j >= 0)
                            && (p.y + j < height)) {
                        p2 = new Point(p.x + i, p.y + j);
                        if (pixels[p2.x][p2.y] == FOREGROUND) {
                            foregroundEdgePixels.add(p2);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the int [] values of the Binary image
     * 
     * @return int[] the array of the image
     */
    public int[] getValues() {
        int[] values1D = new int[size];
        int[] graylevel = new int[size];
        values1D = convertToArray();
        for (int i = 0; i < size; i++) {
            graylevel[i] = values1D[i];
        }
        return graylevel;
    }

    public BufferedImage getBinaryBuffer() {
        if(pixels == null) {
            return null;
        }
        BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        buffer.setRGB(0, 0, width, height, getValues(), 0, width);
        return buffer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSize() {
        return size;
    }

    public int[][] getPixels() {
        return pixels;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        initialize(bufferedImage, true);
    }

	public void setBinaryBuffer(BufferedImage binaryBuffer) {
		initialize(binaryBuffer, false);
	}
}
