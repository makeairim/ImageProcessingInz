package pl.edu.agh.imageprocessing.data.local.converter;

import android.net.Uri;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by bwolcerz on 31.08.2017.
 */

public class UriDeserializer implements JsonDeserializer<Uri> {
    @Override
    public Uri deserialize(final JsonElement src, final Type srcType,
                           final JsonDeserializationContext context) throws JsonParseException {
        return Uri.parse(src.getAsString());
    }
}
