package za.co.bbd.grad.fupboard.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.bbd.grad.fupboard.api.dbobjects.FUp;

public interface FUpRepository extends JpaRepository<FUp, Integer> {
    List<FUp> findByProjectProjectId(int project);
}
