package pl.edu.agh.imageprocessing.features.detail;

import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import pl.edu.agh.imageprocessing.data.local.entity.Operation;
import pl.edu.agh.imageprocessing.data.remote.OperationResourceAPIRepository;

/**
 * Created by bwolcerz on 24.07.2017.
 */

public class OperationsListViewModel extends ViewModel {
    private OperationResourceAPIRepository operationResourceAPIRepository;
    @Inject
    public OperationsListViewModel(OperationResourceAPIRepository operationResourceAPIRepository) {
        this.operationResourceAPIRepository = operationResourceAPIRepository;
    }
    public List<Operation> getOperations(){
        List<Operation> operations = operationResourceAPIRepository.getOperations();
        return operations;
    }
}
