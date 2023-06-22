package me.victor.cbd.repository;

import me.victor.cbd.model.entity.IshiharaTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface IshiharaTestRepository extends JpaRepository<IshiharaTest, Integer> {
    IshiharaTest findByDateStarted(LocalDateTime dateCreated);
}
