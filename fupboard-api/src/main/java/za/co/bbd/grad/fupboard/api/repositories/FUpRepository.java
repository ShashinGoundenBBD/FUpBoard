package za.co.bbd.grad.fupboard.api.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.bbd.grad.fupboard.api.dbobjects.FUp;

public interface FUpRepository extends JpaRepository<FUp, Integer> {
    List<FUp> findByProjectProjectId(int project);
    Optional<FUp> findByProjectProjectIdAndFUpId(int project, int fUpId);
}
