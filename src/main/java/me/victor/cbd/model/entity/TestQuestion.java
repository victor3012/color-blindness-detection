package me.victor.cbd.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "test_questions")
@Data
public class TestQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private IshiharaQuestion question;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private IshiharaTest test;

    private int userGuess;
}
