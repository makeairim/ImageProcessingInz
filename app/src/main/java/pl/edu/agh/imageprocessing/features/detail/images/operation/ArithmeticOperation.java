package pl.edu.agh.imageprocessing.features.detail.images.operation;


import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.features.detail.android.CoreException;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationParameter;

public class ArithmeticOperation extends BasicOperation {
    public static final String TAG = ArithmeticOperation.class.getSimpleName();

    public ArithmeticOperation(ImageOperationParameter parameter, List<Mat> mat) {
        super(parameter, mat);
    }

    @Override
    public BasicOperation execute() {
        Parameters parameters = (Parameters) this.getParameter();
        Mat arg1 = getArguments().get(0);
        Mat arg2 = getArguments().get(1);
        if (!arg1.size().equals(arg2.size()) || arg1.channels() != arg2.channels()) {
            int newHeight = arg2.height();
            int newWidth = arg2.width();
            if (arg1.size().area() < arg2.size().area()) {
                newHeight = arg1.height();
                newWidth = arg1.width();
                Imgproc.resize(arg2, arg2, new Size(newWidth, newHeight));
            } else {
                Imgproc.resize(arg1, arg1, new Size(newWidth, newHeight));
            }
            Log.i(TAG, "execute: args must have same size. resizing", new AssertionError());
            //  throw new AssertionError();
        }

        switch (parameters.getType()) {
            case ADD_IMAGES:
                Core.add(arg1, arg2, createResultMat());
                break;
            case DIFF_IMAGES:
                Core.absdiff(arg1, arg2, createResultMat());
                break;
            case BITWISE_AND:
                Core.bitwise_and(arg1, arg2, createResultMat());
                break;
            case BITWISE_OR:
                Core.bitwise_or(arg1, arg2, createResultMat());
                break;
            case BITWISE_XOR:
                Core.bitwise_xor(arg1, arg2, createResultMat());
                break;
            default:
                throw new UnsupportedOperationException();
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
        private ImageOperationType type;

        public ImageOperationType getType() {
            return type;
        }

        public void setType(ImageOperationType type) {
            this.type = type;
        }
    }
}
