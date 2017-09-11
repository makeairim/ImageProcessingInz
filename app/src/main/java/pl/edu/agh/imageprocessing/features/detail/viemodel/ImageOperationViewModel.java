package pl.edu.agh.imageprocessing.features.detail.viemodel;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.core.Mat;

import java.io.IOException;
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
import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.OperationStatus;
import pl.edu.agh.imageprocessing.data.local.ResourceType;
import pl.edu.agh.imageprocessing.data.local.converter.UriDeserializer;
import pl.edu.agh.imageprocessing.data.local.converter.UriSerializer;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.local.dao.OperationWithChainAndResource;
import pl.edu.agh.imageprocessing.data.local.dao.OperationWithChainAndResourceDao;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.data.local.entity.Resource;
import pl.edu.agh.imageprocessing.data.remote.OperationResourceAPIRepository;
import pl.edu.agh.imageprocessing.features.detail.android.dialog.BinarizationCustomDialog;
import pl.edu.agh.imageprocessing.features.detail.android.dialog.DilationErosionCustomDialog;
import pl.edu.agh.imageprocessing.features.detail.android.dialog.MatrixCustomDialog;
import pl.edu.agh.imageprocessing.features.detail.android.dialog.SizeCustomDialog;
import pl.edu.agh.imageprocessing.features.detail.android.event.ChainOperationEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.DataChangedEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicView;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewConfirmActionVisiblity;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewHideBottomActionParameters;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewMainPhoto;
import pl.edu.agh.imageprocessing.features.detail.android.event.RefreshDataEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowBinarizationEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowErosionAndDilationEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowFilterEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowSizeDialogEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.TriggerServiceWorkEvent;
import pl.edu.agh.imageprocessing.features.detail.home.HomeActivity;
import pl.edu.agh.imageprocessing.features.detail.home.ImageOperationFragment;
import pl.edu.agh.imageprocessing.features.detail.home.OperationFragmentListCallback;
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
    @Subscribe
    public void refreshData(RefreshDataEvent event) {
        loadOperationChain(state.getRootOperationId());
    }
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
        });

    }
    public void showSizeDialog(String title,ImageOperationType imageOperationType) {
        FragmentManager fm = provideActivity().getSupportFragmentManager();
        SizeCustomDialog dialog = SizeCustomDialog.newInstance(title);
        dialog.show(fm, "matrix_values");
        dialog.setListener((sizeXY) -> {
           state.setMatrixHeight(sizeXY);
           state.setMatrixWidth(sizeXY);
            saveOperation(createOperation(imageOperationType, mapStateToParameter(state)));
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
        });
    }


    private ImageOperationResolverParameters mapStateToParameter(ImageOperationViewModelState state) {
        OperationWithChainAndResource lastOperationResource = getLastFinishedOperation(state.getOperationChainAndResource());
        Resource lastFinishedOperationResource=null;
        for (Resource resource : lastOperationResource.getResource()) {
            if( ResourceType.IMAGE_FILE.equals(ResourceType.valueOf(resource.getType())) ){
                lastFinishedOperationResource=resource;
            }
        }
        return new ImageOperationResolverParameters.Builder()
                .imageUri(Uri.parse(lastFinishedOperationResource.getContent()))
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
        FragmentManager fm = provideActivity().getSupportFragmentManager();
        BinarizationCustomDialog dialog = BinarizationCustomDialog.newInstance("Binarization threshold");
        dialog.show(fm, "operation_parameters");
        dialog.setListener(threshold -> {
            state.setThreshold(threshold);
            saveOperation(createOperation(BINARIZATION, mapStateToParameter(state)));
            EventBus.getDefault().post(new EventBasicViewMainPhoto(EventBasicView.ViewState.VISIBLE));
        });
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
    @Subscribe
    public void showSetSizeDialog(ShowSizeDialogEvent event){
        showSizeDialog(provideFragment().getString(R.string.title_size_dialog),ImageOperationType.MEAN_FILTER);
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
                .subscribeOn(Schedulers.newThread())
                .subscribe(
                aBoolean -> {
                    Log.i(TAG, "chainOperation: operationParent=" + event.getBaseOperationId() + " child=" + event.getProcessingOperationId()+
                    " status:"+aBoolean);
                    //showImage(state.getBaseResource());
                    if(aBoolean){
                        EventBus.getDefault().post(new TriggerServiceWorkEvent());
                    }

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

    @Override
    public Bundle saveState() {
        Bundle bundle=new Bundle();
//        bundle.putParcelable(this.STATE_KEY,state);
        return bundle;
    }

    @Override
    public void restoreState(Bundle bundle) {
        if( bundle != null){
//            state=bundle.getParcelable(this.STATE_KEY);
        }

    }

    public void setUp(Long rootOperationId) {
        state.setRootOperationId(rootOperationId);
        loadOperationChain(rootOperationId);
    }

    public Mat obtainImageOperations(Mat src){
        Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new UriDeserializer()).create();
        for (OperationWithChainAndResource operationWithChainAndResource : state.getOperationChainAndResource()) {
            for (int i = operationWithChainAndResource.getResource().size()-1; i >=0; --i) {
                if(operationWithChainAndResource.getOperation().getParentOperationId()!=null){
                    try {
                        ImageOperationParameter params = imageOperationResolver
                                .imageOperationParameterResolver(ImageOperationType
                                                .valueOf(operationWithChainAndResource
                                                        .getOperation()
                                                        .getOperationType()),
                                gson.fromJson(operationWithChainAndResource
                                        .getOperation()
                                        .getObject(), ImageOperationResolverParameters.class));
                        src=imageOperationResolver.resolveOperation(
                                ImageOperationType.valueOf(operationWithChainAndResource.getOperation().getOperationType())
                                , params, src)
                                .execute().getMat();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return src;
    }

    static public class ImageOperationViewModelState implements Parcelable{
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.operationType == null ? -1 : this.operationType.ordinal());
            dest.writeInt(this.morphologyWidth);
            dest.writeInt(this.morphologyHeight);
            dest.writeInt(this.morphologyElementType);
            dest.writeInt(this.threshold);
            dest.writeInt(this.matrixHeight);
            dest.writeInt(this.matrixWidth);
            dest.writeIntArray(this.matrix);
            dest.writeTypedList(this.operationChainAndResource);
            dest.writeValue(this.rootOperationId);
        }

        public ImageOperationViewModelState() {
        }

        protected ImageOperationViewModelState(Parcel in) {
            int tmpOperationType = in.readInt();
            this.operationType = tmpOperationType == -1 ? null : ImageOperationType.values()[tmpOperationType];
            this.morphologyWidth = in.readInt();
            this.morphologyHeight = in.readInt();
            this.morphologyElementType = in.readInt();
            this.threshold = in.readInt();
            this.matrixHeight = in.readInt();
            this.matrixWidth = in.readInt();
            this.matrix = in.createIntArray();
            this.operationChainAndResource = in.createTypedArrayList(OperationWithChainAndResource.CREATOR);
            this.rootOperationId = (Long) in.readValue(Long.class.getClassLoader());
        }

        public static final Creator<ImageOperationViewModelState> CREATOR = new Creator<ImageOperationViewModelState>() {
            @Override
            public ImageOperationViewModelState createFromParcel(Parcel source) {
                return new ImageOperationViewModelState(source);
            }

            @Override
            public ImageOperationViewModelState[] newArray(int size) {
                return new ImageOperationViewModelState[size];
            }
        };
    }
}
