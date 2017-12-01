package pl.edu.agh.imageprocessing.features.detail.images;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.edu.agh.imageprocessing.app.constants.AppConstants;

/**
 * Created by bwolcerz on 25.07.2017.
 */
@Singleton
public class FileTools {
    public static final String TAG = FileTools.class.getSimpleName();
    private final String[] okFileExtensions = new String[]{"jpg", "png", "jpeg"};
    @Inject
    Context context;

    @Inject
    public FileTools(Context context) {
        this.context = context;
    }

    private Uri setImageUri() {
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir(AppConstants.PHOTO_STORAGE_PATH, Context.MODE_PRIVATE);
        File file = new File(directory, System.currentTimeMillis() + ".png");
        Uri imgUri = Uri.fromFile(file);
        return imgUri;
    }

    public Uri saveFile(Bitmap file) {
        Uri uri = setImageUri();
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

    public Bitmap getImageBitmap(Context context, Uri uri) throws IOException {
        InputStream imageStream = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        imageStream.close();
        return bitmap;
    }

    public boolean deleteFile(Uri uri) {
        File file = new File(uri.getPath());
        if (file.exists()) {
            file.delete();
        }
        return true;
    }

    public List<Uri> getPhotos() {
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir(AppConstants.PHOTO_STORAGE_PATH, Context.MODE_PRIVATE);
        List<Uri> result = new LinkedList<>();
        for (File file : directory.listFiles()) {
            for (String ext : okFileExtensions) {
                if (file.getName().toLowerCase().endsWith(ext)) {
                    result.add(Uri.fromFile(file));
                }
            }
        }
        return result;
    }

    public Uri renameFile(Uri uri, String newFileName) {
        File file = new File(uri.getPath());
        if (!file.exists()) {
            Log.e(TAG, "renameFile: file not exists");
            throw new AssertionError();
        }
        String dir = AppConstants.PHOTO_STORAGE_PATH;
        File newFile = new File(dir, newFileName + ".png");
        try {
            file.renameTo(newFile);
        } catch (Exception e) {
            Log.e(TAG, "renameFile: cannot rename" + e.getMessage(), e);
            if (newFile.exists()) {
                newFile.delete();
            }
        }
        return uri;
    }
}
