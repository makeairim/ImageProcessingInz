package pl.edu.agh.imageprocessing.features.detail.images.operation;


import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.features.detail.android.CoreException;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationParameter;

public class FilterOperation extends BasicOperation {
    private static ImageOperationType type = ImageOperationType.DILATION;
    public static final String TAG = FilterOperation.class.getSimpleName();

    public FilterOperation(ImageOperationParameter parameter, Bitmap imageBitmap) {
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
        Log.i(TAG, "execute: matrix=" + Arrays.toString(((Parameters) parameter).getMatrix()));
        Mat src = new Mat(getBitmap().getHeight(), getBitmap().getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(getBitmap(), src);
        Mat kernel = new Mat(((Parameters) parameter).getHeight(), ((Parameters) parameter).getWidth(), CvType.CV_32S); //TODO TYPE ? UP IS ANOTHERCV_16SC1
        //own mask- kernel
//        ((Parameters)parameter).setMatrix(new int[]{0, -1, 0, -1, 5, -1, 0, -1, 0});
// ernel.put(0, 0, 0, -1, 0, -1, 5, -1, 0, -1, 0);
        Log.i(TAG, "execute: matrix:" + ((Parameters) parameter).getMatrix());

//        for(int i=0;i< ((Parameters)parameter).getHeight();++i){
//            for(int j=0;j<((Parameters)parameter).getWidth();++j){
//                kernel.put(i, j, ((Parameters)parameter).getMatrix()[i*j]);
//            }
//        }
        kernel.put(0, 0, ((Parameters) parameter).getMatrix());
        Imgproc.filter2D(src, src, src.depth(), kernel);
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
        private int width, height;
        private int[] matrix;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int[] getMatrix() {
            return matrix;
        }

        public void setMatrix(int[] matrix) {
            this.matrix = matrix;
        }
    }
}
