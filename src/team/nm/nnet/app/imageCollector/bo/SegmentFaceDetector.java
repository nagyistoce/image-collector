package team.nm.nnet.app.imageCollector.bo;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import team.nm.nnet.app.imageCollector.layout.ExtractedFacePanel;
import team.nm.nnet.app.imageCollector.om.Region;
import team.nm.nnet.app.imageCollector.om.Pixel;
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

    public boolean isCandidate(Region colorSegment) {
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
        List<Region> segments = colorSegmentation.segment(bufferedImage);
        if(segments == null) {
            return;
        }
        for(Region segment : segments) {
            if(!state) {
                colorSegmentation.requestStop();
                return;
            }
            if (isCandidate(segment)) {
                try{
                    List<Region> subSegments = separateRegions(segment);
                    if(subSegments == null) {
                        subSegments = new ArrayList<Region>();
                        subSegments.add(segment);
                    }
                    for(Region subSegment : subSegments) {
                    	Region candidate = extractSingleFace(subSegment);
//                        BufferedImage subBuff = bufferedImage.getSubimage(subSegment.getLeft(), subSegment.getBottom(), subSegment.getWidth(), subSegment.getHeight());
                        if(candidate != null) {
        //                  subBuff = ImageUtils.resize(subBuff, Const.FACE_WIDTH, Const.FACE_HEIGHT);
        //                  if(neuralNetwork.gfncGetWinner(subBuff) > Const.NETWORK_FACE_VALIDATION_THRESHOLD) {
                              int x = ((candidate.getLeft() - Const.SPAN_FACE_BOX) > segment.getLeft()) ? candidate.getLeft() - Const.SPAN_FACE_BOX : segment.getLeft(); 
                              int y = ((candidate.getBottom() - Const.SPAN_FACE_BOX) > segment.getBottom()) ? candidate.getBottom() - Const.SPAN_FACE_BOX : segment.getBottom(); 
                              int w = ((candidate.getWidth() + Const.SPAN_FACE_BOX) <= segment.getRight()) ? candidate.getWidth() + Const.SPAN_FACE_BOX : candidate.getWidth(); 
                              int h = ((candidate.getHeight() + Const.SPAN_FACE_BOX) <= segment.getTop()) ? candidate.getHeight() + Const.SPAN_FACE_BOX : candidate.getHeight(); 
                              
//                              BufferedImage subBuff = bufferedImage.getSubimage(candidate.getLeft(), candidate.getBottom(), candidate.getWidth(), candidate.getHeight());
                              
        //                  }
                        } 
                    }
                }catch(Exception e) {
                    System.out.println(String.format("l: %d, r: %d, b: %d, t:%d, w: %d, h: %d", segment.getLeft(), segment.getRight(), segment.getBottom(), segment.getTop(), segment.getWidth(), segment.getHeight()));
                }
            }
        }
    }
    
    protected List<Region> separateRegions(Region segment) {
        List<Pixel> brokenPoints = segment.getBrokenPoints(bufferedImage); 
        if((brokenPoints == null) || (brokenPoints.size() < 1)) {
            return null;
        }
        Collections.sort(brokenPoints);
        
        List<Region> regions = new ArrayList<Region>();
        int top = segment.getTop();
        int bottom = segment.getBottom();
        int left = segment.getLeft();
        for(Pixel p : brokenPoints) {
            regions.add(new Region(left, top, p.getX(), bottom));
            left = p.getX();
        }
        
        // Add the last region
        regions.add(new Region(left, top, segment.getRight(), bottom));
        return regions;
    }
    
    protected Region extractSingleFace(Region segment) {
        int width = segment.getWidth();
        int height = segment.getHeight();
        int left = segment.getLeft();
        int bottom = segment.getBottom();
        BufferedImage segmentBuff = bufferedImage.getSubimage(left, bottom, width, height);
        
        float max = 0;
        Region candidate = null;
        double[] scales = {1, 0.8, 0.6, 0.4};
        for(double scale : scales){
            int w = (int) (width * scale), h = (int) (height * scale);
            for(int i = 0, ww = width - w; i <= ww; i += Const.JUMP_LENGHT) {
                for(int j = 0, hh = height - h; j <= hh; j += Const.JUMP_LENGHT) {
                	if(!state) {
                        return null;
                    }
                    BufferedImage subBuff = segmentBuff.getSubimage(i, j, w, h);
                    subBuff = ImageUtils.resize(subBuff, Const.FACE_WIDTH, Const.FACE_HEIGHT);
                    
//                  ExtractedFacePanel fp = new ExtractedFacePanel(pnlFaces, ImageUtils.toImage(subBuff));
//                  fp.setFaceName((float)segment.getWidth() / segment.getHeight() + " : " + segment.getWidth() + " x " + segment.getHeight());
//                  addFaceCandidates(fp);
                  
                    float outVal = neuralNetwork.gfncGetWinner(subBuff);
                    if((outVal > Const.NETWORK_FACE_VALIDATION_THRESHOLD) && (outVal > max)) {
                        max = outVal;
                        candidate = new Region(left + i, bottom + j + h, left + i + w, bottom + j);
                    }
                }
            }
        }
        return candidate;
    }
}
