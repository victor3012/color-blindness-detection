package me.victor.cbd.model.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record IshiharaCompletedTestData(Integer testId, String testUrl,
                                        LocalDateTime dateCreated, LocalDateTime dateEnded,
                                        Integer totalQuestions, Integer correctQuestions, Integer wrongQuestions,
                                        Double accuracy, Map<String, Double> blindnessTypeAccuracy,
                                        List<QuestionData> questions) {
}
