package pl.edu.agh.imageprocessing;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import dagger.android.AndroidInjection;
import pl.edu.agh.imageprocessing.app.ImageProcessingApplication;
import pl.edu.agh.imageprocessing.features.detail.android.event.TriggerServiceWorkEvent;
import pl.edu.agh.imageprocessing.features.detail.viemodel.BaseViewModel;

/**
 * Created by bwolcerz on 01.08.2017.
 */

public class BaseActivity extends AppCompatActivity implements LifecycleRegistryOwner {
    protected LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    protected BaseViewModel viewModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleRegistry.addObserver(new BaseLifecycle(this));
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }

    class BaseLifecycle implements LifecycleObserver {

        private final BaseActivity activity;

        public BaseLifecycle(BaseActivity baseActivity) {
            this.activity = baseActivity;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        public void onCreate() {
            viewModel.setBinding(activity);
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        public void onResume() {
            viewModel.setBinding(activity);
            EventBus.getDefault().register(activity);
            EventBus.getDefault().register(viewModel);
            viewModel.setUp();
            EventBus.getDefault().post(new TriggerServiceWorkEvent());

        }
        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        public void onPause(){
            EventBus.getDefault().unregister(activity);
            EventBus.getDefault().unregister(viewModel);
        }
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        public void onStart(){
            ((ImageProcessingApplication) getApplication()).bindService();
        }
        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        public void onStop(){
            ((ImageProcessingApplication) getApplication()).unbindService();
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewModel.restoreState(savedInstanceState.getBundle(BaseViewModel.STATE_KEY));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(BaseViewModel.STATE_KEY, viewModel.saveState() );
    }
}
