package sit.int221.backend.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sit.int221.backend.entities.Status;
import sit.int221.backend.exceptions.ItemNotFoundException;
import sit.int221.backend.exceptions.MethodNotAllowedException;
import sit.int221.backend.repositories.StatusRepository;

import java.util.List;

@Service
public class StatusService {
    @Autowired
    private StatusRepository statusRepository;
//    @Autowired
//    private ModelMapper modelMapper;
//    @Autowired
//    private ListMapper listMapper;

    public List<Status> getAllStatus() {
        return statusRepository.findAll();
    }

    public Status getStatus(int id) {
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
        if (status.getName().equalsIgnoreCase("No Status") || status.getName().equalsIgnoreCase("NO_STATUS")) {
            throw new MethodNotAllowedException("The " + status.getName() + " cannot be delete ");
        }
        statusRepository.delete(status);
    }

    @Transactional
    public Status updateStatus(int id, Status status) {
        Status oldStatus = statusRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("NOT FOUND")
        );
        if (status.getName().equalsIgnoreCase("No Status") || status.getName().equalsIgnoreCase("NO_STATUS")) {
            throw new MethodNotAllowedException("The " + oldStatus.getName() + " cannot be edited ");
        }
        if (!oldStatus.equals(status)) {
            oldStatus.setName(status.getName());
            oldStatus.setDescription(status.getDescription());
            return statusRepository.save(oldStatus);
        }
        return oldStatus;
    }

}
