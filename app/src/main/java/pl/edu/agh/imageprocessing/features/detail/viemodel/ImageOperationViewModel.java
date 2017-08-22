package pl.edu.agh.imageprocessing.features.detail.viemodel;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.app.constants.AppConstants;
import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.ResourceType;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.local.dao.OperationWithChainAndResource;
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
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationParameter;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationResolver;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationResolverParameters;
import pl.edu.agh.imageprocessing.features.detail.images.OpenCvTypes;

import static pl.edu.agh.imageprocessing.data.ImageOperationType.BINARIZATION;
import static pl.edu.agh.imageprocessing.data.ImageOperationType.DILATION;
import static pl.edu.agh.imageprocessing.data.ImageOperationType.EROSION;

/**
 * Created by bwolcerz on 20.08.2017.
 */

public class ImageOperationViewModel extends BaseViewModel {
    public static final String TAG = ImageOperationFragment.class.getSimpleName();
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

    private void showImage(Resource resource) {
        if (resource==null || resource.getContent() ==null)
            throw new AssertionError(); //todo to delete after check
        EventBus.getDefault().post(new EventSimpleDataMsg(Uri.parse(resource.getContent())));
    }

    public void showMatrixDialog(String title, int height, int width, ImageOperationType imageOperationType) {
        FragmentManager fm = provideActivity().getSupportFragmentManager();
        MatrixCustomDialog dialog = MatrixCustomDialog.newInstance(title, width, height);
        dialog.show(fm, "matrix_values");
        dialog.setListener((width1, height1, matrix) -> {
            state.setMatrixWidth(width1);
            state.setMatrixHeight(height1);
            state.setMatrix(matrix);
            callImageOperation(state.getBaseResource(),mapStateToParameter(state),imageOperationType);
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
            callImageOperation(state.getBaseResource(),mapStateToParameter(state),imageOperationType);
        });
    }

