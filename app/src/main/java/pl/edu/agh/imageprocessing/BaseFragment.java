package pl.edu.agh.imageprocessing;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import pl.edu.agh.imageprocessing.features.detail.viemodel.BaseViewModel;

/**
 * Created by bwolcerz on 20.08.2017.
 */

public class BaseFragment extends Fragment implements LifecycleRegistryOwner {
    protected LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    protected BaseViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true);
//        lifecycleRegistry.addObserver(new BaseFragment.BaseLifecycle(this, getActivity()));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        lifecycleRegistry.addObserver(new BaseFragment.BaseLifecycle(this, getActivity()));
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }

    class BaseLifecycle implements LifecycleObserver {


        private final BaseFragment fragment;
        private final FragmentActivity baseActivity;

        public BaseLifecycle(BaseFragment baseFragment, FragmentActivity baseActivity) {
            this.baseActivity = baseActivity;
            this.fragment = baseFragment;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        public void onCreate() {
            viewModel.setBinding(fragment);
            viewModel.setBinding((BaseActivity) baseActivity);
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        public void onResume() {
            viewModel.setBinding(fragment);
            viewModel.setBinding((BaseActivity) baseActivity);
            if(!EventBus.getDefault().isRegistered(fragment)) {
                EventBus.getDefault().register(fragment);
            }
            if(!EventBus.getDefault().isRegistered(viewModel)) {
                EventBus.getDefault().register(viewModel);
            }
            viewModel.setUp();//todo check if needed when onRestore called
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        public void onPause() {
//            EventBus.getDefault().unregister(this);
            EventBus.getDefault().unregister(fragment);
            EventBus.getDefault().unregister(viewModel);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null) {
            viewModel.restoreState(savedInstanceState.getBundle(BaseViewModel.STATE_KEY));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(BaseViewModel.STATE_KEY,viewModel.saveState());
    }

}
