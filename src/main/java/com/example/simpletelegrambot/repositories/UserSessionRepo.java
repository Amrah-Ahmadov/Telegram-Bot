package com.example.simpletelegrambot.repositories;

import com.example.simpletelegrambot.models.QuestionEntity;
import com.example.simpletelegrambot.models.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserSessionRepo extends JpaRepository<UserSession, Integer> {
    UserSession getUserSessionByRequestID(String requestId);
    UserSession getUserSessionByChatID(long chatId);
    @Query(value = "select count(*) from sessions s where s.chatid = :chatId AND s.is_active = true",nativeQuery = true)
    int getCountOfUsersActiveSession(long chatId);
    @Query(value = "select * from sessions s where s.chatid = :chatId AND s.is_active = true",nativeQuery = true)
    UserSession getUserSessionByChatIdWhichActive(long chatId);

}
