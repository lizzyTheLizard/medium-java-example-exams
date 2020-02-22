package lizzy.medium.security.exams.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Repository
public interface SolutionAttemptRepository extends CrudRepository<SolutionAttempt, UUID> {
    @Query("select count(a) from SolutionAttempt a where a.participant.userId = :#{#principal.getName()} AND a.exam = :#{#exam}")
    int countForExamAndUser(Exam exam, Principal principal);

    @Query("select a.participant.userId from SolutionAttempt a where a.exam = :#{#exam} GROUP BY a.participant.userId")
    List<String> participantIdsByExam(Exam exam);

    @Query("select a from SolutionAttempt a where a.exam = :#{#exam} and a.participant.userId = :#{#participantId}")
    List<SolutionAttempt> findForParticipantAndExam(String participantId, Exam exam);
}
