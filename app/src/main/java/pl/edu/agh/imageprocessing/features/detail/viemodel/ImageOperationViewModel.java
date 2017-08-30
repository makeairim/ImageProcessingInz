package pl.edu.agh.imageprocessing.features.detail.viemodel;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
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
import pl.edu.agh.imageprocessing.data.local.OperationStatus;
import pl.edu.agh.imageprocessing.data.local.ResourceType;
import pl.edu.agh.imageprocessing.data.local.converter.UriSerializer;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.local.dao.OperationWithChainAndResource;
import pl.edu.agh.imageprocessing.data.local.dao.OperationWithChainAndResourceDao;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.data.local.entity.Resource;
import pl.edu.agh.imageprocessing.data.remote.OperationResourceAPIRepository;
import pl.edu.agh.imageprocessing.features.detail.android.DilationErosionCustomDialog;
import pl.edu.agh.imageprocessing.features.detail.android.MatrixCustomDialog;
import pl.edu.agh.imageprocessing.features.detail.android.event.ChainOperationEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.DataChangedEvent;
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
import pl.edu.agh.imageprocessing.features.detail.home.OperationFragmentListCallback;
import pl.edu.agh.imageprocessing.features.detail.images.FileTools;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationResolver;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationResolverParameters;
import pl.edu.agh.imageprocessing.features.detail.images.OpenCvTypes;

import static pl.edu.agh.imageprocessing.data.ImageOperationType.BINARIZATION;
import static pl.edu.agh.imageprocessing.data.ImageOperationType.DILATION;
import static pl.edu.agh.imageprocessing.data.ImageOperationType.EROSION;

/**
 * Created by bwolcerz on 20.08.2017.
 */

public class ImageOperationViewModel extends BaseViewModel implements OperationFragmentListCallback {
    public static final String TAG = ImageOperationFragment.class.getSimpleName();
    @Inject
    OperationResourceAPIRepository operationResourceAPIRepository;
    @Inject
    OperationWithChainAndResourceDao operationWithChainAndResourceDao;
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

    @Override
    public void onImageOperationClicked(OperationWithChainAndResource operationWithChainAndResource, View sharedView) {
    }

