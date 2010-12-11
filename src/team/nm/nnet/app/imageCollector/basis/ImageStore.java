package team.nm.nnet.app.imageCollector.basis;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import team.nm.nnet.util.IOUtils;
import team.nm.nnet.util.ImageUtils;

public class ImageStore {
    
    private final 	double[] 	ANGLES = {10, 20, 30};
    private final 	int 		WIDTH_SPAN = 30;    
    private final 	int 		HEIGHT_SPAN = 20;
    private 		int 		frameWidth = 150;
    private 		int 		frameHeight = 200;    

    public long createPngFaces(String srcPath, String destPath) {
    	long count = 0;
    	List<String> fileNames = IOUtils.listFileName(srcPath);
    	if((fileNames == null) || (!IOUtils.exists(destPath))) {
    		return count;
    	}
        for(String filename : fileNames) {
            BufferedImage bufferedImage = ImageUtils.load(srcPath + "/" + filename);
            if(bufferedImage != null) {
            	// Hiệu chỉnh khung hình
            	bufferedImage = ImageUtils.scale(bufferedImage, frameWidth, frameHeight);
            	File file = new File(destPath + "/" + filename);
                ImageUtils.drawImageToJpgByteStream(bufferedImage, file);
                count++;
                // Tạo ra các biến thể
                for(double degree : ANGLES) {
                	// Xoay hình 1 gốc degree
                	BufferedImage bufImgVariation = ImageUtils.rotateAndScale(bufferedImage, degree);
                    if(bufImgVariation != null) {
                        file = new File(destPath + "/" + degree + "_" + filename);
                        ImageUtils.saveToPng(bufImgVariation, file);
                        count++;
                    }
                    // Lấy đối xứng d�?c ảnh đã xoay
                    bufImgVariation = ImageUtils.flip(bufImgVariation, false);
                    if(bufImgVariation != null) {
                        file = new File(destPath + "/" + degree + "_x_" + filename);
                        ImageUtils.saveToPng(bufImgVariation, file);
                        count++;
                    }
                }
            }
        }
        return count;
    }
    
    public long createJpgFaces(String srcPath, String destPath) {
    	long count = 0;
    	List<String> fileNames = IOUtils.listFileName(srcPath);
    	if((fileNames == null) || (!IOUtils.exists(destPath))) {
    		return count;
    	}
        for(String filename : fileNames) {
            BufferedImage bufferedImage = ImageUtils.load(srcPath + "/" + filename);
            if(bufferedImage != null) {
            	// Hiệu chỉnh khung hình
            	bufferedImage = ImageUtils.scale(bufferedImage, frameWidth, frameHeight);
            	File file = new File(destPath + "/" + filename);
                ImageUtils.drawImageToJpgByteStream(bufferedImage, file);
                count++;
                // Tạo ra các biến thể
                for(double degree : ANGLES) {
                	// Xoay hình 1 gốc degree
                	BufferedImage bufImgVariation = ImageUtils.rotate(bufferedImage, degree);
                    if(bufImgVariation != null) {
                        file = new File(destPath + "/" + degree + "_" + filename);
                        ImageUtils.drawImageToJpgByteStream(bufImgVariation, file);
                        count++;
                    }
                    // Lấy đối xứng d�?c ảnh đã xoay
                    bufImgVariation = ImageUtils.flip(bufImgVariation, false);
                    if(bufImgVariation != null) {
                        file = new File(destPath + "/" + degree + "_x_" + filename);
                        ImageUtils.drawImageToJpgByteStream(bufImgVariation, file);
                        count++;
                    }
                }
            }
        }
        return count;
    }
    
    /**
     * Tao none face cho viec train
     * @param srcPath Duong dan luu anh de tao none face
     * @param destPath Duong dan luu cac file none face
     * @return So luong none face tao duoc
     */
    public long createNonFaces(String srcPath, String destPath) {
    	long count = 0;
    	List<String> fileNames = IOUtils.listFileName(srcPath);
    	if((fileNames == null) || (!IOUtils.exists(destPath))) {
    		return count;
    	}
        for(String filename : fileNames) {
            BufferedImage bufferedImage = ImageUtils.load(srcPath + "/" + filename);
            int width = bufferedImage.getWidth(null);
    		int height = bufferedImage.getHeight(null);
            if(bufferedImage != null) {
                for(int x = 0, y = 0; (x + frameWidth < width) && (y + frameHeight < height); x += WIDTH_SPAN, y += HEIGHT_SPAN) {
                	// Xoay hình 1 gốc degree
                	BufferedImage bufImgVariation = bufferedImage.getSubimage(x, y, frameWidth, frameHeight);
                	File file = new File(destPath + "/" + x + "x" + y + filename);
                	ImageUtils.drawImageToJpgByteStream(bufImgVariation, file);
                    count++;                    
                }
            }
        }
        return count;
    }

	public int getFrameWidth() {
		return frameWidth;
	}

	public void setFrameWidth(int frameWidth) {
		this.frameWidth = frameWidth;
	}

	public int getFrameHeight() {
		return frameHeight;
	}

	public void setFrameHeight(int frameHeight) {
		this.frameHeight = frameHeight;
	}
}
