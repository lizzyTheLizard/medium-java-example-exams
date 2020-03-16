package lizzy.medium.example.exams.persistence;

import lizzy.medium.example.exams.domain.Exam;
import lizzy.medium.example.exams.domain.Participation;
import lizzy.medium.example.exams.domain.User;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@ImportAutoConfiguration({JdbcParticipationRepository.class})
public class ParticipationRepositoryTest {
    private final static User user = User.builder()
            .firstName("first")
            .lastName("last")
            .id("userid")
            .build();
    @Autowired
    JdbcParticipationRepository jdbcParticipationRepository;
    @Autowired
    JdbcParticipationSpringDataRepository participationSpringDataRepository;
    @Autowired
    JdbcExamSpringDataRepository examRepository;
    @Mock
    private Exam exam;
    private JdbcParticipation jdbcParticipation;

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

        participationSpringDataRepository.findAll().forEach(participationSpringDataRepository::delete);
        JdbcParticipation jdbcParticipation = JdbcParticipation.builder()
                .id(UUID.randomUUID())
                .exam(jdbcExam)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userId(user.getId())
                .time(ZonedDateTime.now().minus(100, ChronoUnit.SECONDS))
                .successful(false)
                .comment("Initial")
                .remainingAttempts(2)
                .build();
        this.jdbcParticipation = participationSpringDataRepository.save(jdbcParticipation);
    }

    @Test
    void getNumberOfFailed() {
        int result = jdbcParticipationRepository.getNumberOfFailed(exam, user);
        Assertions.assertEquals(1, result);
    }

    @Test
    void getMostRecentForeachUser() {
        List<Participation> result = jdbcParticipationRepository.getMostRecentForeachUser(exam);
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
        Participation participation = Participation.builder()
                .id(UUID.randomUUID())
                .user(user)
                .time(ZonedDateTime.now().plus(100, ChronoUnit.SECONDS))
                .successful(true)
                .time(ZonedDateTime.now())
                .comment("This time it worked")
                .remainingAttempts(1)
                .build();
        jdbcParticipationRepository.add(exam, participation);

        List<Participation> result = jdbcParticipationRepository.getMostRecentForeachUser(exam);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(user, result.get(0).getUser());
        Assertions.assertEquals(participation.getId(), result.get(0).getId());
        Assertions.assertEquals(participation.isSuccessful(), result.get(0).isSuccessful());
        Assertions.assertEquals(participation.getComment(), result.get(0).getComment());
        Assertions.assertEquals(participation.getRemainingAttempts(), result.get(0).getRemainingAttempts());
        Assertions.assertEquals(participation.getTime(), result.get(0).getTime());
    }
}