    //    }e->{e.onNext(operationWithChainAndResourceDao.getChainOperationsSortedAsc(rootId));e.onComplete();}
    private void loadOperationChain(long rootId) {
        Observable.create((ObservableOnSubscribe<List<OperationWithChainAndResource>>) e -> {
            e.onNext(operationWithChainAndResourceDao.getChainOperationsSortedAsc(rootId));
            e.onComplete();
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(operationWithChainAndResources -> {
                    state.setOperationChainAndResource(operationWithChainAndResources);
                    EventBus.getDefault().post(new DataChangedEvent());
//            provideFragment().binding.setResource(new pl.edu.agh.imageprocessing.data.remote.Resource(state.getOperationChainAndResource()));
//            provideFragment().binding.notifyChange();
//            provideFragment().binding.recyclerView.invalidate();
//            provideFragment().binding.executePendingBindings();
                });
    }


    private Operation createOperation(ImageOperationType type, ImageOperationResolverParameters parameters){
        return new Operation.Builder()
                .creationDate(new Date(System.currentTimeMillis()))
                .operationType(type.name())
                .object(new GsonBuilder().registerTypeAdapter(Uri.class,new UriSerializer()).create().toJson(parameters)).build();
    }
    private void saveOperation(Operation oper){
        Observable<Long> prcessingOperationObservable = Observable.create(e -> {
            try {
                oper.setStatus(OperationStatus.CREATED);
                Long id = operationDao.save(oper);
                e.onNext(id);
                e.onComplete();
            } catch (Exception error) {
                e.onError(error);
            }
        });
        OperationWithChainAndResource previous = state.getOperationChainAndResource().get(state.getOperationChainAndResource().size() - 1);
        prcessingOperationObservable.subscribeOn(Schedulers.newThread()).observeOn(Schedulers.newThread()).subscribe(o -> {
            oper.setId(o);
            state.getOperationChainAndResource().add(new OperationWithChainAndResource.Builder().operation(oper).resource(Collections.emptyList()).build());
            EventBus.getDefault().post(new DataChangedEvent());
            EventBus.getDefault().post(new ChainOperationEvent(previous.getOperation().getId(),o));
        });//todo handle dispisable
    }
    public void showMatrixDialog(String title, int height, int width, ImageOperationType imageOperationType) {
        FragmentManager fm = provideActivity().getSupportFragmentManager();
        MatrixCustomDialog dialog = MatrixCustomDialog.newInstance(title, width, height);
        dialog.show(fm, "matrix_values");
        dialog.setListener((width1, height1, matrix) -> {
            state.setMatrixWidth(width1);
            state.setMatrixHeight(height1);
            state.setMatrix(matrix);
            saveOperation(createOperation(imageOperationType, mapStateToParameter(state)));
            //todo notify data changed;
//            callImageOperation(mapStateToParameter(state), imageOperationType).subscribeOn(Schedulers.computation()).observeOn(Schedulers.computation()).subscribe(resource -> {
//                state.setProcessingResource(resource);
//                EventBus.getDefault().post(new EventSimpleDataMsg(Uri.parse(resource.getContent())));
//                EventBus.getDefault().post(new EventBasicViewConfirmActionVisiblity(EventBasicView.ViewState.VISIBLE));
//            });
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
            saveOperation(createOperation(imageOperationType, mapStateToParameter(state)));
            //            callImageOperation(mapStateToParameter(state), imageOperationType).subscribeOn(Schedulers.computation()).observeOn(Schedulers.computation()).subscribe(resource -> {
//                state.setProcessingResource(resource);
//                EventBus.getDefault().post(new EventSimpleDataMsg(Uri.parse(resource.getContent())));
//                EventBus.getDefault().post(new EventBasicViewConfirmActionVisiblity(EventBasicView.ViewState.VISIBLE));
//            });
        });
    }

//    private Observable<Resource> callImageOperation(ImageOperationResolverParameters parameter, ImageOperationType imageOperationType) {
//
//        return prcessingOperationObservable.subscribeOn(Schedulers.computation())
//                .doOnError(throwable -> Log.i(TAG, "callImageOperation: " + throwable.getMessage()))
//                .map(id -> imageOperationResolver.processResult(imageOperationResolver.resolveOperation(imageOperationType, parameter, id).execute()))
//                .subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread());
//    }

    private ImageOperationResolverParameters mapStateToParameter(ImageOperationViewModelState state) {
        List<Resource> lastOperationResource = getResourceByTypeFromOperationWithResourceEntity(
                state.getOperationChainAndResource().get(state.getOperationChainAndResource().size() - 1),
                ResourceType.IMAGE_FILE,
                1);
        return new ImageOperationResolverParameters.Builder()
                .imageUri(Uri.parse(lastOperationResource.get(0).getContent()))
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
                    saveOperation(createOperation(BINARIZATION, mapStateToParameter(state)));
                    //                    callImageOperation(mapStateToParameter(state), BINARIZATION).subscribeOn(Schedulers.computation()).observeOn(Schedulers.computation()).subscribe(resource -> {
//                        state.setProcessingResource(resource);
//                        EventBus.getDefault().post(new EventSimpleDataMsg(Uri.parse(resource.getContent())));
//                        EventBus.getDefault().post(new EventBasicViewConfirmActionVisiblity(EventBasicView.ViewState.VISIBLE));
//                    });

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

    private List<Resource> getResourceByTypeFromOperationWithResourceEntity(OperationWithChainAndResource operationWithChainAndResource, ResourceType resourceType, int limit) {
        if (operationWithChainAndResource == null || operationWithChainAndResource.getResource() == null || operationWithChainAndResource.getResource().isEmpty()) {
            throw new AssertionError(); //todo to delete
        }
        List<Resource> result = new LinkedList<>();
        for (int i = 0; i < operationWithChainAndResource.getResource().size(); i++) {
            if (ResourceType.valueOf(operationWithChainAndResource.getResource().get(i).getType()) == resourceType) {
                result.add(operationWithChainAndResource.getResource().get(i));
                if (limit <= result.size()) {
                    return result;
                }
            }
        }

        return result;
    }
    private OperationWithChainAndResource getLastFinishedOperation(List<OperationWithChainAndResource> operationWithChainAndResources){
        if( operationWithChainAndResources.size() == 0){
            Log.e(TAG, "getLastFinishedOperation: ",new AssertionError("Passed zero length list") );
        }
        for (int i = operationWithChainAndResources.size()-1; i >=0; i--) {
            if ( operationWithChainAndResources.get(i).getOperation().getStatus() == OperationStatus.FINISHED ){
                return operationWithChainAndResources.get(i);
            }
        }
        Log.e(TAG, "getLastFinishedOperation: ", new AssertionError("operation chain with no saved resource file"));
        throw new RuntimeException();
    }

    @Subscribe
    public void chainOperation(ChainOperationEvent event) {
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
                    e.onNext(operationResourceAPIRepository
                            .chainOperations(operationDao
                                            .get(event.getBaseOperationId()).blockingFirst(),
                                    operationDao
                                            .get(event.getProcessingOperationId()).blockingFirst()));
                    e.onComplete();
                }
        )
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                aBoolean -> {
                    Log.i(TAG, "chainOperation: operationParent=" + event.getBaseOperationId() + " child=" + event.getProcessingOperationId());
                    //showImage(state.getBaseResource());

                    //todo update item list view status
                    //todo handle failure ?
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void notifyDataChanged(DataChangedEvent event) {
        provideFragment().adapter.setData(state.getOperationChainAndResource());
        provideFragment().adapter.notifyDataSetChanged();
    }
    public void setUp() {
        provideActivity().binding.doOper.setOnFabClickListener(view -> {
            //todo nothing if accepted
//            state.setBaseResource(state.getProcessingResource());
//            OperationWithChainAndResource lastFinished = getLastFinishedOperation(state.getOperationChainAndResource());
//            if(lastFinished.getOperation().getParentOperationId()!=null){
//                throw new RuntimeException("Cannot chain already chained operation");
//            }
            EventBus.getDefault().post(new EventBasicViewHideBottomActionParameters(EventBasicView.ViewState.HIDEN));
            EventBus.getDefault().post(new EventBasicViewConfirmActionVisiblity(EventBasicView.ViewState.HIDEN));
            operationResourceAPIRepository.deleteUnchainedOperations();
        });
        provideActivity().binding.clearOper.setOnFabClickListener(view -> {

            //            EventBus.getDefault().post(new EventSimpleDataMsg(Uri.parse(state.getBaseResource().getContent())));
            EventBus.getDefault().post(new EventBasicViewHideBottomActionParameters(EventBasicView.ViewState.HIDEN));
            EventBus.getDefault().post(new EventBasicViewConfirmActionVisiblity(EventBasicView.ViewState.HIDEN));
            operationResourceAPIRepository.deleteUnchainedOperations();
        });
    }
    public void setUp(Long rootOperationId) {
        loadOperationChain(rootOperationId);
    }


    public class ImageOperationViewModelState {
        private ImageOperationType operationType;
        private int morphologyWidth;
        private int morphologyHeight;
        private int morphologyElementType;
        private int threshold;
        private int matrixHeight;
        private int matrixWidth;
        private int[] matrix;
        private List<OperationWithChainAndResource> operationChainAndResource;
        private Long rootOperationId;

        public List<OperationWithChainAndResource> getOperationChainAndResource() {
            return operationChainAndResource;
        }



        public Long getRootOperationId() {
            return rootOperationId;
        }

        public void setRootOperationId(Long rootOperationId) {
            this.rootOperationId = rootOperationId;
        }

        public void setOperationChainAndResource(List<OperationWithChainAndResource> operationChainAndResource) {
            this.operationChainAndResource = operationChainAndResource;
        }

        public ImageOperationType getOperationType() {
            return operationType;
        }

        public void setOperationType(ImageOperationType operationType) {
            this.operationType = operationType;
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
