package team.nm.nnet.app.imageCollector.bo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImageDB {
	private static Log   log = LogFactory.getLog(ImageDB.class);
	private List<File> files = new ArrayList<File>();

	public int load(File file) throws Exception {
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String path;
		try{
			List<File> list = new ArrayList<File>();
			while((path = br.readLine()) != null) {
				if(StringUtils.isNotBlank(path)) {
					list.add(new File(path.trim()));
				}
			}
			if(list.size() > 1) {
				files = list;
			}
		} catch(Exception e) {
			log.error(e.getMessage());
			throw new IOException(e.getMessage());
		} finally {
			if (fr != null) fr.close();
			if (br != null) br.close();
		}
		return files.size();
	}
	
	public void clear() {
		files.clear();
	}
	
	public void add(List<File> files) {
		this.files.addAll(files);
	}
	
	public void save(File file) {
		if((files == null) || (files.size() == 0)) {
			return;
		}
		FileOutputStream fos; 
	    DataOutputStream dos;

	    try {
			fos = new FileOutputStream(file);
			dos = new DataOutputStream(fos);
			for(File f : files) {
				dos.writeBytes(f.getPath());
				dos.writeChars("\n");
			}

	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	}
	
	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}
}
