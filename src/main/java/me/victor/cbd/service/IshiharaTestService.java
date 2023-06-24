package me.victor.cbd.service;

import me.victor.cbd.exception.DataFormatException;
import me.victor.cbd.exception.ResourceNotFoundException;
import me.victor.cbd.model.entity.IshiharaQuestion;
import me.victor.cbd.model.entity.IshiharaTest;
import me.victor.cbd.model.entity.TestQuestion;
import me.victor.cbd.model.enumration.BlindnessType;
import me.victor.cbd.model.model.IshiharaCompletedTestData;
import me.victor.cbd.model.model.IshiharaTestData;
import me.victor.cbd.model.model.QuestionData;
import me.victor.cbd.repository.IshiharaQuestionRepository;
import me.victor.cbd.repository.IshiharaTestRepository;
import me.victor.cbd.repository.TestQuestionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
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
                dateCreated,
                retrievedTest.getQuestions()
                        .stream()
                        .map(question -> new QuestionData(question.getQuestion().getImageUrl(), null, null))
                        .toList());
    }

    public String submitAnswers(Integer id, IshiharaTestData testData) {
        Optional<IshiharaTest> optTest = this.testRepository.findById(id);

        if (optTest.isEmpty()) {
            throw new ResourceNotFoundException("Test not found");
        }

        List<Integer> answers = testData.questions().stream().map(QuestionData::userGuess).toList();

        if (answers.stream().anyMatch(Objects::isNull)) {
            throw new DataFormatException("All answers must be present");
        }

        IshiharaTest test = optTest.get();

        if (!Objects.isNull(test.getTestUrl())) {
            throw new DataFormatException("Test #%d is already completed".formatted(test.getId()));
        }

        if (answers.size() != test.getQuestions().size()) {
            throw new DataFormatException("Required exactly %d answers".formatted(test.getQuestions().size()));
        }

        for (int i = 0; i < answers.size(); i++) {
            TestQuestion question = test.getQuestions().get(i);
            question.setUserGuess(answers.get(i));
        }

        test.setTestUrl(generateLink());
        test.setDateEnded(LocalDateTime.now(ZoneOffset.UTC));

        this.testRepository.save(test);

        return test.getTestUrl();
    }

    public IshiharaCompletedTestData getTestResult(String testUrl) {
        IshiharaTest test = this.testRepository.findByTestUrl(testUrl)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid test url"));

        test.setQuestions(test.getQuestions()
                .stream()
                .sorted(Comparator.comparingInt(TestQuestion::getId))
                .toList()
        );

        int totalQuestionsCount = test.getQuestions().size();

        int correctQuestionsCount = (int) test.getQuestions()
                .stream()
                .filter(x -> x.getUserGuess() == x.getQuestion().getNumber())
                .count();

        int wrongQuestionsCount = totalQuestionsCount - correctQuestionsCount;

        double accuracy = correctQuestionsCount * 1.0 / totalQuestionsCount;

        Map<String, Double> blindnessTypeAccuracy = getBlindnessTypeAccuracy(test.getQuestions());

        return new IshiharaCompletedTestData(test.getId(), test.getTestUrl(), test.getDateStarted(),
                test.getDateEnded(), totalQuestionsCount, correctQuestionsCount, wrongQuestionsCount,
                accuracy, blindnessTypeAccuracy,
                test.getQuestions()
                        .stream()
                        .map(question -> new QuestionData(question.getQuestion().getImageUrl(),
                                question.getUserGuess(), question.getQuestion().getNumber()))
                        .toList());
    }

    private List<IshiharaQuestion> getShuffledQuestions(int maxAmount) {
        List<IshiharaQuestion> questions = this.questionRepository.findAll();
        Collections.shuffle(questions);
        return questions.stream().limit(maxAmount).collect(Collectors.toList());
    }

    private String generateLink() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();

        StringBuilder link;

        do {
            link = new StringBuilder(this.generatedLinkLength);

            for (int i = 0; i < this.generatedLinkLength; i++) {
                link.append(letters.charAt(random.nextInt(letters.length())));
            }
        } while (this.testRepository.findByTestUrl(link.toString()).isPresent());

        return link.toString();
    }

    private Map<String, Double> getBlindnessTypeAccuracy(List<TestQuestion> questions) {
        Map<String, Double> map = new HashMap<>();

        for (BlindnessType type : BlindnessType.values()) {
            List<TestQuestion> typeQuestions = questions.stream()
                    .filter(x -> x.getQuestion().getColourBlindness() == type)
                    .toList();

            int totalQuestions = typeQuestions.size();
            int correctQuestions = (int) typeQuestions.stream()
                    .filter(x -> x.getUserGuess() == x.getQuestion().getNumber())
                    .count();

            map.put(type.toString(), correctQuestions * 1.0 / totalQuestions);
        }

        return map;
    }
}
