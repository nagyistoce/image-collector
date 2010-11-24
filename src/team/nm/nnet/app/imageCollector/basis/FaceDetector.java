package team.nm.nnet.app.imageCollector.basis;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import team.nm.nnet.app.imageCollector.layout.FacePanel;
import team.nm.nnet.app.imageCollector.utils.ColorDetection;
import team.nm.nnet.core.Const;
import team.nm.nnet.util.ImageUtils;
import team.nm.nnet.util.Matrix;

public class FaceDetector extends Thread {
    
    private volatile boolean state = false;
    private JPanel pnlFaces;
    private Image image;
    
    public FaceDetector(JPanel pnlFaces, Image image) {
        this.pnlFaces = pnlFaces;
        this.image = image;
    }

    public void run() {
        if(image == null) {
            return;
        }
        
        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);
        if(bufferedImage == null) {
            return;
        }

        // Mark this thread is running
        state = true;
        System.out.println("Face Detection Thread running...");
        
        findCandidates(bufferedImage);
        
        // Finish detecting
        state = false;
        System.gc();
        System.out.println("Face Detection Thread finished!");
    }

    private void findCandidates(BufferedImage bufferedImage) {
        Matrix<Integer> integralArr = ColorDetection.getIntegralMatrix(bufferedImage);
        int detectorWidth = Const.FACE_WIDTH;
        int detectorHeight = Const.FACE_HEIGHT;
        int width = integralArr.getWidth();
        int height = integralArr.getHeight();

        do {
            for (int i = 1; i < width; i += Const.JUMP_LENGHT) {
                for (int j = 1; j < height; j += Const.JUMP_LENGHT) {
                    if(!state) {
                        return;
                    }
                    
                    if ((i + detectorWidth < width)
                            && (j + detectorHeight < height)) {
                        int d = integralArr.getValue(i + detectorWidth,
                                j + detectorHeight);
                        int c = integralArr.getValue(i - 1, j
                                + detectorHeight);
                        int b = integralArr.getValue(i - 1, j
                                + detectorWidth);
                        int a = integralArr.getValue(i - 1, j - 1);
                        int whitePixelSum = d - (b + c) + a;
                        float rate = (float) whitePixelSum
                                / (detectorWidth * detectorHeight);
                        System.out.println(String
                                .format("sWidth=%d, sHeight=%d, pos[%d][%d]: d=%d, c=%d, b=%d, a=%d, wiSum=%d, rate=%f",
                                        detectorWidth, detectorHeight,
                                        i, j, d, c, b, a,
                                        whitePixelSum, rate));
                        if (rate > Const.SCANNER_RATE_THRESHOLD) {
                            BufferedImage subBuff = bufferedImage
                                    .getSubimage(i, j, detectorWidth,
                                            detectorHeight);
                            FacePanel fp = new FacePanel(pnlFaces,
                                    ImageUtils.toImage(subBuff));
                            fp.setFaceName(String.valueOf(rate));
                            addFaceCandidates(fp);
                            
                        }
                    }
                }
                System.gc();
            }
            System.gc();
            
            if ((detectorWidth < width) && (detectorHeight < height)) {
                detectorWidth += Const.SCANNER_GROWTH;
                detectorHeight += Const.SCANNER_GROWTH;
            } else {
                break;
            }
        } while ((detectorWidth < width) && (detectorHeight < height));
    }
    
    public boolean isDetecting() {
        return state;
    }

    public void requestStop() {
        state = false;
        System.out.println("----Thread stopped");
    }
    
    private void addFaceCandidates(FacePanel facePanel) {
        pnlFaces.add(facePanel);
        pnlFaces.updateUI();
    }
}
