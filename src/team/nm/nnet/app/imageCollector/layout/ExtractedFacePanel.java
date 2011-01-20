package team.nm.nnet.app.imageCollector.layout;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;

import team.nm.nnet.core.Const;
import team.nm.nnet.core.LearnFace;
import team.nm.nnet.util.ImageUtils;

public class ExtractedFacePanel extends javax.swing.JPanel {

	private static final long serialVersionUID = 8434384268590865473L;
	static ExecutorService executorService = Executors.newCachedThreadPool();
	private BufferedImage imageLable;
	private static String currentDirrectory = "";
	
    public ExtractedFacePanel(JPanel parent, Image faceImage) {
        initComponents();
        this.parent = parent;
        setFaceImage(faceImage);
    }
    
    public void setFaceImage(Image faceImage) {
    	BufferedImage bufferedImage = ImageUtils.toBufferedImage(faceImage);
    	imageLable = bufferedImage;
    	bufferedImage = ImageUtils.resize(bufferedImage, Const.SHOWING_FACE_WIDTH, Const.SHOWING_FACE_HEIGHT);
        faceImage = ImageUtils.toImage(bufferedImage);
    	lblFaceView.setIcon(new ImageIcon(faceImage));
    }
    
    public void displayCheckbox(boolean visible) {
    	chbChoose.setVisible(visible);
    }
    
    public boolean isSelected() {
    	return chbChoose.isSelected();
    }

    // <editor-fold>
    private void initComponents() {

        lblFaceView = new javax.swing.JLabel();
        chbChoose = new javax.swing.JCheckBox();
        txtFaceName = new javax.swing.JTextField("Chưa biết");
        btnUpdate = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();

        btnUpdate.setIcon(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "check.gif"));
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnRemove.setIcon(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "cancel.jpg"));
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        /********************Add by Nhat********************/
        lblFaceView.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		if (e.getButton() == MouseEvent.BUTTON1) {
        			if (currentDirrectory == "") {
        				currentDirrectory = "D:\\";
        			}
        			JFileChooser fileChooser = new JFileChooser();
        			fileChooser.setDialogType(JFileChooser.DIRECTORIES_ONLY);
        			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        			fileChooser.setCurrentDirectory(new File(currentDirrectory));
        			int result = fileChooser.showOpenDialog(null); 
        			if (result == JFileChooser.APPROVE_OPTION) {
        				String path = fileChooser.getSelectedFile().getAbsolutePath();
        				ImageUtils.saveToJpg(imageLable, new File(path + "\\" + System.currentTimeMillis() + ".jpg"));
        				currentDirrectory = path;
        			}
        		}
        	}
		});
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chbChoose)
                        .addGap(39, 39, 39)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(lblFaceView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtFaceName, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblFaceView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFaceName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(chbChoose)
                    .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {
    	String name = txtFaceName.getText();
    	if(StringUtils.isBlank(name)) {
    		return;
    	}
        faceName = name;
        
        final LearnFace learnFace = LearnFace.getInstance();
        learnFace.addImageToTrainFolder("D:/Images/trainFolder", ImageUtils.iconToBufferedImage(lblFaceView.getIcon()), name);
        executorService.execute(new Runnable() {
			@Override
			public void run() {
				learnFace.run();
			}
		});
        chbChoose.setEnabled(true);
    }

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {
        parent.remove(this);
        parent.updateUI();
    }


    // Variables declaration
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JCheckBox chbChoose;
    private javax.swing.JLabel lblFaceView;
    private javax.swing.JTextField txtFaceName;
    
    private String facePath;
    private String faceName;
    private int faceId;
    private JPanel parent;
    // End of variables declaration

	public String getFacePath() {
		return facePath;
	}

	public void setFacePath(String facePath) {
		this.facePath = facePath;
	}

	public String getFaceName() {
		return faceName;
	}

	public void setFaceName(String faceName) {
		this.faceName = faceName;
		txtFaceName.setText(faceName);
	}

	public int getFaceId() {
		return faceId;
	}

	public void setFaceId(int faceId) {
		this.faceId = faceId;
		chbChoose.setEnabled(faceId > 0);
	}

}
