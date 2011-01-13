package team.nm.nnet.app.imageCollector.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import team.nm.nnet.app.imageCollector.support.ImageFilter;
import team.nm.nnet.app.imageCollector.support.ImagePreviewPanel;

public class Chooser extends JFileChooser {
	private static final long serialVersionUID = -8935420444352211064L;

	private static String curPath = ".";
	
    public static File getSingleFile(String caption, FileFilter fileFilter) {
        final JFileChooser chooser = new JFileChooser(curPath);
        chooser.addChoosableFileFilter(fileFilter);
        if(fileFilter instanceof ImageFilter) {
	        ImagePreviewPanel preview = new ImagePreviewPanel();
	        chooser.setAccessory(preview);
	        chooser.addPropertyChangeListener(preview);
        }
        chooser.setDialogTitle(caption);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	File selectedFile = chooser.getSelectedFile();
        	if (fileFilter.accept(selectedFile)) {
        		curPath = selectedFile.getPath();
        		return selectedFile;
        	}
        }
        return null;
    }
    
    public static List<File> getMultiFiles(String caption, FileFilter fileFilter) {
    	final JFileChooser chooser = new JFileChooser(curPath);
    	chooser.addChoosableFileFilter(fileFilter);
        if(fileFilter instanceof ImageFilter) {
	        ImagePreviewPanel preview = new ImagePreviewPanel();
	        chooser.setAccessory(preview);
	        chooser.addPropertyChangeListener(preview);
        }
        chooser.setMultiSelectionEnabled(true);
        chooser.setDialogTitle(caption);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	File[] selectedFiles = chooser.getSelectedFiles();
        	if(selectedFiles != null){
        		List<File> list = new ArrayList<File>();
        		for(File f : selectedFiles) {
        			if (fileFilter.accept(f)) {
        				list.add(f);
        			}
        		}
        		curPath = list.get(0).getPath();
        		return list;
        	}
        }
        return null;
    }
    
    public static File save(String caption, FileFilter fileFilter) {
        final JFileChooser chooser = new JFileChooser(curPath);
        chooser.addChoosableFileFilter(fileFilter);
        chooser.setDialogTitle(caption);
        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	File selectedFile = chooser.getSelectedFile();
        	if (fileFilter.accept(selectedFile)) {
        		curPath = selectedFile.getPath();
        		return selectedFile;
        	}
        	return new File(selectedFile.getPath() + ".nmt");
        }
        return null;
    }
    
    public static File getDirectory(String caption) {
        final JFileChooser chooser = new JFileChooser(curPath);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setDialogTitle(caption);
        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	File selectedFile = chooser.getSelectedFile();
    		curPath = selectedFile.getPath();
    		return selectedFile;
        }
        return null;
    }
}
