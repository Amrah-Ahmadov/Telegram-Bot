package com.example.simpletelegrambot.models;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Getter
@Setter
@Table(name="sessions")
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String requestID;
    private long chatID;
    private int clientID;
    @Column(columnDefinition = "boolean default false")
    private boolean isActive;

}
