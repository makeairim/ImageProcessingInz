package pl.edu.agh.imageprocessing.features.detail.viemodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.vansuita.pickimage.dialog.PickImageDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.app.constants.AppConstants;
import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.ResourceType;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.remote.OperationResourceAPIRepository;
import pl.edu.agh.imageprocessing.features.detail.android.DilationErosionCustomDialog;
import pl.edu.agh.imageprocessing.features.detail.android.MatrixCustomDialog;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicView;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewConfirmActionVisiblity;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewHideBottomActionParameters;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewListOperationsVisiblity;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewMainPhoto;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewSeekBarVisibility;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventSimpleDataMsg;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowMainViewVisibilityEventBasicView;
import pl.edu.agh.imageprocessing.features.detail.home.HomeActivity;
import pl.edu.agh.imageprocessing.features.detail.home.OperationHomeListCallback;
import pl.edu.agh.imageprocessing.features.detail.images.FileTools;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationParameter;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationResolver;
import pl.edu.agh.imageprocessing.features.detail.images.OpenCvTypes;
import pl.edu.agh.imageprocessing.features.detail.images.operation.BasicOperation;

import static pl.edu.agh.imageprocessing.data.ImageOperationType.BINARIZATION;

/**
 * Created by bwolcerz on 27.07.2017.
 */

public class HomeViewModel extends BaseViewModel implements OperationHomeListCallback {
    public static final String TAG = HomeViewModel.class.getSimpleName();
    @Inject
    OperationResourceAPIRepository operationResourceAPIRepository;
    @Inject
    OperationDao operationDao;
    @Inject
    PickImageDialog pickImageDialog;
    @Inject
    FileTools fileTools;
    @Inject
    Context context;
    @Inject
    ImageOperationResolver imageOperationResolver;
    @Inject
    OpenCvTypes openCvTypes;

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

//operationResourceAPIRepository.saveResource(ResourceType.FILE, o.toString())
    public void photoPicker() {
        pickImageDialog.setOnPickResult(pickResult -> {
            io.reactivex.Observable.create(e -> e.onNext(fileTools.saveFile(pickResult.getBitmap(), context)))
                    .observeOn(Schedulers.computation())
                    .subscribe(o ->
                            operationResourceAPIRepository.saveResource(ResourceType.FILE, o.toString(),operationDao.save(operationResourceAPIRepository.createOperation()))
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(idRes->{
                                if (idRes!=null) {
                                    state.setCurrentImageUri((Uri) o);
                                    showImage();
                                } else {
                                    //todo handle failure
                                }
                            } ));
        }).show(provideActivity());
    }

    private void showImage() {
        if (state.getCurrentImageUri() != null) {
            EventBus.getDefault().post(new EventSimpleDataMsg(state.getCurrentImageUri()));
        } else {
            EventBus.getDefault().post(new EventSimpleDataMsg(state.getBitmap()));
        }
    }

