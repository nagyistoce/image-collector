package team.nm.nnet.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import team.nm.nnet.app.imageCollector.layout.MediaCapture;

public class IOUtils {

	private static Log   log = LogFactory.getLog(MediaCapture.class);
	
	/**
	 * Lấy các file name trong thư mục
	 * @param directoryPath Thu muc can lay
	 * @return Danh sach file name
	 */
    public static List<String> listFileName(String directoryPath) {        
        File dir = new File(directoryPath);
        File[] files = dir.listFiles();
        List<String> children = null;
        if(files != null) {
        	children = new ArrayList<String>();
        	for(File file : files) {
        		if(file.isFile()) {
        			children.add(file.getName());
        		}
        	}
        }
        return children;
    }
    
    /**
     * Xac dinh file co ton tai hay khong
     * @param filename File name can xac dinh
     * @return Ket qua
     */
    public static boolean exists(String filename) {
    	File file = new File(filename);
    	return file.exists();
    }
    
    /**
     * List cac folder con trong thu muc. Cac folder con cap 1 thoi nha
     * @param parentFolder Duong dan den folder cha
     * @return Ket qua kiem duoc
     */
    public static List<String> listSubFolder(String parentFolder) {
    	File parentDir = new File(parentFolder);
    	File[] listChildren = parentDir.listFiles();
    	List<String> listSubFolder = new ArrayList<String>();
    	if (listChildren != null) {
    		for (File fileI : listChildren) {
    			if (fileI.isDirectory()) {
    				listSubFolder.add(fileI.getName());
    			}
    		}
    	}
    	return listSubFolder;
    }
    
    public static void copy(String sourcePath, String[] fileNames, String destPath) {
		File dest = new File(destPath);
		if(!dest.exists()) {
			log.error("Destination doesn't exist!");
			return;
		}
		
		for(String fileName : fileNames) {
			File file = new File(sourcePath + fileName);
			File destFile = new File(destPath, fileName);
			if(!destFile.exists()) {
				InputStream in;
				try {
					in = new FileInputStream(file);
				
					//For Overwrite the file.
					OutputStream out = new FileOutputStream(destFile);
	
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0){
						out.write(buf, 0, len);
					}
					in.close();
					out.close();
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}
	}
	
    public static void delete(String sourcePath, String[] fileNames) {
		File dest = new File(sourcePath);
		if(!dest.exists()) {
			log.error("Destination doesn't exist!");
			return;
		}
		
		for(String fileName : fileNames) {
			File destFile = new File(sourcePath, fileName);
			if(destFile.exists()) {
				destFile.delete();
			}
		}
	}
}
