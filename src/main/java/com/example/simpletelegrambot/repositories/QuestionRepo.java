package com.example.simpletelegrambot.repositories;

import com.example.simpletelegrambot.models.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepo extends JpaRepository<QuestionEntity, Integer> {
    QuestionEntity getQuestionEntityByQuestionText(String text);
}
