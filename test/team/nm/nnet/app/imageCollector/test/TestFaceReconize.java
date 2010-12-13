package team.nm.nnet.app.imageCollector.test;

import team.nm.nnet.tmp.NeuralFaceRecognize;

public class TestFaceReconize {
	public static void main(String[] args) {
		NeuralFaceRecognize nfr = new NeuralFaceRecognize("D:\\weight_recog.txt");
		nfr.addTrainFolder("D:\\trainFolder");
		Thread t = new Thread(nfr);
		t.start();
	}
}
