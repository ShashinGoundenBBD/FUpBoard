package za.co.bbd.grad.fupboard.api.services;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Service;

import za.co.bbd.grad.fupboard.api.dbobjects.FUp;
import za.co.bbd.grad.fupboard.api.repositories.FUpRepository;

@Service
public class FUpService {

    private final FUpRepository fUpRepository;

    FUpService(FUpRepository fUpRepository) {
        this.fUpRepository = fUpRepository;
    }

    public FUp saveFUp(FUp fUp) {
        if (fUp.getVotes() == null) {
            fUp.setVotes(new ArrayList<>()); // Prevent null issues
        }
        return fUpRepository.save(fUp);
    }

    

    public Optional<FUp> getFUpById(int id) {
        return fUpRepository.findById(id);
    }

    public void deleteFUp(FUp fUp) {
        fUpRepository.delete(fUp);
    }
}
