package team.nm.nnet.app.imageCollector.bo;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import team.nm.nnet.app.imageCollector.layout.FacePanel;
import team.nm.nnet.app.imageCollector.om.ColorSegment;
import team.nm.nnet.core.Const;
import team.nm.nnet.tmp.NeuralNetwork;
import team.nm.nnet.util.ImageUtils;

public class SegmentFaceDetector extends Thread {
    
    private volatile boolean state = false;
    private JPanel pnlFaces;
    private JLabel lblProcess;
    private BufferedImage bufferedImage;
    private NeuralNetwork neuralNetwork;
    
    public SegmentFaceDetector(JPanel pnlFaces, JLabel lblProcess, Image image, NeuralNetwork neuralNetwork) {
        this.pnlFaces = pnlFaces;
        this.lblProcess = lblProcess;
        if(image != null) {
        	bufferedImage = ImageUtils.toBufferedImage(image);
        }
        this.neuralNetwork = neuralNetwork;
    }

    public void run() {
        if(bufferedImage == null) {
            return;
        }

        String sysPath = System.getProperty("user.dir");
        // Mark this thread is running
        state = true;
        lblProcess.setIcon(new ImageIcon(sysPath + Const.RESOURCE_PATH + "waiting.gif"));
        System.out.println("Face Detection Thread running...");
        
        findCandidates(bufferedImage);
        
        // Finish detecting
        lblProcess.setIcon(new ImageIcon(sysPath + Const.RESOURCE_PATH + "check.gif"));
        state = false;
        System.gc();
        System.out.println("Face Detection Thread finished!");
    }
    
    public boolean isDetecting() {
        return state;
    }

    public void requestStop() {
        state = false;
        System.out.println("----Thread stopped");
    }

    public boolean isCandidate(ColorSegment colorSegment) {
        if(colorSegment.getPixels().size() < Const.MINIMUM_SKIN_PIXEL_THRESHOLD) {
            return false;
        }
        float whiteRatio = (float) colorSegment.getPixels().size() / (colorSegment.getWidth() * colorSegment.getHeight());
        if(whiteRatio < 0.4) {
        	return false;
        }

        return true;
    }
    
    protected void findCandidates(BufferedImage bufferedImage) {
        ColorSegmentation colorSegmentation = new ColorSegmentation();
        List<ColorSegment> segments = colorSegmentation.segment(bufferedImage);
        if(segments == null) {
        	return;
        }
        for(ColorSegment segment : segments) {
            if(!state) {
                colorSegmentation.requestStop();
                return;
            }
            if (isCandidate(segment)) {
            	try{
            	BufferedImage subBuff = extractSingleFace(segment);
//            	BufferedImage subBuff = bufferedImage.getSubimage(segment.getLeft(), segment.getBottom(), segment.getWidth(), segment.getHeight());
            	if(subBuff != null) {
//	                subBuff = ImageUtils.resize(subBuff, Const.FACE_WIDTH, Const.FACE_HEIGHT);
//	                if(neuralNetwork.gfncGetWinner(subBuff) > Const.NETWORK_FACE_VALIDATION_THRESHOLD) {
	//                	int x = ((segment.getLeft() - Const.SPAN_FACE_BOX) <= 0) ? segment.getLeft() - Const.SPAN_FACE_BOX : segment.getLeft(); 
	//                	int y = ((segment.getBottom() - Const.SPAN_FACE_BOX) <= 0) ? segment.getBottom() - Const.SPAN_FACE_BOX : segment.getBottom(); 
	//                	int w = ((segment.getWidth() + Const.SPAN_FACE_BOX) <= bufferedImage.getWidth()) ? segment.getWidth() + Const.SPAN_FACE_BOX : segment.getWidth(); 
	//                	int h = ((segment.getHeight() + Const.SPAN_FACE_BOX) <= bufferedImage.getHeight()) ? segment.getHeight() + Const.SPAN_FACE_BOX : segment.getHeight(); 
	//                	
	//                	subBuff = bufferedImage.getSubimage(x, y, w, h);
	//                    subBuff = ImageUtils.resize(subBuff, Const.FACE_WIDTH, Const.FACE_HEIGHT);
		                FacePanel fp = new FacePanel(pnlFaces, ImageUtils.toImage(subBuff));
		                fp.setFaceName((float)segment.getWidth() / segment.getHeight() + " : " + segment.getWidth() + " x " + segment.getHeight());
		                addFaceCandidates(fp);
//	                }
            	}
            	}catch(Exception e) {
            		System.out.println(String.format("l: %d, r: %d, b: %d, t:%d, w: %d, h: %d", segment.getLeft(), segment.getRight(), segment.getBottom(), segment.getTop(), segment.getWidth(), segment.getHeight()));
            	}
            }
        }
    }
    
    protected BufferedImage extractSingleFace(ColorSegment segment) {
    	int width = segment.getWidth();
    	int height = segment.getHeight();
    	BufferedImage segmentBuff = bufferedImage.getSubimage(segment.getLeft(), segment.getBottom(), width, height);
    	
    	float max = 0;
    	BufferedImage candidate = null;
    	
    	for(int w = width, h = height, s = 0; s < 3; s++, w /= 2, h /= 2){
    		for(int i = 0; i <= width - w; i += Const.JUMP_LENGHT) {
    			for(int j = 0; j <= height - h; j += Const.JUMP_LENGHT) {
    				BufferedImage subBuff = segmentBuff.getSubimage(i, j, w, h);
    				subBuff = ImageUtils.resize(subBuff, Const.FACE_WIDTH, Const.FACE_HEIGHT);
    				float outVal = neuralNetwork.gfncGetWinner(subBuff);
    				if((outVal > Const.NETWORK_FACE_VALIDATION_THRESHOLD) && (outVal > max)) {
    					max = outVal;
    					candidate = subBuff;
    				}
    			}
    		}
    	}
    	System.out.println(max);
    	return candidate;
    }
    
    protected void addFaceCandidates(FacePanel facePanel) {
        pnlFaces.add(facePanel);
        pnlFaces.updateUI();
    }
}
