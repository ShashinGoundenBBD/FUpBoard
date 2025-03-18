package za.co.bbd.grad.fupboard.api.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import za.co.bbd.grad.fupboard.api.dbobjects.FUp;
import za.co.bbd.grad.fupboard.api.dbobjects.User;
import za.co.bbd.grad.fupboard.api.repositories.FUpRepository;

@Service
public class FUpService {

    private final ProjectService projectService;

    private final FUpRepository fUpRepository;

    FUpService(FUpRepository fUpRepository, ProjectService projectService) {
        this.fUpRepository = fUpRepository;
        this.projectService = projectService;
    }

    public boolean allowedToReadFUp(FUp fUp, User user) {
        return projectService.allowedToReadProject(fUp.getProject(), user);
    }

    public boolean allowedToWriteFUp(FUp fUp, User user) {
        return projectService.allowedToWriteProject(fUp.getProject(), user);
    }

    public boolean allowedToDeleteFUp(FUp fUp, User user) {
        return projectService.allowedToWriteProject(fUp.getProject(), user);
    }

    public FUp saveFUp(FUp fUp) {
        return fUpRepository.save(fUp);
    }

    public Optional<FUp> getFUpById(int id) {
        return fUpRepository.findById(id);
    }

    public void deleteFUp(FUp fUp) {
        fUpRepository.delete(fUp);
    }
}
