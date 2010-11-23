package team.nm.nnet.app.imageCollector.test;

import java.awt.image.BufferedImage;
import java.util.List;

import org.springframework.web.jsf.FacesContextUtils;

import team.nm.nnet.core.NeuralFaceClassify;
import team.nm.nnet.util.IOUtils;
import team.nm.nnet.util.ImageProcess;

public class TestFaceClassify {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String sysPath = System.getProperty("user.dir");
		String facePath = sysPath + "/" + "ref/imageStore/faces";
		String noneFacePath = sysPath + "/" + "ref/imageStore/nonFaces";
		String facePathTest = sysPath + "ref/imageStore/testFaces";
		String noneFacePathTest = sysPath + "ref/imageStore/testFaces";
		List<String> listFace = IOUtils.listFileName(facePath);
		List<String> listNoneFace = IOUtils.listFileName(noneFacePath);
		//List<String> listFaceTest = IOUtils.listFileName(facePathTest);
		//List<String> listNoneFaceTest = IOUtils.listFileName(noneFacePathTest);
		NeuralFaceClassify nfc = new NeuralFaceClassify("D:/weight.txt");
		for (int i = 0; i < listFace.size(); i ++) {
			BufferedImage image = ImageProcess.load(facePath + "/" + listFace.get(i));
			nfc.addFaceTrain(image);
			System.out.println("Add face: " + listFace.get(i));
		}
		
		for (int i = 0; i < listNoneFace.size(); i ++) {
			BufferedImage image = ImageProcess.load(noneFacePath + "/" + listNoneFace.get(i));
			nfc.addNoneFaceTrain(image);
			System.out.println("Add none face: " + listNoneFace.get(i));
		}
		
		Thread t = new Thread(nfc);
		t.start();
	}

}
