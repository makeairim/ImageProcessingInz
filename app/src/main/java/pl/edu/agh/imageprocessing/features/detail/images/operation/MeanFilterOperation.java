package pl.edu.agh.imageprocessing.features.detail.images.operation;


import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.features.detail.android.CoreException;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationParameter;

public class MeanFilterOperation extends BasicOperation {
    private static ImageOperationType type = ImageOperationType.MEAN_FILTER;
    public static final String TAG = MeanFilterOperation.class.getSimpleName();

    public MeanFilterOperation(ImageOperationParameter parameter, Bitmap imageBitmap) {
        super(parameter,imageBitmap);
    }

    @Override
    public BasicOperation execute() {
        try {
            if (!validateParameters().isEmpty()) {
                throw new AssertionError("should handle on invalid parameters or validate user");
            }
        } catch (Exception e) {
            //todo handle on invalid params
        }
        Mat src = new Mat(getBitmap().getHeight(), getBitmap().getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(getBitmap(), src);
        Imgproc.blur(src,src,new Size(((Parameters)parameter).getSize(),((Parameters)parameter).getSize()));
         Utils.matToBitmap(src, getBitmap());
        return this;
    }


    @Override
    protected Map<String, String> validateParameters() throws Exception {
        Map result = Collections.EMPTY_MAP;
        //todo valdiate params
        if (parameter == null) {
            throw new CoreException("Parameter is not set");
        }
        return result;
    }

    public static class Parameters extends ImageOperationParameter {
        private int size;

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }
}
