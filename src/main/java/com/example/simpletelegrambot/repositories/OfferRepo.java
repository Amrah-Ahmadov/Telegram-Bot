package com.example.simpletelegrambot.repositories;

import com.example.simpletelegrambot.models.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OfferRepo extends JpaRepository<Offer, Long> {
    Offer getOffersByOfferMessageId(int messageId);
    @Query(value = "select * from offers o where o.request_id = :requestId AND o.offer_message_id = 0 limit :limit",nativeQuery = true)
    List<Offer> getOffersByRequestId(String requestId, @Param("limit") int limit);
}
