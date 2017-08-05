package pl.edu.agh.imageprocessing.model;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.MainThread;
import android.util.Log;

import pl.edu.agh.imageprocessing.data.local.entity.Operation;

/**
 * Created by bwolcerz on 24.07.2017.
 */

public class OperationLiveData extends LiveData<Operation> {
    private final static String TAG=OperationLiveData.class.getSimpleName();
    public static  OperationLiveData sInstance;
    private final Context mContext;

    private OperationLiveData(Context context){
        Log.i(TAG,"OperationLiveData -> operation data");
        mContext=context;
        //TODO last operations

    }
    @MainThread
    public static OperationLiveData get(Context context) {
        if (sInstance == null) {
            sInstance = new OperationLiveData(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    protected void onActive() {
        Log.i(TAG,"onActive");
        super.onActive();
    }

    @Override
    protected void onInactive() {
        Log.i(TAG,"onInactive");
        super.onInactive();
    }
}
