package com.example.simpletelegrambot.repositories;

import com.example.simpletelegrambot.models.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepo extends JpaRepository<AnswerEntity, Integer> {
    List<AnswerEntity> getAnswerEntitiesByQuestionEntityId(int id);
    AnswerEntity getAnswerByAnswerText(String text);
}
