package team.nm.nnet.app.imageCollector.layout;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

import team.nm.nnet.app.imageCollector.support.NMHyperlinkListener;
import team.nm.nnet.core.Const;

public class AboutUs extends javax.swing.JDialog {
	private static final long serialVersionUID = 6546434093998651359L;
	
	private static final String message = "<html><div style=\"font-family:Arial;font-size:12px;\"><p align=center style=\"text-align:center;line-height:normal\">Ứng dụng là phần hiện thực từ đề tài tốt nghiệp \"<b><span style=\"color:red\">Dò tìm và nhận dạng khuôn mặt</span></b>\" của <span style=\"color:#E36C0A\">nhóm NM</span>,<br/> được thực hiện từ 10/2010 đến 1/2011 với sự tham gia của các cá nhân sau:</p><br/>" +
			"<table align=center border=1 cellspacing=0 cellpadding=0 style=\"border-collapse:collapse;border:none\">" +
			"<tr><td width=638 colspan=2 valign=top style=\"border:solid #4BACC6 1px;background:#4BACC6;\"><p align=center style=\"text-align:center;line-height:normal\"><b><span style=\"color:white\">Giảng viên hướng dẫn</span></b></p></td></tr>" +
			"<tr><td width=638 colspan=2 valign=top style=\"border:solid #4BACC6 1px;\">" +
			"<p align=center style=\"text-align:center;line-height:normal\"><b>CN. Mai Ngọc Thu</b></p>" +
			"<p align=center style=\"text-align:center;line-height:normal\">Email: <a href=\"mailto:mnthu@hcmhutech.edu.vn\"><span style=\"color:#003366\">mnthu@hcmhutech.edu.vn</span></a></p>" +
			"<p align=center style=\"text-align:center;line-height:normal\">Yahoo: <a href=\"mailto:maicathu@yahoo.com\">maicathu@yahoo.com</a></p><br/>" +
			"</td></tr>" +
			"<tr><td width=638 colspan=2 valign=top style=\"border:solid #4BACC6 1px;background:#4BACC6;\">" +
			"<p align=center style=\"text-align:center;line-height:normal\"><b><span style=\"color:white\">Thành viên nhóm NM</span></b></p>" +
			"</td></tr>" +
			"<tr><td width=319 valign=top style=\"border:solid #4BACC6 1px;\">" +
			"<p><b>Đoàn Nguyễn Minh Nhật</b></p><p>Mã số: 106102112</p><p>Email: <a href=\"mailto:minhnhat.doannguyen@gmail.com\">minhnhat.doannguyen@gmail.com</a></p><br/></td>" +
			"<td width=319 valign=top style=\"border:solid #4BACC6 1px;\">" +
			"<p><b>Lê Chí Mừng</b></p><p>Mã số: 106102208</p><p>Email: <a href=\"mailto:chimung.hutech@gmail.com\">chimung.hutech@gmail.com</a></p><br/></td>" +
			"</tr></table></div></html>";

	private static AboutUs instance = new AboutUs();
	
	private AboutUs() {
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
	
	public static AboutUs getInstance() {
		return instance;
	}

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jPanel2 = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();

        setModal(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Thông tin thành viên");
        setIconImage(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "icon.png").getImage());
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jEditorPane1.setContentType("text/html");
        jEditorPane1.setEditable(false);
        jEditorPane1.setText(message);
        jEditorPane1.addHyperlinkListener(new NMHyperlinkListener());
        jScrollPane1.setViewportView(jEditorPane1);

        jPanel1.add(jScrollPane1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        btnClose.setText("Đóng");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jPanel2.add(btnClose);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        getContentPane().add(jPanel2, gridBagConstraints);

        pack();
    }// </editor-fold>

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AboutUs().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JButton btnClose;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration

}
