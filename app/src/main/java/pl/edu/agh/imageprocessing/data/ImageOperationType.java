package pl.edu.agh.imageprocessing.data;

/**
 * Created by bwolcerz on 04.08.2017.
 */

public enum ImageOperationType {
    FILTER("Filter"),CONVOLUTION("Convolution"), BINARIZATION("Binarization"), DILATION("Dilation");
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
