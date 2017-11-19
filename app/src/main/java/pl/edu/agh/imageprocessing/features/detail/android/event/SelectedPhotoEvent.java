package pl.edu.agh.imageprocessing.features.detail.android.event;

import android.net.Uri;

/**
 * Created by bwolcerz on 17.11.2017.
 */

public class SelectedPhotoEvent {
    private Uri photoUri;
    private PhotoEvent photoEvent;

    public SelectedPhotoEvent(Uri photoUri, PhotoEvent photoEvent) {
        this.photoEvent = photoEvent;
        this.photoUri = photoUri;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

    public PhotoEvent getPhotoEvent() {
        return photoEvent;
    }

    public void setPhotoEvent(PhotoEvent photoEvent) {
        this.photoEvent = photoEvent;
    }
}
