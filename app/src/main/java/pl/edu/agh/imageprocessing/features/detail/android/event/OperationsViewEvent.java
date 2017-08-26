package pl.edu.agh.imageprocessing.features.detail.android.event;

/**
 * Created by bwolcerz on 26.08.2017.
 */

public class OperationsViewEvent {
    private  long id;
    public OperationsViewEvent(long id) {
        this.id=id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
