package me.victor.cbd.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ishihara_tests")
@Data
public class IshiharaTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private LocalDateTime dateStarted;

    private LocalDateTime dateEnded;
    private String testUrl;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private List<TestQuestion> questions;
}
