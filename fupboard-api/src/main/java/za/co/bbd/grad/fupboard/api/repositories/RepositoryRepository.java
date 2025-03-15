package za.co.bbd.grad.fupboard.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.bbd.grad.fupboard.api.dbobjects.Repository;

public interface RepositoryRepository extends JpaRepository<Repository, Integer> {

}