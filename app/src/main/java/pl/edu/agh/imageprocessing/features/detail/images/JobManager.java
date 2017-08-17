//package pl.edu.agh.imageprocessing.features.detail.images;
//
//import android.util.Log;
//
//import com.github.dmstocking.optional.java.util.Optional;
//
//import java.util.concurrent.Callable;
//
//import pl.edu.agh.imageprocessing.features.detail.images.operation.BasicOperation;
//
///**
// * Created by bwolcerz on 16.08.2017.
// */
//
//public class JobManager extends Sync<Callable<BasicOperation>, Optional<BasicOperation>> {
//    public static final String TAG = JobManager.class.getSimpleName();
//
//    @Override
//    protected Optional doInBackground(Callable<BasicOperation>[] callables) {
//        if (callables.length > 1 || callables.length == 0)
//            throw new AssertionError("Callables jobs number different than 1");
//        try {
//            return Optional.ofNullable(callables[0].call());
//        } catch (Exception e) {
//            Log.i(TAG, "doInBackground: " + e);
//        }
//        return Optional.empty();
//    }
//}
