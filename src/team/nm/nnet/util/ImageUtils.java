package team.nm.nnet.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBuffer;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.Kernel;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;

public class ImageUtils {
	
	public static Image toImage(BufferedImage bufferedImage) {
	    return Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
	}
	
	public static BufferedImage toBufferedImage2(Image image) {
		BufferedImage bufferedImage = null;
		if (image != null) {
			bufferedImage = new BufferedImage(image.getWidth(null),
					image.getHeight(null), BufferedImage.TYPE_INT_RGB);
			bufferedImage.createGraphics().drawImage(image, 0, 0, null);
		}
		return bufferedImage;
	}
	
	// This method returns a buffered image with the contents of an image
	public static BufferedImage toBufferedImage(Image image) {
	    if (image instanceof BufferedImage) {
	        return (BufferedImage)image;
	    }

	    // This code ensures that all the pixels in the image are loaded
	    image = new ImageIcon(image).getImage();

	    // Determine if the image has transparent pixels; for this method's
	    // implementation, see Determining If an Image Has Transparent Pixels
	    boolean hasAlpha = hasAlpha(image);

	    // Create a buffered image with a format that's compatible with the screen
	    BufferedImage bimage = null;
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    try {
	        // Determine the type of transparency of the new buffered image
	        int transparency = Transparency.OPAQUE;
	        if (hasAlpha) {
	            transparency = Transparency.BITMASK;
	        }

	        // Create the buffered image
	        GraphicsDevice gs = ge.getDefaultScreenDevice();
	        GraphicsConfiguration gc = gs.getDefaultConfiguration();
	        bimage = gc.createCompatibleImage(
	            image.getWidth(null), image.getHeight(null), transparency);
	    } catch (HeadlessException e) {
	        // The system does not have a screen
	    }

	    if (bimage == null) {
	        // Create a buffered image using the default color model
	        int type = BufferedImage.TYPE_INT_RGB;
	        if (hasAlpha) {
	            type = BufferedImage.TYPE_INT_ARGB;
	        }
	        bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
	    }

	    // Copy image to buffered image
	    Graphics g = bimage.createGraphics();

	    // Paint the image onto the buffered image
	    g.drawImage(image, 0, 0, null);
	    g.dispose();

	    return bimage;
	}
	
	// This method returns true if the specified image has transparent pixels
	public static boolean hasAlpha(Image image) {
	    // If buffered image, the color model is readily available
	    if (image instanceof BufferedImage) {
	        BufferedImage bimage = (BufferedImage)image;
	        return bimage.getColorModel().hasAlpha();
	    }

	    // Use a pixel grabber to retrieve the image's color model;
	    // grabbing a single pixel is usually sufficient
	     PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
	    try {
	        pg.grabPixels();
	    } catch (InterruptedException e) {
	    }

	    // Get the image's color model
	    ColorModel cm = pg.getColorModel();
	    return cm.hasAlpha();
	}

