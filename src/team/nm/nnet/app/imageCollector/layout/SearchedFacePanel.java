package team.nm.nnet.app.imageCollector.layout;

import java.awt.Desktop;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import team.nm.nnet.core.Const;
import team.nm.nnet.util.ImageUtils;

public class SearchedFacePanel extends javax.swing.JPanel {

	private static final long serialVersionUID = 748605111312762257L;
	private JPopupMenu popupMenu;
    private String imgPath;

    public SearchedFacePanel(BufferedImage imgBuffer, String imgPath) {
        this.imgPath = imgPath;
        initPopup();
        initComponents();
        setFaceImage(imgBuffer);
    }
    
    public void setFaceImage(BufferedImage imgBuffer) {
    	imgBuffer = ImageUtils.resize(imgBuffer, Const.SHOWING_FACE_WIDTH, Const.SHOWING_FACE_HEIGHT);
        Image faceImage = ImageUtils.toImage(imgBuffer);
        lblImgView.setIcon(new ImageIcon(faceImage));
    }

    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        chbChoose = new javax.swing.JCheckBox();
        lblImgView = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(125, 165));

        chbChoose.setSelected(true);
        chbChoose.setBounds(0, 0, 20, 20);
        jLayeredPane1.add(chbChoose, javax.swing.JLayeredPane.DEFAULT_LAYER);

        lblImgView.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImgView.setAutoscrolls(true);
        lblImgView.setComponentPopupMenu(lblImgView.getComponentPopupMenu());
        lblImgView.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImgViewMouseClicked(evt);
            }
        });
        lblImgView.setBounds(0, 0, 120, 160);
        jLayeredPane1.add(lblImgView, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }

    private void lblImgViewMouseClicked(java.awt.event.MouseEvent evt) {
        if(evt.getButton() == MouseEvent.BUTTON3) {
            popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }

    private void initPopup() {
        // Create popup menu, attach popup menu listener
        popupMenu = new JPopupMenu("Extension");

        // Copy full path
        JMenuItem copyFullPathMenuItem = new JMenuItem("Sao chép đường dẫn");
        copyFullPathMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StringSelection ss = new StringSelection(imgPath);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
            }
        });
        popupMenu.add(copyFullPathMenuItem);

        // Separator
        popupMenu.addSeparator();
        
        // Show whole image
        JMenuItem showFullImgMenuItem = new JMenuItem("Xem ảnh đầy đủ");
        showFullImgMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Desktop desktop = Desktop.getDesktop();
                File dirToOpen = new File(imgPath);
                try {
                    desktop.open(dirToOpen);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        popupMenu.add(showFullImgMenuItem);

        // Show in folder
        JMenuItem showInFolderMenuItem = new JMenuItem("Hiển thị trong thư mục");
        showInFolderMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                 Desktop desktop = Desktop.getDesktop();
                File dirToOpen = new File(imgPath);
                try {
                    desktop.open(dirToOpen.getParentFile());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        popupMenu.add(showInFolderMenuItem);
    }

    public boolean isSelected() {
        return chbChoose.isSelected();
    }
    
    public String getFilePath() {
    	return imgPath;
    }

    // Variables declaration - do not modify
    private javax.swing.JCheckBox chbChoose;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLabel lblImgView;
    // End of variables declaration

}
