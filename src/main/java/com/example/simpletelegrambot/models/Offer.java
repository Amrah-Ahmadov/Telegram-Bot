package com.example.simpletelegrambot.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "offers")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int offerMessageId;
    private String offerImageUrl;
    private String requestId;
    private String usersRequestsId;
    private LocalDateTime createdAt;

}
