package pl.edu.agh.imageprocessing.features.detail.images.operation;


import android.net.Uri;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Collections;
import java.util.Map;

import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.features.detail.android.CoreException;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationParameter;

public class BinarizationOperation  extends BasicOperation{
    static  ImageOperationType type=ImageOperationType.BINARIZATION;

    public BinarizationOperation(ImageOperationParameter parameter) {
        super(parameter);
    }

    @Override
    public BasicOperation execute() {
        try {
            if(!validateParameters().isEmpty()){
                throw new AssertionError("should handle on invalid parameters or validate user");
            }
        }catch(Exception e){
            //todo handle on invalid params
        }
        Uri processedImageUri=null;
        Mat src = new Mat(parameter.getImageBitmap().getHeight(), parameter.getImageBitmap().getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(parameter.getImageBitmap(), src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
        Imgproc.adaptiveThreshold(src, src, ((Parameters)parameter).getThreshold(), Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 3, 0);

        Utils.matToBitmap(src, parameter.getImageBitmap());
        return this;
    }

    @Override
    protected Map<String, String> validateParameters() throws Exception {
        Map result = Collections.EMPTY_MAP;
        //todo valdiate params
         if( parameter ==null ){
             throw new CoreException("Parameter is not set");
         }
        return result;
    }

    public static class Parameters extends ImageOperationParameter{
        int threshold;

        public int getThreshold() {
            return threshold;
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
        }
    }
}
