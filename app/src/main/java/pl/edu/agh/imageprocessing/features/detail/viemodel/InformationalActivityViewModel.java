package pl.edu.agh.imageprocessing.features.detail.viemodel;

import android.os.Bundle;

import org.greenrobot.eventbus.Subscribe;

import pl.edu.agh.imageprocessing.features.detail.home.HomeActivity;
import pl.edu.agh.imageprocessing.features.detail.home.InformationalActivity;

/**
 * Created by bwolcerz on 20.09.2017.
 */

public class InformationalActivityViewModel  extends BaseViewModel{
    @Override
    protected InformationalActivity provideActivity() {
        return (InformationalActivity) super.provideActivity();
    }
    @Override
    public void setUp() {

    }

    @Override
    public Bundle saveState() {
        return null;
    }

    @Override
    public void restoreState(Bundle bundle) {

    }
    @Subscribe
    public void onEvent(Object msg){

    }
}
