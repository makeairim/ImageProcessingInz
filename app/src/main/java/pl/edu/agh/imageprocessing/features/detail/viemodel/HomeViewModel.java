package pl.edu.agh.imageprocessing.features.detail.viemodel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.dmstocking.optional.java.util.Optional;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.edu.agh.imageprocessing.BaseFragment;
import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.app.constants.AppConstants;
import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.OperationStatus;
import pl.edu.agh.imageprocessing.data.local.ResourceType;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.data.remote.OperationResourceAPIRepository;
import pl.edu.agh.imageprocessing.features.detail.android.dialog.PhotoPickerDialog;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicView;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewConfirmActionVisiblity;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewHideBottomActionParameters;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewListOperationsVisiblity;
import pl.edu.agh.imageprocessing.features.detail.android.event.LiveVideoEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.OnTransitionToOperationRootViewEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.OperationsViewEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowArithmeticOperationEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowBinarizationEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowCannyEdgeDialogEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowErosionAndDilationEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowFilterEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowHarrisEdgeEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowMainViewVisibilityEventBasicView;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowSizeDialogEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowSobelOperatorEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ToggleOperationToolbar;
import pl.edu.agh.imageprocessing.features.detail.android.operationtypeslist.GroupOperationModel;
import pl.edu.agh.imageprocessing.features.detail.home.GalleryActivity;
import pl.edu.agh.imageprocessing.features.detail.home.HomeActivity;
import pl.edu.agh.imageprocessing.features.detail.home.ImageOperationFragment;
import pl.edu.agh.imageprocessing.features.detail.home.ListOperationsFragment;
import pl.edu.agh.imageprocessing.features.detail.home.OperationHomeListCallback;
import pl.edu.agh.imageprocessing.features.detail.images.FileTools;

/**
 * Created by bwolcerz on 27.07.2017.
 */

public class HomeViewModel extends BaseViewModel implements OperationHomeListCallback {
    public static final String TAG = HomeViewModel.class.getSimpleName();

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

    @Override
    public void setUp() {
        BaseFragment mRetainedFragment = provideActivity().getActiveFragment();
        if (mRetainedFragment == null) {
            showOperationRoots();
        }
    }

    @Override
    public Bundle saveState() {
        return null;
    }

    @Override
    public void restoreState(Bundle bundle) {
    }

    HomeViewModelState state = new HomeViewModelState();
    public Callable<Void> onOutsideListClick = () -> {
        EventBus.getDefault().post(new ShowMainViewVisibilityEventBasicView(EventBasicView.ViewState.VISIBLE));
        return null;
    };

    @Inject
    public HomeViewModel() {
    }

