package pl.edu.agh.imageprocessing.features.detail.images.operation;


import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.features.detail.android.CoreException;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationParameter;

public class BinarizationOperation extends BasicOperation {
    static ImageOperationType type = ImageOperationType.BINARIZATION;

    public BinarizationOperation(ImageOperationParameter parameter, List<Mat> mat) {
        super(parameter, mat);
    }

    @Override
    public BasicOperation execute() {

        if(getArguments().get(0).channels()>=3) {
            Imgproc.cvtColor(getArguments().get(0), getArguments().get(0), Imgproc.COLOR_BGR2GRAY);
        }
        Imgproc.adaptiveThreshold(getArguments().get(0), createResultMat(), ((Parameters) parameter).getThreshold(), Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 3, 0);
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
        int threshold;

        public int getThreshold() {
            return threshold;
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
        }
    }

}
