package team.nm.nnet.app.imageCollector.bo;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sole.hawking.image.filter.EdgeFilter;
import team.nm.nnet.app.imageCollector.layout.FacePanel;
import team.nm.nnet.app.imageCollector.om.ColorSegment;
import team.nm.nnet.app.imageCollector.om.Pixel;
import team.nm.nnet.app.imageCollector.utils.ColorSpace;
import team.nm.nnet.core.Const;
import team.nm.nnet.tmp.NeuralNetwork;
import team.nm.nnet.util.ImageUtils;

public class SegmentFaceDetector2 extends Thread {
    
    private volatile boolean state = false;
    private final int WHITE_COLOR = 0xffffffff;
    
    private JPanel pnlFaces;
    private JLabel lblProcess;
    private BufferedImage bufferedImage;
    private NeuralNetwork neuralNetwork;
    
    public SegmentFaceDetector2(JPanel pnlFaces, JLabel lblProcess, Image image, NeuralNetwork neuralNetwork) {
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

        // Mark this thread is running
        state = true;
        lblProcess.setIcon(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "waiting.gif"));
        
        findCandidates(bufferedImage);
        
        // Finish detecting
        lblProcess.setIcon(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "check.png"));
        state = false;
        System.gc();
    }
    
    public boolean isDetecting() {
        return state;
    }

    public void requestStop() {
        state = false;
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
//              BufferedImage subBuff = bufferedImage.getSubimage(segment.getLeft(), segment.getBottom(), segment.getWidth(), segment.getHeight());
                if(subBuff != null) {
//                  subBuff = ImageUtils.resize(subBuff, Const.FACE_WIDTH, Const.FACE_HEIGHT);
//                  if(neuralNetwork.gfncGetWinner(subBuff) > Const.NETWORK_FACE_VALIDATION_THRESHOLD) {
    //                  int x = ((segment.getLeft() - Const.SPAN_FACE_BOX) <= 0) ? segment.getLeft() - Const.SPAN_FACE_BOX : segment.getLeft(); 
    //                  int y = ((segment.getBottom() - Const.SPAN_FACE_BOX) <= 0) ? segment.getBottom() - Const.SPAN_FACE_BOX : segment.getBottom(); 
    //                  int w = ((segment.getWidth() + Const.SPAN_FACE_BOX) <= bufferedImage.getWidth()) ? segment.getWidth() + Const.SPAN_FACE_BOX : segment.getWidth(); 
    //                  int h = ((segment.getHeight() + Const.SPAN_FACE_BOX) <= bufferedImage.getHeight()) ? segment.getHeight() + Const.SPAN_FACE_BOX : segment.getHeight(); 
    //                  
    //                  subBuff = bufferedImage.getSubimage(x, y, w, h);
    //                    subBuff = ImageUtils.resize(subBuff, Const.FACE_WIDTH, Const.FACE_HEIGHT);
                        FacePanel fp = new FacePanel(pnlFaces, ImageUtils.toImage(subBuff));
                        fp.setFaceName((float)segment.getWidth() / segment.getHeight() + " : " + segment.getWidth() + " x " + segment.getHeight());
                        addFaceCandidates(fp);
//                  }
                }
                }catch(Exception e) {
                    System.out.println(String.format("l: %d, r: %d, b: %d, t:%d, w: %d, h: %d", segment.getLeft(), segment.getRight(), segment.getBottom(), segment.getTop(), segment.getWidth(), segment.getHeight()));
                }
            }
        }
    }
    
    protected List<ColorSegment> separateRegions(ColorSegment segment) {
        List<ColorSegment> regions = new ArrayList<ColorSegment>();
        BufferedImage boundary = ColorSpace.toYCbCr(bufferedImage);
        EdgeFilter edgeFilter = new EdgeFilter();
        boundary = edgeFilter.filter(boundary, null);
        
        return regions;
    }
    
    protected List<Pixel> detectBrokenPoints(BufferedImage boundary, ColorSegment segment) {
        List<Pixel> pixels = new ArrayList<Pixel>();
        
        int startX = segment.getStartPoint().getX();
        int startY = segment.getStartPoint().getY();
        int left = segment.getLeft();
        int right = segment.getRight();
        int bottom = segment.getBottom();
        int top = segment.getTop();
        
        int x = startX, y = startY;
        int uphill = 0;
        
        
        
        return pixels;
    }
    
    protected BufferedImage extractSingleFace(ColorSegment segment) {
        int width = segment.getWidth();
        int height = segment.getHeight();
        BufferedImage segmentBuff = bufferedImage.getSubimage(segment.getLeft(), segment.getBottom(), width, height);
        
        float max = 0;
        BufferedImage candidate = null;
        double[] scales = {1, 0.6, 0.5};
        for(double scale : scales){
            int w = (int) (width * scale), h = (int) (height * scale);
            for(int i = 0, ww = width - w; i <= ww; i += Const.JUMP_LENGHT) {
                for(int j = 0, hh = height - h; j <= hh; j += Const.JUMP_LENGHT) {
                    BufferedImage subBuff = segmentBuff.getSubimage(i, j, w, h);
                    subBuff = ImageUtils.resize(subBuff, Const.FACE_WIDTH, Const.FACE_HEIGHT);
//                  FacePanel fp = new FacePanel(pnlFaces, ImageUtils.toImage(subBuff));
//                  fp.setFaceName((float)segment.getWidth() / segment.getHeight() + " : " + segment.getWidth() + " x " + segment.getHeight());
//                  addFaceCandidates(fp);
                    float outVal = neuralNetwork.gfncGetWinner(subBuff);
                    if((outVal > Const.NETWORK_FACE_VALIDATION_THRESHOLD) && (outVal > max)) {
                        max = outVal;
                        candidate = subBuff;
                    }
                }
            }
        }
        return candidate;
    }
    
    protected void addFaceCandidates(FacePanel facePanel) {
        pnlFaces.add(facePanel);
        pnlFaces.updateUI();
    }
}
