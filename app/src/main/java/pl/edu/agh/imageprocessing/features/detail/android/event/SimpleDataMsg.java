package pl.edu.agh.imageprocessing.features.detail.android.event;

/**
 * Created by bwolcerz on 18.08.2017.
 */

public class SimpleDataMsg {
    Object data;

    public SimpleDataMsg(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
