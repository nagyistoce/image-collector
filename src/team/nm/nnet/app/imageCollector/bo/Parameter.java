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
     * Số lượng pixel thấp nhất mà một vùng còn được xem là vùng khuôn mặt 
     */
    public static int minimumSkinPixelThreshold = 100;
    /**
     * Ngưỡng xác định kết xuất từ neural network là khuôn mặt
     */
    public static float networkFaceValidationThreshold = (float) 0.75;    
    /**
     * Độ giãn giữa tọa độ thật với độ tọa đóng khung khuôn mặt
     */
    public static int spanFaceBox = 10;
    
    static Properties params = new Properties();
    static File paramFile = new File(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "params.properties");
    static {
        FileReader fr;
        try {
            fr = new FileReader(paramFile);
            params.load(fr);
            
            String value = params.getProperty("minimumSkinPixelThreshold");
            minimumSkinPixelThreshold = NumberUtils.toInt(value, minimumSkinPixelThreshold);
            value = params.getProperty("networkFaceValidationThreshold");
            networkFaceValidationThreshold = NumberUtils.toFloat(value, networkFaceValidationThreshold);
            value = params.getProperty("spanFaceBox");
            spanFaceBox = NumberUtils.toInt(value, spanFaceBox);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void save() {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(paramFile);
            params.setProperty("minimumSkinPixelThreshold", String.valueOf(minimumSkinPixelThreshold));
            params.setProperty("networkFaceValidationThreshold", String.valueOf(networkFaceValidationThreshold));
            params.setProperty("spanFaceBox", String.valueOf(spanFaceBox));
            params.store(fos, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
