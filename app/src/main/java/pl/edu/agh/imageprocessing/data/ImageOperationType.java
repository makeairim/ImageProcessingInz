package pl.edu.agh.imageprocessing.data;

import java.util.EnumSet;

/**
 * Created by bwolcerz on 04.08.2017.
 */

public enum ImageOperationType {
    BINARIZATION("Binarization"),
    DILATION("Dilation"),
    EROSION("Erosion"),
    FILTER("Filter"),
    BASIC_PHOTO("Base photo"),
    MEAN_FILTER("Mean filter"),
    UNASSIGNED_TO_RESOURCE_ROOT_CHAIN("Chain root"),
    CANNY_EDGE("Canny edge detector"),
    SOBEL_OPERATOR("Sobel operator"),
    HARRIS_CORNER("Harris corner detection"), ADD_IMAGES("Add images"), DIFF_IMAGES("Subtract images"), BITWISE_AND("Bitwise AND"), BITWISE_OR("Bitwise OR"), BITWISE_XOR("Bitwise XOR");
    private String title;

    ImageOperationType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static EnumSet<ImageOperationType> getNonSingleArgumentoperations() {
        return EnumSet.of(ADD_IMAGES, DIFF_IMAGES, BITWISE_AND, BITWISE_OR, BITWISE_XOR);
    }
}
