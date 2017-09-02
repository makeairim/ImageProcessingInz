package pl.edu.agh.imageprocessing.features.detail.home;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import pl.edu.agh.imageprocessing.data.local.entity.Resource;

/**
 * Created by bwolcerz on 02.09.2017.
 */

public interface ObtainImageFileForOperationCallback {
    Observable<Resource> obtainOperationResourceImageFile(long operationId);
}
