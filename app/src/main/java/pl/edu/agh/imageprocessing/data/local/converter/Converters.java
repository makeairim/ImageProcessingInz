package pl.edu.agh.imageprocessing.data.local.converter;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.OperationStatus;
import pl.edu.agh.imageprocessing.data.local.ResourceType;

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

    @TypeConverter
    public static OperationStatus operationStatusFromString(String value) {
        return value == null ? null : OperationStatus.valueOf(value);
    }
    @TypeConverter
    public static ResourceType resourceTypeFromString(String value) {
        return value == null ? null : ResourceType.valueOf(value);
    }
    @TypeConverter
    public static String resourceTypeToString(ResourceType status) {
        return status == null ? null : status.name();
    }
    @TypeConverter
    public static String operationStatusToString(OperationStatus status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static ImageOperationType operationTypeFromString(String value) {
        return value == null ? null : ImageOperationType.valueOf(value);
    }
    @TypeConverter
    public static String operationTypeToString(ImageOperationType type) {
        return type == null ? null : type.name();
    }
}
