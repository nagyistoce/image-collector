package team.nm.nnet.app.imageCollector.bo;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import team.nm.nnet.app.imageCollector.om.Pixel;
import team.nm.nnet.app.imageCollector.om.Region;
import team.nm.nnet.app.imageCollector.utils.ColorSpace;
import team.nm.nnet.core.Const;

public class ColorSegmentation {

	private int noSegments;
    private List<Region> segments;
    private BufferedImage bufImage;
    private int width, height;
    private volatile boolean state = false;
    
    /**
     * marks value: 
     * * (-2): unvisited pixel
     * * (-1): this pixel is in stack waiting for processing
     * * 0 and above: the region id this pixel is belong to. 
     */
    private int[] marks;
    private final int UNVISITED_PIXEL = -2;
    private final int STACK_PIXEL = -1;
    
    private final int DEVIATION = 15;
    private final int REGION_GAP = 10;

    public ColorSegmentation() {
        noSegments = 0;
        segments = new ArrayList<Region>();
    }
    
    public List<Region> segment(BufferedImage bufferedImage) {
        if(bufferedImage == null) {
            return null;
        }
        bufImage = ColorSpace.getBinaryBuffer(bufferedImage);
        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();
        
        initMarks(width * height);
        // Mark this thread is running
        state = true;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
            	if(!state) {
            		return segments;
            	}
                int index = x * height + y;
                if ((marks[index] == UNVISITED_PIXEL) && (bufImage.getRGB(x, y) == Const.WHITE_COLOR)) {
                    Region region = new Region();
                    region.setId(noSegments++);
                    flood(x, y, region);
                    if(region.isValid()) {
                        region.setStartPoint(new Pixel(x - region.getLeft(), y - region.getBottom()));
                        
                        int value = region.getBottom();
                        region.setBottom((value > 0) ? value - 1 : value);
                        value = region.getLeft();
                        region.setLeft((value > 0) ? value - 1 : value);
                        value = region.getTop();
                        region.setTop((value + 1 < height) ? value + 1 : value);
                        value = region.getRight();
                        region.setRight((value + 1 < width) ? value + 1 : value);
                        
                        segments.add(region);
                    }
                }
            }
        }
        
        // Finish segmenting
        state = false;
        verify();
        return segments;
    }
    
    public boolean isSegmenting() {
        return state;
    }

    public void requestStop() {
        state = false;
    }

    public int getNoSegments() {
        return noSegments;
    }

    public List<Region> getSegments() {
        return segments;
    }
    
    protected void initMarks(int size) {
        marks = new int[size];
        for(int i = 0; i < size; i++) {
            marks[i] = UNVISITED_PIXEL;
        }
    }

    protected void flood(int x, int y, Region region) {
    	Stack<Pixel> stack = new Stack<Pixel>();
    	pushStack(stack, x, y);
    	
    	while(state && !stack.isEmpty()) {
    		Pixel p = stack.pop();
    		int xx = p.getX();
    		int yy = p.getY();
    		
    		marks[xx * height + yy] = region.getId();
    		region.addPixel(new Pixel(xx, yy));

    		// Cập nhật lại các cận của phân vùng
			if(xx < region.getLeft()) {
				region.setLeft(xx);
			} else if(xx > region.getRight()) {
				region.setRight(xx);
			}
			if(yy < region.getBottom()) {
				region.setBottom(yy);
			} else if(yy > region.getTop()) {
				region.setTop(yy);
			}
			
			// Loang hướng lên trên
			pushStack(stack, xx - 1, yy - 1);
			pushStack(stack, xx - 1, yy);
			pushStack(stack, xx - 1, yy + 1);
			
			// Loang hướng xuống dưới
			pushStack(stack, xx + 1, yy - 1);
			pushStack(stack, xx + 1, yy);
			pushStack(stack, xx + 1, yy + 1);
			
			// Loang theo phương ngang
			pushStack(stack, xx, yy - 1);
			pushStack(stack, xx, yy + 1);
    	}
    }
    
    private void pushStack(Stack<Pixel> stack, int x, int y) {
    	int index = x * height + y;
		boolean isX = (0 <= x) && (x < width);
		boolean isY = (0 <= y) && (y < height);
		
		if(isX && isY && (marks[index] == UNVISITED_PIXEL) && (bufImage.getRGB(x, y) == Const.WHITE_COLOR)) {
			stack.push(new Pixel(x, y));
			marks[index] = STACK_PIXEL;
		}
    }
    
    private void verify() {
    	for(Region r : segments) {
    		merge(r);
    	}
    	segments = removeNestedRegions();
    }
    
    private List<Integer> merge(Region region) {
    	List<Integer> mergedRegionIds = new ArrayList<Integer>();
    	List<Integer> neighbourIds = searchNeighbour(region);
    	for(int id : neighbourIds) {
    		Region aboveRegion = segments.get(id);
    		
    		if((aboveRegion.getWidth() > region.getWidth()) || 
    				(aboveRegion.getHeight() > region.getHeight())) {
    			continue;
    		}
    		if(((region.getLeft() - DEVIATION) > aboveRegion.getLeft()) || 
    				((region.getRight() + DEVIATION) < aboveRegion.getRight())) {
    			continue;
    		}
    		
    		int bottom = Math.min(region.getBottom(), aboveRegion.getBottom());
    		region.setBottom(bottom);
    		
    		mergedRegionIds.add(id);
    	}
    	
    	return mergedRegionIds;
    }
    
    private List<Integer> searchNeighbour(Region region) {
    	int h = REGION_GAP;
    	
    	List<Integer> neighbourIds = new ArrayList<Integer>();
    	for(int y = region.getBottom() - 1, bottom = Math.max(y - h, 0); y > bottom; y -= Parameter.minimumSupportedFaceHeight) {
    		for(int x = region.getLeft(), right = region.getRight(); x < right; x++) {
    			int id = marks[x * height + y];
    			if((id >= 0) && !neighbourIds.contains(id)) {
    				neighbourIds.add(id);
    				x += segments.get(id).getRight() + 1;
    			}
    		}
    	}
    	
    	return neighbourIds;
    }
    
    private List<Region> removeNestedRegions() {
    	List<Region> cleanRegions = new ArrayList<Region>();
    	for(Region region : segments) {
    		clean(cleanRegions, region);
    	}
    	return cleanRegions;
    }
    
    private void clean(List<Region> cleanRegions, Region r) {
    	if(cleanRegions.size() == 0) {
    		cleanRegions.add(r);
    		return;
    	}
    	
    	List<Region> nestedRegions = new ArrayList<Region>();
    	for(Region cleanOne : cleanRegions) {
    		int index = isNested(cleanOne, r);
    		if(index == 1) {
    			nestedRegions.add(cleanOne);
    		} else if(index == 2) {
    			return;
    		}
    	}
    	
    	for(Region nested : nestedRegions) {
    		cleanRegions.remove(nested);
    	}
    	cleanRegions.add(r);
    }
    
    /**
     * @param r1
     * @param r2
     * @return
     * 0: r1 and r2 are not nested.
     * 1: r1 is inside of r2.
     * 2: r2 is inside of r1.
     */
    private int isNested(Region r1, Region r2) {
    	    	
    	int left1 = r1.getLeft(), bottom1 = r1.getBottom();
    	int right1 = r1.getRight(), top1 = r1.getTop();
    	
    	int left2 = r2.getLeft(), bottom2 = r2.getBottom();
    	int right2 = r2.getRight(), top2 = r2.getTop();
    	
    	// r1 is inside of r2
    	if((left1 >= left2 - DEVIATION) && (right1 <= right2 + DEVIATION)) {
    		if((bottom1 >= bottom2 - DEVIATION) && (top1 <= top2 + DEVIATION)) {
    			return 1;
    		}
    	}
    	
    	// r2 is inside of r1
    	if((left2 >= left1 - DEVIATION) && (right2 <= right1 + DEVIATION)) {
    		if((bottom2 >= bottom1 - DEVIATION) && (top2 <= top1 + DEVIATION)) {
    			return 2;
    		}
    	}
    	
    	return 0;
    }
}
