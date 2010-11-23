package team.nm.nnet.core;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import team.nm.nnet.util.ImageProcess;

public class NeuralFaceClassify implements Runnable {
	/**
	 * Luu duong dan de luu file weight
	 */
	private String filename = "";
	/**
	 * So luong input
	 */
	private final int NUMBER_OF_INPUT = Const.FACE_WIDTH * Const.FACE_HEIGHT; 															// 20;
	/**
	 * So luong output
	 */
	public final int NUMBER_OF_OUTPUT = 20; //

	/**
	 * So luong neural trong layer an
	 */
	private final int NUMBER_OF_HIDDEN_NEURAL = 800;

	/**
	 * Gia tri bias
	 */
	private final int BIAS = 30;

	/**
	 * So luong layer
	 */
	private final int NUMBER_OF_LAYER = 3;
	private final double SLOPE = 0.014F;
	private final double LEARNING_RATE = 150F;

	/**
	 * So lan lap cho viec hoc
	 */
	private final int EPOCHS = 1200;

	/**
	 * Nguong sai
	 */
	private final double ERROR_THRESHOLD = 0.0002F;

	/**
	 * Luu bo tinh hieu nhap vao
	 */
	private List<double[]> listInputSignal;

	/**
	 * Luu gia tri weight cua neural
	 */
	private double[][][] weight = new double[NUMBER_OF_LAYER][NUMBER_OF_HIDDEN_NEURAL + 100][NUMBER_OF_HIDDEN_NEURAL + 100];

	/**
	 * Luu so luong neural tren tung layer
	 */
	private int[] neuralInLayer;

	/**
	 * Luu gia tri mong muon xuat ra
	 */
	private List<int[]> listDesireOutput;

	/**
	 * Luu gia tri xuat ra cua cac neural trong layer
	 */
	private double[][] outputNode = new double[NUMBER_OF_LAYER][NUMBER_OF_HIDDEN_NEURAL + 100];

	/**
	 * Luu gia tri nhap vao hien tai trong bo gia tri nhap vao
	 */
	private double[] curInput;

	/**
	 * Luu gia tri mong muon xuat ra hien tai trong bo gia tri mong muon xuat ra
	 */
	private int[] curDesireOutput;

	/**
	 * Luu gia tri sai khac ket qua xuat ra voi ket qua mong muon xuat ra
	 */
	private double[][] error = new double[NUMBER_OF_LAYER][NUMBER_OF_HIDDEN_NEURAL + 100];

	/**
	 * Lay gia tri ngau nhien
	 */
	private Random random = new Random();

	public NeuralFaceClassify(String filename) {
		listDesireOutput = new ArrayList<int[]>();
		listInputSignal = new ArrayList<double[]>();
		this.filename = filename;
	}

	/**
	 * Dua anh la face vao
	 * 
	 * @param image
	 *            Anh can dua vao
	 */
	public void addFaceTrain(BufferedImage image) {
		image = ImageProcess.resize(image, Const.FACE_WIDTH, Const.FACE_HEIGHT);
		double[] arrayInput = ImageProcess.imageToArryay(image);
		arrayInput = ImageProcess.adapArray(arrayInput);
		listInputSignal.add(arrayInput);
		int[] arrayDesire = new int[NUMBER_OF_OUTPUT];
		arrayDesire[0] = 1;
		listDesireOutput.add(arrayDesire);
	}

	/**
	 * Dua anh none face vao
	 * 
	 * @param image
	 *            Anh can dua vao
	 */
	public void addNoneFaceTrain(BufferedImage image) {
		image = ImageProcess.resize(image, Const.FACE_WIDTH, Const.FACE_HEIGHT);
		double[] arrayInput = ImageProcess.imageToArryay(image);
		arrayInput = ImageProcess.adapArray(arrayInput);
		listInputSignal.add(arrayInput);
		int[] arrayDesire = new int[NUMBER_OF_OUTPUT];
		arrayDesire[1] = 1;
		listDesireOutput.add(arrayDesire);
	}

	/**
	 * Khoi tao neural cho network
	 */
	public void initNeural() {
		neuralInLayer = new int[NUMBER_OF_LAYER];
		neuralInLayer[0] = NUMBER_OF_INPUT;
		neuralInLayer[1] = NUMBER_OF_HIDDEN_NEURAL;
		neuralInLayer[2] = NUMBER_OF_OUTPUT;
	}

	/**
	 * Khoi tao gia tri weught
	 */
	public void initWeight() {
		for (int i = 1; i < NUMBER_OF_LAYER; i++) {
			for (int j = 0; j < neuralInLayer[i]; j++) {
				for (int k = 0; k < neuralInLayer[i - 1]; k++) {
					weight[i][j][k] = random.nextInt(BIAS * 2) - BIAS;
				}
			}
		}

	}

	/**
	 * Tinh gia tri xuat ra cho mang neural
	 */
	private void calculateOutput() {
		double activeValue = 0.0F;
		int numOfWeigh = 0;
		for (int i = 0; i < NUMBER_OF_LAYER; i++) { // Duyet tung layer
			for (int j = 0; j < neuralInLayer[i]; j++) {
				// Duyet tung neural trong layer
				activeValue = 0.0F;
				if (i == 0) {
					numOfWeigh = 1;
				} else {
					numOfWeigh = neuralInLayer[i - 1];
				}
				for (int k = 0; k < numOfWeigh; k++) {
					if (i == 0) {
						activeValue = curInput[j];
					} else {
						activeValue = activeValue + outputNode[i - 1][k]
								* weight[i][j][k];
					}

				}
				outputNode[i][j] = getTangen(activeValue);
			}
		}
	}

