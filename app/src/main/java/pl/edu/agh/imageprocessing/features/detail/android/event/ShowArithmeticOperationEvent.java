package pl.edu.agh.imageprocessing.features.detail.android.event;

import pl.edu.agh.imageprocessing.data.ImageOperationType;
import pl.edu.agh.imageprocessing.data.local.entity.Resource;

/**
 * Created by bwolcerz on 05.11.2017.
 */

public class ShowArithmeticOperationEvent {
    private ImageOperationType type;
    public ShowArithmeticOperationEvent(ImageOperationType type) {
        this.type = type;
    }

    public ImageOperationType getType() {
        return type;
    }

    public void setType(ImageOperationType type) {
        this.type = type;
    }
}
