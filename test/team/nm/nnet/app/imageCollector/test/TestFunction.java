package team.nm.nnet.app.imageCollector.test;

import java.awt.image.BufferedImage;

import team.nm.nnet.tmp.ImageProcess;

public class TestFunction {

	public static void main(String[] args) {
		BufferedImage bi = ImageProcess.load("D:\\nhat.jpg");
		bi = ImageProcess.resize(bi, 20, 30);
		float[] d = ImageProcess.imageToArray(bi);
		d = ImageProcess.adaptArray(d);
		for (int i = 0; i < 600; i ++) {
			System.out.println(d[i]);
		}
		
		
	}
	
	public static double getDerivativeTangen(double d) {
		double result = 0.5F * (1 - Math.pow(d, 2));
		return result;
	}
	
	public static double getTangen(double d) {
		double result = ((2 / (1 + Math.exp(-1 * 0.014F * d))) - 1);
		return result;
	}
	
	 public static double sigmoid(float f_net)
     {
         //float result=(float)(1/(1+Math.Exp (-1*slope*f_net)));		//Unipolar
         double result = ((2 / (1 + Math.exp(-1 * 0.014F * f_net))) - 1);		//Bipolar			
         return result;
     }
}
