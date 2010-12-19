package team.nm.nnet.core;

/**
 * Store all constraints
 * @author MinhNhat
 *
 */
public class Const {
    /**
     * Height of face image
     */
    public static final int FACE_HEIGHT = 30;

    /**
     * Width of face image
     */
    public static final int FACE_WIDTH = 20;

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
    public static final int JUMP_LENGHT = 5;
    
    /**
     * Độ giãn nở kích thước khung dò khuôn mặt
     */
    public static final int SCANNER_GROWTH = 10;
    
    /**
     * Ngưỡng để xác định một khung hình có khả năng là khuôn mặt hay không
     */
    public static final double SCANNER_RATE_THRESHOLD = 0.6;
    
    /**
     * Số lượng pixel thấp nhất mà một vùng còn được xem là vùng khuôn mặt 
     */
    public static final int MINIMUM_SKIN_PIXEL_THRESHOLD = 100;
    
    /**
     * Độ giãn giữa tọa độ thật với độ tọa đóng khung khuôn mặt
     */
    public static final int SPAN_FACE_BOX = 5;
    
    /**
     * Luu duong dan file mask
     */
    public static final String MASK_IMAGE = System.getProperty("user.dir") + "/ref/imageStore/mask.jpg";
}
