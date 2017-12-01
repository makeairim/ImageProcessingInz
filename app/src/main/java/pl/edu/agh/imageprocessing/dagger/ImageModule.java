package pl.edu.agh.imageprocessing.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.edu.agh.imageprocessing.data.local.dao.OperationDao;
import pl.edu.agh.imageprocessing.data.local.dao.ResourceDao;
import pl.edu.agh.imageprocessing.data.remote.OperationResourceAPIRepository;
import pl.edu.agh.imageprocessing.features.detail.images.FileTools;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationResolver;
import pl.edu.agh.imageprocessing.features.detail.images.OpenCvTypes;

/**
 * Created by bwolcerz on 25.07.2017.
 */
@Module
public class ImageModule {

    @Provides
    @Singleton
    FileTools provideFileTools(Context context) {
        return new FileTools(context);
    }

    @Provides
    @Singleton
    ImageOperationResolver imageOperationTypeResolver(Context context, FileTools fileTools, ResourceDao resourceDao, OperationResourceAPIRepository operationResourceAPIRepository, OperationDao operationDao) {
        return new ImageOperationResolver(context, fileTools, resourceDao, operationResourceAPIRepository, operationDao);
    }

    @Provides
    @Singleton
    OpenCvTypes provideOpenCvTypesResolver() {
        return new OpenCvTypes();
    }

}
