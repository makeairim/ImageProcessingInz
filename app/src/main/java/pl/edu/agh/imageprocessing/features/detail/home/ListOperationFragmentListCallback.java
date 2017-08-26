package pl.edu.agh.imageprocessing.features.detail.home;

import android.view.View;

import pl.edu.agh.imageprocessing.data.local.dao.OperationWithChainAndResource;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;


/**
 *  Created by Anil on 6/7/2017.
 */

public interface ListOperationFragmentListCallback {
    void onImageOperationClicked(Operation operation, View sharedView);
}
