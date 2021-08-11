package com.example.simpletelegrambot.services;

import com.example.simpletelegrambot.AskTourInfoService;
import com.example.simpletelegrambot.MyTelegramBot;
import com.example.simpletelegrambot.models.Offer;
import com.example.simpletelegrambot.models.UserSession;
import com.example.simpletelegrambot.repositories.OfferRepo;
import com.example.simpletelegrambot.repositories.UserSessionRepo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Getter
@Setter
public class ReceiveOfferService {
    @Autowired
    ReplyMessageService messageService;
    @Autowired
    UserSessionRepo userSessionRepo;
    @Autowired
    OfferRepo offerRepo;
    @Autowired
    MyTelegramBot bot;
    @Autowired
    AskTourInfoService askTourInfoService;
    @Autowired
    RedisService redisService;

    Map<String, Integer> countOfOffers = new HashMap<>();
    Map<String, Boolean> isNextButtonShown = new HashMap<>();
    Map<String, Integer> countOfSentOffers = new HashMap<>();
//    @Autowired
//    TelegramService telegramService;

//    @RabbitListener(queues = "my_offer_queue")
//    public void listen(String request) {
//        String requestId = request.substring(0, request.indexOf(' '));
//        String url = request.substring(request.indexOf(' ') + 1);
//        System.out.println("bura geldi 2");
//        System.out.println("Message read from myQueue : request " + requestId + " url " + url);
//        UserSession userSession = userSessionRepo.getUserSessionByRequestID(requestId);
//        System.out.println("CCCCCCCCC " + userSession.getChatID());
//        Message sendPhoto = bot.sendPhoto(userSession.getChatID(), url);
////        sendPhoto.getMessageId();                                           ///////////////////////////
//        SendMessage sendMessage = processPhotos(sendPhoto);
//    }
    @RabbitListener(queues = "my_offer_queue")
    public void listen(Map<String, String> offerMap) {
        String requestId = offerMap.get("RequestId");
        String url = offerMap.get("OfferImageUrl");
        String usersRequestsId = offerMap.get("UsersRequestsId");

        if(countOfOffers.containsKey(requestId)){
            int count = countOfOffers.get(requestId);
            countOfOffers.put(requestId, count + 1);
        }else{
            countOfOffers.put(requestId, 1);
            countOfSentOffers.put(requestId, 0);
            isNextButtonShown.put(requestId, false);
        }
        UserSession userSession = userSessionRepo.getUserSessionByRequestID(requestId);
        System.out.println("CCCCCCCCC " + userSession.getChatID());
        if(countOfOffers.get(requestId) <= 2){
            Message sendPhoto = bot.sendPhoto(userSession.getChatID(), url);
            int countOfSent = countOfSentOffers.get(requestId);
            countOfSentOffers.put(requestId, countOfSent + 1);
            Offer offer = Offer.builder().offerMessageId(sendPhoto.getMessageId()).offerImageUrl(url).requestId(requestId).usersRequestsId(usersRequestsId).createdAt(LocalDateTime.now()).build();
            offerRepo.saveAndFlush(offer);
        }else if(countOfOffers.get(requestId) % 2 == 1 && isNextButtonShown.get(requestId).equals(false)){
            Message message = bot.sendMessage(userSession.getChatID(), "Please press button", userSession.getRequestID());
            isNextButtonShown.put(requestId, true);
            Offer offer = Offer.builder().offerImageUrl(url).requestId(requestId).usersRequestsId(usersRequestsId).createdAt(LocalDateTime.now()).build();
            offerRepo.saveAndFlush(offer);
        }
        else if(isNextButtonShown.get(requestId).equals(false)){
            Message sendPhoto = bot.sendPhoto(userSession.getChatID(), url);
            int countOfSent = countOfSentOffers.get(requestId);
            countOfSentOffers.put(requestId, countOfSent + 1);
            Offer offer = Offer.builder().offerImageUrl(url).requestId(requestId).usersRequestsId(usersRequestsId).createdAt(LocalDateTime.now()).build();
            offerRepo.saveAndFlush(offer);
        }
        else{
            Offer offer = Offer.builder().offerImageUrl(url).requestId(requestId).usersRequestsId(usersRequestsId).createdAt(LocalDateTime.now()).build();
            offerRepo.saveAndFlush(offer);
        }
//        SendMessage sendMessage = processPhotos(sendPhoto);
    }
//    private SendMessage processPhotos(Message inputMsg) {
//        long chatId = inputMsg.getChatId();
//        SendMessage reply = new SendMessage();
//        reply.setReplyMarkup(getInlineMessageButtons());
//        return reply;
//    }

//        public SendMessage processUsersInputMessage(long chatId) {
//            SendMessage replyToUser = new SendMessage();
//            replyToUser = messageService.getReplyMessage(chatId, "For to get next 5 messages press the button");
//            replyToUser.setReplyMarkup(getInlineMessageButtons());
//            return replyToUser;
//        }

//    private InlineKeyboardMarkup getInlineMessageButtons() {
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//
//        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
//        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
//        InlineKeyboardButton button = new InlineKeyboardButton().setText("Next");
//        button.setCallbackData("Next");
//        keyboardButtonsRow.add(button);
//        rowList.add(keyboardButtonsRow);
//
//        inlineKeyboardMarkup.setKeyboard(rowList);
//
//        return inlineKeyboardMarkup;
//    }
}
