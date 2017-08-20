package pl.edu.agh.imageprocessing.features.detail.viemodel;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.edu.agh.imageprocessing.BaseActivity;
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
import pl.edu.agh.imageprocessing.features.detail.images.FileTools;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationResolver;
import pl.edu.agh.imageprocessing.features.detail.images.OpenCvTypes;

import static pl.edu.agh.imageprocessing.data.ImageOperationType.BINARIZATION;

/**
 * Created by bwolcerz on 20.08.2017.
 */

public class ImageOperationViewModel extends BaseViewModel {
    public static final String TAG=ImageOperationFragment.class.getSimpleName();
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

    ImageOperationViewModelState state = new ImageOperationViewModelState();


    @Inject
    public ImageOperationViewModel() {
    }

    @Override
    protected ImageOperationFragment provideFragment() {
        return (ImageOperationFragment) super.provideFragment();
    }

    @Override
    protected HomeActivity provideActivity() {
        return (HomeActivity) super.provideActivity();
    }

    private void showImage() {
        if (state.getResource() == null || state.getResource().getContent() == null)
            throw new AssertionError(); //todo to delete after check
        EventBus.getDefault().post(new EventSimpleDataMsg(Uri.parse(state.getResource().getContent())));
    }

    public void showMatrixDialog(String title, int height, int width, ImageOperationType imageOperationType) {
        FragmentManager fm = provideActivity().getSupportFragmentManager();
        MatrixCustomDialog dialog = MatrixCustomDialog.newInstance(title, width, height);
        dialog.show(fm, "matrix_values");
        dialog.setListener((width1, height1, matrix) -> {
            state.setMatrixWidth(width1);
            state.setMatrixHeight(height1);
            state.setMatrix(matrix);
            callImageOperation(imageOperationType);
        });

    }

    private void showErosionDilationDialog(String title, ImageOperationType imageOperationType) {
        FragmentManager fm = provideActivity().getSupportFragmentManager();
        DilationErosionCustomDialog dialog = DilationErosionCustomDialog.newInstance(title, openCvTypes.getStructuringElementTypes());
        dialog.show(fm, "operation_parameters");
        dialog.setListener((width, height, elementType) -> {
            state.setMorphologyWidth(width);
            state.setMorphologyHeight(height);
            state.setMorphologyElementType(OpenCvTypes.MORPH_ELEMENTS.getTypeFromName(elementType));
            callImageOperation(imageOperationType);
        });
    }

