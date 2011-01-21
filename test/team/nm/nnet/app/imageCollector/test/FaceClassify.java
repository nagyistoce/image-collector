package team.nm.nnet.app.imageCollector.test;

import java.awt.image.BufferedImage;
import java.util.List;

import team.nm.nnet.core.NeuralNetwork;
import team.nm.nnet.util.IOUtils;
import team.nm.nnet.util.ImageProcess;

public class FaceClassify {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NeuralNetwork nn = new NeuralNetwork("D:\\weight.txt");
		List<String> listFaces = IOUtils.listFileName("D:\\Classify\\faces");
		for (int i = 0; i < listFaces.size(); i ++) {
			BufferedImage image = ImageProcess.load("D:\\Classify\\faces\\" + listFaces.get(i));
			nn.addFace(image);
		}
		List<String> listNoneFaces = IOUtils.listFileName("D:\\Classify\\nonFaces");
		for (int i = 0; i < listFaces.size(); i ++) {
			BufferedImage image = ImageProcess.load("D:\\Classify\\nonFaces\\" + listNoneFaces.get(i));
			nn.addNoneFace(image);
		}
		Thread t = new Thread(nn);
		t.start();
	}

}
