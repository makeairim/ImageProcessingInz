package pl.edu.agh.imageprocessing.features.detail.images.operation;


import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Collections;
import java.util.Map;

import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.features.detail.android.CoreException;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationParameter;

public class DilationOperation extends BasicOperation {
    static  ImageOperationType type = ImageOperationType.DILATION;

    public DilationOperation(ImageOperationParameter parameter) {
        super(parameter);
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
        Mat src = new Mat(parameter.getImageBitmap().getHeight(), parameter.getImageBitmap().getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(parameter.getImageBitmap(), src);
        Mat kernelDilate = Imgproc.getStructuringElement(((Parameters) parameter).getStructElement, new Size(((Parameters) parameter).getStructElementWidth(), ((Parameters) parameter).getStructElementHeight()));
        Imgproc.dilate(src, src, kernelDilate);

        Utils.matToBitmap(src, parameter.getImageBitmap());
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
        public int getStructElement;
        private double structElementWidth;
        private double structElementHeight;

        public int getGetStructElement() {
            return getStructElement;
        }

        public void setGetStructElement(int getStructElement) {
            this.getStructElement = getStructElement;
        }

        public double getStructElementWidth() {
            return structElementWidth;
        }

        public void setStructElementWidth(double structElementWidth) {
            this.structElementWidth = structElementWidth;
        }

        public double getStructElementHeight() {
            return structElementHeight;
        }

        public void setStructElementHeight(double structElementHeight) {
            this.structElementHeight = structElementHeight;
        }
    }
}
