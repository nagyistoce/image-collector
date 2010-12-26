package team.nm.nnet.app.imageCollector;

import java.util.Timer;
import java.util.TimerTask;

import sol.hawking.snatcher.core.Application;
import team.nm.nnet.app.imageCollector.layout.FlashScreen;
import team.nm.nnet.app.imageCollector.layout.MainFrame;

public class Starter {

	public static void main(String[] args) {
		System.out.println("//////////  Starting Image Collector Application  ///////////");
		Application app = new Application("team/nm/nnet/app/imageCollector/config/application.cfg.xml");
        app.setPropertiesLocations(new String[]{"/team/nm/nnet/app/imageCollector/config/application.properties"});
		app.start();
		
		final MainFrame frmMain = (MainFrame) app.getBean("mainFrame");
		frmMain.setSize(1050, 650);
		
		final FlashScreen screen = new FlashScreen();
        screen.setVisible(true);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                screen.dispose();
                frmMain.setVisible(true);
            }
        }, 4500);
		
		System.out.println("//////////  Image Collector Application Started ///////////");
	}

}
