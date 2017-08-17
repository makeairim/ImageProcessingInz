package pl.edu.agh.imageprocessing.dagger;

import android.content.Context;

import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.edu.agh.imageprocessing.features.detail.images.FileTools;
import pl.edu.agh.imageprocessing.features.detail.images.ImageOperationResolver;
import pl.edu.agh.imageprocessing.features.detail.images.OpenCvTypes;
import pl.edu.agh.imageprocessing.features.detail.images.operation.BasicOperation;

/**
 * Created by bwolcerz on 25.07.2017.
 */
@Module
public  class ImageModule {
    @Provides
    @Singleton
    PickImageDialog providePhotoSelector() {
        PickSetup setup = new PickSetup().setTitle("title").setMaxSize(500).setPickTypes(EPickType.GALLERY, EPickType.CAMERA).setSystemDialog(false);
        return PickImageDialog.build(setup);
    }
    @Provides
    @Singleton
    FileTools provideFileTools(Context context){
        return new FileTools(context);
    }
    @Provides
    @Singleton
    ImageOperationResolver imageOperationTypeResolver(Context context,FileTools fileTools){
        return new ImageOperationResolver(context,fileTools);
    }
    @Provides
    @Singleton
    OpenCvTypes provideOpenCvTypesResolver(){
        return new OpenCvTypes();
    }

}
