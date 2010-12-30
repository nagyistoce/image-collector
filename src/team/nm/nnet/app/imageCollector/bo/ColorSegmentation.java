package team.nm.nnet.app.imageCollector.bo;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import team.nm.nnet.app.imageCollector.filter.BinaryImage;
import team.nm.nnet.app.imageCollector.filter.OpenFilter;
import team.nm.nnet.app.imageCollector.om.ColorSegment;
import team.nm.nnet.app.imageCollector.om.Pixel;
import team.nm.nnet.core.Const;

public class ColorSegmentation {

	private int noSegments;
    private List<ColorSegment> segments;
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

    public ColorSegmentation() {
        noSegments = 0;
        segments = new ArrayList<ColorSegment>();
    }
    
    public List<ColorSegment> segment(BufferedImage bufferedImage) {
        if(bufferedImage == null) {
            return null;
        }
        BinaryImage binaryImage = new BinaryImage(bufferedImage);
        binaryImage = OpenFilter.filter(binaryImage, Const.KERNEL, 1);
        bufImage = binaryImage.getBinaryBuffer();
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
                    ColorSegment coseg = new ColorSegment();
                    coseg.setId(noSegments++);
                    flood(x, y, coseg);
                    if(coseg.isValid()) {
                        coseg.setStartPoint(new Pixel(x - coseg.getLeft(), y - coseg.getBottom()));
                        
                        int value = coseg.getBottom();
                        coseg.setBottom((value > 0) ? value - 1 : value);
                        value = coseg.getLeft();
                        coseg.setLeft((value > 0) ? value - 1 : value);
                        value = coseg.getTop();
                        coseg.setTop((value + 1 < height) ? value + 1 : value);
                        value = coseg.getRight();
                        coseg.setRight((value + 1 < width) ? value + 1 : value);
                        
                    	segments.add(coseg);
                    }
                }
            }
        }
        
        // Finish segmenting
        state = false;
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

    public List<ColorSegment> getSegments() {
        return segments;
    }
    
    protected void initMarks(int size) {
        marks = new int[size];
        for(int i = 0; i < size; i++) {
            marks[i] = UNVISITED_PIXEL;
        }
    }

    protected void flood(int x, int y, ColorSegment colorSegment) {
    	Stack<Pixel> stack = new Stack<Pixel>();
    	pushStack(stack, x, y);
    	
    	while(state && !stack.isEmpty()) {
    		Pixel p = stack.pop();
    		int xx = p.getX();
    		int yy = p.getY();
    		
    		marks[xx * height + yy] = colorSegment.getId();
    		colorSegment.addPixel(new Pixel(xx, yy));

    		// Cập nhật lại các cận của phân vùng
			if(xx < colorSegment.getLeft()) {
				colorSegment.setLeft(xx);
			} else if(xx > colorSegment.getRight()) {
				colorSegment.setRight(xx);
			}
			if(yy < colorSegment.getBottom()) {
				colorSegment.setBottom(yy);
			} else if(yy > colorSegment.getTop()) {
				colorSegment.setTop(yy);
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
}
