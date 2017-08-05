package pl.edu.agh.imageprocessing.features.detail.android;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.concurrent.Callable;

/**
 * Created by bwolcerz on 05.08.2017.
 */

public class RecyclerViewListenerOutsideClick implements RecyclerView.OnItemTouchListener {
    private final Callable action;
    private final static String TAG = RecyclerViewListenerOutsideClick.class.getSimpleName();

    public RecyclerViewListenerOutsideClick(Callable action) {
        this.action = action;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        if (motionEvent.getAction() != MotionEvent.ACTION_UP) {
            return false;
        }
        View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if (child != null) {
            // tapped on child
            return false;
        } else {
            Log.i(TAG, "onInterceptTouchEvent:");
            try {
                action.call();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onInterceptTouchEvent: ", e);
            }
            return true;
        }
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
