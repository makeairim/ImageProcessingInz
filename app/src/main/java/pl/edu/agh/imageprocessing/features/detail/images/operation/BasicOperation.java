package pl.edu.agh.imageprocessing.features.detail.images.operation;

import org.opencv.core.Mat;

import java.util.List;
import java.util.Map;

import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationParameter;

/**
 * Created by bwolcerz on 05.08.2017.
 */

abstract public class BasicOperation {

    private final List<Mat> arguments;
    private Mat result;
    protected ImageOperationParameter parameter;

    /**
     * execute flow of operation for set Uri and parameters
     */
    abstract public BasicOperation execute();

    public Mat getResult() {
        return result;
    }

    public void setResult(Mat result) {
        this.result = result;
    }

    /**
     * @return empty map if no error encountered
     * @throws Exception if any parameter is missing or set invalid
     */
    abstract protected Map<String, String> validateParameters() throws Exception;

    public BasicOperation(ImageOperationParameter parameter, List<Mat> arguments) {
        this.parameter = parameter;
        this.arguments = arguments;
    }

    public List<Mat> getArguments() {
        return arguments;
    }

    public ImageOperationParameter getParameter() {
        if (parameter == null) {
            throw new AssertionError("Parameter can not be null");
        }
        return parameter;
    }

    public void setParameter(ImageOperationParameter parameter) {
        this.parameter = parameter;
    }

    private Mat createEmptyMatAsFirstArguments() {
        return new Mat(getArguments().get(0).width(), getArguments().get(0).height(), getArguments().get(0).type());
    }

    private Mat createEmptyMatAsSmallerArgument() {
        Mat smaller = getArguments().get(0);
        if (getArguments().get(0).size().area() > getArguments().get(1).size().area()) {
            smaller = getArguments().get(1);
        }
        return new Mat(smaller.width(), smaller.height(), smaller.type());
    }

    Mat createResultMat() {
        setResult(createEmptyMatAsSmallerArgument());
        return result;
    }
}
