package team.nm.nnet.app.imageCollector.test;

import java.awt.image.BufferedImage;

import team.nm.nnet.core.NeuralFaceRecognize;
import team.nm.nnet.util.ImageProcess;

public class TestFaceReconize {
	public static void main(String[] args) {
		
//		NeuralFaceRecognize nfr = new NeuralFaceRecognize("D:\\weight_recog.txt");
//		nfr.addTrainFolder("D:\\trainFolder");
//		Thread t = new Thread(nfr);
//		t.start();
		
		
		NeuralFaceRecognize nfr = new NeuralFaceRecognize("");
		nfr.loadWeight("D:\\weight_recog.txt");
		BufferedImage image = ImageProcess.load("D:\\0.jpg");
		int index = nfr.gfncGetWinner(image);
		System.out.println(index);
		
		image = ImageProcess.load("D:\\1.jpg");
		index = nfr.gfncGetWinner(image);
		System.out.println(index);
//		
//		image = ImageProcess.load("D:\\2.jpg");
//		index = nfr.gfncGetWinner(image);
//		System.out.println(index);
//		
//		image = ImageProcess.load("D:\\3.jpg");
//		index = nfr.gfncGetWinner(image);
//		System.out.println(index);
		
	}
}
