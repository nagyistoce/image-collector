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

	private final int FACE_WIDTH = 20;
	
	private final int FACE_HEIGHT = 30;
	
	/**
	 * Luu duong dan de luu file weight.
	 */
	private String strFilename = "";
	
	/// <summary>
    /// So luong input
    /// </summary>
    private final int CintNumberOfInput = 20 * 30; // 30 X 20;

    /// <summary>
    /// So luong output
    /// </summary>
    private final int CintNumberOfOutput = 20; //

    /// <summary>
    /// So luong neural o layer an
    /// </summary>
    private final int CintNumberOfHiddenNeural = 800;

    /// <summary>
    /// Luu gi tri bia
    /// </summary>
    private final int CintBias = 30;

    /// <summary>
    /// So luong layer cua mang neural
    /// </summary>
    private final int CintNuberOflayers = 3;

    /// <summary>
    /// Gia tri do doc
    /// </summary>
    private final float slope = 0.014F;

    /// <summary>
    /// Muc do learning
    /// </summary>
    private final float CflLearningRate = 150F;

    /// <summary>
    /// So lan hoc
    /// </summary>
    private final int CintEpochs = 800;

    /// <summary>
    /// Nguong sai cho phep
    /// </summary>
    private final float CflErrorThreshold = 0.0002F;

    /// <summary>
    /// Mang luu tinh hieu nhap vao neural
    /// </summary>
    private float[][] pintInputSignal;

    /// <summary>
    /// Luu gia tri weight cua cac neural
    /// </summary>
    private float[][][] pflWeight = new float[CintNuberOflayers][CintNumberOfHiddenNeural + 100][CintNumberOfHiddenNeural + 100];

    /// <summary>
    /// Luu so luong neural tren tung layer
    /// </summary>
    private int[] pintNeural;

    /// <summary>
    /// Mang luu cac tinh hieu mong muon xuat ra
    /// </summary>
    private int[][] pintDesireOutput;

    /// <summary>
    /// Bien dung de luu gia tri xuat ra cua cac neural trong trong tung layer
    /// </summary>
    private float[][] pflOutputNode = new float[CintNuberOflayers][CintNumberOfHiddenNeural + 100];

    /// <summary>
    /// Luu gia tri nhap vao hien tai trong bo gia tri nhap vao cac phan tu
    /// </summary>
    private float[] pintCurInput;

    /// <summary>
    /// Luu gia tri muon xuat ra hien tai trong bo gia tri muon xuat ra cua cac phan tu
    /// </summary>
    private int[] pintCurDesireOutput;

    /// <summary>
    /// Mang luu gia tri sai khac giua ket qua xuat ra voi ket qua mong muon xuat ra
    /// </summary>
    private float[][] pflError = new float[CintNuberOflayers][CintNumberOfHiddenNeural + 100];
    
    /**
     * Luu tinh hieu nhap vao
     */
    private List<float[]> lstListInput = new ArrayList<float[]>();
    
    /**
     * Luu gia tri mong muon xuat ra
     */
    private List<int[]> lstListDesireOutput = new ArrayList<int[]>();

    /// <summary>
    /// Khoi tao ramdom cho viec hoc cac ki tu
    /// </summary>
    private Random rnd = new Random();

    private void psubInitNeural()
    {
        pintNeural = new int[CintNuberOflayers];
        pintNeural[0] = CintNumberOfInput;
        pintNeural[1] = CintNumberOfHiddenNeural;
        pintNeural[2] = CintNumberOfOutput;
    }

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
    
    public NeuralNetwork(String strFilename) {
    	this.strFilename = strFilename;
    	psubInitNeural();
    	psubInitWeight();
    }
    
    /**
     * Them hinh vao bo train
     * @param image
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
    
    public void addNoneFace(BufferedImage image) {
    	image = ImageProcess.resize(image, FACE_WIDTH, FACE_HEIGHT);
    	float[] input = ImageProcess.imageToArray(image);
    	input = ImageProcess.adaptArray(input);
    	lstListInput.add(input);
    	int[] intArrDesireOutput = new int[CintNumberOfOutput];
    	intArrDesireOutput[1] = 1;
    	lstListDesireOutput.add(intArrDesireOutput);
    }
    
    private float pfncGetTangen(float flActiveValue)
    {
        //return (float)(-1 + (2 / (1 + Math.Exp(-2 * flActiveValue))));
        //float flResult = (float)((Math.Exp(2*flActiveValue) - 1) / (Math.Exp(2*flActiveValue) + 1));
        //return flResult;
         float result = (float)((2 / (1 + Math.exp(-1 * 0.014F * flActiveValue))) - 1);		//Bipolar			
        return result;
    }
    
    private float pfncGetDerivative(float fx)
    {
        float flResult = (float)(0.5F * (1 - Math.pow(fx, 2)));
        return flResult;
    }
    
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
    
    public int gfncGetWinner(BufferedImage image) {
    	image = ImageProcess.resize(image, FACE_WIDTH, FACE_HEIGHT);
    	float[] input = ImageProcess.imageToArray(image);
    	pintCurInput = ImageProcess.adaptArray(input);
    	psubCalOutput();
    	float flMax = pflOutputNode[CintNuberOflayers - 1][0];
    	int intIndex = 0;
    	for (int i = 1; i < CintNumberOfOutput; i ++) {
    		if (flMax < pflOutputNode[CintNuberOflayers - 1][i]) {
    			intIndex = i;
    		}
    	}
    	System.out.println(pflOutputNode[CintNuberOflayers - 1][0] + "," + pflOutputNode[CintNuberOflayers - 1][1]);
    	return intIndex;
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
