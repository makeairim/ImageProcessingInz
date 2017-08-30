package pl.edu.agh.imageprocessing.data.local.converter;

import android.net.Uri;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by bwolcerz on 31.08.2017.
 */

public class UriSerializer implements JsonSerializer<Uri> {
    public JsonElement serialize(Uri src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
