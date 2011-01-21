package team.nm.nnet.app.imageCollector.test;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import sol.hawking.snatcher.core.Application;
import team.nm.nnet.util.ImageUtils;


public class TestBase {
	
	static Application app;
	
	static{
		System.out.println("//////////  Starting Image Collector Application  ///////////");
		app = new Application("team/nm/nnet/app/imageCollector/config/application.cfg.xml");
        app.setPropertiesLocations(new String[]{"/team/nm/nnet/app/imageCollector/config/application.properties"});
		app.start();
		System.out.println("//////////  Image Collector Application Started ///////////");
	}
	
	public static void showImage(String title, BufferedImage bufImg) {
		JFrame frm = new JFrame(title);
		frm.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		JLabel lbl = new JLabel(new ImageIcon(ImageUtils.toImage(bufImg)));
		frm.add(lbl);
		frm.setSize(bufImg.getWidth(null), bufImg.getHeight(null));
		frm.setVisible(true);		
	}
}