	/**
	 * Load anh tra ve buffred image
	 * @param fileName Duong dan file anh
	 * @return Ket qua load anh
	 */
	public static BufferedImage load(String fileName) {
		File file = new File(fileName);
		Image image = null;
		try {
			image = ImageIO.read(file);
			if (image != null) {
				return toBufferedImage(image);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Resize anh
	 * @param image Anh can resze
	 * @param width Chieu rong can resize
	 * @param height Chieu dai can resize
	 * @return Anh sau khi resize
	 */
	public static BufferedImage resize(BufferedImage image, int width,
			int height) {
		int type = image.getType() != 0 ? image.getType() : 1;
		BufferedImage resizedImage = new BufferedImage(width, height, type);

		Graphics2D g = resizedImage.createGraphics();
		g.setColor(Color.white);
		g.setComposite(AlphaComposite.Src);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();

		return resizedImage;
	}

	/**
	 * Save anh thanh file png
	 * @param bufferedImage Anh can save
	 * @param file Luon file de luu
	 */
	public static void saveToPng(BufferedImage bufferedImage, File file) {
		try {
			ImageIO.write(bufferedImage,"png",file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveToJpg(BufferedImage bufferedImage, File file) {
		try {
			ImageIO.write(bufferedImage,"jpg",file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void drawImageToJpgByteStream(BufferedImage bufferedImage,
			Object stream) {
		try {
			Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");

			ImageWriter writer = (ImageWriter) iter.next();
			ImageWriteParam iwp = writer.getDefaultWriteParam();
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwp.setCompressionQuality(0.90f);

			ImageOutputStream imageOutputStream = ImageIO
					.createImageOutputStream(stream);
			writer.setOutput(imageOutputStream);

			IIOImage image = new IIOImage(bufferedImage, null, null);
			writer.write(null, image, iwp);
			writer.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public static BufferedImage scale(BufferedImage image, int width, int height) {
		int thumbWidth = width <= 0 ? 0x7fffffff : width;
		int thumbHeight = height <= 0 ? 0x7fffffff : height;

		double thumbRatio = (double) thumbWidth / thumbHeight;

		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);

		double imageRatio = (double) imageWidth / imageHeight;

		if (thumbRatio < imageRatio)
			thumbHeight = (int) (thumbWidth / imageRatio);
		else
			thumbWidth = (int) (thumbHeight * imageRatio);
		return resize(image, thumbWidth, thumbHeight);
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static BufferedImage blurImage(BufferedImage image) {
		float ninth = 0.1111111F;
		float blurKernel[] = { ninth, ninth, ninth, ninth, ninth, ninth, ninth,
				ninth, ninth };
		Map map = new HashMap();
		map.put(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		map.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		map.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		RenderingHints hints = new RenderingHints(map);
		BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, blurKernel), 1,
				hints);
		return op.filter(image, null);
	}
	

	public static byte[] clippedAndScale(byte[] imageData, int frameSize) {
		try {
			InputStream in = new ByteArrayInputStream(imageData);
			BufferedImage image = ImageIO.read(in);

			image = clippedAndScale(image, frameSize);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			drawImageToJpgByteStream(image, stream);
			imageData = stream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageData;
	}

	public static BufferedImage clippedAndScale(BufferedImage image,
			int frameSize) {
		try {
			int h = image.getHeight();
			int w = image.getWidth();
			int size = (h < w) ? h : w;
			image = image.getSubimage(0, 0, size, size);
			image = resize(image, frameSize, frameSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public static BufferedImage toYCbCr(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth(null);
        int  height = bufferedImage.getHeight(null);
        Raster raster = bufferedImage.getRaster();
        float[] sample = new float[4];
        BufferedImage yCbCrBufImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                sample = raster.getPixel(i, j, sample);
//                int y  = (int)( 0.257   * sample[0] + 0.504   * sample[1] + 0.098   * sample[2] + 16);
                int cb = (int)( 0.148   * sample[0] - 0.291   * sample[1] + 0.439   * sample[2] + 128);
                int cr = (int)( 0.439   * sample[0] - 0.368   * sample[1] - 0.071   * sample[2] + 128);
                boolean isCr = (140<cr) && (cr<165);
                boolean isCb = (105<cb) && (cb<130);
                if(isCr || isCb) {
                    yCbCrBufImage.setRGB(i, j, 255);
                } else {
                    yCbCrBufImage.setRGB(i, j, 0);
                }
            }
            System.out.println();
        }
        return yCbCrBufImage;
    }

	/**
	 * Chuyen anh anh anh xam
	 * @param bufferedImage Anh can chuyen 
	 * @return Ket qua chuyen
	 */
	public static BufferedImage grayScale(BufferedImage bufferedImage) {
		ImageFilter filter = new GrayFilter(true, 50);
		ImageProducer producer = new FilteredImageSource(
				bufferedImage.getSource(), filter);
		Image image = Toolkit.getDefaultToolkit().createImage(producer);

		BufferedImage grayScaleBuff = new BufferedImage(image.getWidth(null),
				image.getHeight(null), BufferedImage.TYPE_BYTE_GRAY);
		grayScaleBuff.createGraphics().drawImage(image, 0, 0, null);
		return grayScaleBuff;
	}
	
	/**
	 * Chuyen image thanh array
	 * @param bufferedImage Anh can chuyen thanh array
	 * @return Ket qua chuyen
	 */
	public static double[] toArray(BufferedImage bufferedImage) {
		int height = bufferedImage.getHeight(null);
		int width = bufferedImage.getWidth(null);
		double[] matrix = new double[width * height];
		int[] pixels = bufferedImage
				.getRGB(0, 0, width, height, null, 0, width);
		for (int i = 0, lenght = pixels.length; i < lenght; i++) {
			matrix[i] = pixels[i];
		}
		return matrix;
	}

	public static BufferedImage flip(BufferedImage bufferedImage) {
		int width = bufferedImage.getWidth(null);
		int height = bufferedImage.getHeight(null);
		BufferedImage bi = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		DataBuffer buffer1 = bufferedImage.getRaster().getDataBuffer();
		DataBuffer buffer2 = bi.getRaster().getDataBuffer();
		for (int i = buffer1.getSize() - 1, j = 0; i >= 0; --i, j++) {
			buffer2.setElem(j, buffer1.getElem(i));
		}
		return bi;
	}
	
	public static BufferedImage flip(BufferedImage bufferedImage, boolean vertical) {
		int width = bufferedImage.getWidth(null);
		int height = bufferedImage.getHeight(null);
		BufferedImage flipedBuff = null;
		
		if(vertical) {
			flipedBuff = new BufferedImage(width, height, bufferedImage.getType());
			Graphics2D g = flipedBuff.createGraphics();
			g.drawImage(bufferedImage, 0, 0, width, height, 0, height, width, 0, null);
			g.dispose();
		} else {
			flipedBuff = new BufferedImage(width, height, bufferedImage.getType());
			Graphics2D g = flipedBuff.createGraphics();
			g.drawImage(bufferedImage, 0, 0, width, height, width, 0, 0, height, null);
			g.dispose();
		}
		return flipedBuff;
	}
	
	/**
	 * Xoay anh
	 * @param bufferedImage Anh can xoay
	 * @param angle Goc xoay (Tinh bang do)
	 * @return Ket qua xoay
	 */
	public static BufferedImage rotate(BufferedImage bufferedImage, double angle) {
		int width = bufferedImage.getWidth(null);
		int height = bufferedImage.getHeight(null);
		BufferedImage rotatedBuff = new BufferedImage(width, height, bufferedImage.getType());
		Graphics2D g = rotatedBuff.createGraphics();
		g.rotate(Math.toRadians(angle), width / 2.0, height / 2.0);
//		g.drawImage(bufferedImage, null, 0, 0);
		g.drawRenderedImage(bufferedImage, null);
		g.dispose();
		return rotatedBuff;
	}

	public static BufferedImage rotateAndScale(BufferedImage bufferedImage, double angle) {

		int originWidth = bufferedImage.getWidth();
		int originHeight = bufferedImage.getHeight();

		AffineTransform at = new AffineTransform();

		// rotate around image center
		at.rotate(Math.toRadians(angle), originWidth / 2.0,
				originHeight / 2.0);

		/*
		 * translate to make sure the rotation doesn't cut off any image data
		 */
		AffineTransform translationTransform;
		translationTransform = findTranslation(at, bufferedImage);
		at.preConcatenate(translationTransform);

		// instantiate and apply affine transformation filter
		BufferedImageOp bio;
		bio = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		bufferedImage = bio.filter(bufferedImage, null);

		// Keep the origin size
		bufferedImage = resize(bufferedImage, originWidth, originHeight);

		return bufferedImage;
	}

	/*
	 * find proper translations to keep rotated image correctly displayed
	 */
	private static AffineTransform findTranslation(AffineTransform at,
			BufferedImage bi) {
		Point2D p2din, p2dout;

		p2din = new Point2D.Double(0.0, 0.0);
		p2dout = at.transform(p2din, null);
		double ytrans = p2dout.getY();

		p2din = new Point2D.Double(0, bi.getHeight());
		p2dout = at.transform(p2din, null);
		double xtrans = p2dout.getX();

		AffineTransform tat = new AffineTransform();
		tat.translate(-xtrans, -ytrans);
		return tat;
	}
	
	public static void createRandowImage(int width, int height) {
//		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//		image.s
	}
}
