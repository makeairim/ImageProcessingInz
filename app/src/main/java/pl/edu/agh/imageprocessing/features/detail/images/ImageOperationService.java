package pl.edu.agh.imageprocessing.features.detail.images;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import io.reactivex.Maybe;
import pl.edu.agh.imageprocessing.app.ImageProcessingApplication;
import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.OperationStatus;
import pl.edu.agh.imageprocessing.data.local.ResourceType;
import pl.edu.agh.imageprocessing.data.local.converter.UriDeserializer;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.local.dao.OperationWithChainAndResourceDao;
import pl.edu.agh.imageprocessing.data.local.dao.ResourceDao;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.data.local.entity.Resource;
import pl.edu.agh.imageprocessing.data.remote.OperationResourceAPIRepository;
import pl.edu.agh.imageprocessing.features.detail.android.event.RefreshDataEvent;

/**
 * Created by bwolcerz on 28.08.2017.
 */

public class ImageOperationService extends Service {
    public static final String TAG=ImageOperationService.class.getSimpleName();
    public static final int MSG_CHECK_NEW_OPERATION = 1;
    private Looper mServiceLooper;
    private Messenger mMessenger;
    private ServiceHandler mServiceHandler;
    private FileTools fileTools;
    private OperationDao operationDao;
    private OperationResourceAPIRepository apiRepository;
    private OperationWithChainAndResourceDao operationWithChainResourceDao;
    private ResourceDao resourceDao;
    private ImageOperationResolver resolver;

    //    @Inject
//    OperationDao operationDao;
    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            boolean hasSomeWork=true;
            Operation oldestOperation = operationDao.getOldestUnresolved();
            if( oldestOperation==null) {
                hasSomeWork = false;
            }

            while(hasSomeWork){
                try {
                    Operation previousOperation = operationDao.getOperationByNextOperationId(oldestOperation.getId());
                    if( previousOperation == null ){
                        Log.e(TAG, "handleMessage: cannot find previous operation for operationId= " + oldestOperation.getId());
                    }
                    Maybe<Resource> previousOperationResource = resourceDao.getByOperationAndType(previousOperation.getId(), ResourceType.IMAGE_FILE.name());
                    if( previousOperationResource.blockingGet() == null ){
                        Log.e(TAG, "handleMessage: cannot find previous operation resource for operationId= " + previousOperation.getId());
                    }
                    ImageOperationResolverParameters params = new GsonBuilder().create().fromJson(oldestOperation.getObject(), ImageOperationResolverParameters.class);
                    oldestOperation.setStatus(OperationStatus.IN_PROGRESS);
                    operationDao.update(oldestOperation);
                    resolver.processResult(resolver
                            .resolveOperation(ImageOperationType.valueOf(oldestOperation.getOperationType())
                                    ,params,
                                    Uri.parse(previousOperationResource.blockingGet().getContent())
                                    ,oldestOperation.getId()).execute());
                    oldestOperation.setStatus(OperationStatus.FINISHED);
                } catch (IOException e) {
                    oldestOperation.setStatus(OperationStatus.CANCELLED);
                    Log.e(TAG, "handleMessage: ",e);
                }
                operationDao.update(oldestOperation);
                EventBus.getDefault().post(new RefreshDataEvent());
                //todo notiy about change
                oldestOperation = operationDao.getOldestUnresolved();
                if( oldestOperation==null) {
                    hasSomeWork = false;
                }
            }

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {

        fileTools = new FileTools(getBaseContext());
        operationDao = ((ImageProcessingApplication) getApplication()).getImageProcessingAPIDatabase().operationDao();
        resourceDao = ((ImageProcessingApplication) getApplication()).getImageProcessingAPIDatabase().resourceDao();
        operationWithChainResourceDao = ((ImageProcessingApplication) getApplication()).getImageProcessingAPIDatabase().operationWithChainAndResourceDao();
        apiRepository=new OperationResourceAPIRepository(((ImageProcessingApplication)getApplication()).getImageProcessingAPIDatabase(),operationDao,
                resourceDao,
                operationWithChainResourceDao,
                fileTools);
        resolver=new ImageOperationResolver(getBaseContext(),fileTools,resourceDao,apiRepository,operationDao);
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Thread.NORM_PRIORITY);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        mMessenger=new Messenger(mServiceHandler);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
       return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}
