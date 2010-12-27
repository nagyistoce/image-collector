package team.nm.nnet.app.imageCollector;

import java.util.Timer;
import java.util.TimerTask;

import sol.hawking.snatcher.core.Application;
import team.nm.nnet.app.imageCollector.layout.FlashScreen;

public class Starter {

	public static void main(String[] args) {
		System.out.println("//////////  Starting Image Collector Application  ///////////");
		Application app = new Application("team/nm/nnet/app/imageCollector/config/application.cfg.xml");
        app.setPropertiesLocations(new String[]{"/team/nm/nnet/app/imageCollector/config/application.properties"});
		app.start();
		
		final FlashScreen screen = (FlashScreen) app.getBean("flashScreen");
        screen.setVisible(true);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
            	if(screen.isShowing()) {
            		screen.dispose();
            	}
            }
        }, 4500);
		
		System.out.println("//////////  Image Collector Application Started ///////////");
	}

}
