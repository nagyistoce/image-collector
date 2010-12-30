package team.nm.nnet.util;

public class Matrix<E> {

    /**
     * Giá trị của ma trận
     */
    private E[][] values;
    
    /**
     * Chiều rộng của matran
     */
    private int width;
    
    /**
     * Chiều cao của matran
     */
    private int height;
    
    /**
     * Phương thức khởi tạo cho matran
     * @param value Giá trị của ma tran
     * @param width Chiều rộng ma tran
     * @param height Chiều cao ma tran
     */
    public Matrix(int width, int height, E[][] values) {
        this.values = values;
        this.width = width;
        this.height = height;
    }

    public void setValues(E[][] values) {
        this.values = values;
    }

    public E[][] getValues() {
        return values;
    }
    
    public E get(int x, int y) {
        if((x > width) || (y > height)) {
            return null;
        }
        return values[x][y];
    }
    
    public void set(int x, int y, E value) {
        values[x][y] = value;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }
    
}
