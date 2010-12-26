package team.nm.nnet.app.imageCollector.layout;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import team.nm.nnet.core.Const;

public class FlashScreen extends javax.swing.JFrame {
	private static final long serialVersionUID = 4016063097351651030L;

	public FlashScreen() {
        setUndecorated(true);
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

    private void initComponents() {

        lblFlashScreen = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblFlashScreen.setIcon(new javax.swing.ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "NM.gif")); // NOI18N
        lblFlashScreen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblFlashScreenMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblFlashScreen)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblFlashScreen)
        );

        pack();
    }

    private void lblFlashScreenMouseClicked(java.awt.event.MouseEvent evt) {
        if(evt.getButton() == MouseEvent.BUTTON1) {
            this.dispose();
        }
    }

    // Variables declaration - do not modify
    private javax.swing.JLabel lblFlashScreen;
    // End of variables declaration

}
