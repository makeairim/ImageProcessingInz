package pl.edu.agh.imageprocessing.features.detail.home;

import android.view.View;

import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.dao.OperationWithChainAndResource;


/**
 *  Created by Anil on 6/7/2017.
 */

public interface OperationFragmentListCallback {
    void onImageOperationClicked(OperationWithChainAndResource operationWithChainAndResource, View sharedView);
}
