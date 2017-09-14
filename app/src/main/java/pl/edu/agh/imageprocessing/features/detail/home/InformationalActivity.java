package pl.edu.agh.imageprocessing.features.detail.home;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.view.Window;

import butterknife.ButterKnife;
import pl.edu.agh.imageprocessing.BaseActivity;
import pl.edu.agh.imageprocessing.R;
import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.databinding.ActivityInformationalBinding;

/**
 * Created by bwolcerz on 15.09.2017.
 */

public class InformationalActivity extends BaseActivity {
    private final String TAG =InformationalActivity.class.getSimpleName();
    private static String TYPE_KEY="OPERATION_TYPE_KEY";
    private ActivityInformationalBinding binding;


    public static Intent newInstane(Context context, ImageOperationType type) {
        Intent intent=new Intent(context,InformationalActivity.class);
        intent.putExtra(TYPE_KEY,type);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_informational);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ButterKnife.bind(this);
    }

}
