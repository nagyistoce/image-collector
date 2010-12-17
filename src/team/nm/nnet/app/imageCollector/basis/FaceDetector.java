package team.nm.nnet.app.imageCollector.basis;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import team.nm.nnet.app.imageCollector.layout.FacePanel;
import team.nm.nnet.app.imageCollector.utils.ColorDetection;
import team.nm.nnet.core.Const;
import team.nm.nnet.tmp.NeuralNetwork;
import team.nm.nnet.util.ImageUtils;
import team.nm.nnet.util.Matrix;

public class FaceDetector extends Thread {
    
    private volatile boolean state = false;
    private JPanel pnlFaces;
    private Image image;
    private NeuralNetwork neuralNetwork;
    
    public FaceDetector(JPanel pnlFaces, Image image, NeuralNetwork neuralNetwork) {
        this.pnlFaces = pnlFaces;
        this.image = image;
        this.neuralNetwork = neuralNetwork;
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

    private void findCandidates(BufferedImage bufferedImage) {
        int detectorWidth = Const.FACE_WIDTH;
        int detectorHeight = Const.FACE_HEIGHT;
        int width = bufferedImage.getWidth(null);
        int height = bufferedImage.getHeight(null);

        do {
            for (int i = 1; i < width; i += Const.JUMP_LENGHT) {
                for (int j = 1; j < height; j += Const.JUMP_LENGHT) {
                    if(!state) {
                        return;
                    }
                    
                    if ((i + detectorWidth < width) && (j + detectorHeight < height)) {
	                    BufferedImage subBuff = bufferedImage.getSubimage(i, j, detectorWidth, detectorHeight);
	                	if(neuralNetwork.gfncGetWinner(subBuff)) {
	                		FacePanel fp = new FacePanel(pnlFaces, ImageUtils.toImage(subBuff));
	                		fp.setFaceName(String.valueOf(i + " x " + j));
	                		addFaceCandidates(fp);
	                    }
                    }
                	System.out.println(i + " x " + j);
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
    
    private void findCandidates2(BufferedImage bufferedImage) {
    	BufferedImage yCbCrBuf = ImageUtils.toYCbCr(bufferedImage);
    	Matrix<Integer> integralImg = ColorDetection.getIntegralMatrix(yCbCrBuf);
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
    						if(neuralNetwork.gfncGetWinner(subBuff)) {
    							FacePanel fp = new FacePanel(pnlFaces, ImageUtils.toImage(subBuff));
    							fp.setFaceName(String.valueOf(rate));
    							addFaceCandidates(fp);
    						}
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
