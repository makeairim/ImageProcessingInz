package pl.edu.agh.imageprocessing.features.detail.viemodel;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.vansuita.pickimage.dialog.PickImageDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import io.reactivex.schedulers.Schedulers;
import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.ResourceType;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.local.dao.OperationWithChainAndResource;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.data.local.entity.Resource;
import pl.edu.agh.imageprocessing.data.remote.OperationResourceAPIRepository;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicView;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewConfirmActionVisiblity;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewHideBottomActionParameters;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewListOperationsVisiblity;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowBinarizationEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowErosionAndDilationEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowFilterEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowMainViewVisibilityEventBasicView;
import pl.edu.agh.imageprocessing.features.detail.home.HomeActivity;
import pl.edu.agh.imageprocessing.features.detail.home.ImageOperationFragment;
import pl.edu.agh.imageprocessing.features.detail.home.OperationHomeListCallback;
import pl.edu.agh.imageprocessing.features.detail.images.FileTools;

/**
 * Created by bwolcerz on 27.07.2017.
 */

public class HomeViewModel extends BaseViewModel implements OperationHomeListCallback {
    public static final String TAG = HomeViewModel.class.getSimpleName();

    @Inject
    PickImageDialog pickImageDialog;
    @Inject
    OperationResourceAPIRepository operationResourceAPIRepository;
    @Inject
    FileTools fileTools;
    @Inject
    Context context;
    @Inject
    OperationDao operationDao;

    @Override
    protected HomeActivity provideActivity() {
        return (HomeActivity) super.provideActivity();
    }


    HomeViewModelState state = new HomeViewModelState();
    public Callable<Void> onOutsideListClick = () -> {
        EventBus.getDefault().post(new ShowMainViewVisibilityEventBasicView(EventBasicView.ViewState.VISIBLE));
        return null;
    };

    @Inject
    public HomeViewModel() {
    }

    public void photoPicker() {
        pickImageDialog.setOnPickResult(pickResult -> {
            Observable.create(e -> e.onNext(fileTools.saveFile(pickResult.getBitmap(), context)))
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation())
                    .subscribe(o ->{
                            Operation operation=operationResourceAPIRepository.createOperation();
                            operation.setId(operationDao.save(operation));
                            operationResourceAPIRepository.saveResource(ResourceType.IMAGE_FILE, o.toString(),operation.getId())
                                    .observeOn(AndroidSchedulers.mainThread()).subscribe(res -> {
                                //todo pass to fragment ids
//                                state.setCurrentOperationId(idRes.getOperationId());
//                                state.setCurrentImageUri((Uri) o);
                                showImageOperation(new OperationWithChainAndResource.Builder().operation(operation).resource(Collections.singletonList(res)).build());
                            });}); //todo failure so excpetion probably
        }).show(provideActivity());
    }

    private void showImageOperation(OperationWithChainAndResource  data) {
        //todo
        FragmentTransaction ft = provideActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(provideActivity().binding.container.getId(), ImageOperationFragment.newInstance(data));
        ft.commit();
    }


    @Override
    public void onImageOperationClicked(ImageOperationType imageOperationType, View sharedView) {
        Log.i(TAG, "onImageOperationClicked: " + imageOperationType.name());
        state.setPreviousOperationId(state.currentOperationId);
        state.setOperationType(imageOperationType);
        EventBus.getDefault().post(new EventBasicViewListOperationsVisiblity(EventBasicView.ViewState.HIDEN));

        EventBus.getDefault().post(new EventBasicViewHideBottomActionParameters(EventBasicView.ViewState.HIDEN)); //todo handle in fragment

        EventBus.getDefault().post(new EventBasicViewConfirmActionVisiblity(EventBasicView.ViewState.HIDEN));
        switch (imageOperationType) {
            case BINARIZATION:
                EventBus.getDefault().post(new ShowBinarizationEvent());
                break;
            case EROSION:
            case DILATION:
                EventBus.getDefault().post(new ShowErosionAndDilationEvent(imageOperationType.name()));
                break;
            case FILTER:
                EventBus.getDefault().post(new ShowFilterEvent());
                break;
            default:
                throw new AssertionError("Could not resolve operation type");
        }

    }


    public void provideOperationTypes() {
        operationResourceAPIRepository.getImageOperationTypes()
                .subscribe(resources -> {
                    EventBus.getDefault().post(new EventBasicViewListOperationsVisiblity(EventBasicView.ViewState.VISIBLE));
                    provideActivity().binding.setResource(resources);
                });
    }


    public static class HomeViewModelState {
        private Long currentOperationId;
        private Uri currentImageUri;
        private Long previousOperationId;
        private ImageOperationType operationType;

        public HomeViewModelState() {

        }

        public Long getCurrentOperationId() {
            return currentOperationId;
        }

        public void setCurrentOperationId(Long currentOperationId) {
            this.currentOperationId = currentOperationId;
        }

        public void setPreviousOperationId(Long previousOperationId) {
            this.previousOperationId = previousOperationId;
        }

        public Long getPreviousOperationId() {
            return previousOperationId;
        }

        public void setOperationType(ImageOperationType operationType) {
            this.operationType = operationType;
        }

        public ImageOperationType getOperationType() {
            return operationType;
        }
    }
}
