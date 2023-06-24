package me.victor.cbd.model.model;

import java.time.LocalDateTime;
import java.util.List;

public record IshiharaTestData(int testId, LocalDateTime dateCreated, List<QuestionData> questions) {
}
