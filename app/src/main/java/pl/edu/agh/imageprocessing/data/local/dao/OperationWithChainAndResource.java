package pl.edu.agh.imageprocessing.data.local.dao;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.data.local.entity.Resource;

/**
 * Created by bwolcerz on 19.08.2017.
 */

public class OperationWithChainAndResource {
    @Embedded
    private Operation operation;

    @Relation(parentColumn = "id", entityColumn = "operationId", entity = Resource.class)
    private List<Resource> resource;

    public OperationWithChainAndResource() {
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public List<Resource> getResource() {
        return resource;
    }

    public void setResource(List<Resource> resource) {
        this.resource = resource;
    }

}
