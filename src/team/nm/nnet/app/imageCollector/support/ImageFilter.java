package team.nm.nnet.app.imageCollector.support;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ImageFilter extends FileFilter {

	public final static String JPEG = "jpeg";
    public final static String JPG = "jpg";
    public final static String GIF = "gif";
    public final static String TIFF = "tiff";
    public final static String TIF = "tif";
    public final static String PNG = "png";

    // Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String name = f.getName().toLowerCase();
        if (name.endsWith(TIFF) || name.endsWith(TIF) ||
        	name.endsWith(GIF) || name.endsWith(JPEG) ||
    		name.endsWith(JPG) || name.endsWith(PNG)) {
                return true;
        } 

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "Just Images";
    }
    
}
