package team.nm.nnet.app.imageCollector.om;

import java.awt.image.BufferedImage;

public class DetectedFace {

	private Region region;
	private BufferedImage bufferedImage;
	private int faceId;
	private String faceName;
	private String filePath;
	
	public DetectedFace() {}
	
	public DetectedFace(BufferedImage bufferedImage, String filePath) {
		this.bufferedImage = bufferedImage;
		this.filePath = filePath;
	}
	
	public DetectedFace(BufferedImage bufferedImage, String filePath, int faceId, String faceName) {
		this(bufferedImage, filePath);
		this.faceId = faceId;
		this.faceName = faceName;
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

	public int getFaceId() {
		return faceId;
	}

	public void setFaceId(int faceId) {
		this.faceId = faceId;
	}

	public String getFaceName() {
		return faceName;
	}

	public void setFaceName(String faceName) {
		this.faceName = faceName;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}
	
}
