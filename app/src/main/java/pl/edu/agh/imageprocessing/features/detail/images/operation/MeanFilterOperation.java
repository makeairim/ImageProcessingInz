package pl.edu.agh.imageprocessing.features.detail.images.operation;


import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.features.detail.android.CoreException;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationParameter;

public class MeanFilterOperation extends BasicOperation {
    private static ImageOperationType type = ImageOperationType.MEAN_FILTER;
    public static final String TAG = MeanFilterOperation.class.getSimpleName();

    public MeanFilterOperation(ImageOperationParameter parameter, List<Mat> mat) {
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
        Imgproc.blur(getArguments().get(0), createResultMat(),new Size(((Parameters)parameter).getSize(),((Parameters)parameter).getSize()));
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
