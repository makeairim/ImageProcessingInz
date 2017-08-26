package pl.edu.agh.imageprocessing.features.detail.viemodel;

import android.arch.lifecycle.ViewModel;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import pl.edu.agh.imageprocessing.BaseActivity;

/**
 * Created by bwolcerz on 01.08.2017.
 */

public abstract class BaseViewModel extends ViewModel {
    private BaseActivity activity;
    private Fragment fragment;
    public void setBinding(Fragment fragment) {
        this.fragment=fragment;
    }
    public void setBinding(BaseActivity activity) {
        this.activity=activity;
    }
//    abstract protected void setUpViewElements();
    protected Fragment provideFragment(){
        if(fragment==null){
            throw new AssertionError("Binding unset. Tried to get null fragment");
        }
        return fragment;
    }
    protected BaseActivity provideActivity(){
        if(activity==null){
            throw new AssertionError("Binding unset. Tried to get null activity");
        }
        return activity;
    }

    public abstract void setUp();
}
