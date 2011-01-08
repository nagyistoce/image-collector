package team.nm.nnet.app.imageCollector.layout;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import team.nm.nnet.app.imageCollector.bo.FaceSearcher;
import team.nm.nnet.app.imageCollector.om.DetectedFace;
import team.nm.nnet.app.imageCollector.om.FaceList;
import team.nm.nnet.app.imageCollector.utils.Chooser;
import team.nm.nnet.core.Const;
import team.nm.nnet.util.IOUtils;

public class SearchResult extends FaceList {

	private static final long serialVersionUID = -347426669142200337L;
	
	static ExecutorService executorService = Executors.newCachedThreadPool();
    private static SearchResult instance = new SearchResult();
	private FaceSearcher dbSearcher;
    
	private JFrame frame;
	
	private SearchResult() {
		initComponents();
	}
	
    public static SearchResult getInstance() {
    	return instance;
    }
    
    public void setVisible(boolean b) {
    	frame.setVisible(b);
    }

	@Override
	public void onAddingFace(DetectedFace face) {
		SearchedFacePanel result = new SearchedFacePanel(face.getBufferedImage(), face.getFilePath());
		addResult(result);
	}

	@Override
	public void onFulfiling() {
		lblMsg.setText("<html><span style='color:#FF0000'><b>" + getSize() + "</b></span> khuôn mặt tương tự được tìm thấy!</html>");
        lblMsg.setIcon(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "check.png"));
	}
	
	public void search(final List<File> files, final List<Integer> expectedOutput) {
		if((files == null) || (files.size() < 1)) {
			return;
		}

		dbSearcher.setFaceResults(this);
        executorService.execute(new Runnable() {
			@Override
			public void run() {
				if(dbSearcher != null) {
					dbSearcher.requestStop();
				}
				
				clear();
				pnlResult.removeAll();
				pnlResult.updateUI();
				System.gc();
				
				lblMsg.setText("Đang tìm...");
				lblMsg.setIcon(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "waiting.gif"));

				dbSearcher.prepare();
				dbSearcher.search(files, expectedOutput);
			}
		});
	}

    private void initComponents() {
    	frame = new JFrame("Kết quả tìm kiếm");
        java.awt.GridBagConstraints gridBagConstraints;

        frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        frame.setIconImage(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "icon.png").getImage());
        frame.setMinimumSize(new java.awt.Dimension(950, 700));
        frame.setPreferredSize(new java.awt.Dimension(950, 700));
        frame.getContentPane().setLayout(new java.awt.GridBagLayout());
        frame.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		super.windowClosing(e);
        		dbSearcher.requestStop();
        	}
		});

        lblMsg.setText("Đang tìm...");
        lblMsg.setIcon(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "waiting.gif"));
        lblMsg.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        frame.getContentPane().add(lblMsg, gridBagConstraints);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(850, 550));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(900, 650));

        pnlResult.setLayout(new java.awt.GridLayout(5, 5));
        jScrollPane1.setViewportView(pnlResult);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        frame.getContentPane().add(jScrollPane1, gridBagConstraints);

        btnCopy.setText("Sao chép");
        btnCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopyActionPerformed(evt);
            }
        });
        jPanel2.add(btnCopy);
        
        btnClose.setText("Đóng");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		dbSearcher.requestStop();
        		frame.dispose();
        	}
        });
        jPanel2.add(btnClose);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        frame.getContentPane().add(jPanel2, gridBagConstraints);

        frame.pack();
    }

    private void btnCopyActionPerformed(java.awt.event.ActionEvent evt) {                                         
    	File selectedFile = Chooser.getDirectory("Chọn địa chỉ mới");
        if(selectedFile != null) {
        	int files = IOUtils.copy(getFilePaths(), selectedFile.getPath());
        	JOptionPane.showMessageDialog(frame, files + " tệp đã được sao chép!", "Copy successufully!", JOptionPane.INFORMATION_MESSAGE);
        }
    }                                        
    
    private String[] getFilePaths() {
    	List<String> filePaths = new ArrayList<String>();
    	Component[] components = pnlResult.getComponents();
        for(Component comp : components) {
            SearchedFacePanel item = (SearchedFacePanel) comp;
            String path = item.getFilePath();
            if(StringUtils.isNotBlank(path) && !filePaths.contains(path)) {
                filePaths.add(path);
            }
        }
        return filePaths.toArray(new String[]{});
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
    
    public void addResult(SearchedFacePanel result) {
    	pnlResult.add(result);
    	pnlResult.updateUI();
    }

    // Variables declaration - do not modify
    private javax.swing.JButton btnCopy = new JButton();
    private javax.swing.JButton btnClose = new JButton();
    private javax.swing.JPanel jPanel2 = new JPanel();
    private javax.swing.JPanel pnlResult = new JPanel();
    private javax.swing.JScrollPane jScrollPane1 = new JScrollPane();
    public javax.swing.JLabel lblMsg = new JLabel();
    // End of variables declaration

    @Required
	public void setDbSearcher(FaceSearcher dbSearcher) {
		this.dbSearcher = dbSearcher;
	}

}
