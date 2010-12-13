package team.nm.nnet.tmp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import team.nm.nnet.util.IOUtils;
import team.nm.nnet.util.LogicUtils;

public class NeuralFaceRecognize {

	/**
	 * Cung cap thu muc con chua cac anh de hoc
	 * @param folderPath Duong dan den thu muc train
	 */
	public void addTrainFolder(String folderPath) {
		List<String> listSubFolder = IOUtils.listSubFolder(folderPath);
		List<String> listNumberFolder = new ArrayList<String>();
		for (int i = 0; i < listSubFolder.size(); i ++) {
			if (LogicUtils.isNumber(listSubFolder.get(i))) {
				
			}
		}
	}
}
