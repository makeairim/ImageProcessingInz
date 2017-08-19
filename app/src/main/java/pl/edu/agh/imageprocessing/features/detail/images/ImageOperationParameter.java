package pl.edu.agh.imageprocessing.features.detail.images;

import android.net.Uri;

/**
 * Created by bwolcerz on 05.08.2017.
 */

abstract public class ImageOperationParameter {
    private Uri resultUri;
    protected Uri imageUri;
    protected Long operationId;

    public Long getOperationId() {
        return operationId;
    }

    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    public Uri getResultUri() {
        return resultUri;
    }

    public void setResultUri(Uri resultUri) {
        this.resultUri = resultUri;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }



}
