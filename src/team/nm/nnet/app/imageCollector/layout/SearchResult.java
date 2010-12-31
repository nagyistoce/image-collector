package team.nm.nnet.app.imageCollector.layout;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import team.nm.nnet.core.Const;

public class SearchResult  extends javax.swing.JFrame {

	private static final long serialVersionUID = -347426669142200337L;
	
    public SearchResult() {
        initComponents();
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblMsg = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnlResult = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnCopy = new javax.swing.JButton();
        btnMove = new javax.swing.JButton();

        setTitle("Kết quả tìm kiếm");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "icon.png").getImage());
        setMinimumSize(new java.awt.Dimension(750, 650));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        lblMsg.setText("Đang tìm");
        lblMsg.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(lblMsg, gridBagConstraints);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(700, 500));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(700, 500));

        pnlResult.setLayout(new java.awt.GridLayout(5, 5));
        jScrollPane1.setViewportView(pnlResult);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(jScrollPane1, gridBagConstraints);

        btnCopy.setText("Sao chép");
        btnCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopyActionPerformed(evt);
            }
        });
        jPanel2.add(btnCopy);

        btnMove.setText("Di chuyển");
        btnMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMoveActionPerformed(evt);
            }
        });
        jPanel2.add(btnMove);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        getContentPane().add(jPanel2, gridBagConstraints);

        pack();
    }

    private void btnCopyActionPerformed(java.awt.event.ActionEvent evt) {                                         
        SearchedFacePanel result = null;
        addResult(result);
    }                                        

    private void btnMoveActionPerformed(java.awt.event.ActionEvent evt) {
        Component[] components = pnlResult.getComponents();
        int count = 0;
        for(Component comp : components) {
            SearchedFacePanel item = (SearchedFacePanel) comp;
            if(item.isSelected()) {
                count++;
            }
        }
        JOptionPane.showMessageDialog(null, count);
    }

    public void addResult(SearchedFacePanel result) {
        pnlResult.add(result);
        pnlResult.updateUI();
    }
    
    public int getNoResults() {
    	Component[] components = pnlResult.getComponents();
        int count = 0;
        for(Component comp : components) {
            SearchedFacePanel item = (SearchedFacePanel) comp;
            if(item.isSelected()) {
                count++;
            }
        }
        return count;
    }

    // Variables declaration - do not modify
    private javax.swing.JButton btnCopy;
    private javax.swing.JButton btnMove;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlResult;
    public javax.swing.JLabel lblMsg;
    // End of variables declaration

}
