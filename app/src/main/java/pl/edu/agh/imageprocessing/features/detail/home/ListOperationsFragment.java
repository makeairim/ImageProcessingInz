package pl.edu.agh.imageprocessing.features.detail.home;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import pl.edu.agh.imageprocessing.BaseFragment;
import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.dagger.GlideApp;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.data.local.entity.Resource;
import pl.edu.agh.imageprocessing.databinding.ListOperationsViewBinding;
import pl.edu.agh.imageprocessing.databinding.PhotoViewBinding;
import pl.edu.agh.imageprocessing.features.detail.android.ViewUtils;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicView;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewHideBottomActionParameters;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewMainPhoto;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewSeekBarVisibility;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventSimpleDataMsg;
import pl.edu.agh.imageprocessing.features.detail.viemodel.ImageOperationViewModel;
import pl.edu.agh.imageprocessing.features.detail.viemodel.ListOperationsViewModel;

/**
 * Created by bwolcerz on 20.08.2017.
 */

public class ListOperationsFragment extends BaseFragment {
    public ListOperationsViewBinding binding;

    public static final String KEY_DATA="KEY_DATA";
    @Inject
    ViewUtils viewUtils;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private List<Operation> operationsRoots;

    public static ListOperationsFragment newInstance(pl.edu.agh.imageprocessing.data.remote.Resource<List<Operation>> data) {
        ListOperationsFragment f = new ListOperationsFragment();

        // Supply  index input as an argument.
        Bundle args = new Bundle();
        args.putParcelableArray(KEY_DATA,data.getData().toArray(new Operation[0]));
        args.putString("LAMBADA","TEST");
        f.setArguments(args);
        return f;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ListOperationsViewModel.class);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        if( bundle!=null){
            this.operationsRoots=bundle.getParcelableArrayList(KEY_DATA);
            bindDataToModel(operationsRoots);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        binding= DataBindingUtil.inflate(inflater, R.layout.list_operations_view,container,false);
        binding.setViewModel(getViewModel());
        return binding.getRoot();
    }

    private void bindDataToModel(List<Operation> data){
        getViewModel().setData(data);
        EventBus.getDefault().register(viewModel);
    }
    private ListOperationsViewModel getViewModel() {
        return (ListOperationsViewModel) viewModel;
    }



}
