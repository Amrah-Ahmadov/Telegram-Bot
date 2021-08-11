package com.example.simpletelegrambot;

import com.example.simpletelegrambot.models.Offer;
import com.example.simpletelegrambot.repositories.AnswerRepo;
import com.example.simpletelegrambot.repositories.QuestionRepo;
import com.example.simpletelegrambot.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TelegramService {
    @Autowired
    ReplyMessageService messageService;
    @Autowired
    AskTourInfoService askTourInfoService;
    @Autowired
    QuestionRepo questionRepo;
    @Autowired
    AnswerRepo answerRepo;
    @Autowired
    RedisService redisService;
    @Autowired
    AcceptedOfferService acceptedService;
    @Autowired
    ReceiveOfferService receiveOfferService;
    @Autowired
    MyTelegramBot bot;


    public BotApiMethod<?> handleUpdate(Update update) {

        SendMessage replyMessage = null;
        BotApiMethod<?> test = null;

        if (update.hasCallbackQuery()) {

            CallbackQuery callbackQuery = update.getCallbackQuery();
            return processCallbackQuery(callbackQuery);
        }
        Message message = update.getMessage();

        if (message.hasText()) {
            replyMessage = handleInputMessage(message);
        }
        return replyMessage;
    }

    public SendMessage handleInputMessage(Message message) {
        if(message.isReply() && message.getReplyToMessage().hasPhoto()){
            acceptedService.putOffersToQueue(message.getReplyToMessage().getMessageId(), message.getText());
        }
        return askTourInfoService.handle(message);

    }


    private BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        var callBackAnswer = new SendMessage();
        if (buttonQuery.getData().startsWith("Next")) {
            long chatId = buttonQuery.getMessage().getChatId();
            String requestId = buttonQuery.getData().substring(buttonQuery.getData().indexOf(' ') + 1);
            List<Offer> offers = acceptedService.getOffersByRequestId(requestId, 2);
            for (Offer o : offers) {
                Message sendPhoto = bot.sendPhoto(chatId, o.getOfferImageUrl());
                int count = receiveOfferService.getCountOfSentOffers().get(requestId);
                receiveOfferService.getCountOfSentOffers().put(requestId, count + 1);
                receiveOfferService.getIsNextButtonShown().put(requestId, false);
                o.setOfferMessageId(sendPhoto.getMessageId());
                acceptedService.saveOffer(o);
            }
            if (receiveOfferService.getCountOfSentOffers().get(requestId) < receiveOfferService.getCountOfOffers().get(requestId)) {
                Message message = bot.sendMessage(buttonQuery.getMessage().getChatId(), "Please press button to get next offers", requestId);
            }
        } else {
            final long chatId = buttonQuery.getMessage().getChatId();
            final int userId = buttonQuery.getFrom().getId();
            callBackAnswer = askTourInfoService.handle(buttonQuery.getMessage());
            askTourInfoService.usersInfos.get(buttonQuery.getMessage().getChatId()).put(buttonQuery.getMessage().getText(), buttonQuery.getData());

            var nextQuestion = questionRepo.findById(answerRepo.getAnswerByAnswerText(buttonQuery.getData()).getNextQuestionId()).get();

            callBackAnswer = askTourInfoService.processUsersInputMessage(buttonQuery.getMessage().getChatId(), nextQuestion.getId());
        }

        return callBackAnswer;
    }


    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }
}
