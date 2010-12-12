package team.nm.nnet.app.imageCollector.layout;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.sf.fmj.ejmf.toolkit.install.PackageUtility;
import net.sf.fmj.ui.application.ContainerPlayer;
import net.sf.fmj.utility.PlugInUtility;
import team.nm.nnet.app.imageCollector.utils.ImageFilter;
import team.nm.nnet.util.ImageUtils;

import com.lti.civil.CaptureException;
import com.lti.civil.CaptureSystem;
import com.lti.civil.CaptureSystemFactory;
import com.lti.civil.DefaultCaptureSystemFactorySingleton;


public class MediaCapture extends Panel implements ActionListener {

	private static final long serialVersionUID = 8927815914445440376L;
	
	private static ContainerPlayer containerPlayer;
	private JPanel videoPanel;
	private JButton saveBtn;
	private JButton takeBtn;
	private Image img;
	private MainFrame parent;

	public void initialize() throws Exception {
		
		setLayout(new BorderLayout());

		saveBtn = new JButton("Lưu ảnh");
		takeBtn = new JButton("Chụp ảnh");
		saveBtn.addActionListener(this);
		takeBtn.addActionListener(this);

		try {
			containerPlayer = getContainerPlayer();
			setSize(350, 500);
			add(getVideoPanel(), BorderLayout.CENTER);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Không thể kết nối với camera", "Cannot connect to device", JOptionPane.ERROR_MESSAGE);
			throw new Exception("Cannot connect to device");
		}
		
		Panel footer = new Panel();
		footer.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 10));
		footer.add(saveBtn);
		footer.add(takeBtn);
		add(footer, BorderLayout.SOUTH);
	}

	public void play() {
		if(containerPlayer != null) {
			containerPlayer.start();
		}
	}
	
	public void stop() {
		if(containerPlayer != null) {
			containerPlayer.stop();
		}
	}
	
	public void close() {
		if(containerPlayer != null) {
			containerPlayer.close();
			containerPlayer.deallocate();
		}
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

	public void setParent(MainFrame parent) {
		this.parent = parent;
	}
	
	/*
	 * Internal method region
	 */
	
	protected void takeImage() {
		
		/* get the image on the Panel */
		BufferedImage bufferedImage = new BufferedImage(videoPanel.getWidth(),videoPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = bufferedImage.getGraphics();
		videoPanel.paint(g);
		g.dispose();
		
		img = ImageUtils.toImage(bufferedImage);
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
	
	protected JPanel getVideoPanel() {
		if (videoPanel == null) {
			videoPanel = new JPanel();
			videoPanel.setLayout(new BorderLayout());
			videoPanel.setBackground(SystemColor.controlShadow);
		}
		return videoPanel;
	}
	
	protected ContainerPlayer getContainerPlayer() throws CaptureException {

		PackageUtility.addContentPrefix("net.sf.fmj", false);
		PackageUtility.addProtocolPrefix("net.sf.fmj", false);
		
    	PlugInUtility.registerPlugIn("net.sf.fmj.media.renderer.video.SimpleAWTRenderer");
		
		CaptureSystemFactory factory = DefaultCaptureSystemFactorySingleton.instance();
		CaptureSystem system = factory.createCaptureSystem();
		system.init();
		
		containerPlayer = null;
		List<?> list = system.getCaptureDeviceInfoList();
		for (int i = 0, length = list.size(); i < length; ++i)
		{
			com.lti.civil.CaptureDeviceInfo civilInfo = (com.lti.civil.CaptureDeviceInfo) list.get(i);
			
			{
				if(civilInfo.getDescription().compareToIgnoreCase("Video WebCam") == 0) {
					try
					{
						containerPlayer = new ContainerPlayer(getVideoPanel());
						containerPlayer.setMediaLocation("civil:" + civilInfo.getDeviceID(), true);
					} catch (Exception e)
					{	
						throw new CaptureException(e);
					}
					break;
				}
			}
		}
		return containerPlayer;
	}

	
}
