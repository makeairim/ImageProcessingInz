package pl.edu.agh.imageprocessing.data;

/**
 * Created by bwolcerz on 04.08.2017.
 */

public enum ImageOperationType {
    BINARIZATION("Binarization"), DILATION("Dilation"), EROSION("Erosion"), FILTER("Filter"),BASIC_PHOTO("Base photo"), MEAN_FILTER("Mean filter"),UNASSIGNED_TO_RESOURCE_ROOT_CHAIN("Chain root"), CANNY_EDGE("Canny edge detector"), SOBEL_OPERATOR("Sobel operator"), HARRIS_CORNER("Harris corner detection");
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
}
