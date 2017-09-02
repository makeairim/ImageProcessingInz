package pl.edu.agh.imageprocessing.features.detail.viemodel;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pl.edu.agh.imageprocessing.data.local.ResourceType;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.local.dao.ResourceDao;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.data.local.entity.Resource;
import pl.edu.agh.imageprocessing.features.detail.android.event.DataChangedEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.OperationsViewEvent;
import pl.edu.agh.imageprocessing.features.detail.home.HomeActivity;
import pl.edu.agh.imageprocessing.features.detail.home.ListOperationFragmentListCallback;
import pl.edu.agh.imageprocessing.features.detail.home.ListOperationsFragment;
import pl.edu.agh.imageprocessing.features.detail.home.ObtainImageFileForOperationCallback;
import pl.edu.agh.imageprocessing.features.detail.home.RegisterDisposableCallback;

/**
 * Created by bwolcerz on 20.08.2017.
 */

public class ListOperationsViewModel extends BaseViewModel implements ListOperationFragmentListCallback,RegisterDisposableCallback,ObtainImageFileForOperationCallback{
    public static final String TAG = ListOperationsViewModel.class.getSimpleName();
    @Inject
    OperationDao operationDao;
    @Inject
    ResourceDao resourceDao;

    @Inject
    Context context;

    ListOperationsViewModelState state = new ListOperationsViewModelState();

    @Inject
    public ListOperationsViewModel() {
    }



    @Override
    protected ListOperationsFragment provideFragment() {
        return (ListOperationsFragment) super.provideFragment();
    }

    @Override
    protected HomeActivity provideActivity() {
        return (HomeActivity) super.provideActivity();
    }

    @Override
    public void setUp() {
        getRoots().observeOn(Schedulers.newThread()).subscribe(o -> {
           state.setOperationRoots(o);
           EventBus.getDefault().post(new DataChangedEvent());
        });
    }

    @Override
    public Bundle saveState() {
        Bundle bundle=new Bundle();
     //   bundle.putParcelable(this.STATE_KEY,state);
        return bundle;
    }

    @Override
    public void restoreState(Bundle bundle) {
//        if( bundle!=null){
//            state=bundle.getParcelable(this.STATE_KEY);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void notifyDataChanged(DataChangedEvent event){
        provideFragment().adapter.setData(state.getOperationRoots());
        provideFragment().adapter.notifyDataSetChanged();
    }
    private Observable<List<Operation>> getRoots(){
        return Observable.create( (ObservableOnSubscribe<List<Operation>>)e ->{ e.onNext(operationDao.chainRoots()); e.onComplete();}).subscribeOn(Schedulers.newThread());
    }
    @Override
    public void onImageOperationClicked(Operation operation, View sharedView) {
        EventBus.getDefault().post(new OperationsViewEvent(operation.getId()));
    }

    @Override
    public void registerDisposableCallback(Disposable disposable) {

    }

    @Override
    public Observable<Resource> obtainOperationResourceImageFile(long operationId) {
        return resourceDao.getByOperationAndType(operationId, ResourceType.IMAGE_FILE.name()).toObservable();
    }


static public class ListOperationsViewModelState implements Parcelable{
        private List<Operation> operationRoots;

        public void setOperationRoots(List<Operation> operationRoots) {
            this.operationRoots = operationRoots;
        }

        public List<Operation> getOperationRoots() {
            return operationRoots;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(this.operationRoots);
        }

        public ListOperationsViewModelState() {
        }

        protected ListOperationsViewModelState(Parcel in) {
            this.operationRoots = in.createTypedArrayList(Operation.CREATOR);
        }

        public static final Creator<ListOperationsViewModelState> CREATOR = new Creator<ListOperationsViewModelState>() {
            @Override
            public ListOperationsViewModelState createFromParcel(Parcel source) {
                return new ListOperationsViewModelState(source);
            }

            @Override
            public ListOperationsViewModelState[] newArray(int size) {
                return new ListOperationsViewModelState[size];
            }
        };
    }
}
