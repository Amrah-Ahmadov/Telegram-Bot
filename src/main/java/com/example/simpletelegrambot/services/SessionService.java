package com.example.simpletelegrambot.services;

import com.example.simpletelegrambot.models.UserSession;
import com.example.simpletelegrambot.repositories.UserSessionRepo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    @Autowired
    UserSessionRepo userSessionRepo;
    @Autowired
    RabbitTemplate rabbitTemplate;

    public UserSession getUserSessionByChatId(long chatId){
        return userSessionRepo.getUserSessionByChatID(chatId);
    }

    public int getCountOfUsersActiveSession(long chatId){
        return userSessionRepo.getCountOfUsersActiveSession(chatId);
    }
    public void saveSession(UserSession userSession){
        userSessionRepo.saveAndFlush(userSession);
    }
    public UserSession getUserSessionByChatIdWhichActive(long chatId){
        return userSessionRepo.getUserSessionByChatIdWhichActive(chatId);
    }

    public void putStopToQueue(String requestId){
        rabbitTemplate.convertAndSend("stop_queue", requestId);
    }
}
