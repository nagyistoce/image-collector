package team.nm.nnet.app.imageCollector.om;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import sole.hawking.image.filter.EdgeFilter;
import sole.hawking.image.util.ImageUtils;
import team.nm.nnet.app.imageCollector.utils.ColorSpace;
import team.nm.nnet.core.Const;

public class Region {
    
    private final int BRUSH_WIDTH = 2;
    private final int SLOPE_HEIGHT = 6;
    private final int HOLE_DIAMETER = 6;
    
	private int id;
	private int left, right;
	private int top, bottom;
	private List<Pixel> pixels;
	private List<Pixel> leftBoundary;
	private List<Pixel> rightBoundary;
	private Pixel startPoint;

	public Region() {
		id = -1;
		left = Integer.MAX_VALUE;
		right = -1;
		bottom = Integer.MAX_VALUE;
		top = -1;
		pixels = new ArrayList<Pixel>();
	}
	
	public Region(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
	public boolean isValid() {
		return (left != Integer.MAX_VALUE) && (right != -1) && (bottom != Integer.MAX_VALUE) && (top != -1) && (getWidth() > 0) && (getHeight() > 0);
	}
	
	public void addPixel(Pixel pixel) {
		pixels.add(pixel);
	}

	public double getStandardDev(BufferedImage bufferedImage) {
		double standardDeviation = 0;
		if(pixels.size() < 2) {
			return standardDeviation;
		}
		
		// Tính giá trị trung bình
        double mean = 0;
        for(Pixel p : pixels) {
        	int rgb = bufferedImage.getRGB(p.getX(), p.getY());
        	mean += rgb;
        }
        mean /= pixels.size();
        
        // Tính độ lệch tiêu chuẩn
        for(Pixel p : pixels) {
        	int rgb = bufferedImage.getRGB(p.getX(), p.getY());
        	standardDeviation += Math.pow(rgb - mean, 2);
        }

        standardDeviation = Math.sqrt(standardDeviation / (pixels.size() - 1));
		
		return standardDeviation;
	}

	public int getWidth() {
		return right - left;
	}

	public int getHeight() {
		return top - bottom;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getRight() {
		return right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getBottom() {
		return bottom;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
	}

	public List<Pixel> getPixels() {
		return pixels;
	}

	public void setPixels(List<Pixel> pixels) {
		this.pixels = pixels;
	}

    public Pixel getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Pixel startPoint) {
        this.startPoint = startPoint;
    }
	
	public List<Pixel> getBrokenPoints(BufferedImage bufferedImage) {
	    if(bufferedImage != null) {
           return findBrokenPoints(bufferedImage); 
        }
        
	    return null;
	}
	
	// ----- Internal methods
	
	private List<Pixel> getBoundary(BufferedImage edgeBuffer) {
		BufferedImage edgeBuff = ImageUtils.cloneImage(edgeBuffer);
		
        Stack<Pixel> stack = new Stack<Pixel>();
        int w = getWidth();
        int h = getHeight();
        
        // Walk along left side
        int x = startPoint.getX();
        int y = startPoint.getY();
        stack.push(new Pixel(x - 1, y));
        
        leftBoundary = new ArrayList<Pixel>();
        edgeBuff.setRGB(x, y, 0);
        while(!stack.isEmpty()) {
            Pixel p = stack.pop();
            leftBoundary.add(p);
            
            x = p.getX();
            y = p.getY();
            if((x != startPoint.getX()) && (y != startPoint.getY())) {
                if((x <= 0) || (x >= w) || (y <= 0) || (y >= h)) {
                    break;
                }
            }
            
            // Flood on 8 directs to search the next boundary point.
            int[] xWay = {1, 0, -1, 1, 0, -1,  1,  0, -1};
            int[] yWay = {1, 1,  1, 0, 0,  0, -1, -1, -1};
            for(int i = 0, n = xWay.length; i < n; i++) {
                int xx = x + xWay[i];
                int yy = y + yWay[i];
                if((0 <= xx) && (xx < w) && (0 <= yy) && (yy < h)) {
                    if(edgeBuff.getRGB(xx, yy) == Const.WHITE_COLOR) {
                        stack.push(new Pixel(xx, yy));
                        edgeBuff.setRGB(xx, yy, 0);
                    }
                }
            }
        }

        stack.clear();
        // Walk along right side
        x = startPoint.getX();
        y = startPoint.getY();
        stack.push(new Pixel(x + 1, y));
        
        rightBoundary = new ArrayList<Pixel>();
        edgeBuff.setRGB(x, y, 0);
        while(!stack.isEmpty()) {
            Pixel p = stack.pop();
            rightBoundary.add(p);
            
            x = p.getX();
            y = p.getY();
            if((x != startPoint.getX()) && (y != startPoint.getY())) {
                if((x <= 0) || (x >= w) || (y <= 0) || (y >= h)) {
                    break;
                }
            }
            
            // Flood on 8 directs to search the next boundary point.
            int[] xWay = {-1,  0,  1, -1, 0, 1, -1, 0, 1};
            int[] yWay = {-1, -1, -1,  0, 0, 0,  1, 1, 1};
            for(int i = 0, n = xWay.length; i < n; i++) {
                int xx = x + xWay[i];
                int yy = y + yWay[i];
                if((0 <= xx) && (xx < w) && (0 <= yy) && (yy < h)) {
                    if(edgeBuff.getRGB(xx, yy) == Const.WHITE_COLOR) {
                        stack.push(new Pixel(xx, yy));
                        edgeBuff.setRGB(xx, yy, 0);
                    }
                }
            }
        }
        
        List<Pixel> boundary = new ArrayList<Pixel>();
        boundary.addAll(leftBoundary);
        boundary.add(startPoint);
        boundary.addAll(rightBoundary);
        return boundary;
	}

    private List<Pixel> findBrokenPoints(BufferedImage bufferedImage) {
        if(!isValid()) {
            return null;
        }
        
        BufferedImage edgeBuff = bufferedImage.getSubimage(left, bottom, getWidth(), getHeight());
        edgeBuff = ColorSpace.toYCbCr(edgeBuff);
        EdgeFilter edgeFilter = new EdgeFilter();
        edgeBuff = edgeFilter.filter(edgeBuff, null);

        getBoundary(edgeBuff);
        List<Pixel> brokenPoints = new ArrayList<Pixel>();
        
        int w = getWidth();
        int h = getHeight();
        
        // Walk along left side
        Pixel passedPoint = new Pixel(-1, -1);
        int uphill = 0;
        boolean firstUp = false;
        Pixel cutPoint = null;
        int xThreshold = Integer.MAX_VALUE;

        for(Pixel p : leftBoundary) {
            int x = p.getX();
            int y = p.getY();
            if((x != startPoint.getX()) && (y != startPoint.getY())) {
                if((x <= 0) || (x >= w) || (y <= 0) || (y >= h)) {
                    break;
                }
            }
            if(x <= xThreshold) {
                xThreshold = x;
                if(y < passedPoint.getY()) {
                    uphill++;
                    if(!firstUp) {
                        cutPoint = p;
                        firstUp = true;
                    }
                } else if(y > passedPoint.getY()) {
                    uphill = 0;
                    firstUp = false;
                }
                passedPoint = p;
                if(uphill == SLOPE_HEIGHT) {
                    if(isBrokenBlock(edgeBuff, cutPoint.getX() + 2, cutPoint.getY() + 2, getWidth()) &&
                		(!isHole(edgeBuff, cutPoint.getX(), cutPoint.getY() - 3, HOLE_DIAMETER) ||
                		 !isHole(edgeBuff, cutPoint.getX() + 1, cutPoint.getY() - 3, HOLE_DIAMETER))) {
                        brokenPoints.add(cutPoint);
                        uphill = 0;
                        firstUp = false;
                    }
                }
            } else {
                uphill = 0;
                firstUp = false;
            }
        }

        // Walk along right side
        passedPoint = new Pixel(-1, -1);
        uphill = 0;
        firstUp = false;
        cutPoint = null;
        xThreshold = Integer.MIN_VALUE;
        
        for(Pixel p : rightBoundary) {
            int x = p.getX();
            int y = p.getY();
            if((x != startPoint.getX()) && (y != startPoint.getY())) {
                if((x <= 0) || (x >= w) || (y <= 0) || (y >= h)) {
                    break;
                }
            }
            if(x >= xThreshold) {
                xThreshold = x;
                if(y < passedPoint.getY()) {
                    uphill++;
                    if(!firstUp) {
                        cutPoint = p;
                        firstUp = true;
                    }
                } else if(y > passedPoint.getY()) {
                    uphill = 0;
                    firstUp = false;
                }
                passedPoint = p;

                if(uphill == SLOPE_HEIGHT) {
                    if(isBrokenBlock(edgeBuff, cutPoint.getX() - 2, cutPoint.getY() + 2, getWidth()) &&
	            		(!isHole(edgeBuff, cutPoint.getX(), cutPoint.getY() - 3, HOLE_DIAMETER) ||
	               		 !isHole(edgeBuff, cutPoint.getX() - 1, cutPoint.getY() - 3, HOLE_DIAMETER))) {
                        brokenPoints.add(cutPoint);
                        uphill = 0;
                        firstUp = false;
                    }
                }
            } else {
                uphill = 0;
                firstUp = false;
            }
        }
        
        return brokenPoints;
    }
    
    /**
     * The format of broken blocks is:
     * 1 1 1 0 1 1 1
     * 1 1 1 0 1 1 1
     * 0 0 1 1 1 0 0
     * 0 0 1 1 1 0 0
     */

    protected boolean isBrokenBlock(BufferedImage boundary, int x, int y, int width) {
        if(boundary.getRGB(x, y) != Const.WHITE_COLOR) {
            return false;
        }
        
        boolean flag = isBlock(boundary, x, y, width) &&
                       isBlock(boundary, x - BRUSH_WIDTH, y - BRUSH_WIDTH, width) &&
                       isBlock(boundary, x + BRUSH_WIDTH, y - BRUSH_WIDTH, width);
        
        return flag;
    }
    
    protected boolean isBlock(BufferedImage boundary, int x, int y, int width) {
        int[] xWay = {-1,  0,  1, -1, 0, 1};
        int[] yWay = {-1, -1, -1,  0, 0, 0};
        
        int count = 0;
        for(int i = 0, n = xWay.length; i < n; i++) {
            int xx = x + xWay[i];
            int yy = y + yWay[i];
            if((xx >= 0) && (xx < width) && (yy >= 0)) {
                if(boundary.getRGB(xx, yy) == Const.WHITE_COLOR) {
                    count++;
                }
            }
        }
        return count > 0;
    }
    
    protected boolean isHole(BufferedImage boundary, int x, int y, int diameter) {
        int count = 0;
        while((count++ < diameter) && (--y >= 0)) {
            if(boundary.getRGB(x, y) == Const.WHITE_COLOR) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean isSlope(BufferedImage boundary, Pixel startPoint, int height, int step) {
        int count = 0;
        int x = startPoint.getX();
        int y = startPoint.getY();
        
        do {
            if((x - 1 < 0) || (y - 1 < 0) || (y + 1 >= height)) {
                return false;
            }
            
            if(boundary.getRGB(x - 1, y - 1) == Const.WHITE_COLOR) {
                count++;
                x = x - 1;
                y = y - 1;
            }
            else if(boundary.getRGB(x - 1, y) == Const.WHITE_COLOR) {
                count++;
                x = x - 1;
            }
            else if(boundary.getRGB(x - 1, y + 1) == Const.WHITE_COLOR) {
                count++;
                x = x - 1;
                y = y + 1;
            } else {
                return false;
            }
        }while(count < step);
        
        return true;
    }
}
