package team.nm.nnet.tmp;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NeuralNetwork implements Runnable{

	/**
	 * Chieu rong anh
	 */
	private final int FACE_WIDTH = 20;
	
	/**
	 * Chieu cao anh
	 */
	private final int FACE_HEIGHT = 30;
	
	/**
	 * Luu duong dan de luu file weight.
	 */
	private String strFilename = "";
	
	/**
	 * So luong input
	 */
    private final int CintNumberOfInput = FACE_WIDTH * FACE_HEIGHT; // 30 X 20;

    /**
     * So luong output
     */
    private final int CintNumberOfOutput = 2; //

    /**
     * So luong neural an
     */
    private final int CintNumberOfHiddenNeural = 1000;

    /**
     * Gia tri bias
     */
    private final int CintBias = 30;

    /**
     * So luong layer cua mang neural
     */
    private final int CintNuberOflayers = 3;

    /**
     * Gia tri do doc
     */
    private final float slope = 0.014F;

    /**
     * Muc do hoc
     */
    private final float CflLearningRate = 150F;

    /**
     * So lan hoc
     */
    private final int CintEpochs = 1200;

    /**
     * Nguong sai cuc tieu
     */
    private final float CflErrorThreshold = 0.0002F;

    /**
     * Mang luu cac tinh hieu nhap vao mang neural
     */
    private float[][] pintInputSignal;

    /**
     * Mang luu gia tri weight
     */
    private float[][][] pflWeight = new float[CintNuberOflayers][CintNumberOfHiddenNeural + 100][CintNumberOfHiddenNeural + 100];

    /**
     * So luong neural trong tung layer
     */
    private int[] pintNeural;

    /**
     * Mang luu cac tinh hieu mong muon xuat ra
     */
    private int[][] pintDesireOutput;

    /**
     * Luu gia tri xuat ra cua cac neural
     */
    private float[][] pflOutputNode = new float[CintNuberOflayers][CintNumberOfHiddenNeural + 100];

    /**
     * Gia tri nhap vao hien tai trong bo gia tri nhap vao
     */
    private float[] pintCurInput;

    /**
     * Gia tri mong muon xuat ra trong bo gia tri mong muon xuat ra
     */
    private int[] pintCurDesireOutput;

    /**
     * Gia tri sai khac giu gia tri mong mong muon xuat ra va gia tri xuat ra thuc
     */
    private float[][] pflError = new float[CintNuberOflayers][CintNumberOfHiddenNeural + 100];
    
    /**
     * Luu tinh hieu nhap vao
     */
    private List<float[]> lstListInput = new ArrayList<float[]>();
    
    /**
     * Luu gia tri mong muon xuat ra
     */
    private List<int[]> lstListDesireOutput = new ArrayList<int[]>();

    /**
     * Khoi tao doi tuong random cho viec hoc cac ki tu
     */
    private Random rnd = new Random();

    /**
     * Khoi tao so luong neural cho mang neural
     */
    private void psubInitNeural()
    {
        pintNeural = new int[CintNuberOflayers];
        pintNeural[0] = CintNumberOfInput;
        pintNeural[1] = CintNumberOfHiddenNeural;
        pintNeural[2] = CintNumberOfOutput;
    }

    /**
     * Khoi tao gia tri weight cho mang neural
     */
    private void psubInitWeight()
    {
        Random rnd = new Random();
        for (int i = 1; i < CintNuberOflayers; i++)
        {
            for (int j = 0; j < pintNeural[i]; j++)
            {
                for (int k = 0; k < pintNeural[i - 1]; k++)
                {
                    int intRan = rnd.nextInt(CintBias*2) - CintBias;
                    pflWeight[i][j][k] = intRan;
                   // pflWeight[0, 0, 0] = intRan;
                }
            }
        }

    }
    
    /**
     * Contructor khoi tao doi tuong
     * @param strFilename Duong dan luu file weight chi can thiet cho viec save file weight
     * khi hoc xong
     */
    public NeuralNetwork(String strFilename) {
    	this.strFilename = strFilename;
    	psubInitNeural();
    	psubInitWeight();
    }
    
    /**
     * Them face vao bo trai
     * @param image Face can them
     */
    public void addFace(BufferedImage image) {
    	image = ImageProcess.resize(image, FACE_WIDTH, FACE_HEIGHT);
    	float[] input = ImageProcess.imageToArray(image);
    	input = ImageProcess.adaptArray(input);
    	lstListInput.add(input);
    	int[] intArrDesireOutput = new int[CintNumberOfOutput];
      	intArrDesireOutput[0] = 1;
    	lstListDesireOutput.add(intArrDesireOutput);
    }
    
    /**
     * Them noneface vao bo train
     * @param image Noneface can them
     */
    public void addNoneFace(BufferedImage image) {
    	image = ImageProcess.resize(image, FACE_WIDTH, FACE_HEIGHT);
    	float[] input = ImageProcess.imageToArray(image);
    	input = ImageProcess.adaptArray(input);
    	lstListInput.add(input);
    	int[] intArrDesireOutput = new int[CintNumberOfOutput];
    	intArrDesireOutput[1] = 1;
    	lstListDesireOutput.add(intArrDesireOutput);
    }
    
    /**
     * Tinh gia tri tangen cho mang
     * @param flActiveValue Gia tri can tinh tangen
     * @return Ket qua tinh duoc
     */
    private float pfncGetTangen(float flActiveValue)
    {
        //return (float)(-1 + (2 / (1 + Math.Exp(-2 * flActiveValue))));
        //float flResult = (float)((Math.Exp(2*flActiveValue) - 1) / (Math.Exp(2*flActiveValue) + 1));
        //return flResult;
         float result = (float)((2 / (1 + Math.exp(-1 * 0.014F * flActiveValue))) - 1);		//Bipolar			
        return result;
    }
    
    /**
     * Tinh dan xuat cua gia tri tangen
     * @param fx Gia tri can tinh dan xuat
     * @return Ket qua tinh duoc
     */
    private float pfncGetDerivative(float fx)
    {
        float flResult = (float)(0.5F * (1 - Math.pow(fx, 2)));
        return flResult;
    }
    
    /**
     * Tinh trung binh gia tri sai
     * @return Ket qua tinh duoc
     */
    private float psubGetAvgError()
    {
        float result = 0.0F;
        for (int i = 0; i < CintNumberOfOutput; i++)
        {
            result = result + pflError[CintNuberOflayers - 1][i];
        }
        result = result / CintNumberOfOutput;
        result = Math.abs(result);
        return result;
    }
    
    /**
     * Tinh gia tri xuat ra cua mang
     */
    private void psubCalOutput()
    {
        float flActiveValue = 0.0F;
        int intNumOfWeigh = 0;
        for (int i = 0; i < CintNuberOflayers; i++)
        {   //Duyet tung layer
            for (int j = 0; j < pintNeural[i]; j++)
            {  
                //Duyet tung neural trong layer
                flActiveValue = 0.0F;
                if (i == 0)
                {
                    intNumOfWeigh = 1;
                }
                else
                {
                    intNumOfWeigh = pintNeural[i - 1];
                }
                for (int k = 0; k < intNumOfWeigh; k++)
                {
                    if (i == 0)
                    {
                        flActiveValue = pintCurInput[j];
                    }
                    else
                    {
                        flActiveValue = flActiveValue + pflOutputNode[i - 1][k] * pflWeight[i][j][k];
                    }   


                }
                pflOutputNode[i][j] = pfncGetTangen(flActiveValue);

            }
        }
    }
    
    /**
     * Tinh gia tri sai khac cho mang
     */
    private void psubCalError()
    {
        float sum = 0.0F;
        for (int i = 0; i < CintNumberOfOutput; i++)
        {
            pflError[CintNuberOflayers - 1][i] = (float)((pintCurDesireOutput[i] - pflOutputNode[CintNuberOflayers - 1][i]) * pfncGetDerivative(pflOutputNode[CintNuberOflayers - 1][i]));
        }
        for (int i = CintNuberOflayers - 2; i >= 0; i--)
        {
            for (int j = 0; j < pintNeural[i]; j++)
            {
                sum = 0.0F;
                for (int k = 0; k < pintNeural[i + 1]; k++)
                {
                    sum = sum + pflError[i + 1][k] * pflWeight[i + 1][k][j];

                }
                pflError[i][j] = (float)(pfncGetDerivative(pflOutputNode[i][j]) * sum);
               
            }

        }
    }
    
    /**
     * Tinh gia tri weight cho mang neural
     */
    private void psubCalWeight()
    {
        for (int i = 1; i < CintNuberOflayers; i++)
        {
            for (int j = 0; j < pintNeural[i]; j++)
            {
                for (int k = 0; k < pintNeural[i - 1]; k++)
                {
                    pflWeight[i][j][k] = (float)(pflWeight[i][j][k] + CflLearningRate * pflOutputNode[i - 1][k] * pflError[i][j]);
                }
            }
        }

    }
    
    /**
     * Xac dinh face hay none face
     * @param image Anh can xac dinh
     * @return Ket qua xac dinh
     * Return true: face
     * Return false: none face
     */
    public float gfncGetWinner(BufferedImage image) {
    	image = ImageProcess.resize(image, FACE_WIDTH, FACE_HEIGHT);
    	float[] input = ImageProcess.imageToArray(image);
    	pintCurInput = ImageProcess.adaptArray(input);
    	psubCalOutput();
//    	System.out.println(pflOutputNode[CintNuberOflayers - 1][0] + "," + pflOutputNode[CintNuberOflayers - 1][1]);

    	return pflOutputNode[CintNuberOflayers - 1][0];
    }

    @Override
    public void run() {
    	// TODO Auto-generated method stub
    	psubInitWeight();
    	float avgError = 0.0F;
        for (int epoch = 0; epoch <= CintEpochs; epoch++)
        {
            avgError = 0.0F;
            for (int i = 0; i < lstListDesireOutput.size(); i++)
            {
                int index = rnd.nextInt(lstListDesireOutput.size() - 1);
                pintCurDesireOutput = lstListDesireOutput.get(index);
                pintCurInput = lstListInput.get(index);
                psubCalOutput();
                psubCalError();
                psubCalWeight();
                avgError = avgError + psubGetAvgError();
                
            }
            System.out.println("Epoch: " + epoch + " in " + CintEpochs);
            avgError = avgError / lstListDesireOutput.size();
            System.out.println("Avgerror: " + avgError);
            if (avgError < CflErrorThreshold)
            {
                epoch = CintEpochs + 1;
            }
        }
        System.out.println("Saving weight file in " + strFilename);
        saveWeight(strFilename);
        System.out.println("Xong");
    	
    }
    
    /**
	 * Luu file weight xuong file text
	 * @param filename Duong dan luu file
	 */
	public void saveWeight(String filename) {
		try {
			FileOutputStream fos = new FileOutputStream(filename, false);
			PrintWriter pw = new PrintWriter(fos);
			for (int i = 1; i < CintNuberOflayers; i++) {
				for (int j = 0; j < pintNeural[i]; j++) {
					for (int k = 0; k < pintNeural[i - 1]; k++) {
						pw.println(pflWeight[i][j][k]);
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
			for (int i = 1; i < CintNuberOflayers; i++) {
				for (int j = 0; j < pintNeural[i]; j++) {
					for (int k = 0; k < pintNeural[i - 1]; k++) {
						float value = Float.parseFloat(br.readLine());
						pflWeight[i][j][k] = value;
					}
				}
			}
		}
		catch (Exception ex) {
			System.out.println("File weight bi loi");
		}
	}
	
}
