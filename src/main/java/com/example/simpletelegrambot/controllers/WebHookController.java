package com.example.simpletelegrambot.controllers;


import com.example.simpletelegrambot.MyTelegramBot;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebHookController {
    private final MyTelegramBot telegramBot;

    public WebHookController(MyTelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handlerNotFoundException(Exception exception) {
        System.out.println(exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return telegramBot.onWebhookUpdateReceived(update);
    }
}
