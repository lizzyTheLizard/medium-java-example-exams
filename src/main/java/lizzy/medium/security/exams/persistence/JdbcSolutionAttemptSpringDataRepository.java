package lizzy.medium.security.exams.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
interface JdbcSolutionAttemptSpringDataRepository extends CrudRepository<JdbcSolutionAttempt, UUID> {
    int countByExamAndUserId(JdbcExam jdbcExam, String userId);

    List<JdbcSolutionAttempt> findByExam(JdbcExam jdbcExam);
}
