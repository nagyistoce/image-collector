package team.nm.nnet.app.imageCollector.bo;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import team.nm.nnet.app.imageCollector.om.DetectedFace;
import team.nm.nnet.app.imageCollector.om.FaceList;
import team.nm.nnet.app.imageCollector.om.Pixel;
import team.nm.nnet.app.imageCollector.om.Region;
import team.nm.nnet.core.Const;
import team.nm.nnet.tmp.NeuralNetwork;
import team.nm.nnet.util.ImageUtils;

public class FaceDetector {
    
    private volatile boolean state = false;
    private NeuralNetwork neuralNetwork;
    private BufferedImage bufferedImage;
    private FaceList faceResults;
    private String filePath;
    
    public FaceDetector () {
    	neuralNetwork = new NeuralNetwork("");
    	neuralNetwork.loadWeight(Const.CURRENT_DIRECTORY + "/src/weight.txt");
    }
    
    public void detect(File file) {
    	if(file == null) {
    		return;
    	}
    	bufferedImage = ImageUtils.load(file);
    	filePath = file.getPath();
    	run();
    }
    
    public void detect(Image image, String filePath) {
    	if(image == null) {
    		return;
    	}
    	this.filePath = filePath;
    	bufferedImage = ImageUtils.toBufferedImage(image);
    	run();
    }
    
    public void detect(BufferedImage bufferedImage, String filePath) {
    	if(bufferedImage == null) {
    		return;
    	}
    	this.filePath = filePath;
    	this.bufferedImage = bufferedImage;
    	run();
    }
    
    public void run() {
        if((bufferedImage == null) || (faceResults == null)) {
            return;
        }

        // Mark this thread is running
        state = true;
        
        findCandidates();
        
        // Finish detecting
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
        if(whiteRatio < Const.WHITE_RATIO_THRESHOLD) {
            return false;
        }

        return true;
    }
    
    protected void findCandidates() {
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
                        if(candidate != null) {
                        	int x = ((candidate.getLeft() - Const.SPAN_FACE_BOX) > segment.getLeft()) ? candidate.getLeft() - Const.SPAN_FACE_BOX : segment.getLeft(); 
                            int y = ((candidate.getBottom() - Const.SPAN_FACE_BOX) > segment.getBottom()) ? candidate.getBottom() - Const.SPAN_FACE_BOX : segment.getBottom(); 
                            int w = ((candidate.getRight() + Const.SPAN_FACE_BOX) <= segment.getRight()) ? candidate.getWidth() + Const.SPAN_FACE_BOX : candidate.getWidth(); 
                            int h = ((candidate.getTop() + Const.SPAN_FACE_BOX) <= segment.getTop()) ? candidate.getHeight() + Const.SPAN_FACE_BOX : candidate.getHeight();
                        	
                            BufferedImage bufImg = bufferedImage.getSubimage(x, y, w, h);
                            DetectedFace face = new DetectedFace(bufImg, filePath);
                            faceResults.addFace(face);
                        } 
                    }
                }catch(Exception e) {
                    System.out.println(String.format("l: %d, r: %d, b: %d, t:%d, w: %d, h: %d", segment.getLeft(), segment.getRight(), segment.getBottom(), segment.getTop(), segment.getWidth(), segment.getHeight()));
                }
            }
        }
        faceResults.onFulfiling();
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

	public FaceList getFaceResults() {
		return faceResults;
	}

	public void setFaceResults(FaceList faceResults) {
		this.faceResults = faceResults;
	}
}
