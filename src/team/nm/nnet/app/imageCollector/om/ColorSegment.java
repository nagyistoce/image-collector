package team.nm.nnet.app.imageCollector.om;

public class ColorSegment {

    private int id;
    private int xLeft, xRight;
    private int yTop, yBottom;
    
    public ColorSegment() {
        id = -1;
        xLeft = Integer.MAX_VALUE;
        xRight = -1;
        yBottom = Integer.MAX_VALUE;
        yTop = -1;
    }
    
    public int getWidth() {
        return xRight - xLeft;
    }
    
    public int getHeight() {
        return yTop - yBottom;
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getxLeft() {
        return xLeft;
    }
    public void setxLeft(int xLeft) {
        this.xLeft = xLeft;
    }
    public int getxRight() {
        return xRight;
    }
    public void setxRight(int xRight) {
        this.xRight = xRight;
    }
    public int getyTop() {
        return yTop;
    }
    public void setyTop(int yTop) {
        this.yTop = yTop;
    }
    public int getyBottom() {
        return yBottom;
    }
    public void setyBottom(int yBottom) {
        this.yBottom = yBottom;
    }
}
