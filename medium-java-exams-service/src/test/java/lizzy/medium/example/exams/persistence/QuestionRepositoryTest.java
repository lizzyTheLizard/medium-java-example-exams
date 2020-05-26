package lizzy.medium.example.exams.persistence;

import lizzy.medium.example.exams.domain.TestData;
import lizzy.medium.example.exams.domain.model.Question;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@ImportAutoConfiguration({JdbcQuestionRepository.class})
public class QuestionRepositoryTest {
    @Autowired
    JdbcQuestionRepository jdbcQuestionRepository;

    @Autowired
    JdbcQuestionSpringDataRepository questionRepository;

    @Autowired
    JdbcExamSpringDataRepository examRepository;

    JdbcExam jdbcExam;

    @BeforeEach
    void setup() {
        questionRepository.findAll().forEach(questionRepository::delete);
        examRepository.findAll().forEach(examRepository::delete);
        JdbcExam jdbcExam = JdbcExam.builder()
                .id(TestData.exam.getId())
                .closed(false)
                .maxAttempts(1)
                .owner("test")
                .text("test")
                .title("test")
                .build();
        this.jdbcExam = examRepository.save(jdbcExam);
    }

    @Test
    void findForExam() {
        JdbcQuestion question = JdbcQuestion.builder()
                .correctOption(1)
                .id(UUID.randomUUID())
                .option("op1")
                .text("test")
                .exam(jdbcExam)
                .build();
        questionRepository.save(question);

        List<Question> results = jdbcQuestionRepository.getQuestions(TestData.exam);

        Assertions.assertEquals(1, results.size());
        Question result = results.get(0);
        Assertions.assertIterableEquals(question.getOptions(), result.getOptions());
        Assertions.assertEquals(question.getOptions().size(), result.getOptions().size());
        Assertions.assertEquals(question.getOptions().get(0), result.getOptions().get(0));
    }

    @Test
    void add() {
        jdbcQuestionRepository.add(TestData.exam, TestData.question);

        JdbcQuestion result = questionRepository.findById(TestData.question.getId()).orElseThrow(AssertionFailedError::new);

        Assertions.assertIterableEquals(TestData.question.getOptions(), result.getOptions());
        Assertions.assertEquals(TestData.question.getText(), result.getText());
        Assertions.assertEquals(TestData.question.getCorrectOption(), result.getCorrectOption());
        Assertions.assertEquals(TestData.question.getId(), result.getId());
        Assertions.assertNotNull(result.getExam());
        Assertions.assertEquals(jdbcExam.getId(), result.getExam().getId());
    }
}
