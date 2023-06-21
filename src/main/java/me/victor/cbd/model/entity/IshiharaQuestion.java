package me.victor.cbd.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ishihara_questions")
@Data
public class IshiharaQuestion {
    @Id
    private int id;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false)
    private String imageUrl;
}
