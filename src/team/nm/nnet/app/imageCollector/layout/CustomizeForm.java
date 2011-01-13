package team.nm.nnet.app.imageCollector.layout;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;

import org.apache.commons.lang.math.NumberUtils;

import team.nm.nnet.app.imageCollector.bo.Parameter;
import team.nm.nnet.core.Const;


public class CustomizeForm extends javax.swing.JDialog {
	private static final long serialVersionUID = 1L;

	private static CustomizeForm instance = new CustomizeForm(); 
	private CustomizeForm() {
        initComponents();
     
        // Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        // Determine the new location of the window
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;
        this.setLocation(x, y);
    }

	public static CustomizeForm getInstance() {
		return instance;
	}
	
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setTitle("Tùy Chỉnh - NM Team");
        setIconImage(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "icon.png").getImage());
        getContentPane().setLayout(new java.awt.GridBagLayout());
        addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowOpened(WindowEvent e) {
        		super.windowOpened(e);
        		loadParameters();
        	}
		});

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Số điểm trắng ít nhất cho một phân vùng:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 0);
        jPanel1.add(jLabel1, gridBagConstraints);

        txtMinSkinPixel.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtMinSkinPixel.setText("100");
        txtMinSkinPixel.setPreferredSize(new java.awt.Dimension(10, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 54;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 3);
        jPanel1.add(txtMinSkinPixel, gridBagConstraints);

        jLabel2.setText("Độ rộng nhỏ nhất cho một phân vùng:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 0);
        jPanel1.add(jLabel2, gridBagConstraints);

        txtMinSupWidth.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtMinSupWidth.setText("100");
        txtMinSupWidth.setPreferredSize(new java.awt.Dimension(10, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 54;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 3);
        jPanel1.add(txtMinSupWidth, gridBagConstraints);

        jLabel3.setText("Chiều cao tối thiểu cho một phân vùng:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 0);
        jPanel1.add(jLabel3, gridBagConstraints);

        jLabel4.setText("Tỷ lệ điểm trắng thấp nhất cho một phân vùng:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 0);
        jPanel1.add(jLabel4, gridBagConstraints);

        jLabel5.setText("Ngưỡng dò tìm của mạng nơ-ron:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 0);
        jPanel1.add(jLabel5, gridBagConstraints);

        jLabel6.setText("Ngưỡng nhận dạng của mạng nơ-ron:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 0);
        jPanel1.add(jLabel6, gridBagConstraints);

        txtMinSupHeight.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtMinSupHeight.setText("100");
        txtMinSupHeight.setPreferredSize(new java.awt.Dimension(10, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 54;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 3);
        jPanel1.add(txtMinSupHeight, gridBagConstraints);

        txtMinWhiteRatio.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtMinWhiteRatio.setText("100");
        txtMinWhiteRatio.setPreferredSize(new java.awt.Dimension(10, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 54;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 3);
        jPanel1.add(txtMinWhiteRatio, gridBagConstraints);

        txtValidationThreshold.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtValidationThreshold.setText("100");
        txtValidationThreshold.setPreferredSize(new java.awt.Dimension(10, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 54;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 3);
        jPanel1.add(txtValidationThreshold, gridBagConstraints);

        txtRecognitionThreshold.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        txtRecognitionThreshold.setText("100");
        txtRecognitionThreshold.setPreferredSize(new java.awt.Dimension(10, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 54;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 3);
        jPanel1.add(txtRecognitionThreshold, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(jPanel1, gridBagConstraints);

        btnSave.setText("Lưu");
        btnSave.setToolTipText("Save");
        btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveParameters();
			}
		});
        jPanel2.add(btnSave);

        btnCancel.setText("Đóng");
        btnCancel.setToolTipText("Cancel");
        btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
        jPanel2.add(btnCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        getContentPane().add(jPanel2, gridBagConstraints);

        pack();
    }
    
    private void loadParameters() {
		txtMinSkinPixel.setText(String.valueOf(Parameter.minimumSkinPixelThreshold));
		txtMinSupWidth.setText(String.valueOf(Parameter.minimumSupportedFaceWidth));
		txtMinSupHeight.setText(String.valueOf(Parameter.minimumSupportedFaceHeight));
		txtMinWhiteRatio.setText(String.valueOf(Parameter.minWhiteRatioThreshold));
		txtValidationThreshold.setText(String.valueOf(Parameter.networkFaceValidationThreshold));
		txtRecognitionThreshold.setText(String.valueOf(Parameter.networkFaceRecognitionThreshold));
	}
    
    private void saveParameters() {
    	Parameter.minimumSkinPixelThreshold = NumberUtils.toInt(txtMinSkinPixel.getText(), Parameter.minimumSkinPixelThreshold);
    	Parameter.minimumSupportedFaceWidth = NumberUtils.toInt(txtMinSupWidth.getText(), Parameter.minimumSupportedFaceWidth);
    	Parameter.minimumSupportedFaceHeight = NumberUtils.toInt(txtMinSupHeight.getText(), Parameter.minimumSupportedFaceHeight);
    	Parameter.minWhiteRatioThreshold = NumberUtils.toFloat(txtMinWhiteRatio.getText(), Parameter.minWhiteRatioThreshold);
    	Parameter.networkFaceValidationThreshold = NumberUtils.toFloat(txtValidationThreshold.getText(), Parameter.networkFaceValidationThreshold);
    	Parameter.networkFaceRecognitionThreshold = NumberUtils.toFloat(txtRecognitionThreshold.getText(), Parameter.networkFaceRecognitionThreshold);
    	Parameter.save();
    }

    // Variables declaration
    private javax.swing.JButton btnCancel = new javax.swing.JButton();
    private javax.swing.JButton btnSave = new javax.swing.JButton();
    private javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
    private javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
    private javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
    private javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
    private javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
    private javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
    private javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
    private javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
    private javax.swing.JTextField txtMinSkinPixel = new javax.swing.JTextField();
    private javax.swing.JTextField txtMinSupHeight = new javax.swing.JTextField();
    private javax.swing.JTextField txtMinSupWidth = new javax.swing.JTextField();
    private javax.swing.JTextField txtMinWhiteRatio = new javax.swing.JTextField();
    private javax.swing.JTextField txtRecognitionThreshold = new javax.swing.JTextField();
    private javax.swing.JTextField txtValidationThreshold = new javax.swing.JTextField();
    // End of variables declaration

}
