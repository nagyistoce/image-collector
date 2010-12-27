package team.nm.nnet.app.imageCollector.test;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import team.nm.nnet.app.imageCollector.bo.ImageStore;
import team.nm.nnet.app.imageCollector.utils.ColorSpace;
import team.nm.nnet.util.ImageUtils;

public class TestImgageStore extends TestBase {
	
	private static String classPath;
	static {
		classPath = System.getProperty("user.dir");
		classPath = classPath.replace('\\', '/');
		classPath += "/ref/imageStore"; 
	}
	
	public static void main(String[] args) {
		ImageStore imageStore = (ImageStore) app.getBean("imageStore");
        long count = imageStore.createJpgFaces(classPath + "/faces", classPath + "/faces/output");
        System.out.println("--- " + count + " faces were created!");
        count = imageStore.createNonFaces(classPath + "/nonFaces", classPath + "/nonFaces/output");
        System.out.println("--- " + count + " non-faces were created!");
        
//    	testFunction();
        System.out.println("---- Finished");
    }
    
    static void testFunction() {
    	String filename = "ref/imageStore/faces/a.jpg";
        BufferedImage bufferedImage = ImageUtils.load(filename);        
        
        show(ColorSpace.toGrayScale(bufferedImage));
        
        BufferedImage changedBuffer = ImageUtils.resize(bufferedImage, 300, 200);
        ImageUtils.drawImageToJpgByteStream(changedBuffer, new File(classPath + "/faces/a1.jpg"));
        show(changedBuffer);
        
        changedBuffer = ImageUtils.scale(bufferedImage, 150, 100);
        ImageUtils.drawImageToJpgByteStream(changedBuffer, new File(classPath + "/faces/a2.jpg"));
        show(changedBuffer);
        
        changedBuffer = ImageUtils.rotate(bufferedImage, 30);
        ImageUtils.drawImageToJpgByteStream(changedBuffer, new File(classPath + "/faces/a3.jpg"));
        show(changedBuffer);
        changedBuffer = ImageUtils.rotateAndScale(bufferedImage, 30);
        ImageUtils.saveToPng(changedBuffer, new File(classPath + "/faces/a33.jpg"));
        show(changedBuffer);
        show(ImageUtils.flip(changedBuffer, false));
        show(ImageUtils.flip(changedBuffer, true));
    }
    
    public static void show(BufferedImage bufferedImage) {
        int height = bufferedImage.getHeight(null);
        int width = bufferedImage.getWidth(null);
        JFrame frame = new JFrame(width + " x " + height);
        frame.getContentPane().add(new JLabel( new ImageIcon(bufferedImage)));
        frame.setSize(width + 50, height + 50);
        frame.setVisible(true);
    }
}
