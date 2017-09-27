package pl.edu.agh.imageprocessing.features.detail.images;

import android.net.Uri;

import com.google.gson.GsonBuilder;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import pl.edu.agh.imageprocessing.data.ImageOperationType;

/**
 * Created by bwolcerz on 22.08.2017.
 */

public class ImageOperationResolverParameters{
    private ImageOperationType operationType;
    private int morphologyWidth;
    private int morphologyHeight;
    private int morphologyElementType;
    private int threshold;
    private int matrixHeight;
    private int matrixWidth;
    private int supressedPointsThreshold;
    private int strongPointsThreshold;
    private int[] matrix;

    public ImageOperationType getOperationType() {
        return operationType;
    }

    public int getMorphologyWidth() {
        return morphologyWidth;
    }

    public int getMorphologyHeight() {
        return morphologyHeight;
    }

    public int getMorphologyElementType() {
        return morphologyElementType;
    }

    public int getThreshold() {
        return threshold;
    }

    public int getMatrixHeight() {
        return matrixHeight;
    }

    public int getMatrixWidth() {
        return matrixWidth;
    }

    public int getSupressedPointsThreshold() {
        return supressedPointsThreshold;
    }

    public int getStrongPointsThreshold() {
        return strongPointsThreshold;
    }

    public int[] getMatrix() {
        return matrix;
    }

    private ImageOperationResolverParameters(Builder builder) {
        operationType = builder.operationType;
        morphologyWidth = builder.morphologyWidth;
        morphologyHeight = builder.morphologyHeight;
        morphologyElementType = builder.morphologyElementType;
        threshold = builder.threshold;
        matrixHeight = builder.matrixHeight;
        matrixWidth = builder.matrixWidth;
        supressedPointsThreshold = builder.supressedPointsThreshold;
        strongPointsThreshold = builder.strongPointsThreshold;
        matrix = builder.matrix;
    }


    public static final class Builder {
        private ImageOperationType operationType;
        private int morphologyWidth;
        private int morphologyHeight;
        private int morphologyElementType;
        private int threshold;
        private int matrixHeight;
        private int matrixWidth;
        private int supressedPointsThreshold;
        private int strongPointsThreshold;
        private int[] matrix;

        public Builder() {
        }

        public Builder operationType(ImageOperationType val) {
            operationType = val;
            return this;
        }

        public Builder morphologyWidth(int val) {
            morphologyWidth = val;
            return this;
        }

        public Builder morphologyHeight(int val) {
            morphologyHeight = val;
            return this;
        }

        public Builder morphologyElementType(int val) {
            morphologyElementType = val;
            return this;
        }

        public Builder threshold(int val) {
            threshold = val;
            return this;
        }

        public Builder matrixHeight(int val) {
            matrixHeight = val;
            return this;
        }

        public Builder matrixWidth(int val) {
            matrixWidth = val;
            return this;
        }

        public Builder supressedPointsThreshold(int val) {
            supressedPointsThreshold = val;
            return this;
        }

        public Builder strongPointsThreshold(int val) {
            strongPointsThreshold = val;
            return this;
        }

        public Builder matrix(int[] val) {
            matrix = val;
            return this;
        }

        public ImageOperationResolverParameters build() {
            return new ImageOperationResolverParameters(this);
        }
    }
}

