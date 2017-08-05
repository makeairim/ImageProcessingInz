package pl.edu.agh.imageprocessing.features.detail.images;

import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

/**
 * Created by bwolcerz on 05.08.2017.
 */

public class OpenCvTypes {
    public enum MORPH_ELEMENTS {
        ELLIPSE(Imgproc.MORPH_ELLIPSE), RECTANGLE(Imgproc.MORPH_RECT);
        int type;

        public int getType() {
            return type;
        }

        private MORPH_ELEMENTS(int type) {
            this.type = type;
        }

        static public int getTypeFromName(String name) {
            return MORPH_ELEMENTS.valueOf(name).getType();
        }
    }

    ArrayList<String> result = new ArrayList<>();

    public ArrayList<String> getStructuringElementTypes() {
        if (result.isEmpty()) {
            for (MORPH_ELEMENTS type : MORPH_ELEMENTS.values()) {
                result.add(type.name());
            }
        }
        return result;
    }
}
