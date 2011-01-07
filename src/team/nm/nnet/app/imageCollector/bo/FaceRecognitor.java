package team.nm.nnet.app.imageCollector.bo;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Required;

import team.nm.nnet.app.imageCollector.om.DetectedFace;
import team.nm.nnet.app.imageCollector.om.FaceList;
import team.nm.nnet.core.Const;
import team.nm.nnet.tmp.NeuralFaceRecognize;

public class FaceRecognitor extends FaceList {

	static ExecutorService executorService = Executors.newCachedThreadPool();
	
	private volatile boolean state = false;
	private volatile int addingThreads;
	private volatile int completedAddingThreads;
	private NeuralFaceRecognize neuralRecognition;
	private FaceDetector faceDetector;
	private FaceList faceResults;
	
	public void prepare() {
		addingThreads = Integer.MAX_VALUE;
		completedAddingThreads = 0;
		clear();
	}
	
	public FaceRecognitor () {
		neuralRecognition = new NeuralFaceRecognize("");
		neuralRecognition.loadWeight(Const.CURRENT_DIRECTORY + "/src/weight_recog.txt");
	}
	
	public void recognize(final Image image) {
		state = true;
		faceDetector.setFaceResults(this);
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				faceDetector.detect(image, null);
			}
		});
	}
	
	public void recognize(final File file) {
		state = true;
		faceDetector.setFaceResults(this);
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				faceDetector.detect(file);
			}
		});
	}
		
	@Override
	public void onAddingFace(DetectedFace face) {
//		faceResults.addFace(face);

		BufferedImage bufImg = face.getBufferedImage();
		int index = neuralRecognition.gfncGetWinner(bufImg);
		if(!state) {
			return;
		}
		if(index > 0) {
			face.setFaceId(index);
			face.setFaceName(neuralRecognition.getName(index));
			faceResults.addFace(face);
		}
		if(++completedAddingThreads >= addingThreads) {
			faceResults.onFulfiling();
		}
	}

	@Override
	public void onFulfiling() {
		addingThreads = getSize();
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
