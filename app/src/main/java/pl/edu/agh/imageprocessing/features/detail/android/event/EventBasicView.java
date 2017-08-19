package pl.edu.agh.imageprocessing.features.detail.android.event;

/**
 * Created by bwolcerz on 18.08.2017.
 */

public class EventBasicView {
    ViewState stateToChange;

    public EventBasicView(ViewState stateToChange) {
        this.stateToChange = stateToChange;
    }



    public ViewState getStateToChange() {
        return stateToChange;
    }

    public enum ViewState{
        VISIBLE,HIDEN
    }
}
