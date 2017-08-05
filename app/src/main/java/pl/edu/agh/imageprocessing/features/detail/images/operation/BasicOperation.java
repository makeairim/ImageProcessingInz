package pl.edu.agh.imageprocessing.features.detail.images.operation;

import android.graphics.Bitmap;

import java.util.Map;

import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationParameter;

/**
 * Created by bwolcerz on 05.08.2017.
 */

abstract  public class BasicOperation{
    protected ImageOperationParameter parameter;

    /**
     * execute flow of operation for set Uri and parameters
     */
    abstract public Bitmap execute();
    /**
     *
     * @return empty map if no error encountered
     * @throws Exception if any parameter is missing or set invalid
     */
    abstract protected Map<String,String> validateParameters() throws Exception;


    public ImageOperationParameter getParameter() {
        return parameter;
    }

    public void setParameter(ImageOperationParameter parameter) {
        this.parameter = parameter;
    }
}
