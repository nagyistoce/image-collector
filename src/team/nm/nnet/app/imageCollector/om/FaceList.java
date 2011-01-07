package team.nm.nnet.app.imageCollector.om;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class FaceList {

	static ExecutorService executorService = Executors.newCachedThreadPool();
	
	protected List<DetectedFace> faceList = new ArrayList<DetectedFace>();
	
	public void addFace(final DetectedFace face) {
		executorService.execute(new Runnable() {
			
			@Override
			public void run() {
				faceList.add(face);
				onAddingFace(face);
			}
		});
	}
	
	public int getSize() {
		return faceList.size();
	}
	
	public void clear() {
		if(faceList == null) {
			return;
		}
		faceList.clear();
	}
	
	public abstract void onAddingFace(DetectedFace face);
	public abstract void onFulfiling();
}
