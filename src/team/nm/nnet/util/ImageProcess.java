package team.nm.nnet.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * Lớp dùng để xử lý ảnh tạm thời không dùng lớp của chí mừng
 * @author MinhNhat
 *
 */
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
	 * Resize anh theo phan tram
	 * @param image Anh can resize
	 * @param percent Phan tram can resize
	 * @return Ket qua resize
	 */
	public static BufferedImage resize(BufferedImage image, int percent) {
		int width = image.getWidth();
		int height = image.getHeight();
		int newWidth = percent * width / 100;
		int newHeight = percent * height / 100;
		return ImageProcess.resize(image, newWidth, newHeight);
	}
	
	/**
	 * Chiểu ảnh thành mảng ma tran
	 * @param image Ảnh cần chuyển
	 * @return Kết quả chuyển
	 */
	public static Matrix<Double> imageToMatrix(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Raster raster = image.getRaster();
        Double[][] r = new Double[height][width];
        float[] sample = new float[3];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sample = raster.getPixel(j, i, sample);
                r[i][j] = new Double((sample[0] + sample[1] + sample[2]) / 3);
            }
        }
        Matrix<Double> result = new Matrix<Double>(width, height, r);
        return result;
    }
	
	/**
	 * Chuyển matran thành mảng 
	 * @param matrix Matran cần chuyển
	 * @return Kết quả chuyển
	 */
	public static double[] matrixToArray(Matrix<Double> matrix) {
        Double[][] a = matrix.getValues();
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        double[] result = new double[height * width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                result[i * width + j] = a[i][j];
            }
        }
        return result;

    }
	
	/**
	 * Hàm chuẩn hóa gi tri matran phù hợp với mạng neural
	 * @param matrix Matran cần chuẩn hóa
	 * @return Kết quả chuẩn hóa
	 */
	public static Matrix<Double> nomalizeMatrix(Matrix<Double> matrix) {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		Double[][] result = new Double[height][width]; 
		for (int i = 0; i < height; i ++) {
			for (int j = 0; j < width; j ++) {
				if (matrix.getValues()[i][j] > 127) {
					result[i][j] = new Double(0);
				}
				else {
					result[i][j] = new Double(1);
				}
			}
		}
		return new Matrix<Double>(width, height, result);
	}
	
	/**
	 * Gan mash cho anh. Phan mau den cua mask se duoc to mau den trong anh.
	 * @param image Anh can gan mask
	 * @param mask Mask gan cho anh
	 * @return Ket qua
	 */
	public static BufferedImage maskForImage(BufferedImage image, BufferedImage mask) {
		Raster raster = mask.getRaster();
		int width = mask.getWidth();
		int height = mask.getHeight();
		for (int i = 0; i < height; i ++) {
			for (int j = 0; j < width; j ++) {
				double[] value = new double[3];
				value = raster.getPixel(j, i, value);
				if ((value[0] + value[1] + value[2] < 150)) {
					image.setRGB(j, i, 0);
				}
			}
		}
		return image;
	}
	
	/**
	 * Chuyen anh thanh array
	 * @param image Anh can chuyen
	 * @return Ket qua chuyen
	 */
	public static double[] imageToArryay(BufferedImage image) {
		Matrix<Double> matrix = imageToMatrix(image);
		double[] result = matrixToArray(matrix);
		return result;
	}
	
	/**
	 * Chuan hoa gia tri nhap vao cho phu hop voi mang neural;
	 * @return
	 */
	public static double[] adapArray(double[] input) {
		int len = input.length;
		double[] result = new double[len];
		for (int i = 0; i < len; i ++) {
			if (input[i] == 127) {
				result[i] = 0;
			}
			else {
				if (input[i] > 127) {
					result[i] = (input[i] - 127) / 127;
				}
				else {
					result[i] = -input[i] / 127;
				}
			}
		}
		return result;
	}
	
	public static BufferedImage createRandomImage(int width, int height) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Random random = new Random();
		for (int i = 0; i < height; i ++) {
			for (int j = 0; j < width; j ++) {
				
				int r = random.nextInt(255);
				int g = random.nextInt(255);
				int b =random.nextInt(255);
				Color color = new Color(r, g, b);
				bi.setRGB(j, i, color.getRGB());
			}
		}
		return bi;
	}
	
	public static BufferedImage createHorizoneRandomImage(int width, int height) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Random rand = new Random();
		for (int i = 0; i < width; i ++) {
			int r = rand.nextInt(255);
			int g = rand.nextInt(255);
			int b = rand.nextInt(255);
			Color color = new Color(r, g, b);
			for (int j = 0; j < height; j ++) {
				bi.setRGB(i, j, color.getRGB());
			}
		}
		return bi;
	}
	
	public static BufferedImage createVerticalRandomImage(int width, int height) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Random rand = new Random();
		for (int i = 0; i < height; i ++) {
			int r = rand.nextInt(255);
			int g = rand.nextInt(255);
			int b = rand.nextInt(255);
			Color color = new Color(r, g, b);
			for (int j = 0; j < width; j ++) {
				bi.setRGB(j, i, color.getRGB());
			}
		}
		return bi;
	}
	
	public static void createAllColorImage(int width, int height, int numOfImage) {
		Random random = new Random(); 
		for (int i = 0; i < numOfImage; i ++) {
			int r = random.nextInt(255);
			int g = random.nextInt(255);
			int b = random.nextInt(255);
			Color color = new Color(r,g,b);
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			for (int w = 0; w < width; w ++) {
				for (int h = 0; h < height; h ++) {
					bi.setRGB(w, h, color.getRGB());
				}
			}
			ImageUtils.saveToJpg(bi, new File("D:\\random" + i + ".jpg"));
		}
	}
	
	public static void main(String[] args ) {
		BufferedImage bi = createHorizoneRandomImage(20, 30);
		ImageUtils.saveToJpg(bi, new File("D:/random51.jpg"));
		bi = createVerticalRandomImage(20, 30);
		ImageUtils.saveToJpg(bi, new File("D:/random52.jpg"));
		bi = createRandomImage(20, 30);
		ImageUtils.saveToJpg(bi, new File("D:/random53.jpg"));
		createAllColorImage(20, 30, 50);
		System.out.println("finished");
	}
	

}
