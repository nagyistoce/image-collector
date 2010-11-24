package team.nm.nnet.app.imageCollector.layout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.media.Buffer;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import org.springframework.beans.factory.annotation.Required;

import team.nm.nnet.app.imageCollector.utils.ImageFilter;
import team.nm.nnet.util.ImageUtils;

public class MediaCapture extends Panel implements ActionListener {

	private static final long serialVersionUID = 8927815914445440376L;
	
	private static Player player;
	private MediaLocator mediaLocator;
	private Component comp;
	private JButton saveBtn;
	private JButton takeBtn;
	private Buffer buffer;
	private Image img;
	private BufferToImage buffToImg;
	private String locatorString;
	private MainFrame parent;

	public void initialize() {
		setLayout(new BorderLayout());

		saveBtn = new JButton("Lưu ảnh");
		takeBtn = new JButton("Chụp ảnh");
		saveBtn.addActionListener(this);
		takeBtn.addActionListener(this);

		mediaLocator = new MediaLocator(locatorString);

		try {
			player = Manager.createRealizedPlayer(mediaLocator);
			player.start();
			if ((comp = player.getVisualComponent()) != null) {
				setSize(comp.getSize());
				add(comp, BorderLayout.CENTER);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Panel footer = new Panel();
		footer.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 10));
		footer.add(saveBtn);
		footer.add(takeBtn);
		add(footer, BorderLayout.SOUTH);
	}

	public void play() {
		player.start();
	}
	
	public void stop() {
		player.stop();
	}
	
	public void close() {
		player.close();
		player.deallocate();		
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JComponent) {
			JComponent c = (JComponent) e.getSource();
			if (c == saveBtn) {
				final JFileChooser chooser = new JFileChooser(".");
				ImageFilter imageFilter = new ImageFilter();
				chooser.addChoosableFileFilter(imageFilter);
				int returnVal = chooser.showSaveDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selectedFile = chooser.getSelectedFile();
					if (imageFilter.accept(selectedFile)) {
						action(selectedFile);
					}
				}
			} else if (c == takeBtn) {
				takeImage();
				if(parent != null) {
					parent.displayImage(img, "Camera Image", 0);
				}
			}
		}
	}
	
	protected void takeImage() {
		// Grab a frame
		FrameGrabbingControl fgc = (FrameGrabbingControl) player.getControl("javax.media.control.FrameGrabbingControl");
		buffer = fgc.grabFrame();

		// Convert it to an image
		buffToImg = new BufferToImage((VideoFormat) buffer.getFormat());
		img = buffToImg.createImage(buffer);
	}

	protected void action(File file) {
		
		takeImage();

		// save image
		BufferedImage bufferedImage = ImageUtils.toBufferedImage(img);
		if (bufferedImage != null) {
			if(ImageFilter.getExtension(file).compareToIgnoreCase(ImageFilter.PNG) == 0) {
				ImageUtils.saveToPng(bufferedImage, file);
			} else {
				ImageUtils.drawImageToJpgByteStream(bufferedImage, file);
			}
		}

	}

	@Required
	public void setLocatorString(String locatorString) {
		this.locatorString = locatorString;
	}
	
	public void setParent(MainFrame parent) {
		this.parent = parent;
	}
}
