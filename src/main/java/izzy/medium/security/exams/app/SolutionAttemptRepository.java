package izzy.medium.security.exams.app;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.security.Principal;
import java.util.UUID;

@Repository
public interface SolutionAttemptRepository extends CrudRepository<SolutionAttempt, UUID> {
    @Query("select count(a) from SolutionAttempt a where a.user = :#{#principal.getName()} AND a.exam = :#{#exam}")
    int countForExamAndUser(Exam exam, Principal principal);
}
