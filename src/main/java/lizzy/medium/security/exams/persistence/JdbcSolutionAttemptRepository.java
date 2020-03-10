package lizzy.medium.security.exams.persistence;

import lizzy.medium.security.exams.domain.Exam;
import lizzy.medium.security.exams.domain.SolutionAttempt;
import lizzy.medium.security.exams.domain.SolutionAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JdbcSolutionAttemptRepository implements SolutionAttemptRepository {
    private final JdbcSolutionAttemptSpringDataRepository springDataSolutionAttemptRepository;
    private final EntityManager entityManager;

    @Override
    public int countForExamAndUser(Exam exam, String userId) {
        JdbcExam jdbcExam = JdbcExam.of(exam);
        return springDataSolutionAttemptRepository.countByExamAndUserId(jdbcExam, userId);
    }

    @Override
    public List<SolutionAttempt> findForExam(Exam exam) {
        JdbcExam jdbcExam = JdbcExam.of(exam);
        return springDataSolutionAttemptRepository.findByExam(jdbcExam).stream()
                .map(JdbcSolutionAttempt::toEntity)
                .collect(Collectors.toList());
    }

    //TODO: This is vulnerable to an SQL-Injection
    @Override
    public void add(Exam exam, SolutionAttempt solutionAttempt) {
        Query query = entityManager.createNativeQuery("" +
                "INSERT INTO SOLUTION_ATTEMPT " +
                "(ID, EXAM_ID, SUCCESS, TIME, USER_ID, FIRST_NAME, LAST_NAME, COMMENT) " +
                "values (" +
                "'" + solutionAttempt.getId() + "'," +
                "'" + exam.getId() + "'," +
                "" + solutionAttempt.isSuccess() + "," +
                "NOW()," +
                "'" + solutionAttempt.getUserId() + "'," +
                "'" + solutionAttempt.getFirstName() + "'," +
                "'" + solutionAttempt.getLastName() + "'," +
                "'" + solutionAttempt.getComment() + "')");
        query.executeUpdate();
    }
}



