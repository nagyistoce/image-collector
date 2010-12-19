package team.nm.nnet.app.imageCollector.om;

public class ColorSegment {

	private int id;
	private int left, right;
	private int top, bottom;

	public ColorSegment() {
		id = -1;
		left = Integer.MAX_VALUE;
		right = -1;
		bottom = Integer.MAX_VALUE;
		top = -1;
	}

	public int getWidth() {
		return right - left;
	}

	public int getHeight() {
		return top - bottom;
	}

	public boolean isValid() {
		return (left != Integer.MAX_VALUE) && (right != -1) && (bottom != Integer.MAX_VALUE) && (top != -1);
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
}
