package team.nm.nnet.app.imageCollector.layout;

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import team.nm.nnet.core.Const;

public class Capture {

	private MediaCapture mediaCapture;
	private JFrame frame = null;
	
	public Capture() {
		this.mediaCapture = new MediaCapture();
	}
	
	public void show() {

		if(frame == null) {
			try {
				mediaCapture.initialize();		
			} catch(Exception e) {
				return;
			}
			frame = new JFrame();
	        frame.addWindowListener(new WindowAdapter() {
	 
	            public void windowClosing(WindowEvent e) {
	            	mediaCapture.stop();
	            	super.windowClosing(e);
	            }
	        });

	        frame.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
	        frame.setIconImage(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "icon.png").getImage());
	        frame.add("Center", mediaCapture);
	        //  Resize frame whenever new Component is added
	        mediaCapture.getVideoPanel().addContainerListener(
	            new ContainerListener() {
	                public void componentAdded(ContainerEvent e) {
	                    frame.pack();
	                }
	                public void componentRemoved(ContainerEvent e) {
	                    frame.pack();
	                }
	            }
	        );
	        frame.setTitle("Camera - NM Team");
	        frame.setSize(mediaCapture.getSize());
	        frame.pack();
		} else {
			mediaCapture.play();
		}
        
        frame.setVisible(true);
	}
	
	public void close() {
		if(frame != null) {
			frame.dispose();
		}
		if(mediaCapture != null) {
			mediaCapture.close();
		}
	}
	
	public void setParent(MainFrame parent) {
		mediaCapture.setParent(parent);
	}
}
