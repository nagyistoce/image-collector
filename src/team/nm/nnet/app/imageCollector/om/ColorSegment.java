package team.nm.nnet.app.imageCollector.om;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ColorSegment {

	private int id;
	private int left, right;
	private int top, bottom;
	private List<Pixel> pixels;
	private List<Pixel> brokenPoints;
	private Pixel startPoint;

	public ColorSegment() {
		id = -1;
		left = Integer.MAX_VALUE;
		right = -1;
		bottom = Integer.MAX_VALUE;
		top = -1;
		pixels = new ArrayList<Pixel>();
	}
	
	public ColorSegment(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
	public boolean isValid() {
		return (left != Integer.MAX_VALUE) && (right != -1) && (bottom != Integer.MAX_VALUE) && (top != -1);
	}
	
	public void addPixel(Pixel pixel) {
		pixels.add(pixel);
	}
	
	public double getStandardDev(BufferedImage bufferedImage) {
		double standardDeviation = 0;
		if(pixels.size() < 2) {
			return standardDeviation;
		}
		
		Color pColor;
		// Tính giá trị trung bình
        double mean = 0;
        for(Pixel p : pixels) {
        	pColor = new Color(bufferedImage.getRGB(p.getX(), p.getY()));
        	int rgb = (pColor.getRed() + pColor.getGreen() + pColor.getBlue());
        	mean += rgb;
        }
        mean /= pixels.size();
        
        // Tính độ lệch tiêu chuẩn
        for(Pixel p : pixels) {
        	pColor = new Color(bufferedImage.getRGB(p.getX(), p.getY()));
        	int rgb = (pColor.getRed() + pColor.getGreen() + pColor.getBlue());
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

	public List<Pixel> getBrokenPoints() {
		return brokenPoints;
	}

	public void setBrokenPoints(List<Pixel> brokenPoints) {
		this.brokenPoints = brokenPoints;
	}
}
