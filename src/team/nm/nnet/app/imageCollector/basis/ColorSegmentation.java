package team.nm.nnet.app.imageCollector.basis;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import team.nm.nnet.app.imageCollector.om.ColorSegment;
import team.nm.nnet.util.ImageUtils;

public class ColorSegmentation {

    private int noSegments;
    private List<ColorSegment> segments;
    private int[] marks;
    private int[] pixels;
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
        bufferedImage = ImageUtils.toYCbCr(bufferedImage);
        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();
        pixels = null;
        pixels = bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);
        if(pixels == null) {
            return null;
        }
        
        initMarks(width * height);
        // Mark this thread is running
        state = true;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int index = x * height + y;
                if ((marks[index] == -1) && isWhite(pixels[index])) {
                    ColorSegment coseg = new ColorSegment();
                    coseg.setId(++noSegments);
                    flood(x, y, coseg);
                    segments.add(coseg);
                }
            }
        }
        // Finish segmenting
        state = false;
        return segments;
    }
    
    public boolean isWhite(int rgb) {
        Color pixelColour = new Color(rgb);
        return pixelColour.getBlue() / 255 == 1;
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
        System.out.println(String.format("[%d, %d]", x, y));
        if(!state) {
            return;
        }
        marks[x * height + y] = noSegments;
        
        // Cập nhật lại các cận của phân vùng
        if(x < colorSegment.getxLeft()) {
            colorSegment.setxLeft(x);
        } else if(x > colorSegment.getxRight()) {
            colorSegment.setxRight(x);
        }
        if(y < colorSegment.getyBottom()) {
            colorSegment.setyBottom(y);
        } else if(y > colorSegment.getyTop()) {
            colorSegment.setyTop(y);
        }
        
        int index = x * height + (y + 1);
        System.out.println(String.format("[%d, %d]: white: %s, marks[%d]: %d", x, y, String.valueOf(isWhite(pixels[index])), index, marks[index]));
        if(((y + 1) < height) && (marks[index] == -1) && isWhite(pixels[index])) {
            flood(x, y + 1, colorSegment);
        }
        index = x * height + (y - 1);
        System.out.println(String.format("[%d, %d]: white: %s, marks[%d]: %d", x, y, String.valueOf(isWhite(pixels[index])), index, marks[index]));
        if(((y - 1) >= 0) && (marks[index] == -1) && isWhite(pixels[index])) {
            flood(x, y - 1, colorSegment);
        }
        index = (x + 1) * height + y;
        System.out.println(String.format("[%d, %d]: white: %s, marks[%d]: %d", x, y, String.valueOf(isWhite(pixels[index])), index, marks[index]));
        if((((x + 1) < width) && (y + 1) < height) && (marks[index] == -1) && isWhite(pixels[index])) {
            flood(x + 1, y, colorSegment);
        }
    }
}
