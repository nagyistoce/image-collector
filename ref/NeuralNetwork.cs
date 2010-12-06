using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Drawing;
using System.Threading;
using System.IO;
using CommonSources.CommonSources;
using System.Windows.Forms;



namespace FaceDetectUsingNeuralNetwork.FaceDetect
{
    class NeuralNetwork
    {
        /// <summary>
        /// So luong input
        /// </summary>
        public const int CintNumberOfInput = Const.CintWidth * Const.CintHeight; // 30 X 20;

        /// <summary>
        /// So luong output
        /// </summary>
        public const int CintNumberOfOutput = 20; //

        /// <summary>
        /// So luong neural o layer an
        /// </summary>
        public const int CintNumberOfHiddenNeural = 800;

        /// <summary>
        /// Luu gi tri bia
        /// </summary>
        public const int CintBias = 30;

        /// <summary>
        /// So luong layer cua mang neural
        /// </summary>
        public const int CintNuberOflayers = 3;

        /// <summary>
        /// Gia tri do doc
        /// </summary>
        public const float slope = 0.014F;

        /// <summary>
        /// Muc do learning
        /// </summary>
        public const float CflLearningRate = 150F;

        /// <summary>
        /// So lan hoc
        /// </summary>
        private const int CintEpochs = 800;

        /// <summary>
        /// Nguong sai cho phep
        /// </summary>
        public const float CflErrorThreshold = 0.0002F;

        /// <summary>
        /// Mang luu tinh hieu nhap vao neural
        /// </summary>
        private float[,] pintInputSignal;

        /// <summary>
        /// Luu gia tri weight cua cac neural
        /// </summary>
        private float[, ,] pflWeight = new float[CintNuberOflayers, CintNumberOfHiddenNeural + 100, CintNumberOfHiddenNeural + 100];

        /// <summary>
        /// Luu so luong neural tren tung layer
        /// </summary>
        private int[] pintNeural;

        /// <summary>
        /// Mang luu cac tinh hieu mong muon xuat ra
        /// </summary>
        private int[,] pintDesireOutput;

        /// <summary>
        /// Bien dung de luu gia tri xuat ra cua cac neural trong trong tung layer
        /// </summary>
        private float[,] pflOutputNode = new float[CintNuberOflayers, CintNumberOfHiddenNeural + 100];

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
        private float[,] pflError = new float[CintNuberOflayers, CintNumberOfHiddenNeural + 100];

        /// <summary>
        /// Khoi tao ramdom cho viec hoc cac ki tu
        /// </summary>
        private Random rnd = new Random();

        /// <summary>
        /// Luu trang thai dang hoc
        /// </summary>
        private int pintStatus;

        /// <summary>
        /// Cho biet trang thai toi da khi hong
        /// </summary>
        private int pintMaxStatus;

        public int PintMaxStatus
        {
            get { return pintMaxStatus; }
        }

        public int PintStatus
        {
            get { return pintStatus; }
        }


        public NeuralNetwork()
        {
            psubInitNeural();
            psubInitWeight();
        }

        /// <summary>
        /// so luong guong mat can hoc
        /// </summary>
        private int pintNumOfChar;
        private Thread trainThread;

        /// <summary>
        /// Chuyen chuoi hinh nhap vao thanh ma tran tinh hieu
        /// </summary>
        /// <param name="inputs">Chuoi hinh nhap vao</param>
		private void psubInputSign(Bitmap[] inputs) {
            ImageProcess ip = new ImageProcess();
        	pintInputSignal = ip.gfncImagesToArray(inputs);
        	//pintInputSignal = new ImageProcess().gfncImagesToArray2(inputs);
		}	
		/// <summary>
        /// Khoi tao thong so cho mang neural
        /// </summary>
        private void psubInitNeural()
        {
            pintNeural = new int[CintNuberOflayers];
            pintNeural[0] = CintNumberOfInput;
            pintNeural[1] = CintNumberOfHiddenNeural;
            pintNeural[2] = CintNumberOfOutput;
        }

        /// <summary>
        /// Ham khoi tao gi tri weight
        /// </summary>
        private void psubInitWeight()
        {
            Random rnd = new Random();
            for (int i = 1; i < CintNuberOflayers; i++)
            {
                for (int j = 0; j < pintNeural[i]; j++)
                {
                    for (int k = 0; k < pintNeural[i - 1]; k++)
                    {
                        int intRan = rnd.Next(-CintBias, CintBias);
                        pflWeight[i, j, k] = intRan;
                       // pflWeight[0, 0, 0] = intRan;
                    }
                }
            }

        }

        /// <summary>
        /// Ham tao train cho neural network
        /// </summary>
        /// <param name="inputImages">Mang hinh anh dua vao</param>
        /// <param name="charOfImage">Mang ten cua ca ki tu</param>
        public void gsubTrainNetwork(Bitmap[] inputImages, string[] charOfImage)
        {
            psubInitNeural();//nó day
            psubInputSign(inputImages);
            psubInitWeight();
            psubInitDesire(charOfImage);
            pintNumOfChar = inputImages.Length;
            trainThread = new Thread(new ThreadStart(psubLearn));
            pintMaxStatus = CintEpochs;
            trainThread.Start();


        }


