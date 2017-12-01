package pl.edu.agh.imageprocessing.features.detail.home;

import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import pl.edu.agh.imageprocessing.app.constants.AppConstants;
import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.databinding.ActivityHomeBinding;
import pl.edu.agh.imageprocessing.features.detail.android.ViewUtils;
import pl.edu.agh.imageprocessing.features.detail.android.dialog.InformationCustomDialog;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicView;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewConfirmActionVisiblity;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewListOperationsVisiblity;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicViewMainPhoto;
import pl.edu.agh.imageprocessing.features.detail.android.event.LiveVideoEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.PhotoEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.SelectedPhotoEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.ShowMainViewVisibilityEventBasicView;
import pl.edu.agh.imageprocessing.features.detail.android.operationtypeslist.GroupOperationModel;
import pl.edu.agh.imageprocessing.features.detail.android.operationtypeslist.ItemHeader;
import pl.edu.agh.imageprocessing.features.detail.android.operationtypeslist.ItemHeaderViewBinder;
import pl.edu.agh.imageprocessing.features.detail.android.operationtypeslist.ItemTypeViewBinder;
import pl.edu.agh.imageprocessing.features.detail.viemodel.HomeViewModel;
import tellh.com.stickyheaderview_rv.adapter.DataBean;
import tellh.com.stickyheaderview_rv.adapter.StickyHeaderViewAdapter;

import static pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicView.ViewState.HIDEN;

public class HomeActivity extends BaseActivity implements HasSupportFragmentInjector, OperationInfoCallback {

    public static final String RETAINED_FRAGMENT_TAG = "RETAINED_FRAGMENT_TAG";
    private static final String DIALOG_INFO_TAG = "DIALOG_INFO_KEY";
    private final String TAG = HomeActivity.class.getSimpleName();
    public static final String KEY_HOME_ACTIVITY_ID = "key__home_activity_id";

    @Inject
    ViewUtils viewUtils;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    public ActivityHomeBinding binding;
    private RxPermissions rxPermissions;

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        binding.setViewModel(getViewModel());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ButterKnife.bind(this);

//        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        binding.recyclerView.setAdapter(new OperationHomeListAdapter(getViewModel()));

//        binding.recyclerView.addItemDecoration(adapter.getItemDecorationManager());
//        binding.recyclerView.setLayoutManager(llm);
//        binding.recyclerView.setAdapter(adapter);
//        binding.recyclerView.setOnNoChildClickListener(getViewModel().onOutsideListClick);
//       Map<String, List<GroupOperationModel>> types = getViewModel().provideOperationTypes();
//        adapter.addMorphology(types.get(AppConstants.MOPHOLOGY_HEADER));
//        adapter.addFilter(types.get(AppConstants.FILTER_HEADER));
//        adapter.addOther(types.get(AppConstants.OTHER_HEADER));

//        adapter.setExpandableMode(RecyclerAdapter.EXPANDABLE_MODE_MULTIPLE);
//        adapter.setGroupExpandableMode(RecyclerAdapter.EXPANDABLE_MODE_MULTIPLE);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Map<String, List<GroupOperationModel>> types = getViewModel().provideOperationTypes();
        List<DataBean> operHeaders = new LinkedList<>();
        operHeaders.add(new ItemHeader(AppConstants.MOPHOLOGY_HEADER));
        operHeaders.addAll(types.get(AppConstants.MOPHOLOGY_HEADER));
        operHeaders.add(new ItemHeader(AppConstants.FILTER_HEADER));
        operHeaders.addAll(types.get(AppConstants.FILTER_HEADER));
        operHeaders.add(new ItemHeader(AppConstants.OTHER_HEADER));
        operHeaders.addAll(types.get(AppConstants.OTHER_HEADER));
        operHeaders.add(new ItemHeader(AppConstants.IMAGE_FEATURE));
        operHeaders.addAll(types.get(AppConstants.IMAGE_FEATURE));
        operHeaders.add(new ItemHeader(AppConstants.ARITHMETIC_OPERATIONS_HEADER));
        operHeaders.addAll(types.get(AppConstants.ARITHMETIC_OPERATIONS_HEADER));
        StickyHeaderViewAdapter adapter = new StickyHeaderViewAdapter(operHeaders)
                .RegisterItemType(new ItemTypeViewBinder(getViewModel(), this))
                .RegisterItemType(new ItemHeaderViewBinder());
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setOnNoChildClickListener(getViewModel().onOutsideListClick);
        rxPermissions = new RxPermissions(this);
        rxPermissions
                .requestEach(Manifest.permission_group.STORAGE,
                        Manifest.permission.CAMERA)
                .subscribe(permission -> { // will emit 2 Permission objects
                    if (permission.granted) {
                        // `permission.name` is granted !
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // Denied permission without ask never again
                        finish();
                    } else {
                        finish();
                        // Denied permission with ask never again
                        // Need to go to the settings
                    }
                });
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
//            case R.id.export:
//                getViewModel().provideOperationTypes();
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.videoButton)
    public void setVideoListener() {
        EventBus.getDefault().post(new LiveVideoEvent());
    }

    @OnClick(R.id.imageButton)
    public void setPhotoListener() {
        getViewModel().photoPicker();
    }

    @OnClick(R.id.operationButton)
    public void setOperationPossibleListListener() {
        getViewModel().provideOperationTypes();
    }

    @OnClick(R.id.grid)
    public void showOperationChains() {
        getViewModel().showImageGallery();
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

    @Override
    public void operationInfoClicked(ImageOperationType type) {
        InformationCustomDialog dialog = InformationCustomDialog.newInstance(type.getTitle());
        dialog.show(getSupportFragmentManager(), DIALOG_INFO_TAG);
//        Intent intent = InformationalActivity.newInstane(this, type);
//        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GalleryActivity.REQUEST_CODE:
                if (resultCode != RESULT_OK) {
                    return;
                }
                String uriString = data.getStringExtra(GalleryActivity.PHOTO_URI_KEY);
                PhotoEvent photoEvent = PhotoEvent.valueOf(data.getStringExtra(GalleryActivity.PHOTO_EVENT_TYPE));
                if (uriString != null && photoEvent != null) {
                    EventBus.getDefault().post(new SelectedPhotoEvent(Uri.parse(uriString), photoEvent));
                } else {
                    Log.e(TAG, "onActivityResult: result from gallery empty");
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}