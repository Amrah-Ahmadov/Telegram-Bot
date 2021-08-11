package com.example.simpletelegrambot.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "questions")
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String questionText;
    @OneToMany(mappedBy = "questionEntity",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<AnswerEntity> answerEntity;
    private String regex;
    @Column(columnDefinition = "boolean default false")
    boolean isLast;
}
