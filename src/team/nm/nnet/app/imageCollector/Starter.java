package team.nm.nnet.app.imageCollector;

import sol.hawking.snatcher.core.Application;
import team.nm.nnet.app.imageCollector.layout.MainFrame;

public class Starter {

	public static void main(String[] args) {
		System.out.println("//////////  Starting Image Collector Application  ///////////");
		Application app = new Application("team/nm/nnet/app/imageCollector/config/application.cfg.xml");
        app.setPropertiesLocations(new String[]{"/team/nm/nnet/app/imageCollector/config/application.properties"});
		app.start();
		
		MainFrame frmMain = (MainFrame) app.getBean("mainFrame");
		frmMain.setSize(1050, 650);
		frmMain.setVisible(true);
		
		System.out.println("//////////  Image Collector Application Started ///////////");
	}

}
