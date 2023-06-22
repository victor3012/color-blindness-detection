package me.victor.cbd.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import me.victor.cbd.model.enumration.BlindnessType;

@Entity
@Table(name = "ishihara_questions")
@Data
public class IshiharaQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false)
    private BlindnessType colourBlindness;

    @Column(nullable = false)
    private String imageUrl;
}
