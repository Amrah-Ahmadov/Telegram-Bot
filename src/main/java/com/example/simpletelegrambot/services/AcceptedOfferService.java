package com.example.simpletelegrambot.services;

import com.example.simpletelegrambot.models.Offer;
import com.example.simpletelegrambot.repositories.OfferRepo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AcceptedOfferService {
    @Autowired
    OfferRepo offerRepo;
    @Autowired
    RabbitTemplate rabbitTemplate;
    public void putOffersToQueue(int messageId, String contactInfo){
        Offer offer = getOfferByMessageId(messageId);
        Map<String, String> acceptedMap = new HashMap<>();
        acceptedMap.put("UsersRequestId", offer.getUsersRequestsId());
        acceptedMap.put("UsersInfo", contactInfo);
        rabbitTemplate.convertAndSend("accepted_offer_queue", acceptedMap);

        //        System.out.println("Yeqinki isledi");
    }
    public Offer getOfferByMessageId(int messageId){
        return offerRepo.getOffersByOfferMessageId(messageId);
    }
    public List<Offer> getOffersByRequestId(String requestId, int limit){
        return offerRepo.getOffersByRequestId(requestId, limit);
    }
    public void saveOffer(Offer offer){
        offerRepo.saveAndFlush(offer);
    }
}
