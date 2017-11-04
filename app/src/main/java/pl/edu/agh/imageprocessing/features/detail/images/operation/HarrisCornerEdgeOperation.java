package pl.edu.agh.imageprocessing.features.detail.images.operation;


import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Collections;
import java.util.Map;
import java.util.Random;

import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.features.detail.android.CoreException;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationParameter;

public class HarrisCornerEdgeOperation extends BasicOperation {
    private static ImageOperationType type = ImageOperationType.HARRIS_CORNER;
    public static final String TAG = HarrisCornerEdgeOperation.class.getSimpleName();

    public HarrisCornerEdgeOperation(ImageOperationParameter parameter, Mat mat) {
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
        if(getMat().channels()>=3) {
            Imgproc.cvtColor(getMat(), getMat(), Imgproc.COLOR_BGR2GRAY);
        }
        Imgproc.cornerHarris(getMat(), getMat(), 2, 3, 0.04);
        Mat tempDstNorm = new Mat();
        Core.normalize(getMat(), tempDstNorm, 0, 255, Core.NORM_MINMAX);
        Core.convertScaleAbs(tempDstNorm, getMat());

        //Drawing corners on a new image
        Random r = new Random();
        for (int i = 0; i < tempDstNorm.cols(); i++) {
            for (int j = 0; j < tempDstNorm.rows(); j++) {
                double[] value = tempDstNorm.get(j, i);
                if (value[0] > 150) {
                    Imgproc.circle(getMat(), new Point(i, j), 5, new Scalar(r.nextInt(255)), 2);
                }
            }
        }
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
