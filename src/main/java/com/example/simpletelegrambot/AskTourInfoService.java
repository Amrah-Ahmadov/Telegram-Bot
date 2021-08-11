package com.example.simpletelegrambot;

import com.example.simpletelegrambot.models.AnswerEntity;
import com.example.simpletelegrambot.models.IncompatibleInputErrorMessage;
import com.example.simpletelegrambot.models.QuestionEntity;
import com.example.simpletelegrambot.models.UserSession;
import com.example.simpletelegrambot.repositories.AnswerRepo;
import com.example.simpletelegrambot.repositories.IncompatibleInputErrorMessageRepo;
import com.example.simpletelegrambot.repositories.QuestionRepo;
import com.example.simpletelegrambot.repositories.UserSessionRepo;
import com.example.simpletelegrambot.services.RedisService;
import com.example.simpletelegrambot.services.ReplyMessageService;
import com.example.simpletelegrambot.services.SessionService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

@Component
public class AskTourInfoService {
    private ReplyMessageService messageService;
    @Autowired
    QuestionRepo questionRepo;
    @Autowired
    AnswerRepo answerRepo;
    @Autowired
    UserSessionRepo sessionRepo;
    @Autowired
    IncompatibleInputErrorMessageRepo errorMessageRepo;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    RedisService redisService;
    @Autowired
    SessionService sessionService;
    UserSession session;
    Map<Long, Map<String, String>> usersInfos;
    Map<String, String> userInfos;
    Map<String, String> botStates;

