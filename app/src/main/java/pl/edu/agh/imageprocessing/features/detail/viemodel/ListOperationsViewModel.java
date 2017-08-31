package pl.edu.agh.imageprocessing.features.detail.viemodel;

import android.content.Context;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.features.detail.android.event.DataChangedEvent;
import pl.edu.agh.imageprocessing.features.detail.android.event.OperationsViewEvent;
import pl.edu.agh.imageprocessing.features.detail.home.HomeActivity;
import pl.edu.agh.imageprocessing.features.detail.home.ListOperationFragmentListCallback;
import pl.edu.agh.imageprocessing.features.detail.home.ListOperationsFragment;

/**
 * Created by bwolcerz on 20.08.2017.
 */

public class ListOperationsViewModel extends BaseViewModel implements ListOperationFragmentListCallback{
    public static final String TAG = ListOperationsViewModel.class.getSimpleName();
    @Inject
    OperationDao operationDao;
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



    public class ListOperationsViewModelState {
        private List<Operation> operationRoots;

        public void setOperationRoots(List<Operation> operationRoots) {
            this.operationRoots = operationRoots;
        }

        public List<Operation> getOperationRoots() {
            return operationRoots;
        }
    }
}
