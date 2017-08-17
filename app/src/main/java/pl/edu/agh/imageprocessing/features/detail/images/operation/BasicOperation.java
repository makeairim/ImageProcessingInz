package pl.edu.agh.imageprocessing.features.detail.images.operation;

import android.net.Uri;

import java.sql.CallableStatement;
import java.util.Map;
import java.util.concurrent.Callable;

import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationParameter;

/**
 * Created by bwolcerz on 05.08.2017.
 */

abstract  public class BasicOperation{
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

    public BasicOperation(ImageOperationParameter parameter) {
        this.parameter = parameter;
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

    protected Operation saveOperation(Uri uri,ImageOperationType type) {
        Operation operation = new Operation();
        operation.setOperationType(type.name());
        operation.setPhotoUri(uri.toString());
        return operation;
    }
}