    public AskTourInfoService(ReplyMessageService messagesService) {
        this.messageService = messagesService;
        userInfos = new LinkedHashMap<>();
        usersInfos = new LinkedHashMap<>();
        botStates = new LinkedHashMap<>();
    }

    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    public SendMessage processUsersInputMessage(long chatId, int questionId) {
        SendMessage replyToUser = new SendMessage();
        QuestionEntity questionEntity = questionRepo.getById(questionId);
        replyToUser = messageService.getReplyMessage(chatId, questionEntity.getQuestionText());
        botStates = redisService.getMapValue("botStates");
        botStates.put(String.valueOf(chatId), questionEntity.getQuestionText());
        redisService.setKey("botStates", botStates);
        usersInfos.get(chatId).put(redisService.getMapValue("botStates").get(String.valueOf(chatId)), null);
        List<AnswerEntity> answerEntities = answerRepo.getAnswerEntitiesByQuestionEntityId(questionId);
        if(answerEntities.get(0).getType().equals("button")){
            replyToUser.setReplyMarkup(getInlineMessageButtons(questionId));
        }
        return replyToUser;
    }
    private SendMessage processUsersInput(Message inputMsg) {
        long chatId = inputMsg.getChatId();
        SendMessage replyToUser = new SendMessage();
        if (inputMsg.getText().equals("/start")) {
            int count = sessionService.getCountOfUsersActiveSession(inputMsg.getChatId());
            if(sessionService.getCountOfUsersActiveSession(inputMsg.getChatId()) != 0){
                replyToUser = messageService.getReplyMessage(inputMsg.getChatId(), "You already have a session, for new session please stop current one");

            }else{
            session = UserSession.builder().chatID(inputMsg.getChatId()).clientID(inputMsg.getFrom().getId()).requestID(UUID.randomUUID().toString()).isActive(true).build();
            sessionRepo.save(session);
            QuestionEntity questionEntity = questionRepo.getById(1);
            botStates.put(String.valueOf(inputMsg.getChatId()), questionEntity.getQuestionText());
            redisService.setKey("botStates", botStates);
            userInfos = new LinkedHashMap<>();
            userInfos.put("Request", session.getRequestID());
            usersInfos.put(inputMsg.getChatId(), userInfos);

            replyToUser = messageService.getReplyMessage(inputMsg.getChatId(), questionEntity.getQuestionText());
            replyToUser.setReplyMarkup(getInlineMessageButtons(1));
            }
        }else if(inputMsg.getText().equals("/stop")){
            UserSession userSession = sessionService.getUserSessionByChatIdWhichActive(inputMsg.getChatId());
            userSession.setActive(false);
            sessionService.saveSession(userSession);
            botStates.clear();
            usersInfos.clear();
            redisService.deleteData(Arrays.asList("botStates"));
            sessionService.putStopToQueue(userSession.getRequestID());
        }
        else{
            if(inputMsg.hasReplyMarkup()){
                QuestionEntity question = questionRepo.getQuestionEntityByQuestionText(inputMsg.getText());
                botStates = redisService.getMapValue("botStates");
                botStates.put(String.valueOf(inputMsg.getChatId()), question.getQuestionText());
                redisService.setKey("botStates", botStates);

                usersInfos.get(inputMsg.getChatId()).put(redisService.getMapValue("botStates").get(String.valueOf(inputMsg.getChatId())), null);      /////&&&&&&&

                List<AnswerEntity> answer = answerRepo.getAnswerEntitiesByQuestionEntityId(question.getId());
                QuestionEntity nextQuestion = questionRepo.getById(answer.get(0).getNextQuestionId());

                replyToUser = messageService.getReplyMessage(inputMsg.getChatId(), nextQuestion.getQuestionText());
            }else{
                QuestionEntity question = questionRepo.getQuestionEntityByQuestionText(redisService.getMapValue("botStates").get(String.valueOf(inputMsg.getChatId())));
                if(inputMsg.getText().matches(question.getRegex())) {


                    usersInfos.get(inputMsg.getChatId()).put(redisService.getMapValue("botStates").get(String.valueOf(inputMsg.getChatId())), inputMsg.getText());  /////&&&&&

                    List<AnswerEntity> answer = answerRepo.getAnswerEntitiesByQuestionEntityId(question.getId());
                    QuestionEntity nextQuestion = questionRepo.getById(answer.get(0).getNextQuestionId());
                    botStates = redisService.getMapValue("botStates");
                    botStates.put(String.valueOf(inputMsg.getChatId()), nextQuestion.getQuestionText());
                    redisService.setKey("botStates", botStates);
                    replyToUser = messageService.getReplyMessage(inputMsg.getChatId(), nextQuestion.getQuestionText());
                    if(nextQuestion.isLast() != true){
                        List<AnswerEntity> nextAnswers = answerRepo.getAnswerEntitiesByQuestionEntityId(nextQuestion.getId());
                        if(nextAnswers.get(0).getType().equals("button")){
                            replyToUser = processUsersInputMessage(inputMsg.getChatId(), nextQuestion.getId());
                        }
                    }else{

                        for (Long key : usersInfos.keySet()){

                            if(usersInfos.get(key).containsKey(question.getQuestionText())){
                                rabbitTemplate.convertAndSend("telegram_bot_queue", usersInfos.get(key));

                            }
                        }
                    }
                }else{
                    IncompatibleInputErrorMessage errorMessage = errorMessageRepo.getIncompatibleInputErrorMessageByQuestionEntityId(question.getId());
                    replyToUser = sendErrorMessage(inputMsg.getChatId(), errorMessage.getMessageText(), inputMsg.getMessageId());
                }

            }
        }

        return replyToUser;
    }
    private SendMessage sendErrorMessage(long chatId, String message, int inputMessageId){
        SendMessage sendErrorMessage = new SendMessage(chatId, message);
        sendErrorMessage.setReplyToMessageId(inputMessageId);
        return sendErrorMessage;
    }

    private InlineKeyboardMarkup getInlineMessageButtons(int nextQuestionId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<AnswerEntity> myanswers = answerRepo.getAnswerEntitiesByQuestionEntityId(nextQuestionId);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        if(myanswers.size() <= 3){
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            for (AnswerEntity answer : myanswers) {
                InlineKeyboardButton button = new InlineKeyboardButton().setText(answer.getAnswerText());
                button.setCallbackData(answer.getAnswerText());
                keyboardButtonsRow.add(button);
            }
            rowList.add(keyboardButtonsRow);
        }else{
            List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
            List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
            for(int i = 0; i < myanswers.size() / 2; i++){
                InlineKeyboardButton button = new InlineKeyboardButton().setText(myanswers.get(i).getAnswerText());
                button.setCallbackData(myanswers.get(i).getAnswerText());
                keyboardButtonsRow1.add(button);
            }
            for(int j = myanswers.size() / 2; j < myanswers.size(); j++){
                InlineKeyboardButton button = new InlineKeyboardButton().setText(myanswers.get(j).getAnswerText());
                button.setCallbackData(myanswers.get(j).getAnswerText());
                keyboardButtonsRow2.add(button);
            }
            rowList.add(keyboardButtonsRow1);
            rowList.add(keyboardButtonsRow2);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

}
