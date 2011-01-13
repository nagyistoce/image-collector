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
import team.nm.nnet.core.LearnFace;
import team.nm.nnet.core.NeuralFaceRecognize;

public class FaceSearcher extends FaceList {

	static ExecutorService executorService = Executors.newCachedThreadPool();
	
	private volatile boolean state = false;
	private volatile int addingThreads;
	private volatile int currentProcessId;
	private volatile int completedAddingThreads;
	
	private NeuralFaceRecognize neuralRecognition;
	private LearnFace learnFace;
	private FaceDetector faceDetector;
	private FaceList faceResults;
	
	private List<File> files;
	private List<Integer> expectedOutput;
	

	public FaceSearcher () {
		neuralRecognition = new NeuralFaceRecognize("");
		neuralRecognition.loadWeight(Const.CURRENT_DIRECTORY + "/src/weight_recog.txt");
		learnFace = LearnFace.getInstance();
	}
	
	public void prepare() {
		addingThreads = Integer.MAX_VALUE;
		completedAddingThreads = 0;
		currentProcessId = -1;
		clear();
	}

	public void search(List<File> files, List<Integer> expectedOutput) {
		if((files == null) || (files.size() < 1)) {
			return;
		}
		this.files = files;
		this.expectedOutput = expectedOutput;
		
		state = true;
		faceDetector.setFaceResults(this);
		for(int i = 0; i < 1; i++) {
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					nextProcessing();
				}
			});
		}
	}
	
	public void nextProcessing() {
		if(++currentProcessId >= files.size()) {
			return;
		}
		faceDetector.detect(files.get(currentProcessId));
	}
	
	@Override
	public void onAddingFace(DetectedFace face) {
		BufferedImage bufImg = face.getBufferedImage();
		int index = neuralRecognition.gfncGetWinner(bufImg);
		if(!state) {
			return;
		}
		if(index < 0) {
			index = learnFace.gfncGetWinner(bufImg);
		}
		if(index > 0) {
			if(expectedOutput.contains(index)) {
				faceResults.addFace(face);
			}
		}
		
		if(++completedAddingThreads >= addingThreads) {
			faceResults.onFulfiling();
		}
	}

	@Override
	public void onFulfiling() {
		nextProcessing();
		if(currentProcessId >= files.size()) {
			addingThreads = getSize();
		}
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
