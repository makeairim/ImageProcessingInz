package pl.edu.agh.imageprocessing.features.detail.home;

import android.app.usage.UsageEvents;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import javax.inject.Inject;


import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fotoapparat.Fotoapparat;
import pl.edu.agh.imageprocessing.BaseActivity;
import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.databinding.ActivityHomeBinding;
import pl.edu.agh.imageprocessing.features.detail.android.RecyclerViewListenerOutsideClick;
import pl.edu.agh.imageprocessing.features.detail.viemodel.HomeViewModel;

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
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
        viewModel.setBinding(this);//todo lifecycle event
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
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
        switch(item.getItemId()){
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

}
