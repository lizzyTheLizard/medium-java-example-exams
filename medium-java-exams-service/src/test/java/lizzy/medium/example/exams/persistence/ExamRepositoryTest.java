package lizzy.medium.example.exams.persistence;

import lizzy.medium.example.exams.domain.TestData;
import lizzy.medium.example.exams.domain.model.Exam;
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
@ImportAutoConfiguration({JdbcExamRepository.class, JdbcParticipationRepository.class, JdbcQuestionRepository.class})
public class ExamRepositoryTest {
    @Autowired
    JdbcExamRepository jdbcExamRepository;

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
                .maxAttempts(2)
                .owner(TestData.user.getId())
                .text("old")
                .title("old")
                .build();
        this.jdbcExam = examRepository.save(jdbcExam);
    }

    @Test
    void findAllOwnedBy() {
        List<Exam> results = jdbcExamRepository.findAllRunningOwnedBy(TestData.user);

        Assertions.assertEquals(1, results.size());
        Exam result = results.get(0);
        Assertions.assertEquals(jdbcExam.getId(), result.getId());
        Assertions.assertEquals(jdbcExam.getOwner(), result.getOwnerId());
        Assertions.assertEquals(jdbcExam.getText(), result.getText());
        Assertions.assertEquals(jdbcExam.getTitle(), result.getTitle());
        Assertions.assertEquals(jdbcExam.getMaxAttempts(), result.getMaxAttempts());
        Assertions.assertEquals(jdbcExam.isClosed(), result.isClosed());
    }

    @Test
    void findById() {
        Exam result = jdbcExamRepository.findById(jdbcExam.getId()).orElseThrow(AssertionFailedError::new);

        Assertions.assertEquals(jdbcExam.getId(), result.getId());
        Assertions.assertEquals(jdbcExam.getOwner(), result.getOwnerId());
        Assertions.assertEquals(jdbcExam.getText(), result.getText());
        Assertions.assertEquals(jdbcExam.getTitle(), result.getTitle());
        Assertions.assertEquals(jdbcExam.getMaxAttempts(), result.getMaxAttempts());
        Assertions.assertEquals(jdbcExam.isClosed(), result.isClosed());
    }

    @Test
    void update() {
        jdbcExamRepository.update(TestData.exam);
        Exam result = jdbcExamRepository.findById(jdbcExam.getId()).orElseThrow(AssertionFailedError::new);

        Assertions.assertEquals(TestData.exam.getId(), result.getId());
        Assertions.assertEquals(TestData.exam.getOwnerId(), result.getOwnerId());
        Assertions.assertEquals(TestData.exam.getText(), result.getText());
        Assertions.assertEquals(TestData.exam.getTitle(), result.getTitle());
        Assertions.assertEquals(TestData.exam.getMaxAttempts(), result.getMaxAttempts());
        Assertions.assertEquals(TestData.exam.isClosed(), result.isClosed());
    }

    @Test
    void add() {
        Exam exam = TestData.exam.toBuilder()
                .id(UUID.randomUUID())
                .build();
        jdbcExamRepository.add(exam);
        Exam result = jdbcExamRepository.findById(exam.getId()).orElseThrow(AssertionFailedError::new);

        Assertions.assertEquals(exam.getId(), result.getId());
        Assertions.assertEquals(exam.getOwnerId(), result.getOwnerId());
        Assertions.assertEquals(exam.getText(), result.getText());
        Assertions.assertEquals(exam.getTitle(), result.getTitle());
        Assertions.assertEquals(exam.getMaxAttempts(), result.getMaxAttempts());
        Assertions.assertEquals(exam.isClosed(), result.isClosed());
    }
}
