package team.nm.nnet.app.imageCollector.test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import team.nm.nnet.core.Const;
import team.nm.nnet.core.ControlFaceClassify;
import team.nm.nnet.core.FaceClassify;
import team.nm.nnet.util.IOUtils;
import team.nm.nnet.util.ImageUtils;

public class TestNeuralNetwork{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("Begin time: " + System.currentTimeMillis());
		
		String path = System.getProperty("user.dir");
		String root = path;
		path += "\\ref\\imageStore";
		String pathFace = path + "\\faces";
		String pathNonFace = path + "\\nonFaces";
		System.out.println(path);
		List<String> listFace = IOUtils.listFileName(pathFace);
		List<String> listNonFace = IOUtils.listFileName(pathNonFace);
		System.out.println("Danh sach cac face la:");
		for (String str : listFace) {
			System.out.println(str);
		}
		System.out.println("Danh sach cac non face la:");
		for (String str : listNonFace) {
			System.out.println(str);
		}
		//--------------------------------------------
		FaceClassify fc = new FaceClassify();
		List<double[]> listFaceTrain = new ArrayList<double[]>();
		for (String str : listFace) {
			BufferedImage bi = ImageUtils.load(pathFace + "\\" + str);
			bi = ImageUtils.grayScale(bi);
			bi = ImageUtils.resize(bi, Const.FACE_WIDTH, Const.FACE_HEIGHT);
			double[] inputArray = ImageUtils.toArray(bi);
			listFaceTrain.add(inputArray);
			System.out.println("Face " + str);
			
		}
		fc.addFacesToTrain(listFaceTrain);
		
		List<double[]> listNonFaceTrain = new ArrayList<double[]>();
		for (String str : listNonFace) {
			BufferedImage bi = ImageUtils.load(pathNonFace + "\\" + str);
			bi = ImageUtils.grayScale(bi);
			bi = ImageUtils.resize(bi, Const.FACE_WIDTH, Const.FACE_HEIGHT);
			double[] inputArray = ImageUtils.toArray(bi);
			listNonFaceTrain.add(inputArray);
			System.out.println("Non-Face " + str);
		}
		fc.addNonFaceToTrain(listNonFaceTrain);
		
		
		ControlFaceClassify cf = new ControlFaceClassify(fc, root + "\\ref\\outputNetwork\\output.nnet");
		
		System.out.println("Finish time: " + System.currentTimeMillis());
	}

}
