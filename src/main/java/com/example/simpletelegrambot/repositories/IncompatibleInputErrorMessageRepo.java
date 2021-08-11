package com.example.simpletelegrambot.repositories;

import com.example.simpletelegrambot.models.IncompatibleInputErrorMessage;
import com.example.simpletelegrambot.models.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncompatibleInputErrorMessageRepo extends JpaRepository<IncompatibleInputErrorMessage, Integer> {
    IncompatibleInputErrorMessage getIncompatibleInputErrorMessageByQuestionEntityId(int id);
}