    private Bitmap fromGallery(final Uri selectedImageUri) {
        try {
            Bitmap bm = null;
            try {
                bm = MediaStore.Images.Media.getBitmap(provideActivity().getContentResolver(), selectedImageUri);
            } catch (IOException e) {
                Log.e(TAG, "fromGallery: " + e.getMessage(), e);
            }

            ExifInterface exif = new ExifInterface(selectedImageUri.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    angle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    angle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    angle = 270;
                    break;
                default:
                    angle = 0;
                    break;
            }
            Matrix mat = new Matrix();
            if (angle == 0 && bm.getWidth() > bm.getHeight())
                mat.postRotate(-90);
            else
                mat.postRotate(angle);

            return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), mat, true);

        } catch (IOException e) {
            Log.e("", "-- Error in setting image");
        } catch (OutOfMemoryError oom) {
            Log.e("", "-- OOM Error in setting image");
        }
        return null;
    }

    public void photoPicker() {
        FragmentManager fm = provideActivity().getSupportFragmentManager();
        PhotoPickerDialog dialog = PhotoPickerDialog.newInstance("Pick photo", fileTools.getPhotos());
        dialog.show(fm, "photo_picker");
        dialog.setListener((uri, photoEvent) -> Observable.create(e -> e.onNext(fileTools.saveFile(fromGallery(uri))))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(o -> {
                    Operation operation = operationResourceAPIRepository.createOperation();
                    operation.setOperationType(ImageOperationType.BASIC_PHOTO);
                    operation.setStatus(OperationStatus.FINISHED);
                    operation.setId(operationDao.save(operation));
                    operationResourceAPIRepository.saveResource(ResourceType.IMAGE_FILE, o.toString(), operation.getId())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(res -> EventBus.getDefault().post(new OperationsViewEvent(res.getOperationId())));
                }));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toggleToolbarOperationView(ToggleOperationToolbar event) {
        provideActivity().binding.videoButton.setVisibility(View.VISIBLE);
        provideActivity().binding.operationButton.setVisibility(View.VISIBLE);
        provideActivity().binding.exportButton.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showImageOperation(OperationsViewEvent event) {
        //todo
        toggleToolbarOperationView(new ToggleOperationToolbar());
        FragmentTransaction ft = provideActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(provideActivity().binding.container.getId(), ImageOperationFragment.newInstance(event.getId()), HomeActivity.RETAINED_FRAGMENT_TAG);
        ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
        ft.addToBackStack(HomeActivity.RETAINED_FRAGMENT_TAG);
        ft.commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTransitionToOperationRootView(OnTransitionToOperationRootViewEvent event) {
        onTransitionToRootView();
    }

    private void onTransitionToRootView() {
        provideActivity().binding.videoButton.setVisibility(View.GONE);
        provideActivity().binding.operationButton.setVisibility(View.GONE);
        provideActivity().binding.exportButton.setVisibility(View.GONE);
    }

    private void showOperationRoots() {
        onTransitionToRootView();
        FragmentTransaction ft = provideActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(provideActivity().binding.container.getId(), ListOperationsFragment.newInstance(), HomeActivity.RETAINED_FRAGMENT_TAG);
        ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
//        ft.addToBackStack(HomeActivity.RETAINED_FRAGMENT_TAG);
        ft.commit();
    }


    @Override
    public void onImageOperationClicked(ImageOperationType imageOperationType) {
        Log.i(TAG, "onImageOperationClicked: " + imageOperationType.name());
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
            case MEAN_FILTER:
                EventBus.getDefault().post(new ShowSizeDialogEvent());
                break;
            case CANNY_EDGE:
                EventBus.getDefault().post(new ShowCannyEdgeDialogEvent());
                break;
            case HARRIS_CORNER:
                EventBus.getDefault().post(new ShowHarrisEdgeEvent());
                break;
            case SOBEL_OPERATOR:
                EventBus.getDefault().post(new ShowSobelOperatorEvent());
                break;
            case ADD_IMAGES:
            case DIFF_IMAGES:
            case BITWISE_AND:
            case BITWISE_OR:
            case BITWISE_XOR:
                EventBus.getDefault().post(new ShowArithmeticOperationEvent(imageOperationType));
                break;
            default:
                throw new AssertionError("Could not resolve operation type");
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLiveVideoTriggered(LiveVideoEvent event) {
        Optional<Fragment> visibleFragmentOpt = getVisibleFragment();
        if (visibleFragmentOpt.isPresent() && visibleFragmentOpt.get() instanceof ImageOperationFragment) {
            return;
        }
        Operation operation = operationResourceAPIRepository.createOperation();
        operation.setOperationType(ImageOperationType.UNASSIGNED_TO_RESOURCE_ROOT_CHAIN);
        operation.setStatus(OperationStatus.FINISHED);
        Observable.create((ObservableOnSubscribe<Long>) e ->
        {
            e.onNext(operationDao.save(operation));
            e.onComplete();
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rootId -> EventBus.getDefault().post(new OperationsViewEvent(rootId)));
    }

    private Optional<Fragment> getVisibleFragment() {
        FragmentManager fragmentManager = provideActivity().getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return Optional.of(fragment);
            }
        }
        return Optional.empty();
    }

    public Map<String, List<GroupOperationModel>> provideOperationTypes() {
        HashMap<String, List<GroupOperationModel>> result = new HashMap<>();
        result.put(AppConstants.MOPHOLOGY_HEADER, operationResourceAPIRepository.getMorphologyImageOperationTypes());
        result.put(AppConstants.FILTER_HEADER, operationResourceAPIRepository.getFilterImageOperationTypes());
        result.put(AppConstants.OTHER_HEADER, operationResourceAPIRepository.getBasicImageOperationTypes());
        result.put(AppConstants.IMAGE_FEATURE, operationResourceAPIRepository.getImageFeaturesOperationTypes());
        result.put(AppConstants.ARITHMETIC_OPERATIONS_HEADER, operationResourceAPIRepository.getArithmeticOperationTypes());
        EventBus.getDefault().post(new EventBasicViewListOperationsVisiblity(EventBasicView.ViewState.VISIBLE));
        return result;
    }

    public void showImageGallery() {
        List<Uri> photoUris = fileTools.getPhotos();

        if (photoUris != null && !photoUris.isEmpty()) {
            Intent intent = new Intent(provideActivity().getApplicationContext(), GalleryActivity.class);
            intent.putParcelableArrayListExtra(GalleryActivity.PHOTOS_KEY, new ArrayList<>(photoUris));
            intent.putExtra(GalleryActivity.HIDE_ACCEPT_BTN,Boolean.TRUE);
            provideActivity().startActivityForResult(intent, GalleryActivity.REQUEST_CODE);
        } else {
            Toast.makeText(provideActivity(), provideActivity().getString(R.string.no_photo_in_app_gallery), Toast.LENGTH_LONG).show();
        }

    }


    public static class HomeViewModelState {
    }
}
