package team.nm.nnet.app.imageCollector.utils;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;

import javax.swing.GrayFilter;

import sole.hawking.image.filter.DilateFilter;
import sole.hawking.image.filter.ErodeFilter;
import sole.hawking.image.filter.MedianFilter;
import team.nm.nnet.util.Matrix;

public class ColorSpace {

	/**
	 * Chuyen anh anh anh xam
	 * @param bufferedImage Anh can chuyen 
	 * @return Ket qua chuyen
	 */
	public static BufferedImage toGrayScale(BufferedImage bufferedImage) {
		ImageFilter filter = new GrayFilter(true, 50);
		ImageProducer producer = new FilteredImageSource(
				bufferedImage.getSource(), filter);
		Image image = Toolkit.getDefaultToolkit().createImage(producer);

		BufferedImage grayScaleBuff = new BufferedImage(image.getWidth(null),
				image.getHeight(null), BufferedImage.TYPE_BYTE_GRAY);
		grayScaleBuff.createGraphics().drawImage(image, 0, 0, null);
		return grayScaleBuff;
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
                if( isCr || isCb) {
                    yCbCrBufImage.setRGB(i, j, 0xffffffff);
                } else {
                    yCbCrBufImage.setRGB(i, j, 0);
                }
            }
        }
        return yCbCrBufImage;
    }
	
	public static Matrix<Integer> getIntegralMatrix(BufferedImage bufferedImage) {
		BufferedImage yCbCrBuf = toYCbCr(bufferedImage);
        int width = yCbCrBuf.getWidth(null);
        int  height = yCbCrBuf.getHeight(null);
        Integer[][] integral = new Integer[width][height];
        Color pixelColour = new Color(yCbCrBuf.getRGB(0, 0));
        integral[0][0] = pixelColour.getBlue() / 255;
        for(int i = 1; i < width; i++) {
            pixelColour = new Color(yCbCrBuf.getRGB(i, 0));
            integral[i][0] = integral[i - 1][0] + (pixelColour.getBlue() / 255);
        }
        for(int j = 1; j < height; j++) {
            pixelColour = new Color(yCbCrBuf.getRGB(0, j));
            integral[0][j] = integral[0][j - 1] + (pixelColour.getBlue() / 255);
        }
        for (int i = 1; i < width; i++) {
            for (int j = 1; j < height; j++) {
                pixelColour = new Color(yCbCrBuf.getRGB(i, j));
                integral[i][j] = integral[i - 1][j] + integral[i][j - 1] - integral[i - 1][j - 1] + (pixelColour.getBlue() / 255);
            }
        }
        return new Matrix<Integer>(width, height, integral);
    }
	
	/**
	 * Hệ màu RGB sẽ được chuẩn hóa theo công thức:
	 * 	* rr = r / (r + g + b)
	 *  * gg = g / (r + g + b)
	 *  * bb = b / (r + g + b)
	 *  Trong quá trình chuyển đổi, giá trị trung bình (mean) và độ lệch tiêu chuẩn (standard deviation)
	 *  của <i>rr</i> và <i>gg</i> sẽ được tính toán và trị của chúng sẽ được gán lần lượt cho <i>mr</i>, <i>sr</i>, <i>mg</i> và <i>sg</i>.
	 *  Nếu tại 1 pixel thỏa:
	 *   * mr - a*sr < rr < mr + a*sr, và
	 *   * mg - a*sg < gg < mg + a*sg, với <i>a</i> là hằng số có trị 1.5 được chọn do kinh nghiệm - 
	 *  thì pixel này được xem là 1 điểm trên khuôn mặt (skin pixel)
	 * @param bufferedImage bộ nhớ ảnh cần chuẩn hóa
	 * @return bộ nhớ ảnh dưới dạng nhị phân (binary image) gồm các vùng thể hiện cho khuôn mặt và phần nền.
	 */
	public static BufferedImage normalizeRGB(BufferedImage bufferedImage) {
		int width = bufferedImage.getWidth(null);
        int  height = bufferedImage.getHeight(null);
        BufferedImage normalRGBBuf = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        Color pixelColour;
        // Tính giá trị trung bình của r và g
        double mr = 0, mg = 0;
        for(int i=0; i<width; i++) {
        	for(int j=0; j<height; j++) {
        		pixelColour = new Color(bufferedImage.getRGB(i, j));
        		int sum = pixelColour.getRed() + pixelColour.getGreen() + pixelColour.getBlue();
        		mr += ((double) pixelColour.getRed() / sum);
        		mg += ((double) pixelColour.getGreen() / sum);
        	}
        }
        mr /= (width * height);
        mg /= (width * height);
        
        // Tính độ lệch tiêu chuẩn của r và g
        double sr = 0, sg = 0;
        for(int i=0; i<width; i++) {
        	for(int j=0; j<height; j++) {
        		pixelColour = new Color(bufferedImage.getRGB(i, j));
        		int sum = pixelColour.getRed() + pixelColour.getGreen() + pixelColour.getBlue();
        		double rr = ((double) pixelColour.getRed() / sum);
        		double gg = ((double) pixelColour.getGreen() / sum);
        		sr += Math.pow(rr - mr, 2);
        		sg += Math.pow(gg - mg, 2);
        	}
        }
        sr = Math.sqrt(sr / (width * height - 1));
        sg = Math.sqrt(sg / (width * height - 1));
        
        // Xác định skin pixel và non-skin pixel
        double anpha = 1.5;
        for(int i=0; i<width; i++) {
        	for(int j=0; j<height; j++) {
        		pixelColour = new Color(bufferedImage.getRGB(i, j));
        		int sum = pixelColour.getRed() + pixelColour.getGreen() + pixelColour.getBlue();
        		double rr = ((double) pixelColour.getRed() / sum);
        		double gg = ((double) pixelColour.getGreen() / sum);
        		boolean isR = ((mr - anpha*sr) < rr) && (rr < (mr + anpha*sr));
        		boolean isG = ((mg - anpha*sg) < gg) && (gg < (mg + anpha*sg));
        		
        		if(isR && isG) {
        			normalRGBBuf.setRGB(i, j, 0xffffffff);
        		} else {
        			normalRGBBuf.setRGB(i, j, 0);
        		}
        	}
        }
        
        MedianFilter medianFilter = new MedianFilter();
        normalRGBBuf = medianFilter.filter(normalRGBBuf, null);
        ErodeFilter erodeFilter = new ErodeFilter();
        erodeFilter.setThreshold(9);
        normalRGBBuf = erodeFilter.filter(normalRGBBuf, null);
        DilateFilter dilateFilter = new DilateFilter();
		dilateFilter.setThreshold(10);
		normalRGBBuf = dilateFilter.filter(normalRGBBuf, null);
        return normalRGBBuf;
	}
}