        /// <summary>
        /// Ham hoc dung trong thread
        /// </summary>
        private void psubLearn() {
            float avgError = 0.0F;
            for (int epoch = 0; epoch <= CintEpochs; epoch++)
            {
                avgError = 0.0F;
                for (int i = 0; i < pintNumOfChar; i++)
                {
                    int index = rnd.Next(0, pintNumOfChar - 1);
                    pintCurDesireOutput = pfnGetDesireOutput(index);
                    pintCurInput = pfncGetInput(index);
                    psubCalOutput();
                    psubCalError();
                    psubCalWeight();
                    avgError = avgError + psubGetAvgError();
                }
                avgError = avgError / pintNumOfChar;
                if (avgError < CflErrorThreshold)
                {
                    epoch = CintEpochs + 1;
                    pintStatus = CintEpochs;
                }
                pintStatus = epoch;
            }
            pintStatus = CintEpochs;
        }

        /// <summary>
        /// Ham khoi tao gia tri mong muon neural network xuat ra
        /// </summary>
        /// <param name="charOfInamge">Mang ten cua cac ki tu can hoc</param>
        private void psubInitDesire(string[] charOfInamge)
        {
            int n = charOfInamge.Length;
            pintDesireOutput = new int[n, CintNumberOfOutput];
            for (int i = 0; i < n; i++)
            {
                int[] tmp = pfncDecode(charOfInamge[i][0]);
                for (int j = 0; j < CintNumberOfOutput; j++)
                {
                    pintDesireOutput[i, j] = tmp[j];
                }
            }
        }

        /// <summary>
        /// Ham lay tinh hieu nhap vao cua tung mau ky tu
        /// </summary>
        /// <param name="index">Vi tri mau ki tu</param>
        /// <returns>Mang tinh hieu cua ki tu</returns>
        private float[] pfncGetInput(int index)
        {
            float[] flResult = new float[CintNumberOfInput];
            for (int i = 0; i < CintNumberOfInput; i++)
            {
                flResult[i] = pintInputSignal[index, i];
            }
            return flResult;
        }

        /// <summary>
        /// Lay gia tri mong muon xuat ra cho tung mau ki tu
        /// </summary>
        /// <param name="index">Vi tri mau ki tu</param>
        /// <returns>Mang tinh hieu cua ki tu</returns>
        private int[] pfnGetDesireOutput(int index)
        {
            int[] intResult = new int[CintNumberOfOutput];
            for (int i = 0; i < CintNumberOfOutput; i++)
            {
                intResult[i] = pintDesireOutput[index, i];
            }
            return intResult;
        }