    private void callImageOperation(ImageOperationType imageOperationType) {

        if ( state.getResourcePreviousOperation()==null){
            state.setResourcePreviousOperation(state.getResource());
        }
        Observable.create((ObservableOnSubscribe<Long>) e ->{
            Operation oper = new Operation.Builder()
                    .creationDate(new Date(System.currentTimeMillis()))
                    .operationType(imageOperationType.name())
                    .parentOperationId(state.getResourcePreviousOperation().getId()).build();
            e.onNext(operationDao.save(oper));})
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .subscribe(processingOperationId -> {
                    try {
                        Observable.just(imageOperationResolver.resolveOperation(imageOperationType, state, processingOperationId))
                                .map(basicOperation ->
                                        imageOperationResolver.processResult(basicOperation.execute()))
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(resource -> {
                                    state.setResource(resource);

                                    EventBus.getDefault().post(new EventBasicViewConfirmActionVisiblity(EventBasicView.ViewState.VISIBLE));
                                    showImage();
                                    provideActivity().binding.doOper.setOnClickListener(view -> {
                                        //todo nothing if accepted
                                        Observable.create((ObservableOnSubscribe<Boolean>) e -> operationResourceAPIRepository
                                                .chainOperations(operationDao
                                                                .get(state.getResourcePreviousOperation().getOperationId()).blockingFirst(),
                                                        operationDao
                                                                .get(processingOperationId).blockingFirst()))
                                                .subscribeOn(Schedulers.computation())
                                                .observeOn(AndroidSchedulers.mainThread()).subscribe(aBoolean -> state.setResourcePreviousOperation(resource));
                                        EventBus.getDefault().post(new EventBasicViewHideBottomActionParameters(EventBasicView.ViewState.HIDEN));
                                        EventBus.getDefault().post(new EventBasicViewConfirmActionVisiblity(EventBasicView.ViewState.HIDEN));
                                    });
                                    provideActivity().binding.clearOper.setOnClickListener(view -> {
                                        //todo delete oper
                                        EventBus.getDefault().post(new EventBasicViewHideBottomActionParameters(EventBasicView.ViewState.HIDEN));
                                        EventBus.getDefault().post(new EventBasicViewConfirmActionVisiblity(EventBasicView.ViewState.HIDEN));
                                    });
                                });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

    }

    @Subscribe
    public void showBinarization(ShowBinarizationEvent event){
        state.setOperationType(BINARIZATION);
                        provideFragment().binding.seekbar.setSeekBarValueChangedListener((i, b) -> {
                            Log.i(TAG, "HomeViewModel: seekBar value changed:" + i);
                            state.setThreshold(i);
                            EventBus.getDefault().post(new EventSimpleDataMsg(String.valueOf(state.getThreshold())));
                                callImageOperation(state.getOperationType());

                        }
                );
                EventBus.getDefault().post(new EventBasicViewSeekBarVisibility(EventBasicView.ViewState.VISIBLE));
                provideFragment().binding.seekbar.setMaxValue(AppConstants.MAX_ADAPTIVE_THRESHOLD);
                EventBus.getDefault().post(new EventBasicViewMainPhoto(EventBasicView.ViewState.VISIBLE));
    }

    @Subscribe
    public void showDilationAndErosion(ShowErosionAndDilationEvent event){
    }

    @Subscribe
    public void showFilter(ShowFilterEvent event){
    }

    public void setData(Resource data) {
        state.setResource(data);
        EventBus.getDefault().post(new EventSimpleDataMsg(Uri.parse(data.getContent())));
    }

    public class ImageOperationViewModelState {
        private Resource resourcePreviousOperation;
        private Resource resource;
        private ImageOperationType operationType;
        private int morphologyWidth;
        private int morphologyHeight;
        private int morphologyElementType;
        public int threshold;
        private int matrixHeight;
        private int matrixWidth;
        private int[] matrix;

        public ImageOperationType getOperationType() {
            return operationType;
        }

        public void setOperationType(ImageOperationType operationType) {
            this.operationType = operationType;
        }

        public Resource getResourcePreviousOperation() {
            return resourcePreviousOperation;
        }

        public void setResourcePreviousOperation(Resource resourcePreviousOperation) {
            this.resourcePreviousOperation = resourcePreviousOperation;
        }

        public Resource getResource() {
            return resource;
        }

        public void setResource(Resource resource) {
            this.resource = resource;
        }

        public int getMorphologyWidth() {
            return morphologyWidth;
        }

        public void setMorphologyWidth(int morphologyWidth) {
            this.morphologyWidth = morphologyWidth;
        }

        public int getMorphologyHeight() {
            return morphologyHeight;
        }

        public void setMorphologyHeight(int morphologyHeight) {
            this.morphologyHeight = morphologyHeight;
        }

        public int getMorphologyElementType() {
            return morphologyElementType;
        }

        public void setMorphologyElementType(int morphologyElementType) {
            this.morphologyElementType = morphologyElementType;
        }

        public int getThreshold() {
            return threshold;
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
        }

        public int getMatrixHeight() {
            return matrixHeight;
        }

        public void setMatrixHeight(int matrixHeight) {
            this.matrixHeight = matrixHeight;
        }

        public int getMatrixWidth() {
            return matrixWidth;
        }

        public void setMatrixWidth(int matrixWidth) {
            this.matrixWidth = matrixWidth;
        }

        public int[] getMatrix() {
            return matrix;
        }

        public void setMatrix(int[] matrix) {
            this.matrix = matrix;
        }
    }
}
