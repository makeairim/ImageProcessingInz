package pl.edu.agh.imageprocessing.features.detail.images.operation;


import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.features.detail.android.CoreException;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationParameter;

public class SobelOperatorOperation extends BasicOperation {
    private static ImageOperationType type = ImageOperationType.SOBEL_OPERATOR;
    public static final String TAG = SobelOperatorOperation.class.getSimpleName();

    public SobelOperatorOperation(ImageOperationParameter parameter, List<Mat> mat) {
        super(parameter, mat);
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
        if(getArguments().get(0).channels()>=3) {
            Imgproc.cvtColor(getArguments().get(0), getArguments().get(0), Imgproc.COLOR_BGR2GRAY);
        }
        Mat gradX = new Mat();
        Mat gradY = new Mat();
        Imgproc.Sobel(getArguments().get(0), gradX, CvType.CV_16S, 1, 0, 3, 1, 0);
        Imgproc.Sobel(getArguments().get(0), gradY, CvType.CV_16S, 0, 1, 3, 1, 0);
        Core.convertScaleAbs(gradX, gradX);
        Core.convertScaleAbs(gradY, gradY);
        Core.addWeighted(gradX, 0.5, gradY, 0.5, 1, createResultMat());

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
    }
}
