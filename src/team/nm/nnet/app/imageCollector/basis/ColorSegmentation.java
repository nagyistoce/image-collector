package team.nm.nnet.app.imageCollector.basis;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import team.nm.nnet.app.imageCollector.om.ColorSegment;
import team.nm.nnet.app.imageCollector.om.Pixel;
import team.nm.nnet.app.imageCollector.utils.ColorDetection;

public class ColorSegmentation {

    private int noSegments;
    private List<ColorSegment> segments;
    private int[] marks;
    private BufferedImage bufImage;
    private int width, height;
    private volatile boolean state = false;
    
    public ColorSegmentation() {
        noSegments = 0;
        segments = new ArrayList<ColorSegment>();
    }
    
    public List<ColorSegment> segment(BufferedImage bufferedImage) {
        if(bufferedImage == null) {
            return null;
        }
        bufImage = ColorDetection.toYCbCr(bufferedImage);
        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();
        
        initMarks(width * height);
        // Mark this thread is running
        state = true;
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
            	if(!state) {
            		return segments;
            	}
                int index = x * height + y;
                if ((marks[index] == -1) && (bufImage.getRGB(x, y) == 0xffffffff)) {
                    ColorSegment coseg = new ColorSegment();
                    coseg.setId(++noSegments);
                    flood(x, y, coseg);
                    System.out.println(String.format("[%d -> %d, %d -> %d]: %d, ", x, coseg.getWidth(), y, coseg.getHeight(), noSegments));
                    if(coseg.isValid()) {
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
            marks[i] = -1;
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
			
			// Loang theo phương ngang
			pushStack(stack, xx, yy - 1);
			pushStack(stack, xx, yy + 1);
			
			// Loang hướng lên trên
			pushStack(stack, xx - 1, yy - 1);
			pushStack(stack, xx - 1, yy);
			pushStack(stack, xx - 1, yy + 1);
			
			// Loang hướng xuống dưới
			pushStack(stack, xx + 1, yy - 1);
			pushStack(stack, xx + 1, yy);
			pushStack(stack, xx + 1, yy + 1);
    	}
    }
    
    private void pushStack(Stack<Pixel> stack, int x, int y) {
    	int index = x * height + y;
		boolean isX = (0 <= x) && (x < width);
		boolean isY = (0 <= y) && (y < height);
		
		if(isX && isY && (marks[index] == -1) && (bufImage.getRGB(x, y) == 0xffffffff)) {
			stack.push(new Pixel(x, y));
		}
    }
}
