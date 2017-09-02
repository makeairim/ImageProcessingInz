package pl.edu.agh.imageprocessing.features.detail.home;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import pl.edu.agh.imageprocessing.BaseFragment;
import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.databinding.ListOperationsViewBinding;
import pl.edu.agh.imageprocessing.features.detail.android.ViewUtils;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventSimpleDataMsg;
import pl.edu.agh.imageprocessing.features.detail.viemodel.ListOperationsViewModel;

/**
 * Created by bwolcerz on 20.08.2017.
 */

public class ListOperationsFragment extends BaseFragment {
    public ListOperationsViewBinding binding;

    @Inject
    ViewUtils viewUtils;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    public ListOperationFragmentListAdapter adapter;

    public static ListOperationsFragment newInstance() {
        ListOperationsFragment f = new ListOperationsFragment();
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        binding= DataBindingUtil.inflate(inflater, R.layout.list_operations_view,container,false);
        binding.setViewModel(getViewModel());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new ListOperationFragmentListAdapter(getViewModel(),getViewModel(),getViewModel());
        binding.recyclerView.setAdapter(adapter);
        return binding.getRoot();
    }

    private ListOperationsViewModel getViewModel() {
        return (ListOperationsViewModel) viewModel;
    }
    @Subscribe
    public void mock(EventSimpleDataMsg eventSimpleDataMsg){

    }



}
