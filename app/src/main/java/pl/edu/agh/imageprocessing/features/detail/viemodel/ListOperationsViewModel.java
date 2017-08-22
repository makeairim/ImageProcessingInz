package pl.edu.agh.imageprocessing.features.detail.viemodel;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.app.constants.AppConstants;
import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.data.local.entity.Resource;
import pl.edu.agh.imageprocessing.data.remote.OperationResourceAPIRepository;
import pl.edu.agh.imageprocessing.features.detail.android.DilationErosionCustomDialog;
import pl.edu.agh.imageprocessing.features.detail.android.MatrixCustomDialog;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicView;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewConfirmActionVisiblity;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewHideBottomActionParameters;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewMainPhoto;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewSeekBarVisibility;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventSimpleDataMsg;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowBinarizationEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowErosionAndDilationEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowFilterEvent;
import pl.edu.agh.imageprocessing.features.detail.home.HomeActivity;
import pl.edu.agh.imageprocessing.features.detail.home.ImageOperationFragment;
import pl.edu.agh.imageprocessing.features.detail.home.ListOperationsFragment;
import pl.edu.agh.imageprocessing.features.detail.images.FileTools;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationResolver;
import pl.edu.agh.imageprocessing.features.detail.images.OpenCvTypes;

import static pl.edu.agh.imageprocessing.data.ImageOperationType.BINARIZATION;
import static pl.edu.agh.imageprocessing.data.ImageOperationType.DILATION;
import static pl.edu.agh.imageprocessing.data.ImageOperationType.EROSION;

/**
 * Created by bwolcerz on 20.08.2017.
 */

public class ListOperationsViewModel extends BaseViewModel {
    public static final String TAG = ListOperationsViewModel.class.getSimpleName();
    @Inject
    OperationResourceAPIRepository operationResourceAPIRepository;
    @Inject
    OperationDao operationDao;
    @Inject
    FileTools fileTools;
    @Inject
    Context context;
    @Inject
    ImageOperationResolver imageOperationResolver;
    @Inject
    OpenCvTypes openCvTypes;

    ListOperationsViewModelState state = new ListOperationsViewModelState();

    @Inject
    public ListOperationsViewModel() {
    }



    @Override
    protected ListOperationsFragment provideFragment() {
        return (ListOperationsFragment) super.provideFragment();
    }

    @Override
    protected HomeActivity provideActivity() {
        return (HomeActivity) super.provideActivity();
    }

    private void showImage() {

    }


    public void setData(List<Operation> data) {
        state.setOperationRoots(data);

    }


    public class ListOperationsViewModelState {
        private List<Operation> operationRoots;

        public void setOperationRoots(List<Operation> operationRoots) {
            this.operationRoots = operationRoots;
        }

        public List<Operation> getOperationRoots() {
            return operationRoots;
        }
    }
}
