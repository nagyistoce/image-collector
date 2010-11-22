package team.nm.nnet.core;

import java.awt.image.BufferedImage;

import team.nm.nnet.util.ImageProcess;

public class FindFace {
	/**
	 * Doi tuong dung de phan loai guong mat
	 */
	//private FaceClassify faceClassify;
	
	/**
	 * Chieu rong nguyen thuy cua anh
	 */
	private int ogirinWidth = 0;
	
	/**
	 * Chieu dai nguyen thuy cua anh
	 */
	private int ogirinHeight = 0;
	
	/**
	 * Contruc khoi tao cho lop find face
	 * @param fileSaveNetwork Duong dan luu network de load len
	 */
	public FindFace(String fileSaveNetwork) {
		//faceClassify = new FaceClassify();
		//faceClassify.loadNetwork(fileSaveNetwork);
	}
	
	/**
	 * Lay cac face trong mot buc anh
	 * Truoc tien chi in toa do ra thoi
	 * @param image Anh can lay face
	 */
	public void getFaceInImage(BufferedImage image) {
		/*int width = image.getWidth();
		int height = image.getHeight();
		for (int i = 0; i < height - Const.FACE_HEIGHT; i ++) {
			for (int j = 0; j < width - Const.FACE_WIDTH; j ++) {
				BufferedImage subImage = image.getSubimage(j, i, Const.FACE_WIDTH, Const.FACE_HEIGHT);
				if (faceClassify.isFace(subImage)) {
					System.out.println("Face position: x = " + j + " y: " + i);
				}
			}
		}
		*/
	}
	
	/**
	 * Resize anh va tim khuon mat
	 * Moi lan resize moi lan tim
	 * @param image Anh can tim
	 */
	public void resizeAndGetFace(BufferedImage image) {
		for (int i = 95; i >= 5; i -= 5) {
			BufferedImage iImage = ImageProcess.resize(image, i);
			getFaceInImage(iImage);
		}
	}
}
