package pl.edu.agh.imageprocessing.data.local;

import java.util.EnumSet;

/**
 * Created by bwolcerz on 19.08.2017.
 */

public enum ResourceType {
    IMAGE_FILE, ARGUMENT_OPERATION_RESOURCE, ARGUMENT_IMAGE_FILE;

    static public EnumSet<ResourceType> getMultiArgumentResourceTypes() {
        return EnumSet.of(ARGUMENT_IMAGE_FILE, ARGUMENT_OPERATION_RESOURCE);
    }
}
