package team.nm.nnet.core;

import team.nm.nnet.util.Matrix;

/**
 * Store all constraints
 * @author MinhNhat
 *
 */
public class Const {
	/**
	 * Thư mục hiện hành
	 */
	public static final String CURRENT_DIRECTORY = System.getProperty("user.dir");
	
	/**
	 * Đường dẫn đến thư mục chứa các resource
	 */
	public static final String RESOURCE_PATH = "/src/team/nm/nnet/app/imageCollector/resources/";
	
    /**
     * Height of face image
     */
    public static final int FACE_HEIGHT = 30;

    /**
     * Width of face image
     */
    public static final int FACE_WIDTH = 20;
    
    public static final int WHITE_COLOR = 0xffffffff;

    /**
     * Number of hidden neural
     */
    public static final int FACE_CLASSIFY_NUMBER_OF_HIDDEN_NEURAL = 800;
    
    /**
     * So luong neural output do de la 2 no se khong chay
     */
    public static final int FACE_CLASSIFY_NUMBER_OF_OUTPUT_NEURAL = 20;
    
    /**
     * Luu gia tri bias
     */
    public static final int FACE_CLASSIFY_BIAS = 30;
    
    /**
     * So luong layer cua network
     */
    public static final int NUMBER_OF_LAYER = 3;
    
    /**
     * Buoc nhay frame anh
     */
    public static final int JUMP_LENGHT = 10;
    
    /**
     * Độ giãn nở kích thước khung dò khuôn mặt
     */
    public static final int SCANNER_GROWTH = 10;
    
    public static final double MINIMUM_ASPECT_RATIO = 0.6;
    public static final double MAXIMUM_ASPECT_RATIO = 1.8;
    
    /**
     * Khoảng giá trị của vùng được cho là khuôn mặt 
     */
    public static final int LOWER_STANDARD_DEVIATION = 30;
    public static final int UPPER_STANDARD_DEVIATION = 100;
    
    /**
     * Kích thước khuôn mặt hiển thị đến người dùng
     */
    public static final int SHOWING_FACE_WIDTH = 120;
    public static final int SHOWING_FACE_HEIGHT = 160;
    /**
     * Độ giãn giữa tọa độ thật với độ tọa đóng khung khuôn mặt
     */
    public static final int SPAN_FACE_BOX = 10;
    
    /**
     * Structural element of image processing
     */
    public static final Matrix<Integer> KERNEL = new Matrix<Integer>(3, 3, new Integer[][] {
            {0, 1, 0},
            {1, 1, 1},
            {0, 1, 0}
    });
    /**
     * Mang luu mask cua he thong
     */
    public static final int[] MASK = {
    	1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,
    	1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,
    	1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,
    	1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
    	1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    	1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,
    	1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,
    	1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,
    	1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,
    	1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,
    	1,1,1,1,1,1,0,0,0,0,0,0,0,0,1,1,1,1,1,1,
    };
    
}
