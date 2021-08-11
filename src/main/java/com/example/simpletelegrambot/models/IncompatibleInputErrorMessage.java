package com.example.simpletelegrambot.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name="incompatible_input_error_messages")
public class IncompatibleInputErrorMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String messageText;
    @OneToOne(targetEntity = QuestionEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "question_id")
    private QuestionEntity questionEntity;
}
