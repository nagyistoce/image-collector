package team.nm.nnet.tmp;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;

import javax.imageio.ImageIO;

import team.nm.nnet.core.Const;

public class ImageProcess {
	
	/**
	 * Load ảnh từ file thành buffer image
	 * @param filename Đường dẫn tới file ảnh
	 * @return Kết quả loag được
	 */
	public static BufferedImage load(String filename) {
		File file = new File(filename);
		BufferedImage bufferedImage = null;
		Image image;
		try {
			image = ImageIO.read(file);
			if (image != null) {
				bufferedImage = new BufferedImage(image.getWidth(null),
						image.getHeight(null), BufferedImage.TYPE_INT_RGB);
				bufferedImage.createGraphics().drawImage(image, 0, 0, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bufferedImage;
	}
	
	/**
	 * Resize ảnh theo một kích thước 
	 * @param image Ảnh cần resize
	 * @param width Chiều rộng cần resize
	 * @param height Chiều dài cần resize
	 * @return Kết quả resize
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
	 * Chuyen anh thanh mang mot chieu
	 * @param image Anh can chuyen
	 * @return Ket qua chuyen
	 */
	public static float[] imageToArray(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		Raster raster = image.getRaster();
		float[] sample = new float[3];
		float[] result = new float[width * height];
		for (int i = 0; i < height; i ++) {
			for (int j = 0; j < width; j ++) {
				sample = raster.getPixel(j, i, sample);
				result[i * width + j] = (sample[0] + sample[1] + sample[2]) / 3;
			}
		}
		return result;
	}
	
	/**
	 * Chuan hoa mang dua vao cho phu hop voi neural network
	 * @param array Mang can chuan hoa
	 * @return Ket qua chuan hoa
	 */
	public static float[] adaptArray(float[] array) {
		int len = array.length;
		float[] result = new float[len];
		for (int i = 0; i < len; i ++) {
			 result[i] = array[i] / 255;
		}
		result = addMask(result);
		return result;
	}
	
	/**
	 * Gan mask cho array trong qua trinh chuan hoa
	 * @param array Mang can chuan hoa
	 * @return Ket qua chuan hoa
	 */
	private static float[] addMask(float[] array) {
		int len = array.length;
		int[] mask = Const.MASK;
		for (int i = 0; i < len; i ++) {
			if (mask[i] == 1) {
				array[i] = 0;
			}
		}
		return array;
	}
	
	/**
	 * Create folder
	 * @param folderName Name of folder 
	 */
	public static void createFolder(String folderName) {
		File file = new File(folderName);
		file.mkdir();
	}
}