        /// <summary>
        /// Ham tinh gia tri output cho tung neural khi co mot mau nhap vao
        /// </summary>
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
                            flActiveValue = flActiveValue + pflOutputNode[i - 1, k] * pflWeight[i, j, k];
                        }   


                    }
                    pflOutputNode[i, j] = pfncGetTangen(flActiveValue);
    
                }
            }
            //MessageBox.Show(pflOutputNode[2, 0].ToString());
            //MessageBox.Show(pflOutputNode[2, 1].ToString());


        }

        /// <summary>
        /// Ham tinh gia tri loi khi so sanh ket qua tinh hieu nhap vao voi ket qua tinh hieu mong
        /// muon xuat ra
        /// </summary>
        private void psubCalError()
        {
            float sum = 0.0F;
            for (int i = 0; i < CintNumberOfOutput; i++)
            {
                pflError[CintNuberOflayers - 1, i] = (float)((pintCurDesireOutput[i] - pflOutputNode[CintNuberOflayers - 1, i]) * pfncGetDerivative(pflOutputNode[CintNuberOflayers - 1, i]));
            }
            for (int i = CintNuberOflayers - 2; i >= 0; i--)
            {
                for (int j = 0; j < pintNeural[i]; j++)
                {
                    sum = 0.0F;
                    for (int k = 0; k < pintNeural[i + 1]; k++)
                    {
                        sum = sum + pflError[i + 1, k] * pflWeight[i + 1, k, j];

                    }
                    pflError[i, j] = (float)(pfncGetDerivative(pflOutputNode[i, j]) * sum);
                   
                }

            }
        }

        /// <summary>
        /// ham tinh gia tri weight cho cac phan tu trong mang neural
        /// </summary>
        private void psubCalWeight()
        {
            for (int i = 1; i < CintNuberOflayers; i++)
            {
                for (int j = 0; j < pintNeural[i]; j++)
                {
                    for (int k = 0; k < pintNeural[i - 1]; k++)
                    {
                        pflWeight[i, j, k] = (float)(pflWeight[i, j, k] + CflLearningRate * pflOutputNode[i - 1, k] * pflError[i, j]);
                    }
                }
            }

        }

        /// <summary>
        /// Ham tinh gia tri ham Tangen
        /// </summary>
        /// <param name="flActiveValue">Gia tri Active</param>
        /// <returns>Tra ve gia tri tangen</returns>
        private float pfncGetTangen(float flActiveValue)
        {
            //return (float)(-1 + (2 / (1 + Math.Exp(-2 * flActiveValue))));
            //float flResult = (float)((Math.Exp(2*flActiveValue) - 1) / (Math.Exp(2*flActiveValue) + 1));
            //return flResult;
             float result = (float)((2 / (1 + Math.Exp(-1 * 0.014F * flActiveValue))) - 1);		//Bipolar			
            return result;
        }

        /// <summary>
        /// Ham lay gia tri Derivative cua Tangen value
        /// </summary>
        /// <param name="fx">Tangen value</param>
        /// <returns>Tra ve gia tri Derivative</returns>
        private float pfncGetDerivative(float fx)
        {
            float flResult = (float)(0.5F * (1 - Math.Pow(fx, 2)));
            return flResult;
        }

        /// <summary>
        /// Ham tinh gia tri sai so trung binh cua ket qua xuat ra so voi gia tri mong muon
        /// </summary>
        /// <returns>Ket qua tra ve</returns>
        private float psubGetAvgError()
        {
            float result = 0.0F;
            for (int i = 0; i < CintNumberOfOutput; i++)
            {
                result = result + pflError[CintNuberOflayers - 1, i];
            }
            result = result / CintNumberOfOutput;
            result = Math.Abs(result);
            return result;
        }

        /// <summary>
        /// Ham nhan dang anh
        /// </summary>
        /// <param name="image">Hinh anh can nhan dang</param>
        public string gfncReconize(Bitmap image)
        {
            psubImageToInputSign(image);
            psubCalOutput();
            string str = "";
            int[] resultOutput = new int[CintNumberOfOutput];
            float s1, s2;
            for (int i = 0; i < CintNumberOfOutput; i++)
            {
                if (pflOutputNode[CintNuberOflayers - 1, i] >= 0)
                {
                    resultOutput[i] = 1;
                    
                }
                else
                {
                    resultOutput[i] = -1;
                }
            }
            //str = pfncEnCode(resultOutput).ToString();
            s1 = pflOutputNode[CintNuberOflayers - 1, 0];
            s2 = pflOutputNode[CintNuberOflayers - 1, 1];
            str = s1.ToString() + " " + s2.ToString();
            return str;
        }

        /// <summary>
        /// Ham chuyen hinh anh thanh tinh hieu nhap vao mang neural
        /// </summary>
        public void psubImageToInputSign(Bitmap image)
        {
            pintCurInput = new ImageProcess().gfncGetImgToArr(image);

        }

        /// <summary>
        /// Ham luu gia tri weight xuong file txt
        /// </summary>
        /// <param name="fileName">Duong dan luu file</param>
        public void gsubSaveWeightTXT(string fileName)
        {
            StreamWriter sw = new StreamWriter(fileName);
            for (int i = 1; i < CintNuberOflayers; i++)
            {
                for (int j = 0; j < pintNeural[i]; j++)
                {
                    for (int k = 0; k < pintNeural[i - 1]; k++)
                    {
                        sw.WriteLine(pflWeight[i, j, k]);
                    }
                }
            }
            sw.Close();
        }

        /// <summary>
        /// Ham lay gia tri weight da luu tu file txt
        /// </summary>
        /// <param name="fileName">Duong dan den file</param>
        public void gsubLoadWeightTXT(string fileName)
        {
            StreamReader sr = new StreamReader(fileName);
            for (int i = 1; i < CintNuberOflayers; i++)
            {
                for (int j = 0; j < pintNeural[i]; j++)
                {
                    for (int k = 0; k < pintNeural[i - 1]; k++)
                    {
                        string str = sr.ReadLine();
                        float fl = float.Parse(str);
                        pflWeight[i, j, k] = fl;
                    }
                }
            }
            sr.Close();
        }

        /// <summary>
        /// Ma hoa ki tu mang nhi phan
        /// 1 0 : face
        /// 0 1 : none face
        /// </summary>
        /// <param name="input">Ki tu dua vao</param>
        /// <returns>Mang nhi phan</returns>
        public int[] pfncDecode(char input)
        {
            int[] result = new int[20];
            if (input.CompareTo('f') == 0) {
                result[0] = 1;
                return result;
            }
            result[1] = 1;
            return result;
        }

        /// <summary>
        /// Giai ma mang nhi phan thanh ki tu
        /// 1 0 : face
        /// 0 1 : none face
        /// </summary>
        /// <param name="input">Mang nhi phan dua vao</param>
        /// <returns>Ki tu duoc giai ma</returns>
        public char pfncEnCode(int[] input)
        {
            if (input[0] == 0)
            {
                return 'n';
            }
            else {
                return 'f';
            }
            
        }

    }
}
