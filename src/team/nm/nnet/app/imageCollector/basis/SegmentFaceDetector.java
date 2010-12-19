package team.nm.nnet.app.imageCollector.basis;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;

import team.nm.nnet.app.imageCollector.layout.FacePanel;
import team.nm.nnet.app.imageCollector.om.ColorSegment;
import team.nm.nnet.core.Const;
import team.nm.nnet.tmp.NeuralNetwork;
import team.nm.nnet.util.ImageUtils;

public class SegmentFaceDetector extends Thread {
    
    private volatile boolean state = false;
    private JPanel pnlFaces;
    private Image image;
    private NeuralNetwork neuralNetwork;
    
    public SegmentFaceDetector(JPanel pnlFaces, Image image, NeuralNetwork neuralNetwork) {
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
    
    public boolean isDetecting() {
        return state;
    }

    public void requestStop() {
        state = false;
        System.out.println("----Thread stopped");
    }

    public boolean isCandidate(ColorSegment colorSegment) {
        if(colorSegment.getWidth() * colorSegment.getHeight() < Const.MINIMUM_SKIN_PIXEL_THRESHOLD) {
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
            if (isCandidate(segment) || true) {
            	try{
            	BufferedImage subBuff = bufferedImage.getSubimage(segment.getLeft(), segment.getBottom(), segment.getWidth(), segment.getHeight());
                subBuff = ImageUtils.resize(subBuff, Const.FACE_WIDTH, Const.FACE_HEIGHT);
//                if(neuralNetwork.gfncGetWinner(subBuff)) {
//                	int x = ((segment.getLeft() - Const.SPAN_FACE_BOX) <= 0) ? segment.getLeft() - Const.SPAN_FACE_BOX : segment.getLeft(); 
//                	int y = ((segment.getBottom() - Const.SPAN_FACE_BOX) <= 0) ? segment.getBottom() - Const.SPAN_FACE_BOX : segment.getBottom(); 
//                	int w = ((segment.getWidth() + Const.SPAN_FACE_BOX) <= bufferedImage.getWidth()) ? segment.getWidth() + Const.SPAN_FACE_BOX : segment.getWidth(); 
//                	int h = ((segment.getHeight() + Const.SPAN_FACE_BOX) <= bufferedImage.getHeight()) ? segment.getHeight() + Const.SPAN_FACE_BOX : segment.getHeight(); 
//                	
//                	subBuff = bufferedImage.getSubimage(x, y, w, h);
//                    subBuff = ImageUtils.resize(subBuff, Const.FACE_WIDTH, Const.FACE_HEIGHT);
	                FacePanel fp = new FacePanel(pnlFaces, ImageUtils.toImage(subBuff));
	                fp.setFaceName(segment.getLeft() + ", " + segment.getTop() + " : " + segment.getWidth() + " x " + segment.getHeight());
	                addFaceCandidates(fp);
//                }
            	}catch(Exception e) {
            		System.out.println(String.format("l: %d, r: %d, b: %d, t:%d, w: %d, h: %d", segment.getLeft(), segment.getRight(), segment.getBottom(), segment.getTop(), segment.getWidth(), segment.getHeight()));
            	}
            }
        }
    }
    
    protected void addFaceCandidates(FacePanel facePanel) {
        pnlFaces.add(facePanel);
        pnlFaces.updateUI();
    }
}
