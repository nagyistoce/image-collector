package team.nm.nnet.app.imageCollector.basis;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import team.nm.nnet.app.imageCollector.layout.FacePanel;
import team.nm.nnet.app.imageCollector.utils.ColorDetection;
import team.nm.nnet.core.Const;
import team.nm.nnet.util.ImageUtils;
import team.nm.nnet.util.Matrix;

public class FaceDetector extends Thread {
    
    private volatile boolean state = false;
    private JPanel pnlFaces;
    private Image image;
    
    public FaceDetector(JPanel pnlFaces, Image image) {
        this.pnlFaces = pnlFaces;
        this.image = image;
    }

    public void run() {
        if(image == null) {
            return;
        }
        
        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
        if(bufferedImage == null) {
            return;
        }

        // Mark this thread is running
        state = true;
        System.out.println("Face Detection Thread running...");
        
        findCandidates(bufferedImage);
        
        // Finish detecting
        state = false;
        System.gc();
        System.out.println("Face Detection Thread finished!");
    }
    private void findCandidates2(BufferedImage bufferedImage) {
    	Matrix<Integer> integralImg = ColorDetection.getIntegralMatrix(bufferedImage);
    	int xStart = 0;
    	int yStart = 0;
    	int width = 100;
        int height = 50;
    	for (int i = xStart; i < xStart + width; i ++) {
            for (int j = yStart; j < yStart + height; j ++) {
            	System.out.print(String.format("[%d][%d]: %d   ", i, j, integralImg.getValue(i, j)));
            }
            System.out.println();
    	}
    	int i = xStart + 15, j = yStart + 5;
    	width = 5; height = 6;
    	
    	int a = integralImg.getValue(i - 1, j - 1);
    	int b = integralImg.getValue(i - 1, j + height);
    	int c = integralImg.getValue(i + width, j - 1);
        int d = integralImg.getValue(i + width, j + height);
        int whitePixelSum = d - (b + c) + a;
        float rate = (float) whitePixelSum / (width * height);
        System.out.println(String
                .format("width=%d, height=%d, sWidth=%d, sHeight=%d, pos[%d][%d]: d=%d, c=%d, b=%d, a=%d, wiSum=%d, rate=%f",
                        width, height, width, height,
                        i, j, d, c, b, a,
                        whitePixelSum, rate));
    	System.out.println("Fini");
    }

    private void findCandidates(BufferedImage bufferedImage) {
        Matrix<Integer> integralImg = ColorDetection.getIntegralMatrix(bufferedImage);
        int detectorWidth = Const.FACE_WIDTH;
        int detectorHeight = Const.FACE_HEIGHT;
        int width = integralImg.getWidth();
        int height = integralImg.getHeight();

        do {
            for (int i = 1; i < width; i += Const.JUMP_LENGHT) {
                for (int j = 1; j < height; j += Const.JUMP_LENGHT) {
                    if(!state) {
                        return;
                    }
                    
                    if ((i + detectorWidth < width) && (j + detectorHeight < height)) {
                    	int a = integralImg.getValue(i - 1, j - 1);
                    	int b = integralImg.getValue(i - 1, j + detectorHeight);
                    	int c = integralImg.getValue(i + detectorWidth, j - 1);
                        int d = integralImg.getValue(i + detectorWidth, j + detectorHeight);
                        int whitePixelSum = d - (b + c) + a;
                        float rate = (float) whitePixelSum / ((detectorWidth + 1) * (detectorHeight + 1));
                        System.out.println(String
                                .format("width=%d, height=%d, sWidth=%d, sHeight=%d, pos[%d][%d]: d=%d, c=%d, b=%d, a=%d, wiSum=%d, rate=%f",
                                        width, height, detectorWidth, detectorHeight,
                                        i, j, d, c, b, a,
                                        whitePixelSum, rate));
                        if (rate > Const.SCANNER_RATE_THRESHOLD) {
                            BufferedImage subBuff = bufferedImage.getSubimage(i, j, detectorWidth, detectorHeight);
                            FacePanel fp = new FacePanel(pnlFaces, ImageUtils.toImage(subBuff));
                            fp.setFaceName(String.valueOf(rate));
                            addFaceCandidates(fp);
                        }
                    }
                }
                System.gc();
            }
            
            if ((detectorWidth < width) && (detectorHeight < height)) {
                detectorWidth += Const.SCANNER_GROWTH;
                detectorHeight += Const.SCANNER_GROWTH;
            } else {
                break;
            }
        } while ((detectorWidth < width) && (detectorHeight < height));
    }
    
    public boolean isDetecting() {
        return state;
    }

    public void requestStop() {
        state = false;
        System.out.println("----Thread stopped");
    }
    
    private void addFaceCandidates(FacePanel facePanel) {
        pnlFaces.add(facePanel);
        pnlFaces.updateUI();
    }
}
