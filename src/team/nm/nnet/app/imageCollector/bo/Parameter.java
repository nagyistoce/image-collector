package team.nm.nnet.app.imageCollector.bo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.math.NumberUtils;

import team.nm.nnet.core.Const;

public class Parameter {
    /**
     * Số lượng điểm trắng thấp nhất mà một vùng còn được xem là vùng khuôn mặt 
     */
    public static int minimumSkinPixelThreshold = 100;
    /**
     * Tỷ lệ điểm trắng thấp nhất cho một phân vùng màu.
     */
    public static float minWhiteRatioThreshold = 0.4F;
    /**
     * Kích thước giới hạn hỗ trợ việc dò tìm khuôn mặt.
     */
    public static int minimumSupportedFaceWidth = 10;
    public static int minimumSupportedFaceHeight = 15;
    /**
     * Ngưỡng xác định kết xuất từ neural network là khuôn mặt
     */
    public static float networkFaceValidationThreshold = 0.75F;
    /**
     * Ngưỡng quy định xem ảnh có trong database hay không.
     */
    public static float networkFaceRecognitionThreshold = 0.8F;

    static Properties params = new Properties();
    static File paramFile = new File(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "params.properties");
    static {
    	if(paramFile != null) {
    		FileReader fr;
    		try {
    			fr = new FileReader(paramFile);
    			params.load(fr);
    			
    			String value = params.getProperty("minimumSkinPixelThreshold");
    			minimumSkinPixelThreshold = NumberUtils.toInt(value, minimumSkinPixelThreshold);
    			value = params.getProperty("minimumSupportedFaceWidth");
    			minimumSupportedFaceWidth = NumberUtils.toInt(value, minimumSupportedFaceWidth);
    			value = params.getProperty("minimumSupportedFaceHeight");
    			minimumSupportedFaceHeight = NumberUtils.toInt(value, minimumSupportedFaceHeight);
    			value = params.getProperty("minWhiteRatioThreshold");
    			minWhiteRatioThreshold = NumberUtils.toFloat(value, minWhiteRatioThreshold);
    			value = params.getProperty("networkFaceValidationThreshold");
    			networkFaceValidationThreshold = NumberUtils.toFloat(value, networkFaceValidationThreshold);
    			value = params.getProperty("networkFaceRecognitionThreshold");
    			networkFaceRecognitionThreshold = NumberUtils.toFloat(value, networkFaceRecognitionThreshold);
    			
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    public static void save() {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(paramFile);
            params.setProperty("minimumSkinPixelThreshold", String.valueOf(minimumSkinPixelThreshold));
            params.setProperty("minimumSupportedFaceWidth", String.valueOf(minimumSupportedFaceWidth));
            params.setProperty("minimumSupportedFaceHeight", String.valueOf(minimumSupportedFaceHeight));
            params.setProperty("minWhiteRatioThreshold", String.valueOf(minWhiteRatioThreshold));
            params.setProperty("networkFaceValidationThreshold", String.valueOf(networkFaceValidationThreshold));
            params.setProperty("networkFaceRecognitionThreshold", String.valueOf(networkFaceRecognitionThreshold));
            params.store(fos, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
