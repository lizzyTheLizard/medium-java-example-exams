package lizzy.medium.security.exams.persistence;

import lizzy.medium.security.exams.domain.Exam;
import lizzy.medium.security.exams.domain.SolutionAttempt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@ImportAutoConfiguration({JdbcSolutionAttemptRepository.class})
public class SolutionAttemptRepositoryTest {
    private final static String user = "test";
    @Autowired
    JdbcSolutionAttemptRepository jdbcSolutionAttemptRepository;
    @Autowired
    JdbcSolutionAttemptSpringDataRepository solutionAttemptRepository;
    @Autowired
    JdbcExamSpringDataRepository examRepository;
    @Mock
    private Exam exam;
    private JdbcSolutionAttempt jdbcSolutionAttempt;

    @BeforeEach
    void setup() {
        examRepository.findAll().forEach(examRepository::delete);
        JdbcExam jdbcExam = JdbcExam.builder()
                .id(UUID.randomUUID())
                .closed(false)
                .maxAttempts(1)
                .owner("test")
                .text("test")
                .title("test")
                .build();
        jdbcExam = examRepository.save(jdbcExam);
        Mockito.when(exam.getId()).thenReturn(jdbcExam.getId());

        solutionAttemptRepository.findAll().forEach(solutionAttemptRepository::delete);
        JdbcSolutionAttempt jdbcSolutionAttempt = JdbcSolutionAttempt.builder()
                .id(UUID.randomUUID())
                .exam(jdbcExam)
                .firstName("first")
                .lastName("last")
                .userId(user)
                .time(ZonedDateTime.now())
                .success(false)
                .build();
        this.jdbcSolutionAttempt = solutionAttemptRepository.save(jdbcSolutionAttempt);
    }

    @Test
    void countForExamAndUser() {
        int result = jdbcSolutionAttemptRepository.countForExamAndUser(exam, user);
        Assertions.assertEquals(1, result);
    }

    @Test
    void findForExam() {
        List<SolutionAttempt> result = jdbcSolutionAttemptRepository.findForExam(exam);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(user, result.get(0).getUserId());
        Assertions.assertEquals(jdbcSolutionAttempt.getFirstName(), result.get(0).getFirstName());
        Assertions.assertEquals(jdbcSolutionAttempt.getLastName(), result.get(0).getLastName());
        Assertions.assertEquals(jdbcSolutionAttempt.getId(), result.get(0).getId());
    }

    @Test
    void add() {
        solutionAttemptRepository.findAll().forEach(solutionAttemptRepository::delete);

        SolutionAttempt solutionAttempt = SolutionAttempt.builder()
                .firstName("test")
                .lastName("test")
                .comment("testing")
                .success(true)
                .userId(user)
                .id(UUID.randomUUID())
                .build();
        jdbcSolutionAttemptRepository.add(exam, solutionAttempt);

        List<SolutionAttempt> result = jdbcSolutionAttemptRepository.findForExam(exam);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(user, result.get(0).getUserId());
        Assertions.assertEquals(solutionAttempt.getFirstName(), result.get(0).getFirstName());
        Assertions.assertEquals(solutionAttempt.getLastName(), result.get(0).getLastName());
        Assertions.assertEquals(solutionAttempt.getId(), result.get(0).getId());
        Assertions.assertEquals(solutionAttempt.isSuccess(), result.get(0).isSuccess());
        Assertions.assertEquals(solutionAttempt.getComment(), result.get(0).getComment());
    }

}
