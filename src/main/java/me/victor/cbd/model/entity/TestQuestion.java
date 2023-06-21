package me.victor.cbd.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "test_questions")
public class TestQuestion {
    @Id
    private int id;

    @ManyToOne(optional = false)
    private IshiharaQuestion question;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private IshiharaTest test;

    private int userGuess;
}
