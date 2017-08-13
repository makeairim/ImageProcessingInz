package pl.edu.agh.imageprocessing.features.detail.images;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by bwolcerz on 05.08.2017.
 */

abstract public class ImageOperationParameter {
    protected Uri imageUri;
    protected Long previousOperation;
    protected Bitmap imageBitmap;

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Long getPreviousOperation() {
        return previousOperation;
    }

    public void setPreviousOperation(Long previousOperation) {
        this.previousOperation = previousOperation;
    }


}
