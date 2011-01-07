package team.nm.nnet.app.imageCollector.om;

import java.awt.image.BufferedImage;

public class DetectedFace {

	private BufferedImage bufferedImage;
	private String faceId;
	private String filePath;
	
	public DetectedFace() {}
	
	public DetectedFace(BufferedImage bufferedImage, String filePath) {
		this.bufferedImage = bufferedImage;
		this.filePath = filePath;
	}
	
	public DetectedFace(BufferedImage bufferedImage, String filePath, String faceId) {
		this(bufferedImage, filePath);
		this.faceId = faceId;
	}

	public String getFaceId() {
		return faceId;
	}
	public void setFaceId(String faceId) {
		this.faceId = faceId;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	public void setBufferedImage(BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
	}
	
}
