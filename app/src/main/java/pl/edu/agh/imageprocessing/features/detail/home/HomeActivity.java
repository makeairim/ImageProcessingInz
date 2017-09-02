package pl.edu.agh.imageprocessing.features.detail.home;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import pl.edu.agh.imageprocessing.BaseActivity;
import pl.edu.agh.imageprocessing.BaseFragment;
import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.app.ImageProcessingApplication;
import pl.edu.agh.imageprocessing.databinding.ActivityHomeBinding;
import pl.edu.agh.imageprocessing.features.detail.android.ViewUtils;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicView;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewConfirmActionVisiblity;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewListOperationsVisiblity;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewMainPhoto;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowMainViewVisibilityEventBasicView;
import pl.edu.agh.imageprocessing.features.detail.viemodel.HomeViewModel;

import static pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicView.ViewState.HIDEN;

public class HomeActivity extends BaseActivity implements HasSupportFragmentInjector {

    public static final String RETAINED_FRAGMENT_TAG = "RETAINED_FRAGMENT_TAG";
    private final String TAG = HomeActivity.class.getSimpleName();
    public static final String KEY_HOME_ACTIVITY_ID = "key__home_activity_id";

    @Inject
    ViewUtils viewUtils;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    public ActivityHomeBinding binding;

    private HomeViewModel getViewModel() {
        return (HomeViewModel) viewModel;
    }

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    public BaseFragment getActiveFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return null;
        }
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        return (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

//        viewModel.setBinding(this);//todo lifecycle event
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        binding.setViewModel(getViewModel());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ButterKnife.bind(this);//todo po co to ?
        //when get operation list so on click method in viewmodel
        //
        binding.recyclerView.setOnNoChildClickListener(getViewModel().onOutsideListClick);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(new OperationHomeListAdapter(getViewModel()));
        // find the retained fragment on activity restarts
        // create the fragment and data the first time
//        if (mRetainedFragment == null) {
//            // add the fragment
//            mRetainedFragment = new RetainedFragment();
//            fm.beginTransaction().add(mRetainedFragment, TAG_RETAINED_FRAGMENT).commit();
//            // load data from a data source or perform any calculation
//            mRetainedFragment.setData(loadMyData());
//        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export:
                getViewModel().provideOperationTypes();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.imageButton)
    public void setPhotoListener() {
        getViewModel().photoPicker();
    }

    @OnClick(R.id.grid)
    public void showOperationChains() {
        getViewModel().showOperationRoots();
    }

    @Subscribe
    public void onEvent(Object msg) {
        Log.d("EVENT", "EVENT");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void triggerConfirmAction(EventBasicViewConfirmActionVisiblity eventBasicViewConfirmActionVisiblity) {
        viewUtils.triggerViewVisiblity(binding.parentConfirm, eventBasicViewConfirmActionVisiblity.getStateToChange());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setOperationList(EventBasicViewListOperationsVisiblity event) {
        viewUtils.triggerViewVisiblity(binding.parentRecyclerView, event.getStateToChange());
        if (event.getStateToChange() == EventBasicView.ViewState.VISIBLE) {
            EventBus.getDefault().post(new EventBasicViewMainPhoto(EventBasicView.ViewState.HIDEN));
        } else {
            EventBus.getDefault().post(new EventBasicViewMainPhoto(EventBasicView.ViewState.VISIBLE));
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void triggerMainView(ShowMainViewVisibilityEventBasicView event) {
        EventBus.getDefault().post(new EventBasicViewMainPhoto(event.getStateToChange()));
        if (event.getStateToChange() == EventBasicView.ViewState.VISIBLE)
            viewUtils.triggerViewVisiblity(binding.parentRecyclerView, HIDEN);
        else
            viewUtils.triggerViewVisiblity(binding.parentRecyclerView, EventBasicView.ViewState.VISIBLE);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }
}
