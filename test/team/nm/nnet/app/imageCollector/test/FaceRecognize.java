package team.nm.nnet.app.imageCollector.test;

import team.nm.nnet.core.NeuralFaceRecognize;

public class FaceRecognize {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NeuralFaceRecognize nfr = new NeuralFaceRecognize("D:\\weight_recog.txt");
		nfr.addTrainFolder("D:\\trainFolder");
		Thread t = new Thread(nfr);
		t.start();

	}

}
