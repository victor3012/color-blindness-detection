package me.victor.cbd.repository;

import me.victor.cbd.model.entity.IshiharaQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IshiharaQuestionRepository extends JpaRepository<IshiharaQuestion, Integer> {
}