    private void callImageOperation(Resource base, ImageOperationResolverParameters parameter, ImageOperationType imageOperationType) {

        Observable.create((ObservableOnSubscribe<Long>) e -> {
            Operation oper = new Operation.Builder()
                    .creationDate(new Date(System.currentTimeMillis()))
                    .operationType(imageOperationType.name()).build();
            e.onNext(operationDao.save(oper));
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .subscribe(processingOperationId -> {
                    try {
                        Observable.just(imageOperationResolver.resolveOperation(imageOperationType,parameter , processingOperationId))
                                .map(basicOperation ->
                                        imageOperationResolver.processResult(basicOperation.execute()))
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(resource -> {
                                    EventBus.getDefault().post(new EventBasicViewConfirmActionVisiblity(EventBasicView.ViewState.VISIBLE));
                                    showImage(resource);
                                    provideActivity().binding.doOper.setOnFabClickListener(view -> {
                                        //todo nothing if accepted
                                        Observable.create((ObservableOnSubscribe<Boolean>) e -> e.onNext(operationResourceAPIRepository
                                                .chainOperations(operationDao
                                                                .get(base.getOperationId()).blockingFirst(),
                                                        operationDao
                                                                .get(processingOperationId).blockingFirst())))
                                                .subscribeOn(Schedulers.computation())
                                                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                                                        aBoolean -> {
                                                            state.setBaseResource(resource);
                                                            showImage(state.getBaseResource());
                                                        }
                                        );
                                        EventBus.getDefault().post(new EventBasicViewHideBottomActionParameters(EventBasicView.ViewState.HIDEN));
                                        EventBus.getDefault().post(new EventBasicViewConfirmActionVisiblity(EventBasicView.ViewState.HIDEN));
                                    });
                                    provideActivity().binding.clearOper.setOnFabClickListener(view -> {
                                        //todo delete oper
                                        showImage(base);
                                        EventBus.getDefault().post(new EventBasicViewHideBottomActionParameters(EventBasicView.ViewState.HIDEN));
                                        EventBus.getDefault().post(new EventBasicViewConfirmActionVisiblity(EventBasicView.ViewState.HIDEN));
                                    });
                                });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

    }
    private ImageOperationResolverParameters mapStateToParameter(ImageOperationViewModelState state){
        return new ImageOperationResolverParameters.Builder()
                .imageUri(Uri.parse(state.getBaseResource().getContent()))
                .matrix(state.getMatrix())
                .matrixHeight(state.getMatrixHeight())
                .matrixWidth(state.getMatrixWidth())
                .morphologyElementType(state.getMorphologyElementType())
                .morphologyHeight(state.getMorphologyHeight())
                .morphologyWidth(state.getMorphologyWidth())
                .threshold(state.getThreshold())
                .build();
    }
    @Subscribe
    public void showBinarization(ShowBinarizationEvent event) {
        state.setOperationType(BINARIZATION);
        provideFragment().binding.seekbar.setSeekBarValueChangedListener((i, b) -> {
                    Log.i(TAG, "HomeViewModel: seekBar value changed:" + i);
                    state.setThreshold(i);
                    EventBus.getDefault().post(new EventSimpleDataMsg(String.valueOf(state.getThreshold())));
                    callImageOperation(state.getBaseResource(),mapStateToParameter(state),state.getOperationType());

                }
        );
        EventBus.getDefault().post(new EventBasicViewSeekBarVisibility(EventBasicView.ViewState.VISIBLE));
        provideFragment().binding.seekbar.setMaxValue(AppConstants.MAX_ADAPTIVE_THRESHOLD);
        EventBus.getDefault().post(new EventBasicViewMainPhoto(EventBasicView.ViewState.VISIBLE));
    }

    @Subscribe
    public void showErosionAndDilation(ShowErosionAndDilationEvent event) {
        if (event.getData() instanceof String) {
            String title = null;
            ImageOperationType type = null;
            switch (ImageOperationType.valueOf((String) event.getData())) {
                case DILATION:
                    title = provideFragment().getString(R.string.title_dilation_dialog);
                    type = DILATION;
                    break;
                case EROSION:
                    title = provideFragment().getString(R.string.title_erosion_dialog);
                    type = EROSION;
                    break;

            }
            if (title != null) {
                showErosionDilationDialog(title, type);
            }
        }
    }

    @Subscribe
    public void showFilter(ShowFilterEvent event) {
        showMatrixDialog(provideFragment().getString(R.string.title_matrix_value_dialog), 3, 3, ImageOperationType.FILTER);
    }

    private List<Resource> getResourceByTypeFromOperationWithResourceEntity(OperationWithChainAndResource operationWithChainAndResource, ResourceType resourceType, int limit){
        if( operationWithChainAndResource == null  || operationWithChainAndResource.getResource()==null || operationWithChainAndResource.getResource().size()==0 ){
            throw new AssertionError();
        }
        List<Resource> result = new LinkedList<>();
        for (int i = 0; i < operationWithChainAndResource.getResource().size(); i++) {
            if ( ResourceType.valueOf(operationWithChainAndResource.getResource().get(i).getType()) == resourceType){
                result.add(operationWithChainAndResource.getResource().get(i));
                if(limit<=result.size()){
                    return result;
                }
            }
        }

        return result;
    }
    public void setData(OperationWithChainAndResource data) {
        state.setOperationWithResource=data;
        state.setBaseResource(getResourceByTypeFromOperationWithResourceEntity(data,ResourceType.IMAGE_FILE,1).get(0));
        EventBus.getDefault().post(new EventSimpleDataMsg(Uri.parse(state.getBaseResource().getContent())));
    }

    public class ImageOperationViewModelState {
        private Resource processingResource;
        private Resource baseResource;
        private ImageOperationType operationType;
        private int morphologyWidth;
        private int morphologyHeight;
        private int morphologyElementType;
        private int threshold;
        private int matrixHeight;
        private int matrixWidth;
        private int[] matrix;
        public OperationWithChainAndResource setOperationWithResource;

        public ImageOperationType getOperationType() {
            return operationType;
        }

        public void setOperationType(ImageOperationType operationType) {
            this.operationType = operationType;
        }

        public Resource getProcessingResource() {
            return processingResource;
        }

        public void setProcessingResource(Resource processingResource) {
            this.processingResource = processingResource;
        }

        public Resource getBaseResource() {
            return baseResource;
        }

        public void setBaseResource(Resource baseResource) {
            this.baseResource = baseResource;
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
