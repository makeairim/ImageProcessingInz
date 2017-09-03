package pl.edu.agh.imageprocessing.features.detail.home;

import android.view.View;

import pl.edu.agh.imageprocessing.data.ImageOperationType;


/**
 *  Created by Anil on 6/7/2017.
 */

public interface OperationHomeListCallback {
    void onImageOperationClicked(ImageOperationType imageOperationType);
}
