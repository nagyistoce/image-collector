package team.nm.nnet.app.imageCollector.layout;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

import team.nm.nnet.app.imageCollector.bo.ImageDB;
import team.nm.nnet.core.Const;
import team.nm.nnet.util.ImageUtils;

public class DBViewer extends javax.swing.JFrame {

	private static final long serialVersionUID = -1739927312038881270L;
	private DefaultListModel listModel;
	private ImageDB imageDB;
	
    public DBViewer(ImageDB imageDB) {
        initComponents();
        this.imageDB = imageDB;
        initList(imageDB);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstFiles = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtPath = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        btnRemove = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        lblImage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ảnh trong CSDL");
        setIconImage(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "icon.png").getImage());
        setMinimumSize(new java.awt.Dimension(850, 705));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Danh sách ảnh");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        jPanel1.add(jLabel1, gridBagConstraints);

        lstFiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstFiles.setToolTipText("The images are in database");
        lstFiles.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstFilesValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(lstFiles);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 135;
        gridBagConstraints.ipady = 488;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(13, 0, 0, 0);
        jPanel1.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel3.setPreferredSize(new java.awt.Dimension(554, 30));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jLabel2.setText("Đường dẫn: ");
        jPanel3.add(jLabel2);

        txtPath.setBackground(new java.awt.Color(255, 255, 255));
        txtPath.setEditable(false);
        txtPath.setText("Đường dẫn của ảnh hiện tại");
        txtPath.setToolTipText("The current image path");
        txtPath.setPreferredSize(new java.awt.Dimension(80, 20));
        jPanel3.add(txtPath);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(jPanel3, gridBagConstraints);

        btnRemove.setText("Xóa khỏi CSDL");
        btnRemove.setToolTipText("Remove from the image database");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });
        jPanel4.add(btnRemove);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel2.add(jPanel4, gridBagConstraints);

        lblImage.setPreferredSize(new java.awt.Dimension(600, 470));
        jPanel5.add(lblImage);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel2.add(jPanel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 9, 0, 0);
        getContentPane().add(jPanel2, gridBagConstraints);

        pack();
    }

    private void lstFilesValueChanged(javax.swing.event.ListSelectionEvent evt) {
        File file = imageDB.getFiles().get(lstFiles.getSelectedIndex());
        txtPath.setText(file.getPath());
        BufferedImage bufferedImage = ImageUtils.load(file);
        if(bufferedImage == null) {
        	JOptionPane.showMessageDialog(this, "Không thể hiển thị ảnh này!", "Failed", JOptionPane.ERROR_MESSAGE);
        } else {
        	int width = lblImage.getWidth();
        	int height = lblImage.getHeight();
        	if ((bufferedImage.getWidth(null) > width)
                    || (bufferedImage.getHeight(null)) > height) {
                bufferedImage = ImageUtils.scale(bufferedImage, width, height);
            }
        	lblImage.setIcon(new javax.swing.ImageIcon(ImageUtils.toImage(bufferedImage)));
        }
    }

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {
        int index = lstFiles.getSelectedIndex();
        if(index != -1) {
        	imageDB.getFiles().remove(index);
        	try {
        		listModel.remove(index);
        	} catch(Exception e) {
        		e.printStackTrace();
        	}
        	
        	lstFiles.setModel(listModel);
        	lstFiles.updateUI();
        	lstFiles.setSelectedIndex(0);
        	lstFiles.ensureIndexIsVisible(0);
        }
    }
    
    private void initList(ImageDB imageDB) {
    	listModel = new DefaultListModel();
    	
    	for(File file : imageDB.getFiles()) {
    		listModel.addElement(file.getName());
    	}
    	lstFiles.setModel(listModel);
    	lstFiles.setSelectedIndex(0);
    	lstFiles.ensureIndexIsVisible(0);
    }

    // Variables declaration - do not modify
    private javax.swing.JButton btnRemove;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblImage;
    private javax.swing.JList lstFiles;
    private javax.swing.JTextField txtPath;
    // End of variables declaration

}
