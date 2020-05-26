package lizzy.medium.example.exams.persistence;

import lizzy.medium.example.exams.domain.TestData;
import lizzy.medium.example.exams.domain.model.Participation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@ImportAutoConfiguration({JdbcParticipationRepository.class})
public class ParticipationRepositoryTest {
    @Autowired
    JdbcParticipationRepository jdbcParticipationRepository;
    @Autowired
    JdbcParticipationSpringDataRepository participationSpringDataRepository;
    @Autowired
    JdbcExamSpringDataRepository examRepository;
    private JdbcParticipation jdbcParticipation;

    @BeforeEach
    void setup() {
        examRepository.findAll().forEach(examRepository::delete);
        JdbcExam jdbcExam = JdbcExam.builder()
                .id(TestData.exam.getId())
                .closed(false)
                .maxAttempts(1)
                .owner("test")
                .text("test")
                .title("test")
                .build();
        jdbcExam = examRepository.save(jdbcExam);

        participationSpringDataRepository.findAll().forEach(participationSpringDataRepository::delete);
        JdbcParticipation jdbcParticipation = JdbcParticipation.builder()
                .id(UUID.randomUUID())
                .exam(jdbcExam)
                .firstName(TestData.user.getFirstName())
                .lastName(TestData.user.getLastName())
                .userId(TestData.user.getId())
                .time(ZonedDateTime.now().minus(100, ChronoUnit.SECONDS))
                .successful(false)
                .comment("Initial")
                .remainingAttempts(2)
                .build();
        this.jdbcParticipation = participationSpringDataRepository.save(jdbcParticipation);
    }

    @Test
    void getNumberOfFailed() {
        int result = jdbcParticipationRepository.getNumberOfFailed(TestData.exam, TestData.user);
        Assertions.assertEquals(1, result);
    }

    @Test
    void getMostRecentForeachUser() {
        List<Participation> result = jdbcParticipationRepository.getMostRecentForeachUser(TestData.exam);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(jdbcParticipation.getUserId(), result.get(0).getUser().getId());
        Assertions.assertEquals(jdbcParticipation.getFirstName(), result.get(0).getUser().getFirstName());
        Assertions.assertEquals(jdbcParticipation.getLastName(), result.get(0).getUser().getLastName());
        Assertions.assertEquals(jdbcParticipation.getId(), result.get(0).getId());
        Assertions.assertEquals(jdbcParticipation.isSuccessful(), result.get(0).isSuccessful());
        Assertions.assertEquals(jdbcParticipation.getComment(), result.get(0).getComment());
        Assertions.assertEquals(jdbcParticipation.getRemainingAttempts(), result.get(0).getRemainingAttempts());
        Assertions.assertEquals(jdbcParticipation.getTime(), result.get(0).getTime());
    }

    @Test
    void add() {
        jdbcParticipationRepository.add(TestData.exam, TestData.participation);

        List<Participation> result = jdbcParticipationRepository.getMostRecentForeachUser(TestData.exam);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(TestData.user, result.get(0).getUser());
        Assertions.assertEquals(TestData.participation.getId(), result.get(0).getId());
        Assertions.assertEquals(TestData.participation.isSuccessful(), result.get(0).isSuccessful());
        Assertions.assertEquals(TestData.participation.getComment(), result.get(0).getComment());
        Assertions.assertEquals(TestData.participation.getRemainingAttempts(), result.get(0).getRemainingAttempts());
        Assertions.assertEquals(TestData.participation.getTime(), result.get(0).getTime());
    }
}
