package pl.edu.agh.imageprocessing.features.detail.home;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import pl.edu.agh.imageprocessing.BaseFragment;
import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.dagger.GlideApp;
import pl.edu.agh.imageprocessing.data.local.dao.OperationWithChainAndResource;
import pl.edu.agh.imageprocessing.data.local.entity.Resource;
import pl.edu.agh.imageprocessing.databinding.PhotoViewBinding;
import pl.edu.agh.imageprocessing.features.detail.android.ViewUtils;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicView;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewConfirmActionVisiblity;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewHideBottomActionParameters;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewMainPhoto;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewSeekBarVisibility;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventSimpleDataMsg;
import pl.edu.agh.imageprocessing.features.detail.viemodel.ImageOperationViewModel;

/**
 * Created by bwolcerz on 20.08.2017.
 */

public class ImageOperationFragment  extends BaseFragment {
    public PhotoViewBinding binding;
    public static final String KEY_RESOURCE="KEY_RESOURCE";
    @Inject
    ViewUtils viewUtils;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private OperationWithChainAndResource resource;

    public static ImageOperationFragment newInstance(OperationWithChainAndResource resource) {
        ImageOperationFragment f = new ImageOperationFragment();

        // Supply  index input as an argument.
        Bundle args = new Bundle();
        args.putParcelable(KEY_RESOURCE,resource);
        args.putString("LAMBADA","TEST");
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
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        if( bundle!=null){
            this.resource=bundle.getParcelable(KEY_RESOURCE);
            bindDataToModel(resource);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        binding= DataBindingUtil.inflate(inflater, R.layout.photo_view,container,false);
        binding.setViewModel(getViewModel());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(new OperationFragmentListAdapter(getViewModel()));
        return binding.getRoot();
    }

    private void bindDataToModel(OperationWithChainAndResource data){
        getViewModel().setData(data);
        EventBus.getDefault().register(viewModel);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setProgressBarText(EventSimpleDataMsg event) {
        if ( !(event.getData() instanceof Uri) && !(event.getData() instanceof Bitmap)) {
            viewUtils.triggerViewVisiblity(binding.textViewSeekbarprogress, EventBasicView.ViewState.VISIBLE);
            binding.textViewSeekbarprogress.setText((CharSequence) event.getData());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setVisiblityBinarizationParams(EventBasicViewSeekBarVisibility event) {
        viewUtils.triggerViewVisiblity(binding.parentSeekbar, event.getStateToChange());
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void triggerHideBinarizationParams(EventBasicViewHideBottomActionParameters event) {
        viewUtils.triggerViewVisiblity(binding.parentSeekbar,event.getStateToChange());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setPhotoView(EventBasicViewMainPhoto event) {
        viewUtils.triggerViewVisiblity(binding.recyclerView, event.getStateToChange());
    }
}
