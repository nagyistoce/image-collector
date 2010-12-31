package team.nm.nnet.app.imageCollector.bo;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import team.nm.nnet.app.imageCollector.layout.SearchResult;
import team.nm.nnet.app.imageCollector.layout.SearchedFacePanel;
import team.nm.nnet.core.Const;
import team.nm.nnet.util.ImageUtils;

public class Searcher extends Thread {

	private volatile boolean state = false;
	private SearchResult frmSearchResult;
	
	public Searcher() {
		frmSearchResult = new SearchResult();
	}

	public void run() {

        // Mark this thread is running
        state = true;
        frmSearchResult.lblMsg.setText("Đang tìm...");
        frmSearchResult.lblMsg.setIcon(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "waiting.gif"));
        
        BufferedImage imgBuffer = ImageUtils.load("D:/28.jpg");
        for(int i=0; i<10; i++) {
        	SearchedFacePanel result = new SearchedFacePanel(imgBuffer, "D:/img.jpg");
        	frmSearchResult.addResult(result);
        }
        frmSearchResult.setVisible(true);
                
        // Finish detecting
        frmSearchResult.lblMsg.setText("<html><span style='color:#FF0000'><b>" + frmSearchResult.getNoResults() + "</b></span> khuôn mặt tương tự được tìm thấy!</html>");
        frmSearchResult.lblMsg.setIcon(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "check.png"));
        state = false;
        System.gc();
    }
    
    public boolean isDetecting() {
        return state;
    }

    public void requestStop() {
        state = false;
    }
}
