package team.nm.nnet.app.imageCollector.basis;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;

import team.nm.nnet.app.imageCollector.layout.FacePanel;
import team.nm.nnet.app.imageCollector.om.ColorSegment;
import team.nm.nnet.core.Const;
import team.nm.nnet.util.ImageUtils;

public class SegmentFaceDetector extends Thread {
    
    private volatile boolean state = false;
    private JPanel pnlFaces;
    private Image image;
    
    public SegmentFaceDetector(JPanel pnlFaces, Image image) {
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
        
        BufferedImage subBuff = bufferedImage.getSubimage(127, 347, 20, 30);
        FacePanel fp = new FacePanel(pnlFaces, ImageUtils.toImage(subBuff));
        fp.setFaceName("Check");
        addFaceCandidates(fp);
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
        if(colorSegment.getWidth() < Const.FACE_WIDTH) {
            return false;
        }
        if(colorSegment.getHeight() < Const.FACE_HEIGHT) {
            return false;
        }
        
        return true;
    }
    
    protected void findCandidates(BufferedImage bufferedImage) {
        ColorSegmentation colorSegmentation = new ColorSegmentation();
        List<ColorSegment> segments = colorSegmentation.segment(bufferedImage);
        for(ColorSegment segment : segments) {
            if(!state) {
                colorSegmentation.requestStop();
                return;
            }
            if (isCandidate(segment)) {
                BufferedImage subBuff = bufferedImage.getSubimage(segment.getxLeft(), segment.getyBottom(), segment.getWidth(), segment.getHeight());
                FacePanel fp = new FacePanel(pnlFaces, ImageUtils.toImage(subBuff));
                fp.setFaceName(segment.getWidth() + " x " + segment.getHeight());
                addFaceCandidates(fp);
            }
        }
    }
    
    protected void addFaceCandidates(FacePanel facePanel) {
        pnlFaces.add(facePanel);
        pnlFaces.updateUI();
    }
}
