package pl.edu.agh.imageprocessing.app;

import android.support.annotation.NonNull;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.scheduling.FrameworkJobSchedulerService;

/**
 * Created by bwolcerz on 16.08.2017.
 */

public class MyJobService extends FrameworkJobSchedulerService {
    @NonNull
    @Override
    protected JobManager getJobManager() {
        return ImageProcessingApplication.getInstance().getJobManager();
    }
}
