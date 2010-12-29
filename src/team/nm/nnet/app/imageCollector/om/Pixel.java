package team.nm.nnet.app.imageCollector.om;

public class Pixel implements Comparable<Pixel> {
	int x, y;

	public Pixel() {
		x = -1;
		y = -1;
	}
	
	public Pixel(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

    @Override
    public int compareTo(Pixel o) {
        return this.x - o.x;
    }
}
