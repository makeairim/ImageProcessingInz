package pl.edu.agh.imageprocessing.features.detail.images;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.edu.agh.imageprocessing.app.constants.AppConstants;

/**
 * Created by bwolcerz on 25.07.2017.
 */
@Singleton
public class FileTools {
    @Inject
    Context context;
    @Inject
    public FileTools(Context context) {
        this.context=context;
    }

    private Uri setImageUri(Context context) {
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir(AppConstants.PHOTO_STORAGE_PATH, Context.MODE_PRIVATE);
        File file = new File(directory,System.currentTimeMillis() + ".png");
        Uri imgUri = Uri.fromFile(file);
        return imgUri;
    }
    public Uri saveFile(Bitmap file, Context context){
        Uri uri = setImageUri(context);
        File imageFile = new File(uri.getPath());
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imageFile);
            file.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return uri;
    }
    public Bitmap getImageBitmap(Context context,Uri uri) throws IOException {
        InputStream imageStream =context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        imageStream.close();
        return bitmap;
    }

    public Uri saveFile(Bitmap imageBitmap) {
return saveFile(imageBitmap,context);
    }
}
