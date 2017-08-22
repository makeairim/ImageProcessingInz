package pl.edu.agh.imageprocessing.features.detail.android.event;

/**
 * Created by bwolcerz on 22.08.2017.
 */

public class ChainOperationEvent{
    private long baseOperationId;
    private long processingOperationId;

    public ChainOperationEvent(long baseOperationId, long processingOperationId) {
        this.baseOperationId = baseOperationId;
        this.processingOperationId = processingOperationId;
    }

    public long getBaseOperationId() {
        return baseOperationId;
    }

    public void setBaseOperationId(long baseOperationId) {
        this.baseOperationId = baseOperationId;
    }

    public long getProcessingOperationId() {
        return processingOperationId;
    }

    public void setProcessingOperationId(long processingOperationId) {
        this.processingOperationId = processingOperationId;
    }
}
