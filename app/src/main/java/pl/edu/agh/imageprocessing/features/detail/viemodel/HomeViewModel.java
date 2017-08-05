package pl.edu.agh.imageprocessing.features.detail.viemodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.vansuita.pickimage.dialog.PickImageDialog;

import java.io.IOException;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.edu.agh.imageprocessing.app.constants.AppConstants;
import pl.edu.agh.imageprocessing.dagger.GlideApp;
import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.remote.ImageProcessingAPIRepository;
import pl.edu.agh.imageprocessing.features.detail.android.DilationCustomDialog;
import pl.edu.agh.imageprocessing.features.detail.home.OperationHomeListCallback;
import pl.edu.agh.imageprocessing.features.detail.images.FileTools;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationParameter;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationTypeResolver;
import pl.edu.agh.imageprocessing.features.detail.images.OpenCvTypes;
import pl.edu.agh.imageprocessing.features.detail.images.operation.BasicOperation;
import pl.edu.agh.imageprocessing.features.detail.images.operation.BinarizationOperation;
import pl.edu.agh.imageprocessing.features.detail.images.operation.DilationOperation;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by bwolcerz on 27.07.2017.
 */

public class HomeViewModel extends BaseViewModel implements OperationHomeListCallback {
    public static final String TAG = HomeViewModel.class.getSimpleName();
    @Inject
    ImageProcessingAPIRepository imageProcessingAPIRepository;
    @Inject
    PickImageDialog pickImageDialog;
    @Inject
    FileTools fileTools;
    @Inject
    Context context;
    @Inject
    ImageOperationTypeResolver imageOperationTypeResolver;
    @Inject
    OpenCvTypes openCvTypes;

    HomeViewModelState state = new HomeViewModelState();
    public Callable<Void> onOutsideListClick = () -> {
        provideActivity().binding.parentRecyclerView.setVisibility(GONE);
        provideActivity().binding.ivPhoto.setImageAlpha(AppConstants.IMAGE_VIEW_FULL_OPAQUE);
        return null;
    };

    @Inject
    public HomeViewModel() {
    }

    public void photoPicker() {
        pickImageDialog.setOnPickResult(pickResult -> {
            io.reactivex.Observable.create(e -> e.onNext(fileTools.saveFile(pickResult.getBitmap(), context)))
                    .observeOn(Schedulers.computation())
                    .subscribe(o ->
                            Observable.just(imageProcessingAPIRepository.saveResource((Uri) o))
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(t -> {
                                        if (t) {
                                            state.setCurrentImageUri((Uri) o);
                                            showImage();
                                        } else {
                                            //todo handle failure
                                        }
                                    }));
        }).show(provideActivity());
    }

    private void showImage() {
        provideActivity().binding.ivPhoto.setImageAlpha(AppConstants.IMAGE_VIEW_FULL_OPAQUE);
        if (state.getCurrentImageUri() != null) {
            GlideApp.with(provideActivity()).load(state.getCurrentImageUri()).fitCenter().into(provideActivity().binding.ivPhoto);
        } else if (state.getBitmap() != null) {
            provideActivity().binding.ivPhoto.setImageBitmap(state.getBitmap());
        }
    }

    @Override
    public void onImageOperationClicked(ImageOperationType imageOperationType, View sharedView) {
        Log.i(TAG, "onImageOperationClicked: " + imageOperationType.name());
        switch (imageOperationType) {
            case BINARIZATION:
                provideActivity().binding.parentSeekbar.setVisibility(VISIBLE);
                provideActivity().binding.seekbar.setMaxValue(AppConstants.MAX_ADAPTIVE_THRESHOLD);
                provideActivity().binding.doOper.setOnClickListener((v) -> callImageOperation(imageOperationType));
                provideActivity().binding.seekbar.setSeekBarValueChangedListener((i, b) -> {
                    callImageOperation(imageOperationType);
                });
                provideActivity().binding.parentRecyclerView.setVisibility(GONE);
                provideActivity().binding.ivPhoto.setAlpha(AppConstants.IMAGE_VIEW_FULL_OPAQUE);
                provideActivity().binding.doOper.setOnClickListener(view -> {
                    //todo save image and operation to DB
                    //todo store as chain or add to existing
                    provideActivity().binding.parentSeekbar.setVisibility(GONE);
                });
                provideActivity().binding.clearOper.setOnClickListener(view -> {
                    //todo restore previous image
                    provideActivity().binding.parentSeekbar.setVisibility(GONE);
                });
                break;
            case DILATION:
                showDilationDialog().setListener((width, height, elementType) -> {
                    state.setMorphologyWidth(width);
                    state.setMorphologyHeight(height);
                    state.setMorphologyElementType(OpenCvTypes.MORPH_ELEMENTS.getTypeFromName(elementType));
                    callImageOperation(imageOperationType);
                });
                break;
        }
    }

