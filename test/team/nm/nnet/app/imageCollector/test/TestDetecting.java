package team.nm.nnet.app.imageCollector.test;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;

import team.nm.nnet.core.Const;
import team.nm.nnet.core.NeuralNetwork;
import team.nm.nnet.util.ImageUtils;

public class TestDetecting extends TestBase  {

	public static void main(String[] args) {
		String filename = "D:/Images/change/camera.png";
        BufferedImage bufferedImage = ImageUtils.load(filename);

        NeuralNetwork nn = new NeuralNetwork(Const.CURRENT_DIRECTORY + "/src/weight.txt");
        nn.loadWeight(Const.CURRENT_DIRECTORY + "/src/weight.txt");
        float outVal = nn.gfncGetWinner(bufferedImage);
        
        showImage("RGB", bufferedImage);
        System.out.println(outVal + "\nFinish!");
	}
	
	static BufferedImage toYCbCr(BufferedImage bufferedImage) {
		int width = bufferedImage.getWidth(null);
		int  height = bufferedImage.getHeight(null);
		Raster raster = bufferedImage.getRaster();
        float[] sample = new float[3];
        BufferedImage yCbCrBufImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sample = raster.getPixel(j, i, sample);
                int y  = (int)( 0.257   * sample[0] + 0.504   * sample[1] + 0.098   * sample[2] + 16);
        		int cb = (int)( 0.148   * sample[0] - 0.291   * sample[1] + 0.439   * sample[2] + 128);
        		int cr = (int)( 0.439   * sample[0] - 0.368   * sample[1] - 0.071   * sample[2] + 128);
        		boolean isCr = (140<cr) && (cr<165);
        		boolean isCb = (105<cb) && (cb<130);
        		if( isCr || isCb) {
        			yCbCrBufImage.setRGB(j, i, 255);
        		} else {
        			yCbCrBufImage.setRGB(j, i, 0);
        		}
            }
        }
		return yCbCrBufImage;
	}
}
