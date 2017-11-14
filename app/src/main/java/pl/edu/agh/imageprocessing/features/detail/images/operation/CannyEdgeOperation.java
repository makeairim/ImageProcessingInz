package pl.edu.agh.imageprocessing.features.detail.images.operation;


import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.features.detail.android.CoreException;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationParameter;

public class CannyEdgeOperation extends BasicOperation {
    private static ImageOperationType type = ImageOperationType.CANNY_EDGE;
    public static final String TAG = CannyEdgeOperation.class.getSimpleName();

    public CannyEdgeOperation(ImageOperationParameter parameter, List<Mat> mat) {
        super(parameter,mat);
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
        Imgproc.Canny(getArguments().get(0), createResultMat(),((Parameters)parameter).getSupressedPointsThreshold(),((Parameters)parameter).getStrongPointsThreshold());
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
        private double supressedPointsThreshold;
        private double strongPointsThreshold;

        public double getSupressedPointsThreshold() {
            return supressedPointsThreshold;
        }

        public void setSupressedPointsThreshold(double supressedPointsThreshold) {
            this.supressedPointsThreshold = supressedPointsThreshold;
        }

        public double getStrongPointsThreshold() {
            return strongPointsThreshold;
        }

        public void setStrongPointsThreshold(double strongPointsThreshold) {
            this.strongPointsThreshold = strongPointsThreshold;
        }
    }
}