    private void callImageOperation(ImageOperationType imageOperationType) {
        BasicOperation operation = imageOperationTypeResolver.resolveOperation(imageOperationType);
        try {
            operation.setParameter(imageOperationParameterViewResolver(imageOperationType));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Bitmap resultBitmap = operation.execute();
        state.currentImageUri = null;
        state.bitmap = resultBitmap;
        showImage();
    }

    public void provideOperationTypes() {
        imageProcessingAPIRepository.getImageOperationTypes()
                .subscribe(resources -> {
                    provideActivity().binding.ivPhoto.setImageAlpha(AppConstants.IMAGE_VIEW_PARTIAL_TRANSPARENT);
                    provideActivity().binding.parentRecyclerView.setVisibility(VISIBLE);
                    provideActivity().binding.parentSeekbar.setVisibility(GONE);
                    provideActivity().binding.recyclerView.setAlpha(1.0f);
                    provideActivity().binding.setResource(resources);
                });
    }


    private ImageOperationParameter imageOperationParameterViewResolver(ImageOperationType type) throws IOException {
        switch (type) {
            case BINARIZATION:
                BinarizationOperation.Parameters result;
                result = new BinarizationOperation.Parameters();
                result.setThreshold(provideActivity().binding.seekbar.getProgress());
                result.setImageUri(state.getCurrentImageUri());
                if (state.getCurrentImageUri() != null) {
                    result.setImageBitmap(fileTools.getImageBitmap(context, state.currentImageUri));
                } else if (state.getBitmap() != null) {
                    result.setImageBitmap(state.getBitmap());
                }
                return result;
            case FILTER:
                break;
            case CONVOLUTION:
                break;
            case DILATION:
                DilationOperation.Parameters dilation;
                dilation=new DilationOperation.Parameters();
                dilation.setStructElementHeight(state.getMorphologyElementType());
                dilation.setStructElementHeight(state.getMorphologyHeight());
                dilation.setStructElementWidth(state.getMorphologyWidth());
                dilation.setImageUri(state.getCurrentImageUri());
                if (state.getCurrentImageUri() != null) {
                    dilation.setImageBitmap(fileTools.getImageBitmap(context, state.currentImageUri));
                } else if (state.getBitmap() != null) {
                    dilation.setImageBitmap(state.getBitmap());
                }
                return dilation;
        }
        throw new AssertionError("resolver not provided for operation: " + type.name());
    }

    private DilationCustomDialog showDilationDialog() {
        FragmentManager fm = provideActivity().getSupportFragmentManager();
        DilationCustomDialog dialog = DilationCustomDialog.newInstance("Dilation parameters", openCvTypes.getStructuringElementTypes());
        dialog.show(fm, "operation_parameters");
        return dialog;
    }

    static class HomeViewModelState {
        Long previousOperationId = null;
        ImageOperationType type;
        ImageOperationParameter parameter;
        Uri currentImageUri;
        Bitmap bitmap;
        private int morphologyWidth;
        private int morphologyHeight;
        private int morphologyElementType;

        public HomeViewModelState() {

        }


        public Long getPreviousOperationId() {
            return previousOperationId;
        }

        public void setPreviousOperationId(Long previousOperationId) {
            this.previousOperationId = previousOperationId;
        }

        public ImageOperationType getType() {
            return type;
        }

        public void setType(ImageOperationType type) {
            this.type = type;
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
    }

}
