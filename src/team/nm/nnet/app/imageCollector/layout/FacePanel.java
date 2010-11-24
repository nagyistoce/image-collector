package team.nm.nnet.app.imageCollector.layout;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import team.nm.nnet.core.Const;
import team.nm.nnet.util.ImageUtils;

public class FacePanel extends javax.swing.JPanel {

	private static final long serialVersionUID = 8434384268590865473L;
	
    public FacePanel(JPanel parent, Image faceImage) {
        initComponents();
        this.parent = parent;
        setFaceImage(faceImage);
    }
    
    public void setFaceImage(Image faceImage) {
    	if((faceImage.getWidth(null) > Const.FACE_WIDTH) || (faceImage.getHeight(null)) > Const.FACE_HEIGHT) {
    	    BufferedImage bufferedImage = ImageUtils.toBufferedImage(faceImage);
            bufferedImage = ImageUtils.scale(bufferedImage, Const.FACE_WIDTH, Const.FACE_HEIGHT);
            faceImage = ImageUtils.toImage(bufferedImage);
        }
    	lblFaceView.setIcon(new ImageIcon(faceImage));
    }
    
    public void displayCheckbox(boolean visible) {
    	chbChoose.setVisible(visible);
    }

    // <editor-fold>
    private void initComponents() {

        lblFaceView = new javax.swing.JLabel();
        chbChoose = new javax.swing.JCheckBox();
        txtFaceName = new javax.swing.JTextField("Chưa biết");
        btnUpdate = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();

        btnUpdate.setText("V");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnRemove.setText("X");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chbChoose)
                .addGap(18, 18, 18)
                .addComponent(btnUpdate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRemove))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(lblFaceView, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtFaceName, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblFaceView, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtFaceName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(chbChoose)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
    }// </editor-fold>

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {
        faceName = txtFaceName.getText();
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

}