	/**
	 * Tinh gia tri error cho mang neural
	 */
	private void calculateError() {
		double sum = 0.0F;
		for (int i = 0; i < NUMBER_OF_OUTPUT; i++) {
			error[NUMBER_OF_LAYER - 1][i] = (curDesireOutput[i] - outputNode[NUMBER_OF_LAYER - 1][i]
					* getDerivativeTangen(outputNode[NUMBER_OF_LAYER - 1][i]));
		}
		for (int i = NUMBER_OF_LAYER - 2; i >= 0; i--) {
			for (int j = 0; j < neuralInLayer[i]; j++) {
				sum = 0.0F;
				for (int k = 0; k < neuralInLayer[i + 1]; k++) {
					sum = sum + error[i + 1][k] * weight[i + 1][k][j];

				}
				error[i][j] = getDerivativeTangen(outputNode[i][j]) * sum;

			}

		}
	}

	/**
	 * Ham tinh gia tri weight
	 */
	private void calculateWeight() {
		for (int i = 1; i < NUMBER_OF_LAYER; i++) {
			for (int j = 0; j < neuralInLayer[i]; j++) {
				for (int k = 0; k < neuralInLayer[i - 1]; k++) {
					weight[i][j][k] = weight[i][j][k] + LEARNING_RATE
							* outputNode[i - 1][k] * error[i][j];
				}
			}
		}

	}

	/**
	 * Tinh ham tagen
	 * 
	 * @param d
	 *         Gia tri can tinh
	 * @return Ket qua tinh duoc
	 */
	private double getTangen(double d) {
		double result = ((2 / (1 + Math.exp(-1 * SLOPE * d))) - 1);
		return result;
	}

	/**
	 * Tinh gia tri dan xuat cua ham tangen
	 * 
	 * @param d
	 *            Gia tri can tinh
	 * @return Ket qua tinh duoc
	 */
	private double getDerivativeTangen(double d) {
		double result = 0.5F * (1 - Math.pow(d, 2));
		return result;
	}

	/**
	 * Tinh gia tri loi trung binh
	 * 
	 * @return Ket qua tinh duoc
	 */
	private double getAvgError() {
		double result = 0.0F;
		for (int i = 0; i < NUMBER_OF_OUTPUT; i++) {
			result = result + error[NUMBER_OF_LAYER - 1][i];
		}
		result = result / NUMBER_OF_OUTPUT;
		result = Math.abs(result);
		return result;
	}

	/**
	 * Tra ve vi tri neural cho gia tri lon nhat
	 * 
	 * @param image
	 *            Anh can dua vao
	 * @return Ket qua tinh duoc
	 */
	public int getWinner(BufferedImage image) {
		image = ImageProcess.resize(image, Const.FACE_WIDTH, Const.FACE_HEIGHT);
		double[] inputArray = ImageProcess.imageToArryay(image);
		curInput = ImageProcess.adapArray(inputArray);
		calculateOutput();
		double max = outputNode[NUMBER_OF_LAYER - 1][0];
		int index = 0;
		for (int i = 1; i < NUMBER_OF_OUTPUT; i++) {
			if (max < outputNode[NUMBER_OF_LAYER - 1][i]) {
				max = outputNode[NUMBER_OF_LAYER - 1][i];
				index = i;
			}
		}
		return index;
	}

	@Override
	public void run() {
		System.out.println("Bat dau train");
		initNeural();
		initWeight();
		double avgError = 0.0F;
		for (int epoch = 0; epoch <= EPOCHS; epoch++) {
			avgError = 0.0F;
			for (int i = 0; i < listInputSignal.size(); i++) {
				int index = random.nextInt(listInputSignal.size() - 1);
				curDesireOutput = listDesireOutput.get(index);
				curInput = listInputSignal.get(index);
				calculateOutput();
				calculateError();
				calculateWeight();
				avgError = avgError + getAvgError();
			}
			avgError = avgError / listInputSignal.size();
			if (avgError < ERROR_THRESHOLD) {
				epoch = EPOCHS + 1;
			}
			int percent = (epoch * 100 / EPOCHS);
			System.out.println("Percent: " + percent);
			System.out.println("Epoch: " + epoch + "in: " + EPOCHS);
			System.out.println("AVG Error: " + avgError);
		}
		System.out.println("Xong.");
		System.out.println("Save weight to: " + filename);
		saveWeight(filename);
		System.out.println("Xong.");
	}
	
	/**
	 * Luu file weight xuong file text
	 * @param filename Duong dan luu file
	 */
	public void saveWeight(String filename) {
		try {
			FileOutputStream fos = new FileOutputStream(filename, false);
			PrintWriter pw = new PrintWriter(fos);
			for (int i = 1; i < NUMBER_OF_LAYER; i++) {
				for (int j = 0; j < neuralInLayer[i]; j++) {
					for (int k = 0; k < neuralInLayer[i - 1]; k++) {
						pw.println(weight[i][j][k]);
					}
				}
			}
			pw.close();
		} catch (Exception ex) {
			System.out.println("Khong tim thay duong dan luu file");
		}
	}
	
	/**
	 * Load weight len tu file text
	 * @param filename Duong dan den file weight can load
	 */
	public void loadWeight(String filename) {
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			for (int i = 1; i < NUMBER_OF_LAYER; i++) {
				for (int j = 0; j < neuralInLayer[i]; j++) {
					for (int k = 0; k < neuralInLayer[i - 1]; k++) {
						double value = Double.parseDouble(br.readLine());
						weight[i][j][k] = value;
					}
				}
			}
		}
		catch (Exception ex) {
			System.out.println("File weight bi loi");
		}
	}

}
