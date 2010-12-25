package team.nm.nnet.app.imageCollector.support;

import java.io.File;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class NMFileFilter extends javax.swing.filechooser.FileFilter {

	private String[] extensions = {"nmt"};
	private String description = "NMT file";
	
	/*
	 * Get the extension of a file.
	 */
	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	// Accept all directories and all gif, jpg, tiff, or png files.
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String name = f.getName().toLowerCase();
		for(String ext : extensions) {
			if (name.endsWith(ext)) {
				return true;
			}
		}

		return false;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		if(StringUtils.isBlank(description)) {
			return;
		}
		this.description = description;
	}

	public String[] getExtensions() {
		return extensions;
	}

	public void setExtensions(String[] extensions) {
		if(ArrayUtils.isEmpty(extensions)) {
			return;
		}
		this.extensions = extensions;
	}

}
