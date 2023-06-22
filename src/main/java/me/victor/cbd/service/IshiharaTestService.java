package me.victor.cbd.service;

import me.victor.cbd.exception.DataFormatException;
import me.victor.cbd.exception.ResourceNotFoundException;
import me.victor.cbd.model.entity.IshiharaQuestion;
import me.victor.cbd.model.entity.IshiharaTest;
import me.victor.cbd.model.entity.TestQuestion;
import me.victor.cbd.model.model.IshiharaTestData;
import me.victor.cbd.model.model.QuestionData;
import me.victor.cbd.repository.IshiharaQuestionRepository;
import me.victor.cbd.repository.IshiharaTestRepository;
import me.victor.cbd.repository.TestQuestionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IshiharaTestService {
    @Value("${max.test.questions}")
    private int maxTestQuestions;

    @Value("${generated.link.length}")
    private int generatedLinkLength;

    private final IshiharaTestRepository testRepository;
    private final IshiharaQuestionRepository questionRepository;
    private final TestQuestionRepository testQuestionRepository;

    public IshiharaTestService(IshiharaTestRepository testRepository, IshiharaQuestionRepository questionRepository,
                               TestQuestionRepository testQuestionRepository) {
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.testQuestionRepository = testQuestionRepository;
    }

    public IshiharaTestData createTest() {
        IshiharaTest test = new IshiharaTest();

        LocalDateTime dateCreated = LocalDateTime.now(ZoneOffset.UTC);
        test.setDateStarted(dateCreated);

        test.setQuestions(getShuffledQuestions(maxTestQuestions)
                .stream()
                .map(ishiharaQuestion -> {
                    TestQuestion question = new TestQuestion();
                    question.setQuestion(ishiharaQuestion);
                    question.setTest(test);
                    return question;
                })
                .collect(Collectors.toList())
        );

        this.testRepository.save(test);

        IshiharaTest retrievedTest = this.testRepository.findByDateStarted(dateCreated);

        return new IshiharaTestData(retrievedTest.getId(),
                retrievedTest.getQuestions()
                        .stream()
                        .map(question -> new QuestionData(question.getQuestion().getImageUrl(), null, null))
                        .collect(Collectors.toList()),
                dateCreated);
    }

    public void submitAnswers(Integer id, IshiharaTestData testData) {
        Optional<IshiharaTest> optTest = this.testRepository.findById(id);

        if (optTest.isEmpty()) {
            throw new ResourceNotFoundException("Test not found");
        }

        List<Integer> answers = testData.questions().stream().map(QuestionData::userGuess).toList();

        if (answers.stream().anyMatch(Objects::isNull)) {
            throw new DataFormatException("All answers must be present");
        }

        IshiharaTest test = optTest.get();

        if (answers.size() != test.getQuestions().size()) {
            throw new DataFormatException("Required exactly %d answers".formatted(test.getQuestions().size()));
        }

        for (int i = 0; i < answers.size(); i++) {
            TestQuestion question = test.getQuestions().get(i);
            question.setUserGuess(answers.get(i));
        }

        this.testRepository.save(test);
        // localhost:8080/ishihara-tests/pokDWAdk
    }

    private String generateLink() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        return null;
    }

    private List<IshiharaQuestion> getShuffledQuestions(int maxAmount) {
        List<IshiharaQuestion> questions = this.questionRepository.findAll();
        Collections.shuffle(questions);
        return questions.stream().limit(maxAmount).collect(Collectors.toList());
    }
}
