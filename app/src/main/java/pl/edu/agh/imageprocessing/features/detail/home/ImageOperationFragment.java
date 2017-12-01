package pl.edu.agh.imageprocessing.features.detail.home;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import pl.edu.agh.imageprocessing.BaseFragment;
import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.databinding.PhotoViewBinding;
import pl.edu.agh.imageprocessing.features.detail.android.ViewUtils;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicView;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewMainPhoto;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventSimpleDataMsg;
import pl.edu.agh.imageprocessing.features.detail.android.event.LiveVideoEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.OnTransitionToOperationRootViewEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ToggleOperationToolbar;
import pl.edu.agh.imageprocessing.features.detail.viemodel.ImageOperationViewModel;

/**
 * Created by bwolcerz on 20.08.2017.
 */

public class ImageOperationFragment extends BaseFragment implements CameraBridgeViewBase.CvCameraViewListener2 {
    public PhotoViewBinding binding;
    public static final String TAG=ImageOperationFragment.class.getSimpleName();
    public static final String KEY_ROOT_ID = "KEY_ROOT_ID";
    public static final String KEY_VIDEO_ON = "KYE_VIDEO_ON";
    @Inject
    ViewUtils viewUtils;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    Long rootOperationId;
//    List<Mat> ring = new ArrayList<>();
//    int delay = 100;                       // delay == length of buffer
//    boolean delayed = false;               // state

    public OperationFragmentListAdapter adapter;
    private boolean isLiveVideoOn = false;

    public static ImageOperationFragment newInstance(Long rootOperationId) {
        ImageOperationFragment f = new ImageOperationFragment();

        // Supply  index input as an argument.
        Bundle args = new Bundle();
        args.putLong(KEY_ROOT_ID, rootOperationId);
        f.setArguments(args);
        return f;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ImageOperationViewModel.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.rootOperationId = bundle.getLong(KEY_ROOT_ID);
            bindDataToModel(rootOperationId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.photo_view, container, false);
        binding.setViewModel(getViewModel());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OperationFragmentListAdapter(getViewModel());
        binding.recyclerView.setAdapter(adapter);
        setRetainInstance(true);
        return binding.getRoot();
    }

    private void bindDataToModel(Long rootOperationId) {
        getViewModel().setUp(rootOperationId);
    }

    private ImageOperationViewModel getViewModel() {
        return (ImageOperationViewModel) viewModel;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showImage(EventSimpleDataMsg event) {
//        viewUtils.triggerViewVisiblity(binding.ivPhoto, EventBasicView.ViewState.VISIBLE);
        //todo
//        if (event.getData() instanceof Uri) {
//            GlideApp.with(this).load(event.getData()).fitCenter().into(binding.ivPhoto);
//        } else if (event.getData() instanceof Bitmap) {
//            binding.ivPhoto.setImageBitmap((Bitmap) event.getData());
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().post(new OnTransitionToOperationRootViewEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setLiveVideo(LiveVideoEvent event) {
        if (!isLiveVideoOn) {
            viewUtils.triggerViewVisiblity(binding.recyclerView, EventBasicView.ViewState.HIDEN);
            initVideo();
        } else {
            viewUtils.triggerViewVisiblity(binding.recyclerView, EventBasicView.ViewState.VISIBLE);
            disableLiveVideo();
        }
        isLiveVideoOn = !isLiveVideoOn;
    }

    private void disableLiveVideo() {
        binding.HelloOpenCvView.setVisibility(SurfaceView.GONE);
        binding.HelloOpenCvView.setCvCameraViewListener((CameraBridgeViewBase.CvCameraViewListener2) null);
        binding.HelloOpenCvView.disableView();
    }

    private void initVideo() {
        binding.HelloOpenCvView.setVisibility(SurfaceView.VISIBLE);
        binding.HelloOpenCvView.setCvCameraViewListener(this);
        binding.HelloOpenCvView.enableView();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setPhotoView(EventBasicViewMainPhoto event) {
        viewUtils.triggerViewVisiblity(binding.recyclerView, event.getStateToChange());
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public void onPause() {
        super.onPause();
        if (isLiveVideoOn) {
            disableLiveVideo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        EventBus.getDefault().post(new ToggleOperationToolbar());
        if (isLiveVideoOn) {
            initVideo();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VIDEO_ON, isLiveVideoOn);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            savedInstanceState.getBoolean(KEY_VIDEO_ON, Boolean.FALSE);
        }
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat mRgba = inputFrame.rgba();
        Mat mrgbaT = mRgba.t();
        Core.flip(mRgba.t(), mrgbaT, 1);
        Imgproc.resize(mrgbaT, mRgba, mRgba.size());
        return getViewModel().obtainImageOperations(mRgba);

//        ring.add(mRgba.clone());            // add one at the end
//        if ( ring.size() >= delay ) {       // pop one from the front
//            ring.get(0).release();
//            ring.remove(0);
//        }
//
//        Mat ret;
//        String txt;
//        if ( delayed && ring.size()>0 ) {   // depending on 'delayed' return either playback
//            ret = ring.get(0);              // return the 'oldest'
//            txt = "playback";
//        } else {
//            ret = mRgba;                    // or realtime frame
//            txt = "realtime";
//        }

//        return ret;
    }


}
