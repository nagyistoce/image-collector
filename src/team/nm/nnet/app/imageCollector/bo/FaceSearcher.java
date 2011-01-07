package team.nm.nnet.app.imageCollector.bo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Required;

import team.nm.nnet.app.imageCollector.om.DetectedFace;
import team.nm.nnet.app.imageCollector.om.FaceList;
import team.nm.nnet.core.Const;
import team.nm.nnet.tmp.NeuralFaceRecognize;

public class FaceSearcher extends FaceList {

	static ExecutorService executorService = Executors.newCachedThreadPool();
	
	private volatile boolean state = false;
	private volatile int addingThreads;
	private volatile int completedAddingThreads;
	private int completedFile, sumFaces;
	
	private NeuralFaceRecognize neuralRecognition;
	private FaceDetector faceDetector;
	private FaceList faceResults;
	
	private List<File> files;
	private List<String> expectedOutput;
	private int currentProcessId;
	

	public FaceSearcher () {
		neuralRecognition = new NeuralFaceRecognize("");
		neuralRecognition.loadWeight(Const.CURRENT_DIRECTORY + "/src/weight_recog.txt");
	}
	
	public void prepare() {
		addingThreads = Integer.MAX_VALUE;
		completedAddingThreads = 0;
		completedFile = 0;
		sumFaces = 0;
		currentProcessId = -1;
		clear();
	}

	public void search(List<File> files, List<String> expectedOutput) {
		this.files = files;
		this.expectedOutput = expectedOutput;
		if((files == null) || (files.size() < 1)) {
			return;
		}
		
		faceDetector.setFaceResults(this);
		for(int i = 0; i <2; i++) {
			nextProcessing();
		}
	}
	
	public void nextProcessing() {
		if(++currentProcessId >= files.size()) {
			return;
		}
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				faceDetector.detect(files.get(currentProcessId));
			}
		});
	}
	
	@Override
	public void onAddingFace(DetectedFace face) {
		faceResults.addFace(face);
		
		/*BufferedImage bufImg = face.getBufferedImage();
		int index = neuralRecognition.gfncGetWinner(bufImg);
		if(!state) {
			return;
		}
		if(index > 0) {
			if(expectedOutput.contains(index)) {
				faceResults.addFace(face);
			}
		}
		*/
		if(++completedAddingThreads >= addingThreads) {
			faceResults.onFulfiling();
		}
	}

	@Override
	public void onFulfiling() {
		nextProcessing();
		if(++completedFile == files.size()) {
			addingThreads = sumFaces;
		}
		sumFaces += getSize();
		if(completedAddingThreads >= addingThreads) {
			faceResults.onFulfiling();
		}
	}
	
	public void requestStop() {
		state = false;
        faceDetector.requestStop();
    }
	
	public FaceDetector getFaceDetector() {
		return faceDetector;
	}

	@Required
	public void setFaceDetector(FaceDetector faceDetector) {
		this.faceDetector = faceDetector;
	}

	public FaceList getFaceResults() {
		return faceResults;
	}

	public void setFaceResults(FaceList faceResults) {
		this.faceResults = faceResults;
	}
}
