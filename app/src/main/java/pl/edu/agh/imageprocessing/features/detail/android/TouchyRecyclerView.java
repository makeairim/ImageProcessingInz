package pl.edu.agh.imageprocessing.features.detail.android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.concurrent.Callable;

/**
 * Created by bwolcerz on 05.08.2017.
 */

public class TouchyRecyclerView extends RecyclerView
{
    // Depending on how you're creating this View,
    // you might need to specify additional constructors.
    public TouchyRecyclerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    private Callable listenerOnOutside;

    public void setOnNoChildClickListener(Callable listener)
    {
        this.listenerOnOutside=listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        // The findChildViewUnder() method returns null if the touch event
        // occurs outside of a child View.
        // Change the MotionEvent action as needed. Here we use ACTION_DOWN
        // as a simple, naive indication of a click.
        if (event.getAction() == MotionEvent.ACTION_DOWN
                && findChildViewUnder(event.getX(), event.getY()) == null)
        {
            if (listenerOnOutside != null)
            {
                try {
                    listenerOnOutside.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
