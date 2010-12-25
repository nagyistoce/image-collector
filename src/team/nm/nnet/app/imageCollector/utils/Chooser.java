package team.nm.nnet.app.imageCollector.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import team.nm.nnet.app.imageCollector.support.ImageFilter;
import team.nm.nnet.app.imageCollector.support.ImagePreviewPanel;

public class Chooser extends JFileChooser {
	private static final long serialVersionUID = -8935420444352211064L;

	private static String curPath = ".";
	
    public static File getSingleFile(String caption) {
        final JFileChooser chooser = new JFileChooser(curPath);
        ImageFilter imageFilter = new ImageFilter();
        chooser.addChoosableFileFilter(imageFilter);
        ImagePreviewPanel preview = new ImagePreviewPanel();
        chooser.setAccessory(preview);
        chooser.addPropertyChangeListener(preview);
        int returnVal = chooser.showDialog(null, caption);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	File selectedFile = chooser.getSelectedFile();
        	if (imageFilter.accept(selectedFile)) {
        		curPath = selectedFile.getPath();
        		return selectedFile;
        	}
        }
        return null;
    }
    
    public static List<File> getMultiFiles(String caption) {
    	final JFileChooser chooser = new JFileChooser(curPath);
        ImageFilter imageFilter = new ImageFilter();
        chooser.addChoosableFileFilter(imageFilter);
        ImagePreviewPanel preview = new ImagePreviewPanel();
        chooser.setAccessory(preview);
        chooser.addPropertyChangeListener(preview);
        chooser.setName(caption);
        chooser.setMultiSelectionEnabled(true);
        int returnVal = chooser.showDialog(null, caption);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	File[] selectedFiles = chooser.getSelectedFiles();
        	if(selectedFiles != null){
        		List<File> list = new ArrayList<File>();
        		for(File f : selectedFiles) {
        			if (imageFilter.accept(f)) {
        				list.add(f);
        			}
        		}
        		curPath = list.get(0).getPath();
        		return list;
        	}
        }
        return null;
    }
}
