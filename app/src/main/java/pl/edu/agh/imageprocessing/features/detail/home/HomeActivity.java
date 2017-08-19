package pl.edu.agh.imageprocessing.features.detail.home;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import pl.edu.agh.imageprocessing.BaseActivity;
import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.app.constants.AppConstants;
import pl.edu.agh.imageprocessing.dagger.GlideApp;
import pl.edu.agh.imageprocessing.databinding.ActivityHomeBinding;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewConfirmActionVisiblity;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewHideBottomActionParameters;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowMainViewVisibilityEventBasicView;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventSimpleDataMsg;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicView;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewListOperationsVisiblity;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewMainPhoto;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewSeekBarVisibility;
import pl.edu.agh.imageprocessing.features.detail.viemodel.HomeViewModel;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicView.ViewState.HIDEN;

public class HomeActivity extends BaseActivity {

    private final String TAG = HomeActivity.class.getSimpleName();
    public static final String KEY_HOME_ACTIVITY_ID = "key__home_activity_id";

    public ActivityHomeBinding binding;

    private HomeViewModel getViewModel() {
        return (HomeViewModel) viewModel;
    }

    @Inject
    ViewModelProvider.Factory viewModelFactory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

//        viewModel.setBinding(this);//todo lifecycle event
        binding= DataBindingUtil.setContentView(this,R.layout.activity_home);
        binding.setViewModel(getViewModel());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ButterKnife.bind(this);
        //when get operation list so on click method in viewmodel
        //
        binding.recyclerView.setOnNoChildClickListener(getViewModel().onOutsideListClick);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(new OperationHomeListAdapter(getViewModel()));

    }

    @Override
    protected void onResume() {
        super.onResume();
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

    @Subscribe
    public void onEvent(Object msg) {
        Log.d("EVENT", "EVENT");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void triggerConfirmAction(EventBasicViewConfirmActionVisiblity eventBasicViewConfirmActionVisiblity) {
        triggerViewVisiblity(binding.clearOper, eventBasicViewConfirmActionVisiblity.getStateToChange());
        triggerViewVisiblity(binding.doOper, eventBasicViewConfirmActionVisiblity.getStateToChange());
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void triggerHideActionParameters(EventBasicViewHideBottomActionParameters event) {
        triggerViewVisiblity(binding.seekbar,event.getStateToChange());
        triggerViewVisiblity(binding.textViewSeekbarprogress,event.getStateToChange());
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showImage(EventSimpleDataMsg event) {
        triggerViewVisiblity(binding.ivPhoto, EventBasicView.ViewState.VISIBLE);
        if (event.getData() instanceof Uri) {
            GlideApp.with(this).load(event.getData()).fitCenter().into(binding.ivPhoto);
        } else if (event.getData() instanceof Bitmap) {
            binding.ivPhoto.setImageBitmap((Bitmap) event.getData());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setProgressBarText(EventSimpleDataMsg event) {
        if (event.getData() instanceof CharSequence) {
            triggerViewVisiblity(binding.textViewSeekbarprogress, EventBasicView.ViewState.VISIBLE);
            binding.textViewSeekbarprogress.setText((CharSequence) event.getData());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setVisiblitySeekBar(EventBasicViewSeekBarVisibility event) {
        triggerViewVisiblity(binding.seekbar, event.getStateToChange());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setOperationList(EventBasicViewListOperationsVisiblity event) {
        triggerViewVisiblity(binding.parentRecyclerView, event.getStateToChange());
        if(event.getStateToChange() == EventBasicView.ViewState.VISIBLE) {
            triggerViewVisiblity(binding.ivPhoto, HIDEN);
        }else{
            triggerViewVisiblity(binding.ivPhoto, EventBasicView.ViewState.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setOperationList(EventBasicViewMainPhoto event) {
        triggerViewVisiblity(binding.ivPhoto, event.getStateToChange());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void triggerMainView(ShowMainViewVisibilityEventBasicView event) {
        triggerViewVisiblity(binding.ivPhoto, event.getStateToChange());
        if(event.getStateToChange() == EventBasicView.ViewState.VISIBLE)
        triggerViewVisiblity(binding.parentRecyclerView, HIDEN);
        else
            triggerViewVisiblity(binding.parentRecyclerView, EventBasicView.ViewState.VISIBLE);
    }

    private void triggerViewVisiblity(View view, EventBasicView.ViewState stateToChange) {
        if (view instanceof ImageView &&  ! (view instanceof ImageButton)) {
            switch (((ImageView) view).getImageAlpha()) {
                case AppConstants.IMAGE_VIEW_FULL_OPAQUE:
                    if (stateToChange == EventBasicView.ViewState.HIDEN) {
                        ((ImageView) view).setImageAlpha(AppConstants.IMAGE_VIEW_PARTIAL_TRANSPARENT);
                    }
                    return;
                case AppConstants.IMAGE_VIEW_PARTIAL_TRANSPARENT:
                    if (stateToChange == EventBasicView.ViewState.VISIBLE) {
                        ((ImageView) view).setImageAlpha(AppConstants.IMAGE_VIEW_FULL_OPAQUE);
                    }
                    return;
            }
        }
        switch (view.getVisibility()) {
            case GONE:
            case INVISIBLE:
                if (stateToChange == EventBasicView.ViewState.VISIBLE) {
                    view.setVisibility(View.VISIBLE);
                }

                break;
            case VISIBLE:
                if (stateToChange == HIDEN) {
                    view.setVisibility(GONE);
                }
                break;
        }
    }

}
