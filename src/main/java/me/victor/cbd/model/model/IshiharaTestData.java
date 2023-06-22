package me.victor.cbd.model.model;

import java.time.LocalDateTime;
import java.util.List;

public record IshiharaTestData(int testId, List<QuestionData> questions, LocalDateTime dateCreated) {
}
