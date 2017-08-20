package pl.edu.agh.imageprocessing.features.detail.android;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.edu.agh.imageprocessing.app.constants.AppConstants;
import pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicView;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static pl.edu.agh.imageprocessing.features.detail.android.event.EventBasicView.ViewState.HIDEN;

/**
 * Created by bwolcerz on 20.08.2017.
 */
@Singleton
public class ViewUtils {

    @Inject
    public ViewUtils() {
    }

    public void triggerViewVisiblity(View view, EventBasicView.ViewState stateToChange) {
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
