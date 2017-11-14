package pl.edu.agh.imageprocessing;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.ImageProcessingAPIDatabase;
import pl.edu.agh.imageprocessing.data.local.OperationStatus;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.local.dao.OperationWithChainAndResource;
import pl.edu.agh.imageprocessing.data.local.dao.OperationWithChainAndResourceDao;
import pl.edu.agh.imageprocessing.data.local.dao.ResourceDao;
import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.data.local.entity.Resource;
import pl.edu.agh.imageprocessing.features.detail.home.HomeActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseUnitTest {
    private ImageProcessingAPIDatabase db;
    private OperationDao operationDao;
    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule(HomeActivity.class);
    private ResourceDao resourceDao;
    OperationWithChainAndResourceDao operationWithResDao;


    @Inject
    public DatabaseUnitTest() {
    }

    @Before
    public void before() {
        Context context = InstrumentationRegistry.getTargetContext();
        db = Room.inMemoryDatabaseBuilder(context, ImageProcessingAPIDatabase.class).build();
        operationDao = db.operationDao();
        resourceDao = db.resourceDao();
        operationWithResDao = db.operationWithChainAndResourceDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void canAccessDatabase() throws Exception {
        List<Operation> operations = operationDao.all();
        assertTrue(operations.isEmpty());
    }

    @Test
    public void insertOperations() throws IOException {
        operationDao.save(createOperation());
        assertTrue(operationDao.all().size() + "", operationDao.all().size() == 1);
    }

    @Test
    public void testRelationOperationResourceChainDao() {
        Operation oper = createOperation();
        operationDao.save(oper);
        oper = operationDao.all().get(0);

        resourceDao.save(createResourceFileMock());
        Resource res = createResourceFileMock();
        String otherFileName = "other file uri";
        res.setContent(otherFileName);
        res.setOperationId(oper.getId());
        resourceDao.save(res);

        List<Resource> resources = resourceDao.all();
        assertEquals(2, resources.size());

        List<Operation> operations = operationDao.all();
        assertEquals(1, operations.size());
        List<OperationWithChainAndResource> operationsWithEntities = operationWithResDao.loadOperationSortedByDate();
        assertEquals(1, operationsWithEntities.size());
        assertNotNull(operationsWithEntities.get(0).getOperation());
        assertNotNull(operationsWithEntities.get(0).getResource());
        assertTrue(operationsWithEntities.get(0).getResource().get(0).getContent().equals(otherFileName));
    }

    @Test
    public void testOperationChainRetrieve() {
        Operation parent = createOperation();
        parent.setId(operationDao.save(parent));
        long parentId = parent.getId();
        Operation oper = createOperation();
        oper.setParentOperationId(parentId);
        oper.setId(operationDao.save(oper));
        chainOperations(parent, oper);
        Operation oper1 = createOperation();
        oper1.setId(operationDao.save(oper1));
        chainOperations(oper, oper1);
        List<Operation> all = operationDao.all();
        Observable<List<OperationWithChainAndResource>> result = Observable.create((ObservableOnSubscribe<List<OperationWithChainAndResource>>) e -> {
            e.onNext(operationWithResDao.getChainOperationsSortedAsc(parentId));
            e.onComplete();
        });
        result.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(operationWithChainAndResources -> {
            assertTrue(operationWithChainAndResources.size() > 0);
        });
    }

    public static final String TAG = DatabaseUnitTest.class.getSimpleName();

    @Test
    public void testOldestOperationQuery() {
        Operation oper = createOperation();
        operationDao.save(oper);
        operationDao.save(createOperation());

        Operation operation = operationDao.getOldestUnresolved();
        Log.i(TAG, "testOldestOperationQuery: " + operation.getCreationDate() + " " + operation.getId());
        assertTrue(oper.getCreationDate().equals(operation.getCreationDate()));
    }

    public Resource createResourceFileMock() {
        Resource res = new Resource();
        res.setContent("some file uri");
        res.setCreationDate(new Date(System.currentTimeMillis()));
        return res;
    }

    private Operation createOperation() {
        return new Operation.Builder().creationDate(new Date(System.currentTimeMillis())).operationType(ImageOperationType.BINARIZATION)
                .status(OperationStatus.CREATED).build();
    }

    public boolean chainOperations(Operation parent, Operation child) {
        parent.setNextOperationId(child.getId());
        child.setParentOperationId(parent.getParentOperationId() != null ? parent.getParentOperationId() : parent.getId());
        operationDao.update(parent);
        operationDao.update(child);
        return true;
    }
}