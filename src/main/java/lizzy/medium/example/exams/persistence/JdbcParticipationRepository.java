package lizzy.medium.example.exams.persistence;

import lizzy.medium.example.exams.domain.Exam;
import lizzy.medium.example.exams.domain.Participation;
import lizzy.medium.example.exams.domain.ParticipationRepository;
import lizzy.medium.example.exams.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JdbcParticipationRepository implements ParticipationRepository {
    private final JdbcParticipationSpringDataRepository springDataSolutionAttemptRepository;
    private final EntityManager entityManager;

    @Override
    public int getNumberOfFailed(Exam exam, User user) {
        JdbcExam jdbcExam = JdbcExam.of(exam);
        return springDataSolutionAttemptRepository.countByExamAndUserIdAndSuccessful(jdbcExam, user.getId(), false);
    }

    @Override
    public List<Participation> getMostRecentForeachUser(Exam exam) {
        JdbcExam jdbcExam = JdbcExam.of(exam);
        return springDataSolutionAttemptRepository.findLatestForExam(jdbcExam).stream()
                .map(JdbcParticipation::toEntity)
                .collect(Collectors.toList());
    }

    //TODO: This is vulnerable to an SQL-Injection
    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void add(Exam exam, Participation participation) {
        Query query = entityManager.createNativeQuery("" +
                "INSERT INTO PARTICIPATION " +
                "(ID, EXAM_ID, SUCCESSFUL, TIME, USER_ID, FIRST_NAME, LAST_NAME, REMAINING_ATTEMPTS, COMMENT) " +
                "values (" +
                "'" + participation.getId() + "'," +
                "'" + exam.getId() + "'," +
                "" + participation.isSuccessful() + "," +
                "'" + participation.getTime() + "'," +
                "'" + participation.getUser().getId() + "'," +
                "'" + participation.getUser().getFirstName() + "'," +
                "'" + participation.getUser().getLastName() + "'," +
                "'" + participation.getRemainingAttempts() + "'," +
                "'" + participation.getComment() + "')");
        query.executeUpdate();
    }
}