    @Override
    public void onImageOperationClicked(ImageOperationType imageOperationType, View sharedView) {
        Log.i(TAG, "onImageOperationClicked: " + imageOperationType.name());
        state.setOperationType(imageOperationType);
        EventBus.getDefault().post(new EventBasicViewListOperationsVisiblity(EventBasicView.ViewState.HIDEN));
        EventBus.getDefault().post(new EventBasicViewHideBottomActionParameters(EventBasicView.ViewState.HIDEN));
        EventBus.getDefault().post(new EventBasicViewConfirmActionVisiblity(EventBasicView.ViewState.HIDEN));
        switch (imageOperationType) {
            case BINARIZATION:
                provideActivity().binding.seekbar.setSeekBarValueChangedListener((i, b) -> {
                            Log.i(TAG, "HomeViewModel: seekBar value changed:" + i);
                            state.setThreshold(i);
                            EventBus.getDefault().post(new EventSimpleDataMsg(String.valueOf(state.getThreshold())));
                            if (BINARIZATION.equals(state.getOperationType())) {
                                callImageOperation(state.getOperationType());
                            }
                        }
                );
                EventBus.getDefault().post(new EventBasicViewSeekBarVisibility(EventBasicView.ViewState.VISIBLE));
                provideActivity().binding.seekbar.setMaxValue(AppConstants.MAX_ADAPTIVE_THRESHOLD);
                EventBus.getDefault().post(new EventBasicViewMainPhoto(EventBasicView.ViewState.VISIBLE));
                break;
            case EROSION:
                showErosionDilationDialog(provideActivity().getString(R.string.title_erosion_dialog), imageOperationType);
                break;
            case DILATION:
                showErosionDilationDialog(provideActivity().getString(R.string.title_dilation_dialog), imageOperationType);
                break;
            case FILTER:
                showMatrixDialog(provideActivity().getString(R.string.title_matrix_value_dialog), 3, 3, imageOperationType);
                break;
            default:
                throw new AssertionError("Could not resolve operation type");
        }

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
        BasicOperation operation = null;
        try {
            operation = imageOperationResolver.resolveOperation(imageOperationType, state);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap resultBitmap = null;
        resultBitmap = operation.execute().getParameter().getImageBitmap();
        state.setCurrentImageUri(null);
        state.setBitmap(resultBitmap);
        EventBus.getDefault().post(new EventBasicViewConfirmActionVisiblity(EventBasicView.ViewState.VISIBLE));
        showImage();
        provideActivity().binding.doOper.setOnClickListener(view -> {
            //todo save image and operation to DB
            //todo store as chain or add to existing
            EventBus.getDefault().post(new EventBasicViewHideBottomActionParameters(EventBasicView.ViewState.HIDEN));
            EventBus.getDefault().post(new EventBasicViewConfirmActionVisiblity(EventBasicView.ViewState.HIDEN));
        });
        provideActivity().binding.clearOper.setOnClickListener(view -> {
            //todo restore previous image
            EventBus.getDefault().post(new EventBasicViewHideBottomActionParameters(EventBasicView.ViewState.HIDEN));
            EventBus.getDefault().post(new EventBasicViewConfirmActionVisiblity(EventBasicView.ViewState.HIDEN));
        });
    }

    public void provideOperationTypes() {
        operationResourceAPIRepository.getImageOperationTypes()
                .subscribe(resources -> {
                    EventBus.getDefault().post(new EventBasicViewListOperationsVisiblity(EventBasicView.ViewState.VISIBLE));
                    provideActivity().binding.setResource(resources);
                });
    }


    public static class HomeViewModelState {
        private Long previousOperationId = null;
        private ImageOperationType operationType;
        private ImageOperationParameter parameter;
        private Uri currentImageUri;
        private Bitmap bitmap;
        private int morphologyWidth;
        private int morphologyHeight;
        private int morphologyElementType;
        public int threshold;
        private int matrixHeight;
        private int matrixWidth;
        private int[] matrix;

        public HomeViewModelState() {

        }


        public Long getPreviousOperationId() {
            return previousOperationId;
        }

        public void setPreviousOperationId(Long previousOperationId) {
            this.previousOperationId = previousOperationId;
        }

        public ImageOperationType getOperationType() {
            return operationType;
        }

        public void setOperationType(ImageOperationType operationType) {
            this.operationType = operationType;
        }

        public ImageOperationParameter getParameter() {
            return parameter;
        }

        public void setParameter(ImageOperationParameter parameter) {
            this.parameter = parameter;
        }

        public Uri getCurrentImageUri() {
            return currentImageUri;
        }

        public void setCurrentImageUri(Uri currentImageUri) {
            this.currentImageUri = currentImageUri;
        }


        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public void setMorphologyWidth(int morphologyWidth) {
            this.morphologyWidth = morphologyWidth;
        }

        public int getMorphologyWidth() {
            return morphologyWidth;
        }

        public void setMorphologyHeight(int morphologyHeight) {
            this.morphologyHeight = morphologyHeight;
        }

        public int getMorphologyHeight() {
            return morphologyHeight;
        }

        public void setMorphologyElementType(int morphologyElementType) {
            this.morphologyElementType = morphologyElementType;
        }

        public int getMorphologyElementType() {
            return morphologyElementType;
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
        }

        public int getThreshold() {
            return threshold;
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
