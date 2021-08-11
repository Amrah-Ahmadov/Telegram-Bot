package com.example.simpletelegrambot.models;

import lombok.*;

import javax.persistence.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name="answers")
public class AnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String type;
    private String answerText;
    @ManyToOne
    @JoinColumn(name="question_id", nullable=false)
    private QuestionEntity questionEntity;
    @Column(name="next_question_id")
    private int nextQuestionId;
}
