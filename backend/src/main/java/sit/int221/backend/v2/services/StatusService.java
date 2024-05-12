package sit.int221.backend.v2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.backend.exceptions.ItemNotFoundException;
import sit.int221.backend.exceptions.MethodNotAllowedException;
import sit.int221.backend.v2.entities.Status;
import sit.int221.backend.v2.repositories.StatusRepository;

import java.util.List;

@Service
public class StatusService {
    @Autowired
    private StatusRepository statusRepository;

    public List<Status> getAllStatus() {
        return statusRepository.findAll();
    }


    public Status getStatusById(int id) {
        return statusRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("Status id " + id + "does not exit !!!")
        );
    }

    @Transactional
    public Status createNewStatus(Status status) {
        return statusRepository.save(status);
    }

    @Transactional
    public void removeStatus(int id) {
        Status status = statusRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND")
        );
        if (status.getName().equals("No Status") || status.getName().equals("NO_STATUS")) {
            throw new MethodNotAllowedException("The " + status.getName() + " cannot be delete ");
        }
        statusRepository.delete(status);
    }

    @Transactional
    public Status updateStatus(int id, Status status) {
        Status oldStatus = statusRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND")
        );
        if (status.getName().equals("No Status") || status.getName().equals("NO_STATUS")) {
            throw new MethodNotAllowedException("The " + oldStatus.getName() + " cannot be edited ");
        }
        if (!oldStatus.getName().equals(status.getName())) {
            oldStatus.setName(status.getName());
            oldStatus.setDescription(status.getDescription());
            return statusRepository.save(oldStatus);
        }
        return oldStatus;
    }

//    @Transactional
//    public List<Status> transferStatusAndDelete(Integer statusId, Integer newStatusId) {
//        Status status = statusRepository.findById(statusId)
//                .orElseThrow(() -> new ItemNotFoundException("Status with ID " + statusId + " not found"));
//
//        if ((status.getId() == 1)) {
//            throw new BadRequestException("Cannot delete default status 'No Status'");
//        }
//
//        Status newStatus = statusRepository.findById(newStatusId)
//                .orElseThrow(() -> new ItemNotFoundException("Status with new ID " + newStatusId + " not found"));
//
//        List<Task> tasks = taskRepository.findByStatusId(statusID);
//        List<StatusTransferDTO> transferTask = new ArrayList<>();
//
//        for (Task task : tasks) {
//            task.setStatus(newStatus);
//            taskRepository.save(task);
//
//            Status status = new Status();
//            status.setTitle(task.getTitle());
//            status.setTaskID(task.getId());
//            status.setStatusID(task.getId());
//            status.setNewStatusID(newStatus.getId());
//            transferTask.add(status);
//        }
//
//        statusRepository.deleteById(statusId);
//
//        return transferTask;
}
