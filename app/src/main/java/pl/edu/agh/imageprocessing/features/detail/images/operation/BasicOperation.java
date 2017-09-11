package pl.edu.agh.imageprocessing.features.detail.images.operation;

import android.graphics.Bitmap;
import android.net.Uri;

import org.opencv.core.Mat;

import java.sql.CallableStatement;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.features.detail.images.FileTools;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationParameter;

/**
 * Created by bwolcerz on 05.08.2017.
 */

abstract  public class BasicOperation{

    private final Mat mat;
    protected ImageOperationParameter parameter;

    /**
     * execute flow of operation for set Uri and parameters
     */
    abstract public BasicOperation execute();

    /**
     *
     * @return empty map if no error encountered
     * @throws Exception if any parameter is missing or set invalid
     */
    abstract protected Map<String,String> validateParameters() throws Exception;

    public BasicOperation(ImageOperationParameter parameter,Mat mat) {
        this.parameter = parameter;
        this.mat=mat;
    }

    public Mat getMat() {
        return mat;
    }

    public ImageOperationParameter getParameter() {
        if(parameter==null){
            throw new AssertionError("Parameter can not be null");
        }
        return parameter;
    }

    public void setParameter(ImageOperationParameter parameter) {
        this.parameter = parameter;
    }

}
