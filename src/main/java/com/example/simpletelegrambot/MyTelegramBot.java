package com.example.simpletelegrambot;

import com.example.simpletelegrambot.services.MainMenuService;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyTelegramBot extends TelegramWebhookBot {
    private String webHookPath;
    private String botUserName;
    private String botToken;
    @Autowired
    MainMenuService mainMenuService;
    @Autowired
    AskTourInfoService askTourInfoService;
    @Autowired
    TelegramService telegramService;

    public MyTelegramBot(DefaultBotOptions botOptions) {
        super(botOptions);
    }


    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotPath() {
        return webHookPath;
    }


    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        final BotApiMethod<?> replyMessageToUser = telegramService.handleUpdate(update);
        return replyMessageToUser;
    }
    @SneakyThrows
    public Message sendPhoto(Long chatId, String imagePath) {
        URL url = new URL(imagePath);
        File file = new File("filename.jpg");
        FileUtils.copyURLToFile(url, file);
        SendPhoto sendPhoto = new SendPhoto().setPhoto(file);
        sendPhoto.setChatId(chatId);
        return execute(sendPhoto);
   }
    @SneakyThrows
    public Message sendMessage(Long chatId, String message, String requestId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(getInlineMessageButtons(requestId));
        return execute(sendMessage);
    }
    private InlineKeyboardMarkup getInlineMessageButtons(String requestId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton().setText("Next");
        button.setCallbackData("Next " + requestId);
        keyboardButtonsRow.add(button);
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }


    public void setWebHookPath(String webHookPath) {
        this.webHookPath = webHookPath;
    }

    public void setBotUserName(String botUserName) {
        this.botUserName = botUserName;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

}
