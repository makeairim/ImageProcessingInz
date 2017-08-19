package pl.edu.agh.imageprocessing.data.local;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by bwolcerz on 19.08.2017.
 */

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
