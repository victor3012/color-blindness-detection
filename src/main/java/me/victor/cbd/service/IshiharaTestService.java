package me.victor.cbd.service;

import me.victor.cbd.model.entity.IshiharaQuestion;
import me.victor.cbd.model.entity.IshiharaTest;
import me.victor.cbd.model.entity.TestQuestion;
import me.victor.cbd.model.model.CreatedTestData;
import me.victor.cbd.model.model.QuestionData;
import me.victor.cbd.repository.IshiharaQuestionRepository;
import me.victor.cbd.repository.IshiharaTestRepository;
import me.victor.cbd.repository.TestQuestionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IshiharaTestService {
    private final int MAX_TEST_QUESTIONS = 25;

    private final IshiharaTestRepository testRepository;
    private final IshiharaQuestionRepository questionRepository;
    private final TestQuestionRepository testQuestionRepository;

    public IshiharaTestService(IshiharaTestRepository testRepository, IshiharaQuestionRepository questionRepository,
                               TestQuestionRepository testQuestionRepository) {
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.testQuestionRepository = testQuestionRepository;
    }

    public CreatedTestData createTest() {
        IshiharaTest test = new IshiharaTest();

        LocalDateTime dateCreated = LocalDateTime.now(ZoneOffset.UTC);
        test.setDateStarted(dateCreated);

        test.setQuestions(getShuffledQuestions(MAX_TEST_QUESTIONS)
                .stream()
                .map(ishiharaQuestion -> {
                    TestQuestion question = new TestQuestion();
                    question.setQuestion(ishiharaQuestion);
                    return question;
                })
                .collect(Collectors.toList())
        );

        this.testRepository.save(test);

        test = this.testRepository.findByDateStarted(dateCreated);

        return new CreatedTestData(test.getId(),
                test.getQuestions()
                        .stream()
                        .map(question -> new QuestionData(question.getQuestion().getImageUrl(), null, null))
                        .collect(Collectors.toList()),
                dateCreated);
    }

    private List<IshiharaQuestion> getShuffledQuestions(int maxAmount) {
        List<IshiharaQuestion> questions = this.questionRepository.findAll();
        Collections.shuffle(questions);
        return questions.stream().limit(maxAmount).collect(Collectors.toList());
    }
}
