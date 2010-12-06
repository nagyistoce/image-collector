package team.nm.nnet.app.imageCollector.test;

public class TestFunction {

	public static void main(String[] args) {
		
			System.out.println(getDerivativeTangen(0.5F));
		
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
