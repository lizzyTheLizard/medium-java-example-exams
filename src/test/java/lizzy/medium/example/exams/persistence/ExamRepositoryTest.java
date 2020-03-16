package lizzy.medium.example.exams.persistence;

import lizzy.medium.example.exams.domain.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@ImportAutoConfiguration({JdbcExamRepository.class, JdbcParticipationRepository.class, JdbcQuestionRepository.class})
public class ExamRepositoryTest {
    private final static User user = User.builder()
            .id("test")
            .firstName("first")
            .lastName("last")
            .build();

    @Autowired
    JdbcExamRepository jdbcExamRepository;

    @Autowired
    JdbcQuestionSpringDataRepository questionRepository;

    @Autowired
    JdbcExamSpringDataRepository examRepository;

    JdbcExam jdbcExam;

    @MockBean
    ExamFactory examFactory;

    @BeforeEach
    void setup() {
        questionRepository.findAll().forEach(questionRepository::delete);
        examRepository.findAll().forEach(examRepository::delete);
        JdbcExam jdbcExam = JdbcExam.builder()
                .id(UUID.randomUUID())
                .closed(false)
                .maxAttempts(1)
                .owner("test")
                .text("test")
                .title("test")
                .build();
        this.jdbcExam = examRepository.save(jdbcExam);
        Mockito.when(examFactory.create()).thenReturn(Exam.builder()
                .questionRepository(Mockito.mock(QuestionRepository.class))
                .participationRepository(Mockito.mock(ParticipationRepository.class))
                .examRepository(Mockito.mock(ExamRepository.class)));
    }

    @Test
    void findAllOwnedBy() {
        List<Exam> results = jdbcExamRepository.findAllRunningOwnedBy(user);

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
        Exam exam = examFactory.create()
                .closed(true)
                .maxAttempts(2)
                .ownerId("change")
                .title("change")
                .text("change")
                .id(jdbcExam.getId())
                .build();
        jdbcExamRepository.update(exam);
        Exam result = jdbcExamRepository.findById(jdbcExam.getId()).orElseThrow(AssertionFailedError::new);

        Assertions.assertEquals(exam.getId(), result.getId());
        Assertions.assertEquals(exam.getOwnerId(), result.getOwnerId());
        Assertions.assertEquals(exam.getText(), result.getText());
        Assertions.assertEquals(exam.getTitle(), result.getTitle());
        Assertions.assertEquals(exam.getMaxAttempts(), result.getMaxAttempts());
        Assertions.assertEquals(exam.isClosed(), result.isClosed());
    }

    @Test
    void add() {
        Exam exam = examFactory.create()
                .closed(true)
                .maxAttempts(2)
                .ownerId("change")
                .title("change")
                .text("change")
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
